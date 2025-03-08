package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.audition.model.AuditionPost;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

        List<AuditionPost> posts = auditionIntegrationClient.getPosts();
        assertNotNull(posts);
        assertEquals(Collections.emptyList(), posts);
    }
}
