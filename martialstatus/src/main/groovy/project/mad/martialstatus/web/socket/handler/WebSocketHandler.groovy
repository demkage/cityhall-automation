package project.mad.martialstatus.web.socket.handler

import project.mad.martialstatus.config.AvailableFields
import project.mad.martialstatus.config.Configuration

import javax.json.JsonObject
import javax.websocket.Session
import java.security.MessageDigest

interface WebSocketHandler extends WebSocketHandlerSessionKeyConstructor,
        WebSocketHandlerValidFieldsChecker {

    boolean accept(JsonObject object, Session session)

    boolean accept(JsonObject object, Session session, Configuration configuration)

    void handle(JsonObject object, Session session)

}


trait WebSocketHandlerSessionKeyConstructor {
    String constructSessionKey(String body, String firstKey, String secondKey) {
        MessageDigest md = MessageDigest.getInstance("MD5")

        String encrypt = firstKey + body + secondKey

        byte[] bytesOfMessages = encrypt.getBytes("UTF-8")

        byte[] digest = md.digest(bytesOfMessages)

        return toHex(digest)
    }

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes)
        return String.format("%0" + (bytes.length << 1) + "x", bi)
    }
}

trait WebSocketHandlerValidFieldsChecker {
    boolean hasValidFields(AvailableFields availableFields, JsonObject object) {
        object.entrySet().find {
            entry ->
                availableFields.validFields.contains(entry.key)
        }
    }
}