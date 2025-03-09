package com.audition.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import com.audition.web.advice.ExceptionControllerAdvice;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuditionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditionService auditionService;

    @Mock
    private AuditionLogger auditionLogger;

    @InjectMocks
    private AuditionController auditionController;

    @InjectMocks
    private ExceptionControllerAdvice exceptionControllerAdvice;

    private static final List<AuditionPost> MOCK_POSTS = Arrays.asList(
        new AuditionPost(1, 101, "Title1", "Body1"),
        new AuditionPost(2, 102, "Title2", "Body2")
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(auditionController)
            .setControllerAdvice(exceptionControllerAdvice)
            .build();
    }

    private void performGetRequest(String filter, int expectedSize, String expectedTitle) throws Exception {
        mockMvc.perform(get("/posts")
                .param("filter", filter)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(expectedSize))
            .andExpect(
                expectedSize > 0 ? jsonPath("$[0].title").value(expectedTitle) : jsonPath("$.length()").value(0));
    }

    @Test
    void testGetPosts_NoFilter_ReturnsAllPosts() throws Exception {
        when(auditionService.getPosts(null)).thenReturn(MOCK_POSTS);

        mockMvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Title1"))
            .andExpect(jsonPath("$[1].title").value("Title2"));
    }

    @ParameterizedTest
    @CsvSource({
        "Title1, 1, Title1",
        "Body2, 1, Title2",
        "TITLE1, 1, Title1",
        "NotFound, 0, ''",
        "'', 2, Title1"
    })
    void testGetPosts_WithVariousFilters(String filter, int expectedSize, String expectedTitle) throws Exception {
        List<AuditionPost> expectedPosts = (filter == null || filter.isEmpty())
            ? MOCK_POSTS
            : MOCK_POSTS.stream()
                .filter(post -> post.getTitle().equalsIgnoreCase(filter) || post.getBody().equalsIgnoreCase(filter))
                .toList();

        when(auditionService.getPosts(filter)).thenReturn(expectedPosts);
        performGetRequest(filter, expectedSize, expectedTitle);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 101, Title1, Body1",
        "2, 102, Title2, Body2"
    })
    void getPostById_ValidId_ReturnsPost(int expectedUserId, int postId, String expectedTitle, String expectedBody)
        throws Exception {
        AuditionPost expectedPost = new AuditionPost(expectedUserId, postId, expectedTitle, expectedBody);
        when(auditionService.getPostById(postId)).thenReturn(expectedPost);

        mockMvc.perform(get("/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(postId))
            .andExpect(jsonPath("$.userId").value(expectedUserId))
            .andExpect(jsonPath("$.title").value(expectedTitle))
            .andExpect(jsonPath("$.body").value(expectedBody));
    }

    @ParameterizedTest
    @CsvSource({
        "0",
        "-1"
    })
    void getPostById_InvalidId_ReturnsBadRequest(int postId) throws Exception {
        mockMvc.perform(get("/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Invalid post ID " + postId))
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

}
