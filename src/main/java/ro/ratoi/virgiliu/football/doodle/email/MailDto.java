package ro.ratoi.virgiliu.football.doodle.email;

import lombok.Data;

import java.util.Date;

/**
 * Created by vigi on 8/16/2014.
 */
@Data
public final class MailDto {

    private String title;

    private String location;

    private String body;

    private String recipients;

    private Date matchDate;

    private String matchTime;

    private String initiatorName;

    private String initiatorEmail;

    private EmailProvider emailProvider;

    private String emailPassword;

}
