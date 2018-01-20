package project.mad.martialstatus.web.connection

interface Connection {
    void connect(Closure callback)

    void await()
}
