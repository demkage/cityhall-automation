package project.mad.martialstatus.config

import javax.enterprise.inject.Any

@Any
class Configuration {
    Network network
    AppointmentStrategy appointmentStrategy
    Step1 step1
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
}

class Step1 {
    int actor
    String name
    String cnp
    String email
}
