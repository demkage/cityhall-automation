package project.mad.martialstatus.web.socket

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.Configuration
import project.mad.martialstatus.web.connection.WebSocketConnection
import project.mad.martialstatus.web.connection.strategy.ConnectionStrategy
import project.mad.martialstatus.web.connection.strategy.qualifier.AggressiveConnectionStrategyType

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.DeploymentException

@ApplicationScoped
class WebSocketController {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Inject
    Configuration configuration

    @Inject
    CityHallMartialStatusFlow flow

    @Inject
    @AggressiveConnectionStrategyType
    ConnectionStrategy connectionStrategy

    void run(CityHallMartialStatusProgramClient client) {

        while (!connect(client)) {
            log.info("Reconnect after: {} ms", configuration.network.retryTimeMs)
            Thread.sleep(configuration.network.retryTimeMs)
            log.info("Reconnecting...")
        }

    }

    private boolean connect(CityHallMartialStatusProgramClient client) {
        try {
            flow.phaser.register()
            log.info("Connecting to: {}", configuration.network.webSocketUrl)
            WebSocketConnection connection = new WebSocketConnection(
                    configuration.network.webSocketUrl,
                    client,
                    configuration.network.connection.socketConnectionTimeout,
                    connectionStrategy
            )

            connection.connect {}

            connection.await()

            flow.waitUntilEnd()
            log.info("Closed by not open yet: {}", flow.closedByNotOpenYet)

            return !flow.closedByNotOpenYet && !flow.makeNewAppointment
        } catch (DeploymentException exception) {
            log.info("Catch deployment exception", exception)
            flow.signalEnd()
            return false
        }

    }
}
