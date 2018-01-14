package project.mad.martialstatus.web.socket.decoder

import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonReader
import javax.websocket.DecodeException
import javax.websocket.Decoder
import javax.websocket.EndpointConfig

class JsonDecoder implements Decoder.TextStream<JsonObject> {
    @Override
    JsonObject decode(Reader reader) throws DecodeException, IOException {
        JsonReader jsonReader = Json.createReader(reader)
        try {
            return jsonReader.readObject()
        } finally {
            jsonReader.close()
        }
    }

    @Override
    void init(EndpointConfig config) {

    }

    @Override
    void destroy() {

    }
}
