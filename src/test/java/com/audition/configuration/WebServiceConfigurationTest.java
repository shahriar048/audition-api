package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.audition.model.AuditionPost;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class WebServiceConfigurationTest {

    @InjectMocks
    private WebServiceConfiguration webServiceConfiguration;

    @Mock
    private LoggingInterceptor loggingInterceptor;

    private final ObjectMapper objectMapper = new WebServiceConfiguration().objectMapper();

    @Test
    void testObjectMapper_SerializesWithoutNulls() throws JsonProcessingException {
        AuditionPost post = new AuditionPost(1, 101, "Test Title", null);
        String json = objectMapper.writeValueAsString(post);

        assertFalse(json.contains("body"));
        assertEquals("{\"userId\":1,\"id\":101,\"title\":\"Test Title\"}", json);
    }

    @Test
    void testObjectMapper_IgnoresUnknownProperties() throws IOException {
        String json = "{\"userId\":1,\"id\":101,\"title\":\"Test Title\",\"body\":\"Test Body\",\"extraField\":\"ignored\"}";
        AuditionPost post = objectMapper.readValue(json, AuditionPost.class);

        assertNotNull(post);
        assertEquals(1, post.getUserId());
        assertEquals(101, post.getId());
        assertEquals("Test Title", post.getTitle());
        assertEquals("Test Body", post.getBody());
    }

    @Test
    void testObjectMapper_UsesCamelCase() throws JsonProcessingException {
        AuditionPost post = new AuditionPost(1, 101, "Test Title", "Test Body");
        String json = objectMapper.writeValueAsString(post);

        assertTrue(json.contains("userId"));
    }

    @Test
    void testObjectMapper_FormatsDateCorrectly() throws JsonProcessingException {
        LocalDate date = LocalDate.now();
        String expectedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"" + expectedDate + "\"", json);
    }

    @Test
    void testRestTemplate_UsesCustomObjectMapper() {
        RestTemplate restTemplate = webServiceConfiguration.restTemplate(objectMapper, loggingInterceptor);

        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        boolean containsCustomMapper = messageConverters.stream()
            .filter(MappingJackson2HttpMessageConverter.class::isInstance)
            .map(converter -> ((MappingJackson2HttpMessageConverter) converter).getObjectMapper())
            .anyMatch(mapper -> mapper == objectMapper);

        assertTrue(containsCustomMapper, "RestTemplate should use the provided ObjectMapper.");
    }

    @Test
    void testRestTemplate_HasLoggingInterceptor() {
        RestTemplate restTemplate = webServiceConfiguration.restTemplate(objectMapper, loggingInterceptor);

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        assertTrue(interceptors.contains(loggingInterceptor), "RestTemplate should contain LoggingInterceptor.");
    }
}
