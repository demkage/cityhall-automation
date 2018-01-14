package project.mad.martialstatus.web.socket.handler.strategy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.enterprise.inject.Any
import javax.json.JsonObject

@Any
class SelectAppointmentWithMostRecordsAvailable implements ThirdStepSelectAppointmentsStrategy {
    private final Logger log = LoggerFactory.getLogger(this.getClass())
    int maxAppointments

    @Override
    JsonObject select(JsonObject object) {
        log.info("Select appointment with most records available from: {}", object)
        selectDateWithMostRecordsAvailable(object)
    }

    private selectDateWithMostRecordsAvailable(JsonObject object) {
        if(object.keySet() == null)
            return null

        def selectedKey = ""
        def selectedValue = 0
        object.keySet()?.each {
            key ->
                if (object.getInt(key) > selectedValue) {
                    selectedKey = key
                    selectedValue = object.getInt(key)
                }
        }

        log.info("Selected day: {}", selectedKey)
        constructObject(object, selectedKey,
                (selectedValue > maxAppointments) ? maxAppointments : selectedValue)
    }
}
