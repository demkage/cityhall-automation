package project.mad.martialstatus.web.socket.handler.qualifier

import javax.inject.Qualifier
import java.lang.annotation.Retention
import java.lang.annotation.Target

import static java.lang.annotation.ElementType.*
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Qualifier
@Retention(RUNTIME)
@Target([TYPE, METHOD, FIELD, PARAMETER])
@interface WebSocketHandlerChainType {
}
