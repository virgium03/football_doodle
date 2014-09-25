package ro.ratoi.virgiliu.football.doodle;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.swing.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
        sender.setUsername(config.getGoogleMailEmail());
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.ssl.trust", config.getGoogleMailHost());
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailProperties.put("mail.smtp.auth", true);
        sender.setJavaMailProperties(mailProperties);
        return sender;
    }

    @Bean
    public JavaMailSender yahooMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        AppConfigParams config = mailConfig();
        sender.setHost(config.getYahooMailHost());
        sender.setPort(config.getYahooMailPort());
        sender.setUsername(config.getYahooMailEmail());
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.socketFactory.port","465");
        mailProperties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.smtp.host","smtp.mail.yahoo.com");
        mailProperties.put("mail.smtp.port","465");
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

    @Bean
    public Client restClient() {
        AppConfigParams config = mailConfig();
        if (!config.isUsesProxyServer()) {
            return ClientBuilder.newClient();
        }

        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.PROXY_URI, String.format("%s://%s:%s",
                config.getProxyProtocol(), config.getProxyHost(), config.getProxyPort()));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        final AuthScope ntlmAuthScope = new AuthScope(config.getProxyHost(), config.getProxyPort(),
                AuthScope.ANY_REALM, null);
        credentialsProvider.setCredentials(ntlmAuthScope,
                new UsernamePasswordCredentials(config.getProxyUsername(), config.getProxyPassword()));
        clientConfig.property(ApacheClientProperties.CREDENTIALS_PROVIDER, credentialsProvider);
        clientConfig.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
        clientConfig.connectorProvider(new ApacheConnectorProvider());
        return ClientBuilder.newClient(clientConfig);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
