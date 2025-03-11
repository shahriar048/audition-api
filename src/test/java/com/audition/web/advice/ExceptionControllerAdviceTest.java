package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerAdviceTest {

    @Mock
    private AuditionLogger auditionLogger;

    @InjectMocks
    private ExceptionControllerAdvice exceptionControllerAdvice;

    @Test
    void handleHttpClientException_ReturnsProblemDetailWithStatusCode() {
        HttpClientErrorException exception = mock(HttpClientErrorException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));
        when(exception.getMessage()).thenReturn("Bad Request");

        var problemDetail = exceptionControllerAdvice.handleHttpClientException(exception);

        assertEquals(400, problemDetail.getStatus());
        assertEquals("Bad Request", problemDetail.getDetail());
        assertEquals(ExceptionControllerAdvice.DEFAULT_TITLE, problemDetail.getTitle());
    }

    @Test
    void handleMainException_ReturnsProblemDetailWithInternalServerError() {
        Exception exception = new Exception("Internal Error");

        var problemDetail = exceptionControllerAdvice.handleMainException(exception);

        assertEquals(500, problemDetail.getStatus());
        assertEquals("Internal Error", problemDetail.getDetail());
        assertEquals(ExceptionControllerAdvice.DEFAULT_TITLE, problemDetail.getTitle());
    }

    @Test
    void handleSystemException_ReturnsProblemDetailWithCustomStatusCode() {
        SystemException exception = mock(SystemException.class);
        when(exception.getStatusCode()).thenReturn(404);
        when(exception.getMessage()).thenReturn("Not Found");
        when(exception.getTitle()).thenReturn("Custom Title");

        var problemDetail = exceptionControllerAdvice.handleSystemException(exception);

        assertEquals(404, problemDetail.getStatus());
        assertEquals("Not Found", problemDetail.getDetail());
        assertEquals("Custom Title", problemDetail.getTitle());
    }
}
