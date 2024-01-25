package project.Todolist.api.email.otp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import project.Todolist.api.model.ResetPassword;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class OtpReminderService implements OtpReminderSender {
    private final static Logger LOGGER = LoggerFactory.getLogger(OtpReminderService.class);
    private final JavaMailSender mailSender;

    public OtpReminderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOTPEmail(String to, ResetPassword resetPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject("OTP For Reset Password");
            helper.setFrom("zainabfadeyi@gmail.com");
            helper.setText("Your OTP For Reset Password: " + resetPassword.getOtp());
            String emailContent = buildOTPReminderEmail(resetPassword.getOtp(),resetPassword.getExpirationDateTime());
            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            LOGGER.error("Failed to send OTP email", e);
            throw new IllegalStateException("Failed to send OTP email");
        }
    }

    public String buildOTPReminderEmail(String otp, LocalDateTime expirationDateTime) {
        return "<html>\n" +
                "  <head>\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: 'Helvetica', 'Arial', sans-serif;\n" +
                "        font-size: 16px;\n" +
                "        margin: 0;\n" +
                "        color: #0b0c0c;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; min-width: 100%; width: 100% !important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "      <tbody>\n" +
                "        <tr>\n" +
                "          <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "            <table role=\"presentation\" width=\"100%\" style=\"border-collapse: collapse; max-width: 580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "              <tbody>\n" +
                "                <tr>\n" +
                "                  <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                    <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse\">\n" +
                "                      <tbody>\n" +
                "                        <tr>\n" +
                "                          <td style=\"padding-left: 10px\"></td>\n" +
                "                          <td style=\"font-size: 28px; line-height: 1.315789474; Margin-top: 4px; padding-left: 10px\">\n" +
                "                            <span style=\"font-family: Helvetica, Arial, sans-serif; font-weight: 700; color: #ffffff; text-decoration: none; vertical-align: top; display: inline-block\">Password OTP </span>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </tbody>\n" +
                "                    </table>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </tbody>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </tbody>\n" +
                "    </table>\n" +
                "    <!-- ... Additional HTML styling ... -->\n" +
                "    <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse: collapse; max-width: 580px; width: 100% !important\" width=\"100%\">\n" +
                "      <tbody>\n" +
                "        <tr>\n" +
                "          <td height=\"30\"><br></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "          <td style=\"font-family: Helvetica, Arial, sans-serif; font-size: 19px; line-height: 1.315789474; max-width: 560px\">\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">Hi,</p>\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">This is your rest password OTP:</p>\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\"><b>OTP:</b> " + otp + "</p>\n" +
                "            <p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c\">The OTP expires on " + formatDateTime(expirationDateTime) + ".</p>\n" +
                "          </td>\n" +
                "          <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td height=\"30\"><br></td>\n" +
                "        </tr>\n" +
                "      </tbody>\n" +
                "    </table><div class=\"yj6qo\"></div><div class=\"adL\"></div></body>\n" +
                "</html>";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}

