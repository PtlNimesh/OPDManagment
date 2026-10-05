package com.OPD.OPDManagment.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.OPD.OPDManagment.controller..*) || within(com.OPD.OPDManagment.service..*)")
    public void applicationPackagePointcut() {
        // Pointcut for Controllers and Services
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (log.isDebugEnabled()) {
            log.debug("AOP Entering: {}.{}() with argument(s) = {}", className, methodName, Arrays.toString(args));
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            if (log.isDebugEnabled()) {
                log.debug("AOP Exiting: {}.{}() with result = {} (Execution time: {} ms)", className, methodName, result, elapsedTime);
            } else {
                log.info("AOP Execution: {}.{}() completed in {} ms", className, methodName, elapsedTime);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("AOP Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()), className, methodName);
            throw e;
        }
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(ProceedingJoinPoint joinPoint, Throwable e) {
        log.error("AOP Exception in {}.{}() with cause = '{}' and message = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage());
    }
}
