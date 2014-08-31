package ro.ratoi.virgiliu.football.doodle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by vigi on 8/16/2014.
 */
@Component
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

    @Value("${doodle.default.initiator.name}")
    private String doodleInitiatorName;

    @Value("${doodle.default.initiator.email}")
    private String doodleInitiatorEmail;

    @Value("${email.default.recipients}")
    private String emailDefaultRecipients;

    @Value("${google.mail.smtp.host}")
    private String googleMailHost;

    @Value("${google.mail.port}")
    private int googleMailPort;

    @Value("${google.mail.username}")
    private String googleMailUsername;

    @Value("${ec.mail.smtp.host}")
    private String ecMailHost;

    public String getVersion() {
        return version;
    }

    public String getDoodleBaseUrl() {
        return doodleBaseUrl;
    }

    public String getDoodleDefaultTitle() {
        return doodleDefaultTitle;
    }

    public String getDoodleDefaultLocation() {
        return doodleDefaultLocation;
    }

    public String getDoodleDefaultTime() {
        return doodleDefaultTime;
    }

    public String getEmailDefaultRecipients() {
        return emailDefaultRecipients;
    }

    public String getGoogleMailHost() {
        return googleMailHost;
    }

    public int getGoogleMailPort() {
        return googleMailPort;
    }

    public String getGoogleMailUsername() {
        return googleMailUsername;
    }

    public String getEcMailHost() {
        return ecMailHost;
    }

    public String getDoodleInitiatorEmail() {
        return doodleInitiatorEmail;
    }

    public String getDoodleInitiatorName() {
        return doodleInitiatorName;
    }

}
