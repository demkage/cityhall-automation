package project.mad.martialstatus.web.socket.handler.strategy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.enterprise.inject.Any
import javax.json.JsonObject
import javax.json.JsonValue

@Any
class SelectAppointmentWithPreferableDays implements ThirdStepSelectAppointmentsStrategy {
    private final Logger log = LoggerFactory.getLogger(this.getClass())
    private List<String> preferableDays
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

    String keyWithPreferableDay(JsonObject object) {
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


}
