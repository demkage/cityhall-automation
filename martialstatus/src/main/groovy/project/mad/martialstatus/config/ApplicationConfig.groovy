package project.mad.martialstatus.config

import org.slf4j.LoggerFactory
import project.mad.martialstatus.web.connection.strategy.qualifier.ConnectionStrategyExecutorService
import project.mad.martialstatus.web.http.client.RoDistrict1HallMartialStatusHttpClient
import project.mad.martialstatus.web.http.qualifier.SecretKeysType
import project.mad.martialstatus.web.socket.handler.WebSocketHandler
import project.mad.martialstatus.web.socket.handler.WebSocketHandlerChain
import project.mad.martialstatus.web.socket.handler.qualifier.*
import project.mad.martialstatus.web.socket.handler.strategy.SelectAppointWithMostRecordsAvailableAndIgnoreDays
import project.mad.martialstatus.web.socket.handler.strategy.SelectAppointmentWithMostRecordsAvailable
import project.mad.martialstatus.web.socket.handler.strategy.SelectAppointmentWithPreferableDays
import project.mad.martialstatus.web.socket.handler.strategy.ThirdStepSelectAppointmentsStrategy

import javax.enterprise.context.Dependent
import javax.enterprise.inject.Default
import javax.enterprise.inject.Produces
import javax.inject.Inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ApplicationConfig {

    @FirstStepHandlerType
    @Inject
    WebSocketHandler firstStepHandler

    @SecondStepHandlerType
    @Inject
    WebSocketHandler secondStepHandler

    @ThirdStepHandlerType
    @Inject
    WebSocketHandler thirdStepHandler

    @ConfirmStepHandlerType
    @Inject
    WebSocketHandler confirmStepHandler

    @NotOpenYetHandlerType
    @Inject
    WebSocketHandler notOpenYetHandler

    @UpdateQueueHandlerType
    @Inject
    WebSocketHandler updateQueueHandler

    @ErrorHandlerType
    @Inject
    WebSocketHandler errorHandler

    @Inject
    RoDistrict1HallMartialStatusHttpClient client

    @Default
    @Inject
    Configuration configuration


    @Produces
    @ConnectionStrategyExecutorService
    ExecutorService connectionStrategyExecutorService() {
        Executors.newFixedThreadPool(configuration.network.connection.parallelConnections)
    }

    //FIXME: redundant
    @Produces
    @Default
    FirstStepConfiguration firstStepConfiguration() {
        new FirstStepConfiguration(
                actor: configuration.step1.actor,
                name: configuration.step1.name,
                cnp: configuration.step1.cnp,
                email: configuration.step1.email
        )
    }

    @Produces
    @SecretKeysType
    Map secretKeys() {
        Map<String, String> keys = client.getSecretKeys()

        LoggerFactory.getLogger(this.getClass())
                .info("Keys. '{}' : '{}'", keys.get("first"), keys.get("second"))

        return keys
    }

    @Produces
    @Default
    ThirdStepSelectAppointmentsStrategy thirdStepSelectAppointmentsStrategy() {
        if(!configuration.appointmentStrategy.anyDay &&
                !configuration.appointmentStrategy.preferableDays.empty) {
            return new SelectAppointmentWithPreferableDays(
                    maxAppointments: configuration.appointmentStrategy.maxAppointments,
                    preferableDays: configuration.appointmentStrategy.preferableDays
            )
        } else if(configuration.appointmentStrategy.anyDay &&
                !configuration.appointmentStrategy.ignoreDays.empty) {
            return new SelectAppointWithMostRecordsAvailableAndIgnoreDays(
                    maxAppointments: configuration.appointmentStrategy.maxAppointments,
                    ignoreDays: configuration.appointmentStrategy.ignoreDays
            )
        } else {
            return new SelectAppointmentWithMostRecordsAvailable(
                    maxAppointments: configuration.appointmentStrategy.maxAppointments
            )
        }
    }

    @Produces
    @BackupSelectAppointmentStrategy
    ThirdStepSelectAppointmentsStrategy backupSelectAppointmentStrategy() {
        new SelectAppointmentWithMostRecordsAvailable(
                maxAppointments: configuration.appointmentStrategy.maxAppointments
        )
    }

    @Produces
    @WebSocketHandlerChainType
    @Dependent
    WebSocketHandler webSocketHandler() {
        WebSocketHandlerChain webSocketHandlerChain = new WebSocketHandlerChain()
        webSocketHandlerChain.handlers.add(firstStepHandler)
        webSocketHandlerChain.handlers.add(secondStepHandler)
        webSocketHandlerChain.handlers.add(thirdStepHandler)
        webSocketHandlerChain.handlers.add(confirmStepHandler)
        webSocketHandlerChain.handlers.add(notOpenYetHandler)
        webSocketHandlerChain.handlers.add(updateQueueHandler)
        webSocketHandlerChain.handlers.add(errorHandler)

        return webSocketHandlerChain
    }
}
