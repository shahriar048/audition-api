package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditionService {

    private final AuditionIntegrationClient auditionIntegrationClient;

    public AuditionService(AuditionIntegrationClient auditionIntegrationClient) {
        this.auditionIntegrationClient = auditionIntegrationClient;
    }

    public List<AuditionPost> getPosts(String filter) {
        List<AuditionPost> posts = auditionIntegrationClient.getPosts();

        if (filter != null && !filter.isEmpty()) {
            String lowerCaseFilter = filter.toLowerCase();

            posts = posts.stream()
                .filter(post -> post.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                    post.getBody().toLowerCase().contains(lowerCaseFilter))
                .toList();
        }

        return posts;
    }

    public AuditionPost getPostById(final int postId) {
        return auditionIntegrationClient.getPostById(postId);
    }
}
