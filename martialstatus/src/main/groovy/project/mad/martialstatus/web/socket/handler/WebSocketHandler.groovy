package project.mad.martialstatus.web.socket.handler

import sun.misc.HexDumpEncoder

import javax.json.JsonObject
import javax.websocket.Session
import java.security.MessageDigest

interface WebSocketHandler extends WebSocketHandlerSessionKeyConstructor {

    boolean accept(JsonObject object, Session session)

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