package project.mad.web.socket.encoder

import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonWriter
import javax.websocket.EncodeException
import javax.websocket.Encoder
import javax.websocket.EndpointConfig

class JsonEncoder implements Encoder.TextStream<JsonObject> {

    @Override
    void init(EndpointConfig config) {}

    @Override
    void encode(JsonObject payload, Writer writer) throws EncodeException, IOException {

        JsonWriter jsonWriter = Json.createWriter(writer)
        jsonWriter.writeObject(payload)
    }

    @Override
    void destroy() {}
}