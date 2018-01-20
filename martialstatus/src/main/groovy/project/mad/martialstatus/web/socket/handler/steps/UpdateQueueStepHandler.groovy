package project.mad.martialstatus.web.socket.handler.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.qualifier.UpdateQueueHandlerType

import javax.enterprise.context.ApplicationScoped
import javax.json.JsonObject
import javax.websocket.Session

@UpdateQueueHandlerType
@ApplicationScoped
class UpdateQueueStepHandler extends AbstractSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session) && isValidUpdateQueueStep(object)
    }

    @Override
    void handle(JsonObject object, Session session) {
        log.info("Queue updated. Ticket in queue: ", object.getString("first"))
    }

    private boolean isValidUpdateQueueStep(JsonObject object) {
        return (object.containsKey("status") && object.containsKey("first") &&
                !object.containsKey("ticket"))
    }
}
