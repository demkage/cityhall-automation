package project.mad.web.socket.endpoints

import project.mad.web.socket.encoder.JsonEncoder
import project.mad.web.socket.session.RoDisctrict1HallMaritalStatusSessionHandler

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.json.JsonObject
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint
import java.util.logging.Logger

@ApplicationScoped
@ServerEndpoint(value = "/api/core/", encoders = [JsonEncoder.class])
class RoDistrict1HallMaritalStatus {

    private final Logger log = Logger.getLogger(RoDistrict1HallMaritalStatus.class.toString())

    private Random rnd = new Random()

    private RoDisctrict1HallMaritalStatusSessionHandler sessionHandler = new RoDisctrict1HallMaritalStatusSessionHandler()

    @OnOpen
    void open(Session session) {
        log.info("Session open: " + session.getId())
        sessionHandler.addSession(session)
        sessionHandler.returnOnConnectStatus(session)
    }

    @OnClose
    void close(Session session) {
        sessionHandler.removeSession(session)
    }


    @OnMessage
    void message(String message, Session session) {
        log.info("Message: " + message)
        if(message.contains("actor") && message.contains("nume") && message.contains("email"))
            sessionHandler.onSubmitFirstStep(session)

        if(message.contains("status"))
            sessionHandler.onQueueAccept(session)

        if(message.contains("programari"))
            sessionHandler.onConfirmed(session)
    }


}
