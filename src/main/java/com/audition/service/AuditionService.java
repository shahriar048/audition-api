package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class AuditionService {

    // TODO: Split this into PostService and CommentService

    private final AuditionIntegrationClient auditionIntegrationClient;

    public AuditionService(final AuditionIntegrationClient auditionIntegrationClient) {
        this.auditionIntegrationClient = auditionIntegrationClient;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public List<AuditionPost> getPosts(final String filter) {
        final List<AuditionPost> posts = auditionIntegrationClient.getPosts();

        if (filter != null && !filter.isEmpty()) {
            final String lowerCaseFilter = filter.toLowerCase(Locale.ROOT);

            return posts.stream()
                .filter(post -> post.getTitle().toLowerCase(Locale.ROOT).contains(lowerCaseFilter) ||
                    post.getBody().toLowerCase(Locale.ROOT).contains(lowerCaseFilter))
                .toList();
        }

        return posts;
    }

    public AuditionPost getPostById(final int postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    public AuditionPost getPostWithComments(final int postId) {
        final AuditionPost post = getPostById(postId);
        post.setComments(auditionIntegrationClient.getCommentsForPost(postId));

        return post;
    }

    public List<AuditionComment> getComments(final Integer postId) {
        return auditionIntegrationClient.getComments(postId);
    }

}
