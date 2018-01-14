package project.mad

import org.jboss.shrinkwrap.api.ShrinkWrap
import org.wildfly.swarm.Swarm
import org.wildfly.swarm.undertow.WARArchive

class WebSocketMockApplication {
    static void main(String[] args) {
        Swarm swarm = new Swarm()

        WARArchive deployment = ShrinkWrap.create(WARArchive.class)
        deployment.addPackage('wildflyswarm.websocket')
        deployment.addAsResource(new File("static/icon.css"), "static/icon.css")

        println deployment.toString(true)

        swarm.start().deploy(deployment)
    }
}
