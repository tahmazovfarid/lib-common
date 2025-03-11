package az.ailab.lib.common.aspect;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * A logging aspect that provides automatic method entry, exit, and exception logging
 * for services and controllers.
 * <p>This aspect logs method calls, execution time, and exceptions while allowing detailed
 * logging in specific environments like "local", "dev", "test", and "preprod".</p>
 *
 * <h2>Features:</h2>
 * - Logs method entry and exit with arguments and return values (for debugging).
 * - Logs exceptions, providing detailed stack traces in specific environments.
 * - Can be extended to customize pointcuts for different application layers.
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    /**
     * Pointcut that applies to all classes under the az.ailab namespace,
     * excluding the aspect classes themselves to prevent circular dependencies.
     * <p>This includes all services, controllers, repositories, and other components
     * defined within the application's packages.</p>
     */
    @Pointcut("within(az.ailab..*) && !within(az.ailab.lib.common.aspect..*)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Logs exceptions occurring in services and controllers.
     * <p>
     * In detailed logging mode, it logs the full exception stack trace.
     * Otherwise, it logs only the exception cause.
     * </p>
     * <p>Example log output (detailed logging enabled):</p>
     * <pre>
     * 2025-03-05T12:30:45.123 ERROR 12345 --- [main] az.ailab.service.UserService :
     * Exception in az.ailab.service.UserService.getUserById() with cause = 'java.sql.SQLException'
     * and exception = 'Database connection timeout'
     * java.sql.SQLException: Database connection timeout
     *     at az.ailab.repository.UserRepository.findById(UserRepository.java:45)
     *     at az.ailab.service.UserService.getUserById(UserService.java:30)
     *     at az.ailab.controller.UserController.getUser(UserController.java:25)
     * </pre>
     * <p>Example log output (detailed logging disabled):</p>
     * <pre>
     * 2025-03-05T12:30:45.123 ERROR 12345 --- [main] az.ailab.service.UserService :
     * Exception in az.ailab.service.UserService.getUserById() with cause = java.sql.SQLException
     * </pre>
     *
     * @param joinPoint The method where the exception occurred.
     * @param e         The thrown exception.
     */
    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String declaringType = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object exceptionCause = getExceptionCause(e);

        if (log.isDebugEnabled()) {
            log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'", declaringType, methodName, exceptionCause,
                    StringUtils.hasText(e.getMessage()) ? e.getMessage() : "No message", e);
        } else {
            log.error("Exception in {}.{}() with cause = {}", declaringType, methodName, exceptionCause);
        }
    }

    /**
     * Logs method entry, execution, and exit for debugging purposes.
     * <p>
     * If debug logging is enabled, it logs method arguments before execution and
     * logs return values upon exit.
     * </p>
     * <p>Example log output (method entry and exit):</p>
     * <pre>
     * 2025-03-05T12:32:10.456 DEBUG 12345 --- [main] az.ailab.service.UserService :
     * Enter: az.ailab.service.UserService.getUserById() with argument[s] = [11]
     *
     * 2025-03-05T12:32:10.457 DEBUG 12345 --- [main] az.ailab.service.UserService :
     * Exit: az.ailab.service.UserService.getUserById() with result = User{id=11, name='Tahmazov Farid'}
     * </pre>
     * <p>Example log output (illegal argument error):</p>
     * <pre>
     * 2025-03-05T12:33:15.789 ERROR 12345 --- [main] az.ailab.service.UserService :
     * Illegal argument: [-1] in az.ailab.service.UserService.getUserById()
     * </pre>
     *
     * @param joinPoint The method being executed.
     * @return The result of the method execution.
     * @throws Throwable If the method throws an exception.
     */
    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {
            return joinPoint.proceed();
        }

        String declaringType = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String arguments = Arrays.toString(joinPoint.getArgs());

        log.debug("Enter: {}.{}() with argument[s] = {}", declaringType, methodName, arguments);

        try {
            Object result = joinPoint.proceed();
            log.debug("Exit: {}.{}() with result = {}", declaringType, methodName, result);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", arguments, declaringType, methodName);
            throw e;
        }
    }

    /**
     * Retrieve the root cause of an exception.
     */
    private Object getExceptionCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getClass().getName();
    }

}
