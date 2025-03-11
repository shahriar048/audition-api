package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
public class AuditionIntegrationClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final String POSTS_URL = BASE_URL + "/posts";
    private static final String COMMENTS_URL = BASE_URL + "/comments";

    @Autowired
    private RestTemplate restTemplate;

    public List<AuditionPost> getPosts() {
        // TODO: Implement pagination (e.g. https://jsonplaceholder.typicode.com/posts?_page=1&_limit=10)
        try {
            return Optional.ofNullable(restTemplate.getForObject(POSTS_URL, AuditionPost[].class))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Posts",
                HttpStatus.SERVICE_UNAVAILABLE.value(), e);
        }
    }

    public AuditionPost getPostById(final int id) {
        try {
            return restTemplate.getForObject(POSTS_URL + "/" + id, AuditionPost.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find post with ID " + id, "Resource Not Found",
                    HttpStatus.NOT_FOUND.value(), e);
            }
            throw new SystemException(e.getMessage(), "Client Error", e.getStatusCode().value(), e);
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Post with ID " + id,
                HttpStatus.SERVICE_UNAVAILABLE.value(), e);
        }
    }

    public List<AuditionComment> getCommentsForPost(final int postId) {
        try {
            return Optional.ofNullable(
                    restTemplate.getForObject(POSTS_URL + "/" + postId + "/comments", AuditionComment[].class, postId))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find post with ID " + postId, "Resource Not Found",
                    HttpStatus.NOT_FOUND.value(), e);
            }
            throw new SystemException(e.getMessage(), "Client Error", e.getStatusCode().value(), e);
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Comments for Post ID " + postId,
                HttpStatus.SERVICE_UNAVAILABLE.value(), e);
        }
    }

    public List<AuditionComment> getComments(final Integer postId) {
        try {
            final String url = (postId != null) ? COMMENTS_URL + "?postId=" + postId : COMMENTS_URL;
            return Optional.ofNullable(restTemplate.getForObject(url, AuditionComment[].class))
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
        } catch (RestClientException e) {
            throw new SystemException(e.getMessage(), "Error Fetching Comments",
                HttpStatus.SERVICE_UNAVAILABLE.value(), e);
        }
    }

}
