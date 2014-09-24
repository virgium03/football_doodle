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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${usesProxyServer}")
    private boolean usesProxyServer;

    @Value("${proxy.protocol}")
    private String proxyProtocol;

    @Value("${proxy.username}")
    private String proxyUsername;

    @Value("${proxy.password}")
    private String proxyPassword;
    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private int proxyPort;

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

    @Bean
    public Client restClient() {
        if (!usesProxyServer) {
            return ClientBuilder.newClient();
        }

        final ClientConfig config = new ClientConfig();
        config.property(ClientProperties.PROXY_URI, String.format("%s://%s:%s", proxyProtocol, proxyHost, proxyPort));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        final AuthScope ntlmAuthScope = new AuthScope(proxyHost, proxyPort, AuthScope.ANY_REALM, null);
        credentialsProvider.setCredentials(ntlmAuthScope, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
        config.property(ApacheClientProperties.CREDENTIALS_PROVIDER, credentialsProvider);
        config.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
        config.connectorProvider(new ApacheConnectorProvider());
        return ClientBuilder.newClient(config);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
