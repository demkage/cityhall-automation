package project.mad.martialstatus.web.socket.handler.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.Configuration
import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.qualifier.FirstStepHandlerType

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.json.JsonObject
import javax.json.spi.JsonProvider
import javax.websocket.Session

@FirstStepHandlerType
@ApplicationScoped
class FirstStepHandler extends AbstractSocketHandler {
    @Inject
    @Default
    Configuration configuration

    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session, configuration) &&
                (object.getInt("step", 0) == 0 && object.containsKey("config"))
    }

    @Override
    void handle(JsonObject object, Session session) {

        String sessionKey = constructSessionKey(
                configuration.step1.actor + configuration.step1.name
                        + configuration.step1.cnp + configuration.step1.email,
                object.getString("secretKey1"),
                object.getString("secretKey2")
        )

        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject jsonObject = jsonProvider.createObjectBuilder()
                .add("actor", configuration.step1.actor)
                .add("nume", configuration.step1.name)
                .add("cnp", configuration.step1.cnp)
                .add("email", configuration.step1.email)
                .add("session", sessionKey)
                .build()

        log.info("First step send: {}", jsonObject)

        session.getBasicRemote().sendObject(jsonObject)

    }

}
