package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {

    @Mock
    private AuditionLogger auditionLogger;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse clientHttpResponse;

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    private byte[] body;

    @BeforeEach
    void setUp() {
        body = "request body".getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void interceptLogsRequestAndResponse() throws IOException {
        when(execution.execute(httpRequest, body)).thenReturn(clientHttpResponse);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);

        ClientHttpResponse response = loggingInterceptor.intercept(httpRequest, body, execution);

        verify(auditionLogger).info(any(), eq("Request URI: {}, Method: {}, Headers: {}, Body: {}"),
            any(), any(), any(), eq("request body"));
        verify(auditionLogger).info(any(), eq("Response Status Code: {}, Headers: {}"),
            eq(HttpStatus.OK), any());
        assertEquals(clientHttpResponse, response);
    }

    @Test
    void interceptHandlesIOException() throws IOException {
        when(execution.execute(httpRequest, body)).thenThrow(new IOException("IO Exception"));

        assertThrows(IOException.class, () -> loggingInterceptor.intercept(httpRequest, body, execution));
    }
}
