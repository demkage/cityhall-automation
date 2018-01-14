package project.mad.martialstatus.web.socket.handler.steps

import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.exception.NotOpenYetException
import project.mad.martialstatus.web.socket.handler.qualifier.NotOpenYetHandlerType

import javax.enterprise.context.ApplicationScoped
import javax.json.JsonObject
import javax.websocket.DeploymentException
import javax.websocket.Session

@NotOpenYetHandlerType
@ApplicationScoped
class NotOpenYetHandler extends AbstractSocketHandler {
    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session) && object.getInt("step") == 9
    }

    @Override
    void handle(JsonObject object, Session session) {
        throw new NotOpenYetException()
    }
}
