package project.mad.martialstatus.web.socket.handler.strategy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.enterprise.inject.Any
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.spi.JsonProvider

@Any
class SelectAppointWithMostRecordsAvailableAndIgnoreDays extends SelectAppointmentWithMostRecordsAvailable {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    int maxAppointments
    List<String> ignoreDays

    @Override
    JsonObject select(JsonObject object) {
        JsonObject result = removeIgnoredDays(object)
        log.info("Removed ignored days. Result: {}. Original: {}", result, object)
        return super.select(result)
    }

    private removeIgnoredDays(JsonObject object) {
        JsonProvider provider = JsonProvider.provider()
        JsonObjectBuilder builder = provider.createObjectBuilder()

        object.entrySet().each {
            entry ->
                if (entry.getValue() != null && ignoreDays.find { day -> entry.getKey() == day } == null)
                    builder.add(entry.getKey(), entry.getValue())
        }

        builder.build()
    }
}
