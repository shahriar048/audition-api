package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";

    private final RestTemplate restTemplate;

    public AuditionIntegrationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AuditionPost> getPosts() {
        try {
            return Optional.ofNullable(restTemplate.getForObject(POSTS_URL, AuditionPost[].class))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Posts",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public AuditionPost getPostById(final String id) {
        try {
            return new AuditionPost();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find post with ID " + id, "Resource Not Found",
                    HttpStatus.NOT_FOUND.value());
            } else {
                // TODO Find a better way to handle the exception so that the original error message is not lost. Feel free to change this function.
                throw new SystemException("Unknown Error message");
            }
        }
    }

    // TODO Write a method GET comments for a post from https://jsonplaceholder.typicode.com/posts/{postId}/comments - the comments must be returned as part of the post.

    // TODO write a method. GET comments for a particular Post from https://jsonplaceholder.typicode.com/comments?postId={postId}.
    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.
}
