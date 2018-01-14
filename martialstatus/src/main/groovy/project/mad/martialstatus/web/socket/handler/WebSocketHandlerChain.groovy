package project.mad.martialstatus.web.socket.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.json.JsonObject
import javax.websocket.Session

class WebSocketHandlerChain implements WebSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass())
    Set<WebSocketHandler> handlers = new HashSet<>()

    @Override
    boolean accept(JsonObject object, Session session) {
        log.info("[" + session.getId() + "] Accept " + object + " handlers: " + handlers.size())
        true
    }

    @Override
    void handle(JsonObject object, Session session) {
        handlers.each {
            handler ->
                if (handler.accept(object, session))
                    handler.handle(object, session)
        }
    }
}
