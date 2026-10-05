package com.OPD.OPDManagment.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "interceptor_start_time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        if (handler instanceof HandlerMethod handlerMethod) {
            log.debug("Interceptor PreHandle: Preparing to invoke Controller [{}.{}]",
                    handlerMethod.getBeanType().getSimpleName(),
                    handlerMethod.getMethod().getName());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            log.debug("Interceptor PostHandle: Successfully executed [{}.{}]",
                    handlerMethod.getBeanType().getSimpleName(),
                    handlerMethod.getMethod().getName());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long executionTime = (startTime != null) ? (System.currentTimeMillis() - startTime) : 0;

        if (handler instanceof HandlerMethod handlerMethod) {
            if (ex != null) {
                log.error("Interceptor AfterCompletion: Controller [{}.{}] threw exception: {} after {}ms",
                        handlerMethod.getBeanType().getSimpleName(),
                        handlerMethod.getMethod().getName(),
                        ex.getMessage(),
                        executionTime);
            } else {
                log.debug("Interceptor AfterCompletion: Controller [{}.{}] finished in {}ms with Status {}",
                        handlerMethod.getBeanType().getSimpleName(),
                        handlerMethod.getMethod().getName(),
                        executionTime,
                        response.getStatus());
            }
        }
    }
}
