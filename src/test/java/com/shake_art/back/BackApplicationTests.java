package com.shake_art.back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.context.annotation.Bean;
import org.mockito.Mockito;

@ActiveProfiles("test")
@SpringBootTest
class BackApplicationTests {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }
    }

    @Test
    void contextLoads() {
        // Test to ensure the Spring context loads successfully
    }

}
