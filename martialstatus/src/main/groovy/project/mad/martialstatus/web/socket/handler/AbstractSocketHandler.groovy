package project.mad.martialstatus.web.socket.handler

import project.mad.martialstatus.config.Configuration

import javax.json.JsonObject
import javax.websocket.Session

abstract class AbstractSocketHandler implements WebSocketHandler {
    @Override
    boolean accept(JsonObject object, Session session) {
        return true
    }

    @Override
    boolean accept(JsonObject object, Session session, Configuration configuration) {
        return hasValidFields(configuration.availableFields, object)
    }
}
