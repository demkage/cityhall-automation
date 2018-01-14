package project.mad.martialstatus.web.socket.handler.steps

import project.mad.martialstatus.web.socket.CityHallMartialStatusFlow
import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.qualifier.ConfirmStepHandlerType

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.json.JsonObject
import javax.websocket.CloseReason
import javax.websocket.Session

@ConfirmStepHandlerType
@ApplicationScoped
class ConfirmStepHandler extends AbstractSocketHandler {
    @Inject
    CityHallMartialStatusFlow flow

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session) && object.getInt("step") == 3
    }

    @Override
    void handle(JsonObject object, Session session) {
        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, ""))
        flow.signalEnd()
    }
}
