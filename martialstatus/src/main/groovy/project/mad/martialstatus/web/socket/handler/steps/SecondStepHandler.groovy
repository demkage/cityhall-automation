package project.mad.martialstatus.web.socket.handler.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.qualifier.SecondStepHandlerType

import javax.enterprise.context.ApplicationScoped
import javax.json.JsonObject
import javax.json.spi.JsonProvider
import javax.websocket.Session

@SecondStepHandlerType
@ApplicationScoped
class SecondStepHandler extends AbstractSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    Timer timer

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session) && (object.getInt("step") == 1 ||
                object.getBoolean("timer", false))
    }

    @Override
    void handle(JsonObject object, Session session) {
        if (object.getBoolean("timer", false)) {
            log.info("Stop timer")
            timer?.cancel()
            return
        }

        log.info("Your ticket is '{}'. First ticket in queue '{}'",
                object.getInt("ticket", 0), object.getInt("first", 0))

        timer = new Timer()

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            void run() {
                JsonProvider jsonProvider = JsonProvider.provider()
                JsonObject jsonObject = jsonProvider.createObjectBuilder()
                        .add("status", 0)
                        .build()

                session.getBasicRemote().sendObject(jsonObject)
            }
        }, 5000, 5000)


    }
}
