package com.audition.configuration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResponseHeaderInjectorTest {

    @Mock
    private CurrentTraceContext currentTraceContext;

    @InjectMocks
    private ResponseHeaderInjector responseHeaderInjector;

    @Mock
    private ServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockChain;

    @Test
    void shouldInjectHeadersWhenTraceContextIsAvailable() throws IOException, ServletException {
        TraceContext traceContext = TraceContext.newBuilder()
            .traceId(123456789L)
            .spanId(987654321L)
            .sampled(true)
            .build();

        when(currentTraceContext.get()).thenReturn(traceContext);

        responseHeaderInjector.doFilter(mockRequest, mockResponse, mockChain);
        verify(mockResponse).setHeader("X-Trace-Id", "00000000075bcd15"); // 123456789 in hex
        verify(mockResponse).setHeader("X-Span-Id", "000000003ade68b1"); // 987654321 in hex
        verify(mockResponse).setHeader("traceparent", "00-00000000075bcd15-000000003ade68b1-01");
        verify(mockChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotInjectHeadersWhenTraceContextIsNull() throws IOException, ServletException {
        when(currentTraceContext.get()).thenReturn(null);

        responseHeaderInjector.doFilter(mockRequest, mockResponse, mockChain);
        verify(mockResponse, never()).setHeader(anyString(), anyString());
        verify(mockChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldInjectHeadersWithSampledFalse() throws IOException, ServletException {
        TraceContext traceContext = TraceContext.newBuilder()
            .traceId(987654321L)
            .spanId(123456789L)
            .sampled(false)
            .build();

        when(currentTraceContext.get()).thenReturn(traceContext);

        responseHeaderInjector.doFilter(mockRequest, mockResponse, mockChain);
        verify(mockResponse).setHeader("X-Trace-Id", "000000003ade68b1"); // 987654321 in hex
        verify(mockResponse).setHeader("X-Span-Id", "00000000075bcd15"); // 123456789 in hex
        verify(mockResponse).setHeader("traceparent", "00-000000003ade68b1-00000000075bcd15-00");
        verify(mockChain).doFilter(mockRequest, mockResponse);
    }
}
