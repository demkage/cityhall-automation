package project.mad.martialstatus.web.connection.strategy

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.Configuration
import project.mad.martialstatus.web.connection.strategy.qualifier.AggressiveConnectionStrategyType
import project.mad.martialstatus.web.connection.strategy.qualifier.ConnectionStrategyExecutorService
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import javax.enterprise.context.Dependent
import javax.enterprise.inject.Default
import javax.inject.Inject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Semaphore

@AggressiveConnectionStrategyType
@Dependent
class AggressiveConnectionStrategy implements ConnectionStrategy {

    @Inject
    @ConnectionStrategyExecutorService
    ExecutorService executorService

    @Inject
    @Default
    Configuration configuration

    @Override
    ConnectionStrategy connect(Closure connectionPoint, Closure closeConnection, Closure callback) {
        ConnectionStrategy worker = new AggressiveConnectionStrategyWorker(
                configuration: configuration,
                executorService: executorService
        )

        return worker.connect(connectionPoint, closeConnection, callback)
    }

    @Override
    void awaitValidConnection() {
        throw new NotImplementedException()
    }
}

class AggressiveConnectionStrategyWorker implements ConnectionStrategy {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    ExecutorService executorService

    Configuration configuration

    private boolean connected = false

    private Semaphore semaphore = new Semaphore(1)

    private CountDownLatch releaseSignal = new CountDownLatch(1)

    ConnectionStrategy connect(Closure connectionPoint, Closure closeConnection, Closure callback) {

        def callbackWrapper = {
            def connection ->
                semaphore.acquire()

                if (connected) {
                    log.info("Client is already connected. Skip connection")
                    closeConnection.call(connection)
                    semaphore.release()
                    return
                }

                log.info("Successful connected to endpoint")
                connected = true
                callback.call(connection)

                releaseSignal.countDown()
                semaphore.release()
        }

        raiseNewThreads(connectionPoint, callbackWrapper,
                configuration.network.connection.parallelConnections)

        return this
    }

    void awaitValidConnection() {
        releaseSignal.await()
    }


    private raiseNewThreads(Closure connectionPoint, Closure callback, int count) {
        for (int i = 0; i < count; ++i) {
            executorService.submit({
                try {
                    def connection = connectionPoint.call()
                    callback.call(
                            connection
                    )
                } catch (ignored) {

                    semaphore.acquire()
                    if (connected) {
                        log.info("Connection failed. Skip since client is already connected.")
                        return
                    }

                    log.info("Connection failed. Raise new connection.")

                    raiseNewThreads(connectionPoint, callback, 1)

                    semaphore.release()
                }
            })

            Thread.sleep(configuration.network.connection.waitTimeBeforeRaiseNewConnection)
        }
    }
}
