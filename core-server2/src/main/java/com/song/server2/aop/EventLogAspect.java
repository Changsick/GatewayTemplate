package com.song.server2.aop;

import com.song.server2.exception.CustomException;
import com.song.server2.util.CustomEventLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EventLogAspect {
    private static final Logger log = LoggerFactory.getLogger(EventLogAspect.class);

    @Before("@annotation(event)") // around 안쓸거면 호출전 쓰면될듯
    public void applyEventLog(JoinPoint jp, CustomEventLog event) {
        String domain = event.domain();
        String menu = event.menu();
        String action = event.action();
        Object[] args = jp.getArgs();
        log.info("[EventLogAspect Before] domain={}, menu={}, action={}, input={}", domain, menu, action, args);
    }

    @AfterThrowing(pointcut = "@annotation(event)", throwing = "ex")
    public void afterThrowingErrorLog(JoinPoint jp, CustomEventLog event, Throwable ex) {
        String domain = event.domain();
        String menu = event.menu();
        String action = event.action();
        if (ex instanceof CustomException) {
            CustomException ce = (CustomException) ex;
            log.error("[EventLogAspect CustomError] domain={}, menu={}, action={}, location={}, code={}, message={}",
                    domain, menu, action, ce.getLocation(), ce.getErrorCode(), ce.getMessage(), ce);
            // [EventLogAspect CustomError] domain=KVMS, menu=선박탐지분류결과, action=GET, location=TestService, code=E001, message=error message
        } else {
            log.error("[EventLogAspect Error] domain={}, menu={}, action={}, exception={}",
                    domain, menu, action, ex.getMessage(), ex);
        }
    }

    /*
    @Around("@annotation(event)")
    public Object applyEventLog(ProceedingJoinPoint pjp, CustomEventLog event) throws Throwable {
        String domain = event.domain();
        String menu = event.menu();
        String action = event.action();
        Object[] args = pjp.getArgs();
        log.info("[EventLogAspect Start] domain={}, menu={}, action={}, input={}"
                , domain, menu, action, args);

        Object result;
        try {
            result = pjp.proceed();
            log.info("[EventLogAspect Success] domain={}, menu={}, action={}, output={}"
                    , domain, menu, action, result);
            return result;
        } catch (Throwable ex) {
            log.error("[EventLogAspect Error] domain={}, menu={}, action={}, exception={}"
                    , domain, menu, action, ex.getMessage(), ex);
            if (ex instanceof CustomException) {
                CustomException ce = (CustomException) ex;
                log.error("[CoreException] location={}, errorCode={}, errorMessage={}]"
                        , ce.getLocation(), ce.getErrorCode(), ce.getMessage());
            }
            throw ex;
        }
    }
    */

    /*
    // 분리하면 이렇게 될듯
    @Before("@annotation(event)") // around 안쓸거면 호출전 쓰면될듯
    public void applyEventLog(JoinPoint jp, CustomEventLog event) {
        String domain = event.domain();
        String menu = event.menu();
        String action = event.action();
        Object[] args = jp.getArgs();
        log.info("[Audit Before] menu={}, action={}, input={}", menu, action, args);
    }

    @Around("@annotation(event)")
    public Object applyEventLog(ProceedingJoinPoint pjp, CustomEventLog event) throws Throwable {
        String domain = event.domain();
        String menu = event.menu();
        String action = event.action();
        Object[] args = pjp.getArgs();
        log.info("[Audit Start] menu={}, action={}, input={}", menu, action, args);

        Object result = pjp.proceed();
        log.info("[Audit Success] menu={}, action={}, output={}", menu, action, result);
        return result;
    }

    // Handle exceptions separately
    @AfterThrowing(pointcut = "@annotation(event)", throwing = "ex")
    public void afterThrowingErrorLog(JoinPoint jp, CustomEventLog event, Throwable ex) {
        String domain = event.domain();
        String menu = event.menu();
        String action = event.action();
        if (ex instanceof CustomException) {
            CustomException ce = (CustomException) ex;
            log.error("[Audit CustomError] menu={}, action={}, location={}, code={}, message={}",
                      menu, action, ce.getLocation(), ce.getErrorCode(), ce.getMessage(), ce);
        } else {
            log.error("[Audit Error] menu={}, action={}, exception={}",
                      menu, action, ex.getMessage(), ex);
        }
    }
    */

}
