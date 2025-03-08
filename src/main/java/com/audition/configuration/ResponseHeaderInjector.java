package com.audition.configuration;

import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class ResponseHeaderInjector implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String SPAN_ID_HEADER = "X-Span-Id";
    private static final String TRACEPARENT_HEADER = "traceparent";

    private final CurrentTraceContext currentTraceContext;

    public ResponseHeaderInjector(CurrentTraceContext currentTraceContext) {
        this.currentTraceContext = currentTraceContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        if (response instanceof HttpServletResponse httpResponse) {
            TraceContext traceContext = currentTraceContext.get();

            if (traceContext != null) {
                httpResponse.setHeader(TRACE_ID_HEADER, traceContext.traceIdString());
                httpResponse.setHeader(SPAN_ID_HEADER, traceContext.spanIdString());
                httpResponse.setHeader(TRACEPARENT_HEADER,
                    String.format("00-%s-%s-%s",
                        traceContext.traceIdString(),
                        traceContext.spanIdString(),
                        Boolean.TRUE.equals(traceContext.sampled()) ? "01" : "00"));
            }
        }

        chain.doFilter(request, response);
    }
}
