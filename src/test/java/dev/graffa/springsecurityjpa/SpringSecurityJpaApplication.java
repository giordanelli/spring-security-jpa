package dev.graffa.springsecurityjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringSecurityJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityJpaApplication.class, args);
    }

    /**
     * @return the bean responsible for encoding passwords in a secure way with BCrypt strong hashing function
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
