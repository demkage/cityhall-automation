package project.mad.martialstatus.web.connection.strategy

interface ConnectionStrategy {
    ConnectionStrategy connect(Closure connectionPoint, Closure closeConnection, Closure callback)

    void awaitValidConnection()
}


