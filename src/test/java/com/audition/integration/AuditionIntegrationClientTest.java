package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
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

        AuditionPost result = auditionIntegrationClient.getPostById("1");
        assertNotNull(result);
        assertEquals(post, result);
    }

    @Test
    void getPostById_NotFound() {
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("1"));
        assertEquals("Cannot find post with ID 1", exception.getMessage());
        assertEquals("Resource Not Found", exception.getTitle());
        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatusCode());
    }

    @Test
    void getPostById_ClientError() {
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("1"));
        assertEquals("400 Bad Request", exception.getMessage());
        assertEquals("Client Error", exception.getTitle());
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getStatusCode());
    }

    @Test
    void getPostById_RestClientException() {
        when(restTemplate.getForObject(POSTS_URL + "/1", AuditionPost.class))
            .thenThrow(new RestClientException("Service Unavailable"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("1"));
        assertEquals("Service Unavailable", exception.getMessage());
        assertEquals("Error Fetching Post with ID 1", exception.getTitle());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), exception.getStatusCode());
    }

}
