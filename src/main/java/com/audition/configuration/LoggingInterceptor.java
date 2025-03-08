package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    private final AuditionLogger auditionLogger;

    public LoggingInterceptor(AuditionLogger auditionLogger) {
        this.auditionLogger = auditionLogger;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        auditionLogger.info(LOG, "Request URI: {}, Method: {}, Headers: {}, Body: {}",
            request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        auditionLogger.info(LOG, "Response Status Code: {}, Headers: {}, Body: {}",
            response.getStatusCode(), response.getHeaders());
    }
}
