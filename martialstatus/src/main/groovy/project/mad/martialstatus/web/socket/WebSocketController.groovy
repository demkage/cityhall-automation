package project.mad.martialstatus.web.socket

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.Configuration

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.websocket.ContainerProvider
import javax.websocket.DeploymentException
import javax.websocket.Session
import javax.websocket.WebSocketContainer

@ApplicationScoped
class WebSocketController {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Inject
    Configuration configuration

    @Inject
    CityHallMartialStatusFlow flow

    void run(CityHallMartialStatusProgramClient client) {

        while (!connect(client)) {
            log.info("Reconnect after: {} ms", configuration.network.retryTimeMs)
            Thread.sleep(configuration.network.retryTimeMs)
            log.info("Reconnecting...")
        }

    }

    private boolean connect(CityHallMartialStatusProgramClient client) {
        try {
            log.info("Connecting to: {}", configuration.network.webSocketUrl)
            WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer()
            Session session = webSocketContainer.connectToServer(client,
                    URI.create(configuration.network.webSocketUrl))
            flow.waitUntilEnd()
            log.info("Closed by not open yet: {}", flow.closedByNotOpenYet)
            return !flow.closedByNotOpenYet
        } catch (DeploymentException exception) {
            flow.signalEnd()
            return false
        }

    }
}
