package project.mad.martialstatus.web.socket.handler

import javax.json.JsonObject
import javax.websocket.Session

abstract class AbstractSocketHandler implements WebSocketHandler {
    @Override
    boolean accept(JsonObject object, Session session) {
        return true
    }
}
