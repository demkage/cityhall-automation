package project.mad.martialstatus.web.socket.handler.strategy

import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.spi.JsonProvider

interface ThirdStepSelectAppointmentsStrategy extends ThirdStepSelectAppointmentsConstructor {

    JsonObject select(JsonObject object)

}


trait ThirdStepSelectAppointmentsConstructor {

    JsonObject constructObject(JsonObject object, String selectedKey, int value) {
        JsonProvider provider = JsonProvider.provider()
        JsonObjectBuilder builder = provider.createObjectBuilder()
        object.keySet().each {
            key ->
                if (key == selectedKey) {
                    builder.add(key, value)
                } else {
                    builder.add(key, 0)
                }
        }

        builder.build()
    }
}