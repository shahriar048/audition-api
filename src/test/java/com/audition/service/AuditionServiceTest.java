package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    private static final List<AuditionPost> MOCK_POSTS;

    static {
        AuditionPost post1 = new AuditionPost(1, 101, "Title1", "Body1");
        AuditionPost post2 = new AuditionPost(2, 102, "Title2", "Body2");
        MOCK_POSTS = Arrays.asList(post1, post2);
    }

    @ParameterizedTest
    @CsvSource({
        "null, 2",    // No filter → return all posts
        "'', 2",      // Empty filter → return all posts
        "Title1, 1",  // Title match → return 1 post
        "Body2, 1",   // Body match → return 1 post
        "nonexistent, 0",  // No match → return empty list
        "TITLE1, 1"   // Case-insensitive match → return 1 post
    })
    void testGetPosts_Filtering(String filter, int expectedSize) {
        when(auditionIntegrationClient.getPosts()).thenReturn(MOCK_POSTS);

        List<AuditionPost> result = auditionService.getPosts("null".equals(filter) ? null : filter);

        assertNotNull(result);
        assertEquals(expectedSize, result.size());
    }

    @Test
    void testGetPosts_NoPosts() {
        when(auditionIntegrationClient.getPosts()).thenReturn(Collections.emptyList());

        List<AuditionPost> result = auditionService.getPosts(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getPostById_Success() {
        AuditionPost post = new AuditionPost();
        when(auditionIntegrationClient.getPostById(1)).thenReturn(post);

        AuditionPost result = auditionService.getPostById(1);
        assertNotNull(result);
        assertEquals(post, result);
    }

    @Test
    void testGetPostById_PropagatesExceptions() {
        SystemException expectedException = new SystemException("Exception message",
            "Exception Title", HttpStatus.INTERNAL_SERVER_ERROR.value());

        when(auditionIntegrationClient.getPostById(1)).thenThrow(expectedException);

        SystemException actualException = assertThrows(SystemException.class, () -> auditionService.getPostById(1));
        assertEquals(expectedException.getMessage(), actualException.getMessage());
        assertEquals(expectedException.getTitle(), actualException.getTitle());
        assertEquals(expectedException.getStatusCode(), actualException.getStatusCode());
    }

    @Test
    void getPostWithComments_Success() {
        AuditionPost post = new AuditionPost(1, 101, "Title1", "Body1");
        AuditionComment[] comments = {
            new AuditionComment(1, "Name 1", "Email 1", "Comment 1"),
            new AuditionComment(2, "Name 2", "Email 2", "Comment 2")
        };

        when(auditionIntegrationClient.getPostById(1)).thenReturn(post);
        when(auditionIntegrationClient.getCommentsForPost(1)).thenReturn(Arrays.asList(comments));

        AuditionPost result = auditionService.getPostWithComments(1);
        assertNotNull(result);
        assertNotNull(result.getComments());
        assertEquals(2, result.getComments().size());
        assertEquals("Comment 1", result.getComments().get(0).getBody());
    }

    @Test
    void getPostWithComments_PostNotFound() {
        when(auditionIntegrationClient.getPostById(1)).thenThrow(
            new SystemException("Cannot find post with ID 1", "Resource Not Found", HttpStatus.NOT_FOUND.value()));

        SystemException exception = assertThrows(SystemException.class, () -> auditionService.getPostWithComments(1));
        assertEquals("Cannot find post with ID 1", exception.getMessage());
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }

    @Test
    void getPostWithComments_CommentsNotFound() {
        AuditionPost post = new AuditionPost(1, 101, "Title1", "Body1");

        when(auditionIntegrationClient.getPostById(1)).thenReturn(post);
        when(auditionIntegrationClient.getCommentsForPost(1)).thenThrow(
            new SystemException("Cannot find comments for post with ID 1", "Resource Not Found",
                HttpStatus.NOT_FOUND.value()));

        SystemException exception = assertThrows(SystemException.class, () -> auditionService.getPostWithComments(1));
        assertEquals("Cannot find comments for post with ID 1", exception.getMessage());
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }

    @Test
    void getPostWithComments_EmptyComments() {
        AuditionPost post = new AuditionPost(1, 101, "Title1", "Body1");

        when(auditionIntegrationClient.getPostById(1)).thenReturn(post);
        when(auditionIntegrationClient.getCommentsForPost(1)).thenReturn(Collections.emptyList());

        AuditionPost result = auditionService.getPostWithComments(1);
        assertNotNull(result);
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void getComments_WithPostId_Success() {
        AuditionComment[] comments = {
            new AuditionComment(1, "Name 1", "Email 1", "Comment 1"),
            new AuditionComment(2, "Name 2", "Email 2", "Comment 2")
        };

        when(auditionIntegrationClient.getComments(1)).thenReturn(Arrays.asList(comments));

        List<AuditionComment> result = auditionService.getComments(1);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        assertEquals("Comment 2", result.get(1).getBody());
    }

    @Test
    void getComments_WithoutPostId_Success() {
        AuditionComment[] comments = {
            new AuditionComment(1, "Name 1", "Email 1", "Comment 1"),
            new AuditionComment(2, "Name 2", "Email 2", "Comment 2")
        };

        when(auditionIntegrationClient.getComments(null)).thenReturn(Arrays.asList(comments));

        List<AuditionComment> result = auditionService.getComments(null);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        assertEquals("Comment 2", result.get(1).getBody());
    }

    @Test
    void getComments_WithPostId_NotFound() {
        when(auditionIntegrationClient.getComments(1)).thenThrow(
            new SystemException("Cannot find comments for post with ID 1", "Resource Not Found",
                HttpStatus.NOT_FOUND.value()));

        SystemException exception = assertThrows(SystemException.class, () -> auditionService.getComments(1));
        assertEquals("Cannot find comments for post with ID 1", exception.getMessage());
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }

    @Test
    void getComments_WithPostId_ClientError() {
        when(auditionIntegrationClient.getComments(1)).thenThrow(
            new SystemException("Client Error", "Error Fetching Comments", HttpStatus.BAD_REQUEST.value()));

        SystemException exception = assertThrows(SystemException.class, () -> auditionService.getComments(1));
        assertEquals("Client Error", exception.getMessage());
        assertEquals("Error Fetching Comments", exception.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
    }

    @Test
    void getComments_WithPostId_RestClientException() {
        when(auditionIntegrationClient.getComments(1)).thenThrow(
            new SystemException("Service Unavailable", "Error Fetching Comments",
                HttpStatus.SERVICE_UNAVAILABLE.value()));

        SystemException exception = assertThrows(SystemException.class, () -> auditionService.getComments(1));
        assertEquals("Service Unavailable", exception.getMessage());
        assertEquals("Error Fetching Comments", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getComments_WithPostId_EmptyResponse() {
        when(auditionIntegrationClient.getComments(1)).thenReturn(Collections.emptyList());

        List<AuditionComment> result = auditionService.getComments(1);
        assertTrue(result.isEmpty());
    }

}
