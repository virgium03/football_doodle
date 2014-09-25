package ro.ratoi.virgiliu.football.doodle;

import com.inet.editor.BaseEditor;
import com.toedter.calendar.JDateChooser;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import ro.ratoi.virgiliu.football.doodle.email.DoodleEmailService;
import ro.ratoi.virgiliu.football.doodle.email.EmailProvider;
import ro.ratoi.virgiliu.football.doodle.email.MailDto;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vigi on 8/16/2014.
 */
@org.springframework.stereotype.Component
class FootballPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(FootballPanel.class);

    public static final String TIME_FORMAT = "##:##-##:##";

    private final JComboBox<EmailProvider> emailProviderCombo = new JComboBox<EmailProvider>();
    private final JTextField titleTextField = new JTextField(100);
    private final JTextField locationTextField = new JTextField(100);
    private final JDateChooser dateChooser = new JDateChooser(new Date());
    private final JFormattedTextField timeTextField = new JFormattedTextField(createFormatter());
    private final BaseEditor emailTextEditor = new BaseEditor(true);
    private final JTextArea recipientsTextArea = new JTextArea(3, 100);
    private final JTextField initiatorName = new JTextField(30);
    private final JTextField initiatorEmail = new JTextField(30);
    private final JPasswordField emailPassword = new JPasswordField(30);
    private final JButton sendButton = new JButton("Create doodle and send emails");
    private final Font defaultFont = new Font("Serif", Font.PLAIN, 18);

    private final DoodleEmailService emailService;

    private final AppConfigParams appConfigParams;

    @Autowired
    FootballPanel(DoodleEmailService emailService, AppConfigParams appConfigParams) {
        this.emailService = emailService;
        this.appConfigParams = appConfigParams;
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);

        addDoodleTitle(c);
        addDoodleLocation(c);
        addDoodleMatchDate(c);
        addDoodleMatchTime(c);
        addDoodleInitiatorName(c);
        addDoodleInitiatorEmail(c);
        addEmailText(c);
        addRecipients(c);
        addEmailProvider(c);
        addGmailPassword(c);
        addSendButton(c);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Create Doodle and send email"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

    }

    private void addDoodleTitle(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel titleLabel = new JLabel("1. Doodle Title");
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        titleLabel.setFont(defaultFont);
        add(titleLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        titleTextField.setFont(defaultFont);
        titleTextField.setText(this.appConfigParams.getDoodleDefaultTitle());
        add(titleTextField, c);
    }

    private void addDoodleLocation(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel locationLabel = new JLabel("2. Doodle Location");
        locationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        locationLabel.setFont(defaultFont);
        add(locationLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        locationTextField.setFont(defaultFont);
        locationTextField.setText(this.appConfigParams.getDoodleDefaultLocation());
        add(locationTextField, c);
    }

    private void addDoodleMatchDate(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel dateLabel = new JLabel("3. Doodle Date");
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateLabel.setFont(defaultFont);
        add(dateLabel, c);

        c.gridx = 1;
        c.gridy = 2;
        dateChooser.setFont(defaultFont);
        dateChooser.setDateFormatString("EEEEEEEEEE, d MMM yyyy");
        add(dateChooser, c);
        dateChooser.setDate(suggestFootballMatchDate());
    }

    private Date suggestFootballMatchDate() {
        Calendar c = Calendar.getInstance();
        int diff = appConfigParams.getDoodleDefaultDayOfWeek() - c.get(Calendar.DAY_OF_WEEK);
        if (!(diff > 0)) {
            diff += 7;
        }
        c.add(Calendar.DAY_OF_MONTH, diff);
        return c.getTime();
    }

    private void addDoodleMatchTime(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel timeLabel = new JLabel("4. Doodle Time");
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        timeLabel.setFont(defaultFont);
        add(timeLabel, c);

        c.gridx = 1;
        c.gridy = 3;
        timeTextField.setFont(defaultFont);
        timeTextField.setText(this.appConfigParams.getDoodleDefaultTime());
        add(timeTextField, c);
    }

    private static MaskFormatter createFormatter() {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(TIME_FORMAT);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    private void addDoodleInitiatorName(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        c.weighty = 0.0;
        JLabel initiatorNameLabel = new JLabel("5. Doodle Initiator Name");
        initiatorNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        initiatorNameLabel.setFont(defaultFont);
        add(initiatorNameLabel, c);

        c.gridx = 1;
        c.gridy = 4;
        initiatorName.setFont(defaultFont);
        initiatorName.setText(appConfigParams.getDoodleInitiatorName());
        add(initiatorName, c);
    }

    private void addDoodleInitiatorEmail(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 5;
        c.gridheight = 1;
        c.weighty = 0.0;
        JLabel initiatorEmailLabel = new JLabel("6. Doodle Initiator Email");
        initiatorEmailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        initiatorEmailLabel.setFont(defaultFont);
        add(initiatorEmailLabel, c);

        c.gridx = 1;
        c.gridy = 5;
        initiatorEmail.setFont(defaultFont);
        initiatorEmail.setText(this.appConfigParams.getDoodleECEmail());
        add(initiatorEmail, c);
    }

    private void addEmailText(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 6;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel emailTextLabel = new JLabel("7. Email Text");
        emailTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        emailTextLabel.setFont(defaultFont);
        emailTextLabel.setToolTipText("This is the email template that uses dynamic variables like ${matchDate}");
        add(emailTextLabel, c);

        java.net.URL emailTemplateURL = loadEmailTemplate();
        emailTextEditor.setPage(emailTemplateURL);
        c.gridx = 1;
        c.gridy = 6;
        c.gridheight = 10;
        c.weighty = 1.0;
        add(emailTextEditor, c);
    }

    private URL loadEmailTemplate() {
        try {
            // try to load external email template file
            FileSystemResource res = new FileSystemResource("emailTemplate.html");
            if (res.isReadable()) {
                return res.getURL();
            }
            // otherwise fallback to the one inside the application jar
            return new ClassPathResource("mail/emailTemplate.html").getURL();
        } catch (IOException e) {
            throw new RuntimeException("Could not load email template.");
        }
    }

    private void addRecipients(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 17;
        c.gridheight = 1;
        c.weighty = 0.1;
        JLabel recipientsLabel = new JLabel("8. Email Recipients");
        recipientsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        recipientsLabel.setFont(defaultFont);
        recipientsLabel.setToolTipText("Example: hodor@hodor.com; jonsnow@wall.org");
        add(recipientsLabel, c);

        c.gridx = 1;
        c.gridy = 17;
        JScrollPane scp = new JScrollPane(recipientsTextArea);
        recipientsTextArea.setFont(defaultFont);
        recipientsTextArea.setText(this.appConfigParams.getEmailDefaultRecipients());
        add(scp, c);
    }

    private void addEmailProvider(GridBagConstraints c) {
        for (EmailProvider prov : EmailProvider.values()) {
            emailProviderCombo.addItem(prov);
        }

        c.gridx = 0;
        c.gridy = 18;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel emailProviderLabel = new JLabel("9. Email Provider");
        emailProviderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        emailProviderLabel.setFont(defaultFont);
        emailProviderLabel.setToolTipText("Which SMTP server to use for sending the notification email.");
        add(emailProviderLabel, c);

        c.gridx = 1;
        c.gridy = 18;
        emailProviderCombo.setFont(defaultFont);
        add(emailProviderCombo, c);

        emailProviderCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmailAddress();
            }
        });
    }

    private void updateEmailAddress() {
        EmailProvider prov = (EmailProvider) emailProviderCombo.getSelectedItem();
        if (prov == null) {
            return;
        }
        if (EmailProvider.GOOGLE.equals(prov)) {
            initiatorEmail.setText(appConfigParams.getGoogleMailEmail());
        } else if (EmailProvider.YAHOO.equals(prov)) {
            initiatorEmail.setText(appConfigParams.getYahooMailEmail());
        } else {
            initiatorEmail.setText(appConfigParams.getDoodleECEmail());
        }
    }

    private void addGmailPassword(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 19;
        c.gridheight = 1;
        c.weighty = 0.0;
        JLabel passwordLabel = new JLabel("10. Email Password");
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLabel.setFont(defaultFont);
        passwordLabel.setToolTipText("In case you chose Google's or Yahoo's SMTP server, you need to authenticate");
        add(passwordLabel, c);

        c.gridx = 1;
        c.gridy = 19;
        emailPassword.setFont(defaultFont);
        add(emailPassword, c);
    }

    private void addSendButton(GridBagConstraints c) {
        c.gridx = 1;
        c.gridy = 20;
        sendButton.setFont(defaultFont);
        add(sendButton, c);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMail();
            }
        });
    }

    private void sendMail() {
        Object[] options = {"Yes, please", "No, thanks"};
        int n = JOptionPane.showOptionDialog(this,
                "Are you sure that you want to create the Doodle and send the invitation email?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (n != 0) {
            return;
        }

        try {
            validateFields();
            MailDto dto = buildMailDto();
            emailService.sendMail(dto);
            JOptionPane.showMessageDialog(this,
                    "Doodle created successfully and emails have been sent.",
                    "Doodle success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Internal error while trying to create the doodle",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateFields() {
        validateTextComponent(titleTextField, "Title is mandatory.");
        validateTextComponent(locationTextField, "Location is mandatory.");
        validateRecipients();
        validateTextComponent(initiatorName, "Doodle initiator name is mandatory.");
        validateInitiatorEmail();
        validateGmailPassword();
    }

    private void validateTextComponent(JTextComponent tc, String errorMessage) {
        if (StringUtils.isBlank(tc.getText())) {
            tc.requestFocus();
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateRecipients() {
        validateTextComponent(recipientsTextArea, "Recipients are mandatory.");
        validateEmails(recipientsTextArea);
    }

    private void validateInitiatorEmail() {
        validateTextComponent(initiatorEmail, "Doodle initiator email is mandatory.");
        validateEmails(initiatorEmail);
        if (EmailProvider.GOOGLE.equals(emailProviderCombo.getSelectedItem()) &&
                !initiatorEmail.getText().endsWith("gmail.com")) {
            initiatorEmail.requestFocus();
            throw new IllegalArgumentException("Initiator email must end in gmail.com.");
        } else if (EmailProvider.YAHOO.equals(emailProviderCombo.getSelectedItem()) &&
                !initiatorEmail.getText().contains("@yahoo")) {
            initiatorEmail.requestFocus();
            throw new IllegalArgumentException("Initiator email must end in yahoo.com.");
        }
    }

    private void validateEmails(JTextComponent tc) {
        StringBuilder buff = new StringBuilder(128);
        String[] tokens = tc.getText().split(";");
        for (String email : tokens) {
            if (!validEmail(email)) {
                buff.append(email).append("\n");
            }
        }
        if (buff.length() > 0) {
            tc.requestFocus();
            throw new IllegalArgumentException("The following email addresses are invalid: " + buff);
        }
    }

    private boolean validEmail(String email) {
        try {
            InternetAddress addr = new InternetAddress(email);
            addr.validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }

    private void validateGmailPassword() {
        if (missingEmailPassword()) {
            emailPassword.requestFocus();
            throw new IllegalArgumentException("Unfortunately email password is mandatory when using gmail or Yahoo servers.");
        }
    }

    private boolean missingEmailPassword() {
        return !EmailProvider.EUROPEAN_COMMISSION.equals(emailProviderCombo.getSelectedItem()) &&
                ArrayUtils.isEmpty(emailPassword.getPassword());
    }

    private MailDto buildMailDto() {
        MailDto dto = new MailDto();
        dto.setTitle(titleTextField.getText());
        dto.setLocation(locationTextField.getText());
        dto.setMatchTime(timeTextField.getText());
        dto.setBody(emailTextEditor.getText());
        dto.setMatchDate(dateChooser.getDate());
        dto.setRecipients(recipientsTextArea.getText());
        dto.setInitiatorName(initiatorName.getText());
        dto.setInitiatorEmail(initiatorEmail.getText());
        dto.setEmailProvider((EmailProvider) emailProviderCombo.getSelectedItem());
        dto.setEmailPassword(new String(emailPassword.getPassword()));
        return dto;
    }

}