package project.mad.martialstatus.config

import javax.enterprise.inject.Any

@Any
class Configuration {
    Network network
    AppointmentStrategy appointmentStrategy
    Step1 step1
    AvailableFields availableFields
}

class AppointmentStrategy {
    int maxAppointments
    List<String> preferableDays
    List<String> ignoreDays
    boolean anyDay
}

class Network {
    String webSocketUrl
    String httpUrl
    String userAgent
    int retryTimeMs
    NetworkConnection connection
}

class NetworkConnection {
    int httpConnectionTimeout
    int socketConnectionTimeout
    int parallelConnections
    int waitTimeBeforeRaiseNewConnection
}

class Step1 {
    int actor
    String name
    String cnp
    String email
}


class AvailableFields {
    List<String> validFields
    String confirmStepField
}