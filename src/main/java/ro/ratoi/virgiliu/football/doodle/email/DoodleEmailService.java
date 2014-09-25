package ro.ratoi.virgiliu.football.doodle.email;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ro.ratoi.virgiliu.football.doodle.AppConfigParams;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

/**
 * Created by vigi on 8/16/2014.
 */
@Component
public class DoodleEmailService {

    private static final Logger log = LoggerFactory.getLogger(DoodleEmailService.class);

    private final JavaMailSender gmailSender;

    private final JavaMailSender yahooMailSender;

    private final JavaMailSender europeanCommissionSender;

    private final AppConfigParams appConfigParams;

    private final Client restClient;

    private final SimpleDateFormat mailDateFormat = new SimpleDateFormat("EEEEEEEEEE, d MMM yyyy");

    @Autowired
    DoodleEmailService(@Qualifier("gmailMailSender") JavaMailSender gmailSender,
                       @Qualifier("yahooMailSender") JavaMailSender yahooMailSender,
                       @Qualifier("ecMailSender") JavaMailSender europeanCommissionSender,
                       Client restClient,
                       AppConfigParams appConfigParams) {
        this.gmailSender = gmailSender;
        this.yahooMailSender = yahooMailSender;
        this.europeanCommissionSender = europeanCommissionSender;
        this.restClient = restClient;
        this.appConfigParams = appConfigParams;
        Velocity.init();
    }

    public void sendMail(MailDto dto) {
        DoodleResponse doodleResponse = buildDoodleResponse(dto);

        VelocityContext context = buildEmailTemplateContext(dto, doodleResponse);
        StringWriter bodyWriter = new StringWriter();
        Velocity.evaluate(context, bodyWriter, "errorTemplate", dto.getBody());

        JavaMailSender mailSender = getSelectedMailSender(dto);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(dto.getInitiatorEmail());
            helper.setTo(dto.getRecipients().split(";"));
            helper.setText(bodyWriter.toString(), true);
            helper.setSubject(composeSubject(dto));
            log.info("--- Sending email message");
            log.info("--- Recipients: {}", new Object[]{message.getRecipients(Message.RecipientType.TO)});
            log.info("--- Body: {}", bodyWriter);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(message);
    }

    private DoodleResponse buildDoodleResponse(MailDto dto) {
        WebTarget target = restClient.target(appConfigParams.getDoodleBaseUrl()).path("/np/new-polls/");

        Form form = new Form()
                .param("title", dto.getTitle())
                .param("locName", dto.getLocation())
                .param("initiatorAlias", dto.getInitiatorName())
                .param("initiatorEmail", dto.getInitiatorEmail())
                .param("hidden", "false")
                .param("ifNeedBe", "false")
                .param("askAddress", "false")
                .param("askEmail", "false")
                .param("askPhone", "false")
                .param("optionsMode", "dates")
                .param("options[]", buildDoodleTime(dto))
                .param("type", "DATE");

        Response response = target.request().post(Entity.form(form));
        String responseString = response.readEntity(String.class);
        if (response.getStatus() != 200) {
            throw new IllegalArgumentException(responseString);
        }
        return new Gson().fromJson(responseString, DoodleResponse.class);
    }

    private String buildDoodleTime(MailDto dto) {
        if (StringUtils.isBlank(dto.getMatchTime()) || dto.getMatchDate() == null) {
            return "";
        }
        String[] tokens = dto.getMatchTime().split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String doodleDate = sdf.format(dto.getMatchDate());
        String startHour = tokens[0].replaceAll(":", "");
        String endHour = tokens[1].replaceAll(":", "");

        // time format looks like options[]:201408312000-201408312100
        return String.format("%s%s-%s%s", doodleDate, startHour, doodleDate, endHour);
    }

    private VelocityContext buildEmailTemplateContext(MailDto dto, DoodleResponse doodleResponse) {
        VelocityContext context = new VelocityContext();
        context.put("matchDate", dto.getMatchDate());
        context.put("matchTime", dto.getMatchTime());
        context.put("gameLink", buildDoodleLink(doodleResponse));
        context.put("initiatorName", dto.getInitiatorName());
        return context;
    }

    private JavaMailSender getSelectedMailSender(MailDto dto) {
        if (EmailProvider.GOOGLE.equals(dto.getEmailProvider())) {
            // we have to set the gmail password coming from the form
            ((JavaMailSenderImpl) gmailSender).setPassword(dto.getEmailPassword());
            return gmailSender;
        } else if (EmailProvider.YAHOO.equals(dto.getEmailProvider())) {
            ((JavaMailSenderImpl) yahooMailSender).setPassword(dto.getEmailPassword());
            return yahooMailSender;
        }
        return europeanCommissionSender;
    }

    private String buildDoodleLink(DoodleResponse doodleResponse) {
        return String.format("%s/%s", appConfigParams.getDoodleBaseUrl(), doodleResponse.getId());
    }

    private String composeSubject(MailDto dto) {
        return String.format("%s on %s %s at %s", dto.getTitle(), mailDateFormat.format(dto.getMatchDate()),
                dto.getMatchTime(), dto.getLocation());
    }
}
