package com.abenezer.personalfinancetracker.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.abenezer.personalfinancetracker.budget.BudgetService.createBudget(..))")
    public void logBeforeCreateBudget(JoinPoint joinPoint) {
        logger.info("Calling method: {}", joinPoint.getSignature().getName());

        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            logger.info("Request: {}", args[0]); // Assuming the first argument is the @RequestBody DTO
        } else {
            logger.warn("No arguments passed to the method.");
        }
    }
}