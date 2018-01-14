package project.mad.martialstatus.web.socket

import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default
import javax.inject.Singleton
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Phaser
import java.util.concurrent.locks.ReentrantLock

@Singleton
@Default
class CityHallMartialStatusFlow {
    boolean closedByNotOpenYet
    Phaser phaser = new Phaser(2)


    void signalEnd() {
        phaser.arrive()
    }

    void waitUntilEnd() {
        phaser.arriveAndAwaitAdvance()
    }
}
