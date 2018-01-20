package project.mad.martialstatus

import groovy.json.JsonSlurper
import org.jboss.weld.environment.se.Weld
import org.jboss.weld.environment.se.WeldContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import project.mad.martialstatus.config.ApplicationConfig
import project.mad.martialstatus.config.Configuration
import project.mad.martialstatus.web.http.qualifier.SecretKeysType
import project.mad.martialstatus.web.socket.CityHallMartialStatusFlow
import project.mad.martialstatus.web.socket.CityHallMartialStatusProgramClient
import project.mad.martialstatus.web.socket.WebSocketController
import project.mad.martialstatus.web.socket.handler.WebSocketHandler
import project.mad.martialstatus.web.socket.handler.qualifier.WebSocketHandlerChainType

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.enterprise.inject.Produces
import javax.enterprise.util.AnnotationLiteral
import javax.inject.Inject
import javax.inject.Singleton
import javax.websocket.DeploymentException
import javax.websocket.Session

@Singleton
class ProgramYourselfApplication {
    private static final Logger log = LoggerFactory.getLogger(ProgramYourselfApplication.class)

    private static File configurationFile

    @Produces
    @Default
    @ApplicationScoped
    static Configuration configuration() {
        JsonSlurper jsonSlurper = new JsonSlurper()

        Configuration configuration = new Configuration(jsonSlurper.parse(configurationFile))

        log.info("Configuration: {}", configuration)

        return configuration
    }

    @Inject
    WebSocketController controller

    void run(CityHallMartialStatusProgramClient client) {
        controller.run(client)
    }

    static void main(String[] args) throws DeploymentException, IOException, InterruptedException {
        Session session = null

        if (args.length < 1) {
            log.error("Please provide configuration file")
            return
        }

        //dirty hack
        configurationFile = new File(args[0])

        Weld weld = new Weld()
        WeldContainer weldContainer = weld.initialize()
        ProgramYourselfApplication application = weldContainer.instance().select(ProgramYourselfApplication.class).get()


        weldContainer.instance().select(ApplicationConfig.class)

        CityHallMartialStatusProgramClient client = new CityHallMartialStatusProgramClient(
                socketHandler: weldContainer.instance()
                        .select(WebSocketHandler.class, new AnnotationLiteral<WebSocketHandlerChainType>() {}).get(),
                secretKeys: weldContainer.instance()
                        .select(Map.class, new AnnotationLiteral<SecretKeysType>() {}).get(),
                flow: weldContainer.instance()
                        .select(CityHallMartialStatusFlow.class).get()
        )

        application.run(client)



        weld.shutdown()
    }
}
