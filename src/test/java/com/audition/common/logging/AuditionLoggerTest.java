package com.audition.common.logging;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;

class AuditionLoggerTest {

    private AuditionLogger auditionLogger;
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        auditionLogger = new AuditionLogger();
        mockLogger = mock(Logger.class);
    }

    @Test
    void infoLogsMessageWhenInfoEnabled() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(mockLogger, "Info message");
        verify(mockLogger).info("Info message");
    }

    @Test
    void infoDoesNotLogMessageWhenInfoDisabled() {
        when(mockLogger.isInfoEnabled()).thenReturn(false);
        auditionLogger.info(mockLogger, "Info message");
        verify(mockLogger, never()).info(anyString());
    }

    @Test
    void debugLogsMessageWhenDebugEnabled() {
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        auditionLogger.debug(mockLogger, "Debug message");
        verify(mockLogger).debug("Debug message");
    }

    @Test
    void debugDoesNotLogMessageWhenDebugDisabled() {
        when(mockLogger.isDebugEnabled()).thenReturn(false);
        auditionLogger.debug(mockLogger, "Debug message");
        verify(mockLogger, never()).debug(anyString());
    }

    @Test
    void warnLogsMessageWhenWarnEnabled() {
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        auditionLogger.warn(mockLogger, "Warn message");
        verify(mockLogger).warn("Warn message");
    }

    @Test
    void warnDoesNotLogMessageWhenWarnDisabled() {
        when(mockLogger.isWarnEnabled()).thenReturn(false);
        auditionLogger.warn(mockLogger, "Warn message");
        verify(mockLogger, never()).warn(anyString());
    }

    @Test
    void errorLogsMessageWhenErrorEnabled() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.error(mockLogger, "Error message");
        verify(mockLogger).error("Error message");
    }

    @Test
    void errorDoesNotLogMessageWhenErrorDisabled() {
        when(mockLogger.isErrorEnabled()).thenReturn(false);
        auditionLogger.error(mockLogger, "Error message");
        verify(mockLogger, never()).error(anyString());
    }

    @Test
    void logErrorWithExceptionLogsMessageAndExceptionWhenErrorEnabled() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        Exception exception = new Exception("Exception message");
        auditionLogger.logErrorWithException(mockLogger, "Error message", exception);
        verify(mockLogger).error("Error message", exception);
    }

    @Test
    void logStandardProblemDetailLogsMessageAndExceptionWhenErrorEnabled() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail("Invalid input");
        Exception exception = new Exception("Exception message");
        auditionLogger.logStandardProblemDetail(mockLogger, problemDetail, exception);
        verify(mockLogger).error("Title: Bad Request, Status: 400, Detail: Invalid input", exception);
    }

}
