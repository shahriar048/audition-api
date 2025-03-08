package com.audition.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
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

    @InjectMocks
    private AuditionController auditionController;

    private static final List<AuditionPost> MOCK_POSTS = Arrays.asList(
        new AuditionPost(1, 101, "Title1", "Body1"),
        new AuditionPost(2, 102, "Title2", "Body2")
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditionController).build();
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

}
