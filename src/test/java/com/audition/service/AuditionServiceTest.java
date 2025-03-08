package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
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
}
