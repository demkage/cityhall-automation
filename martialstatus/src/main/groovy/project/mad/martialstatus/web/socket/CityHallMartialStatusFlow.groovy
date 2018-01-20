package project.mad.martialstatus.web.socket

import javax.enterprise.inject.Default
import javax.inject.Singleton
import java.util.concurrent.Phaser

@Singleton
@Default
class CityHallMartialStatusFlow {
    boolean closedByNotOpenYet
    boolean makeNewAppointment = true
    Phaser phaser = new Phaser(0)


    void signalEnd() {
        phaser.arrive()
    }

    void waitUntilEnd() {
        phaser.arriveAndAwaitAdvance()
    }
}
