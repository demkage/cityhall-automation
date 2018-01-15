package project.mad.martialstatus.web.socket.handler.strategy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.enterprise.inject.Any
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.JsonValue
import javax.json.spi.JsonProvider

@Any
class SelectAppointmentWithPreferableDays implements ThirdStepSelectAppointmentsStrategy {
    private final Logger log = LoggerFactory.getLogger(this.getClass())
    private List<String> preferableDays
    private List<String> ignoreDays
    private int maxAppointments

    @Override
    JsonObject select(JsonObject object) {
        log.info("Select using preferable days")
        String key
        if ((key = keyWithPreferableDay(object)) != null) {
            log.info("Found preferable day: {} with value: {}", key, object.getInt(key))
            return constructObject(object, key, object.getInt(key))
        }

        log.info("Not found any preferable day with appointments count: {}", maxAppointments)
        return null
    }

    String keyWithPreferableDay(JsonObject original) {
        JsonObject object = removeIgnoredDays(original)

        log.info("Remove days. Original: {}, Result: {}", original, object)

        findWithMostAppointmentsAvailable(object.entrySet().findAll {
            entry ->
                preferableDays.find {
                    day -> (entry.getKey() == day)
                }
        }, object)
    }

    static String findWithMostAppointmentsAvailable(Set<Map.Entry<String, JsonValue>> foundDays, JsonObject object) {
        if(foundDays == null || foundDays.empty)
            return null

        String selectedKey
        def selectedValue = 0

        foundDays.each {
            entry ->
                if (object.getInt(entry.getKey()) > selectedKey) {
                    selectedKey = entry.getKey()
                    selectedValue = object.getInt(entry.getKey())
                }
        }

        return selectedKey
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
