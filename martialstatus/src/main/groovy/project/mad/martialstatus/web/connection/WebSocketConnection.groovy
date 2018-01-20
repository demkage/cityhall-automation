package project.mad.martialstatus.web.connection

import project.mad.martialstatus.web.connection.strategy.ConnectionStrategy

import javax.websocket.CloseReason
import javax.websocket.ContainerProvider
import javax.websocket.Session
import javax.websocket.WebSocketContainer

class WebSocketConnection extends AbstractConnection {
    private static final DEFAULT_TIMEOUT = 10000

    String webSocketUrl
    int timeout = DEFAULT_TIMEOUT
    Object client

    WebSocketConnection(String webSocketUrl, Object client, int timeout, ConnectionStrategy strategy) {
        super(strategy)
        this.webSocketUrl = webSocketUrl
        this.client = client
        this.timeout = timeout
    }

    @Override
    void connect(Closure callback) {
        executeStrategy(
                {
                    executeConnect()
                },
                {
                    session ->
                        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, ""))
                },
                callback
        )
    }

    Session executeConnect() {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer()
        webSocketContainer.setAsyncSendTimeout(timeout)
        webSocketContainer.connectToServer(client,
                URI.create(webSocketUrl))
    }
}
