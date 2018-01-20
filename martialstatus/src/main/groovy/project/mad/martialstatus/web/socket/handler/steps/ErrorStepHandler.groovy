package project.mad.martialstatus.web.socket.handler.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.exception.ReceivedUnexpectedErrorException
import project.mad.martialstatus.web.socket.handler.qualifier.ErrorHandlerType

import javax.enterprise.context.ApplicationScoped
import javax.json.JsonObject
import javax.websocket.Session

@ErrorHandlerType
@ApplicationScoped
class ErrorStepHandler extends AbstractSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session) &&
                (object.getInt("step", -1) != 9 && object.containsKey("error"))
    }

    @Override
    void handle(JsonObject object, Session session) {
        log.error("Received error: {}",
                object.getString("error", "Unknown"))

        throw new ReceivedUnexpectedErrorException()
    }
}
