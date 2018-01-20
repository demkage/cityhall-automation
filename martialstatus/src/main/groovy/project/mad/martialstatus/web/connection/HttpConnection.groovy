package project.mad.martialstatus.web.connection

import project.mad.martialstatus.web.connection.strategy.ConnectionStrategy

import java.util.zip.GZIPInputStream

class HttpConnection extends AbstractConnection {

    private static final DEFAULT_TIMEOUT = 10000

    private String endpoint
    private String userAgent
    int timeout = DEFAULT_TIMEOUT

    HttpConnection(String endpoint, String userAgent, ConnectionStrategy strategy) {
        super(strategy)
        this.endpoint = endpoint
        this.userAgent = userAgent
    }

    @Override
    void connect(Closure callback) {
        executeStrategy(
                {
                    executeConnect()
                },
                {
                    connection ->
                        connection.disconnect()
                },
                callback
        )
    }

    private BufferedReader executeConnect() {
        URL url = new URL(endpoint)

        HttpURLConnection connection = (HttpURLConnection) url.openConnection()

        connection.setRequestMethod("GET")
        connection.setRequestProperty("User-Agent", userAgent)
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate")
        connection.setRequestProperty("Accept-Language", "ro,en;q=0.9,ro;q=0.8")
        connection.setRequestProperty("Cache-Control", "max-age=0")
        connection.setRequestProperty("Connection", "keep-alive")
        connection.setRequestProperty("Host", "programari.starecivila1.ro")
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1")

        connection.setConnectTimeout(timeout)

        if ("gzip".equals(connection.getContentEncoding())) {
            return new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(connection.getInputStream())
            ))
        } else {
            return new BufferedReader(new InputStreamReader(connection.getInputStream()))
        }
    }
}
