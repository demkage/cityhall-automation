package project.mad.martialstatus.web.socket.handler.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.FirstStepConfiguration
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
    FirstStepConfiguration configuration

    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session) && object.getInt("step") == 0
    }

    @Override
    void handle(JsonObject object, Session session) {

        String sessionKey = constructSessionKey(
                configuration.actor + configuration.name + configuration.cnp + configuration.email,
                object.getString("secretKey1"),
                object.getString("secretKey2")
        )

        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject jsonObject = jsonProvider.createObjectBuilder()
                .add("actor", configuration.actor)
                .add("nume", configuration.name)
                .add("cnp", configuration.cnp)
                .add("email", configuration.email)
                .add("session", sessionKey)
                .build()

        session.getBasicRemote().sendObject(jsonObject)

    }

}
