package ro.ratoi.virgiliu.football.doodle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.swing.*;
import java.util.Properties;

@Configuration
@EnableAutoConfiguration(exclude = {VelocityAutoConfiguration.class})
@ComponentScan
public class Application extends JFrame {

    @Bean
    AppConfigParams mailConfig() {
        return new AppConfigParams();
    }

    @Bean
    public JavaMailSender gmailMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        AppConfigParams config = mailConfig();
        sender.setHost(config.getGoogleMailHost());
        sender.setPort(config.getGoogleMailPort());
        sender.setUsername(config.getGoogleMailUsername());
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.ssl.trust", config.getGoogleMailHost());
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailProperties.put("mail.smtp.auth", true);
        sender.setJavaMailProperties(mailProperties);
        return sender;
    }

    @Bean
    public JavaMailSender ecMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        AppConfigParams config = mailConfig();
        sender.setHost(config.getEcMailHost());
        return sender;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
