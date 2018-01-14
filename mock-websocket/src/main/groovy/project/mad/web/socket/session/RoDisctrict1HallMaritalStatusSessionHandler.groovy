package project.mad.web.socket.session;

import javax.enterprise.context.ApplicationScoped
import javax.json.Json
import javax.json.JsonObject
import javax.json.spi.JsonProvider
import javax.websocket.Session

@ApplicationScoped
class RoDisctrict1HallMaritalStatusSessionHandler {
    private final Set<Session> sessions = new HashSet<>()


    void addSession(Session session) {
        sessions.add(session)
    }

    void removeSession(Session session) {
        sessions.remove(session)
    }

    void returnOnConnectStatus(Session session) {
        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject currentStatus = jsonProvider.createObjectBuilder()
                .add("status", "CONNECTED")
                .add("step", 0)
                .add("config", jsonProvider.createObjectBuilder().build())
                .build()

        session.getBasicRemote().sendObject(currentStatus)
    }

    void sendToAllConnectedSessions(JsonObject object) {
        sessions.each {
            session -> session.getBasicRemote().sendObject(object)
        }
    }

    void onSubmitFirstStep(Session session) {
        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject currentStatus = jsonProvider.createObjectBuilder()
                .add("status", "QUEUED")
                .add("step", 1)
                .add("first", 1)
                .add("ticket", 123)
                .build()
        session.getBasicRemote().sendObject(currentStatus)
    }

    void onQueueAccept(Session session) {
        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject currentStatus = jsonProvider.createObjectBuilder()
                .add("status", "ACTIVE")
                .add("step", 2)
                .add("key", "randomKey")
                .add("version", 2)
                .add("date_disponibile",
                jsonProvider.createObjectBuilder()
                        .add("08-12-2018", 2)
                        .add("08-11-208", 3)
                        .build())
                .build()
        session.getBasicRemote().sendObject(currentStatus)
    }

    void onConfirmed(Session session) {
        JsonProvider jsonProvider = JsonProvider.provider()
        JsonObject currentStatus = jsonProvider.createObjectBuilder()
                .add("status", "CONFIRMED")
                .add("step", 3)
                .build()
        session.getBasicRemote().sendObject(currentStatus)
    }


}
