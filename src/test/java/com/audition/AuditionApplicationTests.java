package com.audition;

import brave.propagation.CurrentTraceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AuditionApplicationTests {

    @MockBean
    private CurrentTraceContext currentTraceContext;

    @Test
    void contextLoads() {
    }
}
