package project.mad.martialstatus.web.socket.handler.steps

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.Configuration
import project.mad.martialstatus.web.socket.CityHallMartialStatusFlow
import project.mad.martialstatus.web.socket.handler.AbstractSocketHandler
import project.mad.martialstatus.web.socket.handler.qualifier.BackupSelectAppointmentStrategy
import project.mad.martialstatus.web.socket.handler.qualifier.ThirdStepHandlerType
import project.mad.martialstatus.web.socket.handler.strategy.ThirdStepSelectAppointmentsStrategy

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.json.JsonObject
import javax.json.spi.JsonProvider
import javax.websocket.Session

@ThirdStepHandlerType
@ApplicationScoped
class ThirdStepHandler extends AbstractSocketHandler {
    private final Logger log = LoggerFactory.getLogger(ThirdStepHandler.class)

    @Inject
    @Default
    ThirdStepSelectAppointmentsStrategy selectAppointmentsStrategy

    @Inject
    @BackupSelectAppointmentStrategy
    ThirdStepSelectAppointmentsStrategy backupSelectStrategy

    @Inject
    Configuration configuration

    @Inject
    CityHallMartialStatusFlow flow

    @Override
    boolean accept(JsonObject object, Session session) {
        return super.accept(object, session, configuration) && (
                object.getInt("step", 2) == 2 && isValidThirdStep(object))
    }

    @Override
    void handle(JsonObject object, Session session) {
        JsonObject selected

        try {
            selected = selectAppointmentsStrategy
                    .select(object.getJsonObject("date_disponibile"))

            if (selected == null) {
                selected = backupSelectStrategy.select(object.getJsonObject("date_disponibile"))
            }
        } catch (NullPointerException e) {
            e.printStackTrace()
            log.warn("Select appointments for sure throw NPE. " +
                    "If appointments was already selected then that is a bug with previous step :)")
            return
        }

        String sessionKey = constructSessionKey(object.getString("key"),
                object.getString("secretKey1"),
                object.getString("secretKey2"))

        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject jsonObject = jsonProvider.createObjectBuilder()
                .add("code", object.getString("key"))
                .add("programari", selected)
                .add("session", sessionKey)
                .build()

        session.getBasicRemote().sendObject(jsonObject)

        selected.entrySet().each {
            entry ->
                if (selected.getInt(entry.getKey()) > 0) {
                    log.info("Add to ignored days: {}", entry.getKey())
                    configuration.appointmentStrategy.ignoreDays.add(entry.getKey())
                }
        }

        boolean allMatched = configuration.appointmentStrategy.ignoreDays
                .containsAll(configuration.appointmentStrategy.preferableDays)

        flow.makeNewAppointment = !allMatched
    }

    private boolean isValidThirdStep(JsonObject object) {
        return object.containsKey("date_disponibile")
    }
}
