package project.mad.martialstatus.web.http.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.Configuration
import project.mad.martialstatus.web.http.qualifier.CityHallHtppUserAgent
import project.mad.martialstatus.web.http.qualifier.CityHallUrlType

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Inject
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream

@ApplicationScoped
@Default
class RoDistrict1HallMartialStatusHttpClient {
    private final Logger log = LoggerFactory.getLogger(this.getClass())

    @Inject
    @Default
    Configuration configuration

    private static final Pattern mainJsPattern = Pattern.compile("src\\s*=\\s*\"\\s*?\\.?([A-z/]*?main\\.*?.*?(?:bundle|)\\.js)(?=\")")
    private static final Pattern firstKeyPattern = Pattern.compile("[A-z0-9]{1,3}\\s*=\\s*\"\\s*([A-z0-9-]{36})\"\\s*")
    private static final Pattern secondKeyPattern = Pattern.compile("[A-z0-9]{1,3}\\s*=\\s*\"\\s*([0-9]{1,3}\\.[0-9]{1,3})\\s*?\"")


    Map<String, String> getSecretKeys() {
        Map<String, String> keys = new HashMap<>()

        Optional<String> mainJsPath = Optional.ofNullable(getMainJsPath(connect("")))

        mainJsPath.ifPresent {
            path ->
                log.info("MainJsPath: {}", path)
                resolveSecretKeys(keys, connect(path))
        }


        keys
    }


    private BufferedReader connect(String path) {
        URL url = new URL(resolveUri(URI.create(configuration.network.httpUrl), path))

        HttpURLConnection connection = (HttpURLConnection) url.openConnection()

        connection.setRequestMethod("GET")
        connection.setRequestProperty("User-Agent", configuration.network.userAgent)
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate")
        connection.setRequestProperty("Accept-Language", "ro,en;q=0.9,ro;q=0.8")
        connection.setRequestProperty("Cache-Control", "max-age=0")
        connection.setRequestProperty("Connection", "keep-alive")
        connection.setRequestProperty("Host", "programari.starecivila1.ro")
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1")

        int responseCode = connection.getResponseCode()

        log.info("Connection '{}' response code '{}'", url.toString(), responseCode)

        if ("gzip".equals(connection.getContentEncoding())) {
            return new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(connection.getInputStream())
            ))
        } else {
            return new BufferedReader(new InputStreamReader(connection.getInputStream()))
        }
    }

    static String resolveUri(URI toResolveUri, String path) {
        String uriString = toResolveUri.toString()

        return uriString.endsWith('/') ? uriString + path : uriString + '/' + path
    }

    private String getMainJsPath(BufferedReader reader) {
        def inputLine

        while ((inputLine = reader.readLine()) != null) {
            Matcher matcher = mainJsPattern.matcher(inputLine)

            if (matcher.find()) {
                reader.close()
                return matcher.group(1)
            }
        }

        return null
    }

    private static void resolveSecretKeys(Map<String, String> keys, BufferedReader reader) {
        String inputLine
        while ((inputLine = reader.readLine()) != null) {
            Optional<String> firstMagicKey = Optional.ofNullable(getFirstMagicKey(inputLine))
            firstMagicKey.ifPresent {
                magicKey ->
                    keys.put("first", magicKey)
            }

            Optional<String> secondMagicKey = Optional.ofNullable(getSecondMagicKey(inputLine))

            secondMagicKey.ifPresent {
                magicKey ->
                    keys.put("second", magicKey)
            }

        }
        reader.close()
    }

    private static String getFirstMagicKey(String inputLine) {
        Matcher matcher = firstKeyPattern.matcher(inputLine)

        if (matcher.find())
            return matcher.group(1)


        return null
    }

    private static String getSecondMagicKey(String inputLine) {
        Matcher matcher = secondKeyPattern.matcher(inputLine)

        if (matcher.find())
            return matcher.group(1)

        return null
    }
}
