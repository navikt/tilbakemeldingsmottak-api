package no.nav.serviceklagemottak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EpostConfig {

    @Bean
    public JavaMailSender javaMailSender() {return new JavaMailSenderImpl();}

}
