package com.OPD.OPDManagment.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final String CORRELATION_ID_HEADER_NAME = "X-Request-Id";
    private static final String CORRELATION_ID_LOG_VAR_NAME = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String requestId = request.getHeader(CORRELATION_ID_HEADER_NAME);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(CORRELATION_ID_LOG_VAR_NAME, requestId);
        response.setHeader(CORRELATION_ID_HEADER_NAME, requestId);

        log.info("Incoming Request: [Method: {}] [URI: {}] [RemoteAddr: {}] [RequestId: {}]",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            response.setHeader("X-Response-Time-ms", String.valueOf(duration));
            log.info("Completed Request: [Method: {}] [URI: {}] [Status: {}] [Duration: {}ms]",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
            MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
        }
    }
}
