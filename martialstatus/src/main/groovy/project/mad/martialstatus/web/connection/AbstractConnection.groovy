package project.mad.martialstatus.web.connection

import project.mad.martialstatus.web.connection.strategy.ConnectionStrategy

abstract class AbstractConnection implements Connection {
    ConnectionStrategy strategy

    AbstractConnection(ConnectionStrategy strategy) {
        this.strategy = strategy
    }

    @Override
    void await() {
        strategy.awaitValidConnection()
    }

    protected executeStrategy(Closure connectionPoint, Closure closeConnection, Closure callback) {
        strategy = strategy.connect(connectionPoint, closeConnection, callback)
    }
}
