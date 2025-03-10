package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AuditionIntegrationClientTest {

    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";
    private static final String COMMENTS_URL = "https://jsonplaceholder.typicode.com/comments";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @Test
    void testGetPosts_Success() {
        AuditionPost[] postsArray = {new AuditionPost(), new AuditionPost()};
        when(restTemplate.getForObject(POSTS_URL, AuditionPost[].class)).thenReturn(postsArray);

        List<AuditionPost> posts = auditionIntegrationClient.getPosts();
        assertNotNull(posts);
        assertEquals(2, posts.size());
    }

    @Test
    void testGetPosts_EmptyResponse() {
        when(restTemplate.getForObject(POSTS_URL, AuditionPost[].class)).thenReturn(null);

        List<AuditionPost> posts = auditionIntegrationClient.getPosts();
        assertNotNull(posts);
        assertEquals(Collections.emptyList(), posts);
    }

    @Test
    void testGetPosts_RestClientException() {
        when(restTemplate.getForObject(POSTS_URL, AuditionPost[].class))
            .thenThrow(new RestClientException("Error"));

        SystemException exception = assertThrows(SystemException.class, () -> auditionIntegrationClient.getPosts());
        assertEquals("Error", exception.getMessage());
        assertEquals("Error Fetching Posts", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getPostById_Success() {
        AuditionPost post = new AuditionPost();
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class)).thenReturn(post);

        AuditionPost result = auditionIntegrationClient.getPostById(1);
        assertNotNull(result);
        assertEquals(post, result);
    }

    @Test
    void getPostById_NotFound() {
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById(1));
        assertEquals("Cannot find post with ID 1", exception.getMessage());
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }

    @Test
    void getPostById_ClientError() {
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById(1));
        assertEquals("400 Bad Request", exception.getMessage());
        assertEquals("Client Error", exception.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
    }

    @Test
    void getPostById_RestClientException() {
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class))
            .thenThrow(new RestClientException("Service Unavailable"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById(1));
        assertEquals("Service Unavailable", exception.getMessage());
        assertEquals("Error Fetching Post with ID 1", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getCommentsForPost_Success() {
        AuditionComment[] comments = {
            new AuditionComment(1, "Name 1", "Email 1", "Comment 1"),
            new AuditionComment(2, "Name 2", "Email 2", "Comment 2"),
        };

        when(restTemplate.getForObject(POSTS_URL + "/1/comments", AuditionComment[].class, 1))
            .thenReturn(comments);

        List<AuditionComment> result = auditionIntegrationClient.getCommentsForPost(1);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        assertEquals("Comment 2", result.get(1).getBody());
    }

    @Test
    void getCommentsForPost_NotFound() {
        when(restTemplate.getForObject(POSTS_URL + "/1/comments", AuditionComment[].class, 1))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getCommentsForPost(1));

        assertEquals("Cannot find post with ID 1", exception.getMessage());
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }

    @Test
    void getCommentsForPost_ClientError() {
        when(restTemplate.getForObject(POSTS_URL + "/1/comments", AuditionComment[].class, 1))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getCommentsForPost(1));

        assertEquals("400 Bad Request", exception.getMessage());
        assertEquals("Client Error", exception.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
    }

    @Test
    void getCommentsForPost_RestClientException() {
        when(restTemplate.getForObject(POSTS_URL + "/1/comments", AuditionComment[].class, 1))
            .thenThrow(new RestClientException("Service Unavailable"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getCommentsForPost(1));

        assertEquals("Service Unavailable", exception.getMessage());
        assertEquals("Error Fetching Comments for Post ID 1", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getCommentsForPost_EmptyResponse() {
        when(restTemplate.getForObject(POSTS_URL + "/1/comments", AuditionComment[].class, 1))
            .thenReturn(null);

        List<AuditionComment> result = auditionIntegrationClient.getCommentsForPost(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void getComments_WithPostId_Success() {
        AuditionComment[] comments = {
            new AuditionComment(1, "Name 1", "Email 1", "Comment 1"),
            new AuditionComment(2, "Name 2", "Email 2", "Comment 2"),
        };

        when(restTemplate.getForObject(COMMENTS_URL + "?postId=1", AuditionComment[].class))
            .thenReturn(comments);

        List<AuditionComment> result = auditionIntegrationClient.getComments(1);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        assertEquals("Comment 2", result.get(1).getBody());
    }

    @Test
    void getComments_WithoutPostId_Success() {
        AuditionComment[] comments = {
            new AuditionComment(1, "Name 1", "Email 1", "Comment 1"),
            new AuditionComment(2, "Name 2", "Email 2", "Comment 2"),
        };

        when(restTemplate.getForObject(COMMENTS_URL, AuditionComment[].class))
            .thenReturn(comments);

        List<AuditionComment> result = auditionIntegrationClient.getComments(null);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getBody());
        assertEquals("Comment 2", result.get(1).getBody());
    }

    @Test
    void getComments_WithPostId_NotFound() {
        when(restTemplate.getForObject(COMMENTS_URL + "?postId=1", AuditionComment[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getComments(1));

        assertEquals("Error Fetching Comments", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getComments_WithPostId_ClientError() {
        when(restTemplate.getForObject(COMMENTS_URL + "?postId=1", AuditionComment[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getComments(1));

        assertEquals("Error Fetching Comments", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getComments_WithPostId_RestClientException() {
        when(restTemplate.getForObject(COMMENTS_URL + "?postId=1", AuditionComment[].class))
            .thenThrow(new RestClientException("Service Unavailable"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getComments(1));

        assertEquals("Error Fetching Comments", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

    @Test
    void getComments_WithPostId_EmptyResponse() {
        when(restTemplate.getForObject(COMMENTS_URL + "?postId=1", AuditionComment[].class))
            .thenReturn(null);

        List<AuditionComment> result = auditionIntegrationClient.getComments(1);
        assertTrue(result.isEmpty());
    }

}
