package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@SuppressWarnings("PMD.GuardLogStatement")
@Getter
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    private final AuditionLogger auditionLogger;

    public LoggingInterceptor(final AuditionLogger auditionLogger) {
        this.auditionLogger = auditionLogger;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(
        @NonNull final HttpRequest request,
        final byte @NonNull [] body,
        @NonNull final ClientHttpRequestExecution execution
    ) throws IOException {
        logRequest(request, body);
        final ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }


    private void logRequest(final HttpRequest request, final byte[] body) {
        auditionLogger.info(LOG, "Request URI: {}, Method: {}, Headers: {}, Body: {}",
            request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {
        auditionLogger.info(LOG, "Response Status Code: {}, Headers: {}",
            response.getStatusCode(), response.getHeaders());
    }
}
