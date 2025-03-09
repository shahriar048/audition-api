package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";

    @Autowired
    private RestTemplate restTemplate;

    public List<AuditionPost> getPosts() {
        try {
            return Optional.ofNullable(restTemplate.getForObject(POSTS_URL, AuditionPost[].class))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Posts",
                HttpStatus.SERVICE_UNAVAILABLE.value());
        }
    }

    public AuditionPost getPostById(final int id) {
        try {
            return restTemplate.getForObject(POSTS_URL + "/" + id, AuditionPost.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find post with ID " + id, "Resource Not Found",
                    HttpStatus.NOT_FOUND.value());
            }
            throw new SystemException(e.getMessage(), "Client Error", e.getStatusCode().value());
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Post with ID " + id,
                HttpStatus.SERVICE_UNAVAILABLE.value());
        }
    }

    public List<Comment> getCommentsForPost(int postId) {
        try {
            return Optional.ofNullable(
                    restTemplate.getForObject(POSTS_URL + "/" + postId + "/comments", Comment[].class, postId))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find post with ID " + postId, "Resource Not Found",
                    HttpStatus.NOT_FOUND.value());
            }
            throw new SystemException(e.getMessage(), "Client Error", e.getStatusCode().value());
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Comments for Post ID " + postId,
                HttpStatus.SERVICE_UNAVAILABLE.value());
        }
    }

}
