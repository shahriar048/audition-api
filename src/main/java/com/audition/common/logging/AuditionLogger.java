package com.audition.common.logging;

import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public void warn(final Logger logger, final String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void error(final Logger logger, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error("{}\n", createBasicErrorResponseMessage(errorCode, message));
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        if (standardProblemDetail == null) {
            return "ProblemDetail is null";
        }

        StringBuilder message = new StringBuilder();
        message.append("Title: ").append(Optional.ofNullable(standardProblemDetail.getTitle()).orElse("No Title"));
        message.append(", Status: ").append(standardProblemDetail.getStatus());
        message.append(", Detail: ").append(Optional.ofNullable(standardProblemDetail.getDetail()).orElse("No Detail"));

        return message.toString();
    }

    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        return "Error Code: " + errorCode + ", Message: " + message;
    }
}
