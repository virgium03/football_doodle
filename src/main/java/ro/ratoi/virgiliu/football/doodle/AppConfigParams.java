package ro.ratoi.virgiliu.football.doodle;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by vigi on 8/16/2014.
 */
@Component
@Data
public class AppConfigParams {

    @Value("${application.version}")
    private String version;

    @Value("${doodle.baseUrl}")
    private String doodleBaseUrl;

    @Value("${doodle.default.title}")
    private String doodleDefaultTitle;

    @Value("${doodle.default.location}")
    private String doodleDefaultLocation;

    @Value("${doodle.default.time}")
    private String doodleDefaultTime;

    @Value("${doodle.ec.email}")
    private String doodleECEmail;

    @Value("${doodle.default.initiator.name}")
    private String doodleInitiatorName;

    @Value("${doodle.default.day.of.week}")
    private int doodleDefaultDayOfWeek;

    @Value("${email.default.recipients}")
    private String emailDefaultRecipients;

    @Value("${google.mail.smtp.host}")
    private String googleMailHost;

    @Value("${google.mail.port}")
    private int googleMailPort;

    @Value("${google.mail.email}")
    private String googleMailEmail;

    @Value("${yahoo.mail.smtp.host}")
    private String yahooMailHost;

    @Value("${yahoo.mail.port}")
    private int yahooMailPort;

    @Value("${yahoo.mail.email}")
    private String yahooMailEmail;

    @Value("${ec.mail.smtp.host}")
    private String ecMailHost;

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

}
