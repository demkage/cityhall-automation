package project.mad.martialstatus.web.socket

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.web.socket.decoder.JsonDecoder
import project.mad.martialstatus.web.socket.encoder.JsonEncoder
import project.mad.martialstatus.web.socket.handler.WebSocketHandler
import project.mad.martialstatus.web.socket.handler.exception.NotOpenYetException
import project.mad.martialstatus.web.socket.handler.exception.ReceivedUnexpectedErrorException

import javax.enterprise.context.Dependent
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.json.JsonObject
import javax.json.JsonObjectBuilder
import javax.json.spi.JsonProvider
import javax.websocket.*

@Dependent
@ClientEndpoint(encoders = [JsonEncoder.class], decoders = [JsonDecoder.class])
class CityHallMartialStatusProgramClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    WebSocketHandler socketHandler

    Map<String, String> secretKeys

    @Inject
    @Default
    CityHallMartialStatusFlow flow

    @OnOpen
    void open(Session session) {
        flow.phaser.register()
        log.info("Open session: " + session.getId())
    }

    @OnClose
    void close(Session session) {
        log.info("Session closed")
    }

    @OnMessage
    void onMessage(JsonObject message, Session session) {
        message = appendKeys(message)

        //stop timer on confirm
        if (message.getInt("step") == 3) {
            JsonProvider jsonProvider = JsonProvider.provider()

            def object = jsonProvider.createObjectBuilder()
                    .add("step", 666 /*fix NullPointerException on groovy call()*/)
                    .add("timer", true)
                    .build()

            socketHandler.handle(object, session)
        }

        if (socketHandler.accept(message, session)) {
            log.info("Message Accepted")
            try {
                socketHandler.handle(message, session)
            } catch (NotOpenYetException exception) {
                log.info("Exception: {}", exception.class.toString())
                flow.closedByNotOpenYet = true
                flow.signalEnd()
            } catch (ReceivedUnexpectedErrorException exception) {
                log.info("Exception: {}", exception.class.toString())
                flow.signalEnd()
            }
        }
    }

    private JsonObject appendKeys(JsonObject original) {
        JsonProvider provider = JsonProvider.provider()
        JsonObjectBuilder builder = provider.createObjectBuilder()

        original.entrySet().each {
            entry ->
                if(entry.getValue() != null)
                    builder.add(entry.getKey(), entry.getValue())
        }

        builder
                .add("secretKey1", secretKeys.get("first"))
                .add("secretKey2", secretKeys.get("second"))
                .build()
    }
}
