package com.reviewia.reviewiabackend.registration;

import com.reviewia.reviewiabackend.email.EmailSenderService;
import com.reviewia.reviewiabackend.registration.token.ConfirmationToken;
import com.reviewia.reviewiabackend.registration.token.ConfirmationTokenService;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserRole;
import com.reviewia.reviewiabackend.user.UserService;
import com.reviewia.reviewiabackend.utils.EmailValidator;
import com.reviewia.reviewiabackend.utils.MD5Util;
import com.reviewia.reviewiabackend.utils.PropertyLoader;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {
    private UserService userService;
    private ConfirmationTokenService confirmationTokenService;
    private EmailSenderService emailSenderService;
    private PropertyLoader propertyLoader;

    public void register(RegistrationRequest request) {
        String serverUrl = propertyLoader.serverUrl;

        if (EmailValidator.isValid(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Email is not valid");
        }

        try {
            String token = userService.signUpUser(new User(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    UserRole.USER,
                    "https://www.gravatar.com/avatar/" + MD5Util.md5Hex(request.getEmail().toLowerCase())
            ));

            // todo: add to application.properties
            String link = serverUrl + "/api/registration/confirm?token=" + token;
//            String link = "https://reviewia.herokuapp.com/api/registration/confirm?token=" + token;
//        String link = "http://reviewia.us-east-2.elasticbeanstalk.com/api/registration/confirm?token=" + token;

            emailSenderService.send(
                    request.getEmail(),
                    "info@reviewia.com",
                    "Reviewia - Email Address Verification Request",
                    buildEmail(StringUtils.capitalize(request.getFirstName()), link)
            );


        } catch (MessagingException | MailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void registerAdmin(RegistrationRequest request) {
        String serverUrl = propertyLoader.serverUrl;

        if (EmailValidator.isValid(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Email is not valid");
        }

        try {
            // register users with role:USER
            String token = userService.signUpUser(new User(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword(),
                    UserRole.ADMIN,
                    "https://www.gravatar.com/avatar/" + MD5Util.md5Hex(request.getEmail().toLowerCase())
            ));

            // todo: add to application.properties
            String link = serverUrl + "/api/registration/confirm?token=" + token;
//        String link = "http://reviewia.us-east-2.elasticbeanstalk.com/api/registration/confirm?token=" + token;

            emailSenderService.send(
                    request.getEmail(),
                    "info@reviewia.com",
                    "Reviewia - Email Address Verification Request",
                    buildEmail(StringUtils.capitalize(request.getFirstName()), link));

        } catch (MessagingException | MailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String resetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        String serverUrl = propertyLoader.serverUrl;

        String token = userService.resetPassword(forgetPasswordRequest.getEmail(), forgetPasswordRequest.getPassword());
        String link = serverUrl + "/api/registration/confirm?token=" + token;
//        String link = "http://reviewia.us-east-2.elasticbeanstalk.com/api/registration/confirm?token=" + token;

        try {
            emailSenderService.send(
                    forgetPasswordRequest.getEmail(),
                    "info@reviewia.com",
                    "Reviewia - Password Verification Request",
                    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
                            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">"+
                            "<head>"+
                            "<!--[if gte mso 9]>"+
                            "<xml>"+
                            "  <o:OfficeDocumentSettings>"+
                            "    <o:AllowPNG/>"+
                            "    <o:PixelsPerInch>96</o:PixelsPerInch>"+
                            "  </o:OfficeDocumentSettings>"+
                            "</xml>"+
                            "<![endif]-->"+
                            "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
                            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                            "  <meta name=\"x-apple-disable-message-reformatting\">"+
                            "  <!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><!--<![endif]-->"+
                            "  <title></title>"+
                            "  "+
                            "    <style type=\"text/css\">"+
                            "      table, td { color: #000000; } a { color: #0000ee; text-decoration: underline; }"+
                            "@media only screen and (min-width: 620px) {"+
                            "  .u-row {"+
                            "    width: 600px !important;"+
                            "  }"+
                            "  .u-row .u-col {"+
                            "    vertical-align: top;"+
                            "  }"+
                            ""+
                            "  .u-row .u-col-100 {"+
                            "    width: 600px !important;"+
                            "  }"+
                            ""+
                            "}"+
                            ""+
                            "@media (max-width: 620px) {"+
                            "  .u-row-container {"+
                            "    max-width: 100% !important;"+
                            "    padding-left: 0px !important;"+
                            "    padding-right: 0px !important;"+
                            "  }"+
                            "  .u-row .u-col {"+
                            "    min-width: 320px !important;"+
                            "    max-width: 100% !important;"+
                            "    display: block !important;"+
                            "  }"+
                            "  .u-row {"+
                            "    width: calc(100% - 40px) !important;"+
                            "  }"+
                            "  .u-col {"+
                            "    width: 100% !important;"+
                            "  }"+
                            "  .u-col > div {"+
                            "    margin: 0 auto;"+
                            "  }"+
                            "}"+
                            "body {"+
                            "  margin: 0;"+
                            "  padding: 0;"+
                            "}"+
                            ""+
                            "table,"+
                            "tr,"+
                            "td {"+
                            "  vertical-align: top;"+
                            "  border-collapse: collapse;"+
                            "}"+
                            ""+
                            "p {"+
                            "  margin: 0;"+
                            "}"+
                            ""+
                            ".ie-container table,"+
                            ".mso-container table {"+
                            "  table-layout: fixed;"+
                            "}"+
                            ""+
                            "* {"+
                            "  line-height: inherit;"+
                            "}"+
                            ""+
                            "a[x-apple-data-detectors='true'] {"+
                            "  color: inherit !important;"+
                            "  text-decoration: none !important;"+
                            "}"+
                            ""+
                            "</style>"+
                            "  "+
                            "  "+
                            ""+
                            "<!--[if !mso]><!--><link href=\"https://fonts.googleapis.com/css?family=Cabin:400,700&display=swap\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->"+
                            ""+
                            "</head>"+
                            ""+
                            "<body class=\"clean-body\" style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #f9f9f9;color: #000000\">"+
                            "  <!--[if IE]><div class=\"ie-container\"><![endif]-->"+
                            "  <!--[if mso]><div class=\"mso-container\"><![endif]-->"+
                            "  <table style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #f9f9f9;width:100%\" cellpadding=\"0\" cellspacing=\"0\">"+
                            "  <tbody>"+
                            "  <tr style=\"vertical-align: top\">"+
                            "    <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                            "    <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\" style=\"background-color: #f9f9f9;\"><![endif]-->"+
                            "    "+
                            ""+
                            "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                            "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">"+
                            "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                            "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->"+
                            "      "+
                            "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                            "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                            "  <div style=\"width: 100% !important;\">"+
                            "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                            "  "+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:20px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"+
                            "  <tr>"+
                            "    <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">"+
                            "      "+
                            "      <img align=\"center\" border=\"0\" src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-4.png?alt=media&token=98a8dbf6-6629-43af-9437-072ec060b344\" alt=\"Image\" title=\"Image\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 32%;max-width: 179.2px;\" width=\"179.2\"/>"+
                            "      "+
                            "    </td>"+
                            "  </tr>"+
                            "</table>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                            "  </div>"+
                            "</div>"+
                            "<!--[if (mso)|(IE)]></td><![endif]-->"+
                            "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                            "    </div>"+
                            "  </div>"+
                            "</div>"+
                            ""+
                            ""+
                            ""+
                            "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                            "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #003399;\">"+
                            "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                            "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #003399;\"><![endif]-->"+
                            "      "+
                            "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                            "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                            "  <div style=\"width: 100% !important;\">"+
                            "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                            "  "+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:40px 10px 10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"+
                            "  <tr>"+
                            "    <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">"+
                            "      "+
                            "      <img align=\"center\" border=\"0\" src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-5.png?alt=media&token=4511aec3-a779-48e3-8a34-ccdb214aeb4e\" alt=\"Image\" title=\"Image\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 26%;max-width: 150.8px;\" width=\"150.8\"/>"+
                            "      "+
                            "    </td>"+
                            "  </tr>"+
                            "</table>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "  <div style=\"color: #e5eaf5; line-height: 140%; text-align: center; word-wrap: break-word;\">"+
                            "    <p style=\"font-size: 14px; line-height: 140%;\"></p>"+
                            "  </div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:0px 10px 31px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "  <div style=\"color: #e5eaf5; line-height: 140%; text-align: center; word-wrap: break-word;\">"+
                            "    <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 28px; line-height: 39.2px;\"><strong><span style=\"line-height: 39.2px; font-size: 28px;\">Verify Your Password</span></strong></span></p>"+
                            "  </div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                            "  </div>"+
                            "</div>"+
                            "<!--[if (mso)|(IE)]></td><![endif]-->"+
                            "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                            "    </div>"+
                            "  </div>"+
                            "</div>"+
                            ""+
                            ""+
                            ""+
                            "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                            "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">"+
                            "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                            "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->"+
                            "      "+
                            "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                            "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                            "  <div style=\"width: 100% !important;\">"+
                            "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                            "  "+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:33px 55px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "  <div style=\"line-height: 160%; text-align: center; word-wrap: break-word;\">"+
                            "    <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 22px; line-height: 35.2px;\">Hi, </span></p>"+
                            "<p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 18px; line-height: 28.8px;\">Your account password successfully updated. Please click on the button below to verify!</span></p>"+
                            "  </div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "<div align=\"center\">"+
                            "  <!--[if mso]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;font-family:'Cabin',sans-serif;\"><tr><td style=\"font-family:'Cabin',sans-serif;\" align=\"center\"><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"\" style=\"height:46px; v-text-anchor:middle; width:234px;\" arcsize=\"8.5%\" stroke=\"f\" fillcolor=\"#ff6600\"><w:anchorlock/><center style=\"color:#FFFFFF;font-family:'Cabin',sans-serif;\"><![endif]-->"+
                            "    <a href=\"" + link + "\" target=\"_blank\" style=\"box-sizing: border-box;display: inline-block;font-family:'Cabin',sans-serif;text-decoration: none;-webkit-text-size-adjust: none;text-align: center;color: #FFFFFF; background-color: #ff6600; border-radius: 4px; -webkit-border-radius: 4px; -moz-border-radius: 4px; width:auto; max-width:100%; overflow-wrap: break-word; word-break: break-word; word-wrap:break-word; mso-border-alt: none;\">"+
                            "      <span style=\"display:block;padding:14px 44px 13px;line-height:120%;\"><span style=\"font-size: 16px; line-height: 19.2px;\"><strong><span style=\"line-height: 19.2px; font-size: 16px;\">VERIFY PASSWORD</span></strong></span></span>"+
                            "    </a>"+
                            "  <!--[if mso]></center></v:roundrect></td></tr></table><![endif]-->"+
                            "</div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:33px 55px 60px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "  <div style=\"line-height: 160%; text-align: center; word-wrap: break-word;\">"+
                            "    <p style=\"line-height: 160%; font-size: 14px;\"><span style=\"font-size: 18px; line-height: 28.8px;\">Thanks,</span></p>"+
                            "<p style=\"line-height: 160%; font-size: 14px;\"><span style=\"font-size: 18px; line-height: 28.8px;\">The Reviewia Team</span></p>"+
                            "  </div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                            "  </div>"+
                            "</div>"+
                            "<!--[if (mso)|(IE)]></td><![endif]-->"+
                            "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                            "    </div>"+
                            "  </div>"+
                            "</div>"+
                            ""+
                            ""+
                            ""+
                            "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                            "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #e5eaf5;\">"+
                            "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                            "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #e5eaf5;\"><![endif]-->"+
                            "      "+
                            "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                            "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                            "  <div style=\"width: 100% !important;\">"+
                            "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                            "  "+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:41px 55px 18px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "  <div style=\"color: #003399; line-height: 160%; text-align: center; word-wrap: break-word;\">"+
                            "    <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 20px; line-height: 32px;\"><strong>Get in touch</strong></span></p>"+
                            "<p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 16px; line-height: 25.6px; color: #000000;\"></span></p>"+
                            "<p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 16px; line-height: 25.6px; color: #000000;\">Info@Reviewia.com</span></p>"+
                            "  </div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 10px 33px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "<div align=\"center\">"+
                            "  <div style=\"display: table; max-width:146px;\">"+
                            "  <!--[if (mso)|(IE)]><table width=\"146\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"border-collapse:collapse;\" align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse; mso-table-lspace: 0pt;mso-table-rspace: 0pt; width:146px;\"><tr><![endif]-->"+
                            "  "+
                            "    "+
                            "    <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->"+
                            "    <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">"+
                            "      <tbody><tr style=\"vertical-align: top\"><td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                            "        <a href=\"https://facebook.com/\" title=\"Facebook\" target=\"_blank\">"+
                            "          <img src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-2.png?alt=media&token=c3ecb376-59ec-4245-ab29-7c8f4e126218\" alt=\"Facebook\" title=\"Facebook\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">"+
                            "        </a>"+
                            "      </td></tr>"+
                            "    </tbody></table>"+
                            "    <!--[if (mso)|(IE)]></td><![endif]-->"+
                            "    "+
                            "    <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->"+
                            "    <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">"+
                            "      <tbody><tr style=\"vertical-align: top\"><td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                            "        <a href=\"https://linkedin.com/\" title=\"LinkedIn\" target=\"_blank\">"+
                            "          <img src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-3.png?alt=media&token=77b93198-dfaa-4957-9297-e1b111945f66\" alt=\"LinkedIn\" title=\"LinkedIn\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">"+
                            "        </a>"+
                            "      </td></tr>"+
                            "    </tbody></table>"+
                            "    <!--[if (mso)|(IE)]></td><![endif]-->"+
                            "    "+
                            "    <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 0px;\" valign=\"top\"><![endif]-->"+
                            "    <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 0px\">"+
                            "      <tbody><tr style=\"vertical-align: top\"><td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                            "        <a href=\"https://email.com/\" title=\"Email\" target=\"_blank\">"+
                            "          <img src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-1.png?alt=media&token=fd742c96-e5d5-4256-9d6c-d983d0c3a637\" alt=\"Email\" title=\"Email\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">"+
                            "        </a>"+
                            "      </td></tr>"+
                            "    </tbody></table>"+
                            "    <!--[if (mso)|(IE)]></td><![endif]-->"+
                            "    "+
                            "    "+
                            "    <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                            "  </div>"+
                            "</div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                            "  </div>"+
                            "</div>"+
                            "<!--[if (mso)|(IE)]></td><![endif]-->"+
                            "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                            "    </div>"+
                            "  </div>"+
                            "</div>"+
                            ""+
                            ""+
                            ""+
                            "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                            "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #003399;\">"+
                            "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                            "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #003399;\"><![endif]-->"+
                            "      "+
                            "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                            "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                            "  <div style=\"width: 100% !important;\">"+
                            "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                            "  "+
                            "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                            "  <tbody>"+
                            "    <tr>"+
                            "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                            "        "+
                            "  <div style=\"color: #fafafa; line-height: 180%; text-align: center; word-wrap: break-word;\">"+
                            "    <p style=\"font-size: 14px; line-height: 180%;\"><span style=\"font-size: 16px; line-height: 28.8px;\">Copyrights © Reviewia All Rights Reserved</span></p>"+
                            "  </div>"+
                            ""+
                            "      </td>"+
                            "    </tr>"+
                            "  </tbody>"+
                            "</table>"+
                            ""+
                            "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                            "  </div>"+
                            "</div>"+
                            "<!--[if (mso)|(IE)]></td><![endif]-->"+
                            "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                            "    </div>"+
                            "  </div>"+
                            "</div>"+
                            ""+
                            ""+
                            "    <!--[if (mso)|(IE)]></td></tr></table><![endif]-->"+
                            "    </td>"+
                            "  </tr>"+
                            "  </tbody>"+
                            "  </table>"+
                            "  <!--[if mso]></div><![endif]-->"+
                            "  <!--[if IE]></div><![endif]-->"+
                            "</body>"+
                            "</html>");
            return token;
        } catch (MessagingException | MailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void userBlock(String email) {
        User user = userService.getUser(email);
        if(user.isLocked()) return;
        user.setLocked(true);
        userService.saveUser(user);

        try {
            emailSenderService.send(
                    email,
                    "info@reviewia.com",
                    "Reviewia - Account Status",
                    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
                            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">"+
                            "<head>"+
                            "<meta charset=\"UTF-8\">"+
                            "<meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">"+
                            "<meta name=\"x-apple-disable-message-reformatting\">"+
                            "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"+
                            "<meta content=\"telephone=no\" name=\"format-detection\">"+
                            "<title>New email template 2021-09-20</title>"+
                            "<!--[if (mso 16)]>"+
                            "<style type=\"text/css\">"+
                            "a {text-decoration: none;}"+
                            "</style>"+
                            "<![endif]-->"+
                            "<!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]-->"+
                            "<!--[if gte mso 9]>"+
                            "<xml>"+
                            "<o:OfficeDocumentSettings>"+
                            "<o:AllowPNG></o:AllowPNG>"+
                            "<o:PixelsPerInch>96</o:PixelsPerInch>"+
                            "</o:OfficeDocumentSettings>"+
                            "</xml>"+
                            "<![endif]-->"+
                            "<style type=\"text/css\">"+
                            "#outlook a {"+
                            "padding:0;"+
                            "}"+
                            ".ExternalClass {"+
                            "width:100%;"+
                            "}"+
                            ".ExternalClass,"+
                            ".ExternalClass p,"+
                            ".ExternalClass span,"+
                            ".ExternalClass font,"+
                            ".ExternalClass td,"+
                            ".ExternalClass div {"+
                            "line-height:100%;"+
                            "}"+
                            ".es-button {"+
                            "mso-style-priority:100!important;"+
                            "text-decoration:none!important;"+
                            "}"+
                            "a[x-apple-data-detectors] {"+
                            "color:inherit!important;"+
                            "text-decoration:none!important;"+
                            "font-size:inherit!important;"+
                            "font-family:inherit!important;"+
                            "font-weight:inherit!important;"+
                            "line-height:inherit!important;"+
                            "}"+
                            ".es-desk-hidden {"+
                            "display:none;"+
                            "float:left;"+
                            "overflow:hidden;"+
                            "width:0;"+
                            "max-height:0;"+
                            "line-height:0;"+
                            "mso-hide:all;"+
                            "}"+
                            ".es-button-border:hover a.es-button, .es-button-border:hover button.es-button {"+
                            "background:#ffffff!important;"+
                            "border-color:#ffffff!important;"+
                            "}"+
                            ".es-button-border:hover {"+
                            "background:#ffffff!important;"+
                            "border-style:solid solid solid solid!important;"+
                            "border-color:#3d5ca3 #3d5ca3 #3d5ca3 #3d5ca3!important;"+
                            "}"+
                            "[data-ogsb] .es-button {"+
                            "border-width:0!important;"+
                            "padding:15px 20px 15px 20px!important;"+
                            "}"+
                            "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:20px!important; text-align:center } h2 { font-size:16px!important; text-align:left } h3 { font-size:20px!important; text-align:center } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:20px!important } h2 a { text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:16px!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:10px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:12px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:14px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }"+
                            "</style>"+
                            "</head>"+
                            "<body style=\"width:100%;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">"+
                            "<div class=\"es-wrapper-color\" style=\"background-color:#FAFAFA\">"+
                            "<!--[if gte mso 9]>"+
                            "<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">"+
                            "<v:fill type=\"tile\" color=\"#fafafa\"></v:fill>"+
                            "</v:background>"+
                            "<![endif]-->"+
                            "<table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top\">"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td valign=\"top\" style=\"padding:0;Margin:0\">"+
                            "<table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td style=\"padding:0;Margin:0;background-color:#fafafa\" bgcolor=\"#fafafa\" align=\"center\">"+
                            "<table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#ffffff;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td style=\"padding:0;Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;background-color:transparent\" bgcolor=\"transparent\" align=\"left\">"+
                            "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">"+
                            "<table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:left top\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td align=\"center\" bgcolor=\"#f40b04\" style=\"padding:0;Margin:0;padding-top:15px;padding-bottom:15px\"><h1 style=\"Margin:0;line-height:30px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:20px;font-style:normal;font-weight:normal;color:#ffffff\"><strong>YOUR ACCOUNT HAS BEEN DISABLED</strong></h1></td>"+
                            "</tr>"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:40px;padding-left:40px;padding-right:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:30px;color:#333333;font-size:20px\"><strong>Why we disabled your account</strong></p></td>"+
                            "</tr>"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td align=\"left\" style=\"padding:0;Margin:0;padding-top:10px;padding-left:40px;padding-right:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#504c4c;font-size:16px\">You cannot use Reviewia because your activity on it, didn't follow Community Standards. We have already reviewed this decision and it cannot be reversed.</p></td>"+
                            "</tr>"+
                            "<tr style=\"border-collapse:collapse\">"+
                            "<td align=\"left\" style=\"Margin:0;padding-top:10px;padding-bottom:40px;padding-left:40px;padding-right:40px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:19px;color:#504c4c;font-size:16px\">Thank you, Reviewia Team</p></td>"+
                            "</tr>"+
                            "</table></td>"+
                            "</tr>"+
                            "</table></td>"+
                            "</tr>"+
                            "</table></td>"+
                            "</tr>"+
                            "</table></td>"+
                            "</tr>"+
                            "</table>"+
                            "</div>"+
                            "</body>"+
                            "</html>"
                    );
        } catch (MessagingException | MailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public User update(String email, String newFirstName, String newLastName) {
        User user = userService.getUser(email);
        if (!newFirstName.equals(user.getFirstName())) user.setFirstName(newFirstName);
        if (!newLastName.equals(user.getLastName())) user.setLastName(newLastName);
        return userService.saveUser(user);
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Email is already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(confirmationToken.getUser().getEmail());
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#003399\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#003399\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#003399\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Verification\n" +
                "                           is complete!</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "</div>";
    }

    private String buildEmail(String name, String link) {

        return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">"+
                "<head>"+
                "<!--[if gte mso 9]>"+
                "<xml>"+
                "  <o:OfficeDocumentSettings>"+
                "    <o:AllowPNG/>"+
                "    <o:PixelsPerInch>96</o:PixelsPerInch>"+
                "  </o:OfficeDocumentSettings>"+
                "</xml>"+
                "<![endif]-->"+
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"+
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"+
                "  <meta name=\"x-apple-disable-message-reformatting\">"+
                "  <!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><!--<![endif]-->"+
                "  <title></title>"+
                "  "+
                "    <style type=\"text/css\">"+
                "      table, td { color: #000000; } a { color: #0000ee; text-decoration: underline; }"+
                "@media only screen and (min-width: 620px) {"+
                "  .u-row {"+
                "    width: 600px !important;"+
                "  }"+
                "  .u-row .u-col {"+
                "    vertical-align: top;"+
                "  }"+
                ""+
                "  .u-row .u-col-100 {"+
                "    width: 600px !important;"+
                "  }"+
                ""+
                "}"+
                ""+
                "@media (max-width: 620px) {"+
                "  .u-row-container {"+
                "    max-width: 100% !important;"+
                "    padding-left: 0px !important;"+
                "    padding-right: 0px !important;"+
                "  }"+
                "  .u-row .u-col {"+
                "    min-width: 320px !important;"+
                "    max-width: 100% !important;"+
                "    display: block !important;"+
                "  }"+
                "  .u-row {"+
                "    width: calc(100% - 40px) !important;"+
                "  }"+
                "  .u-col {"+
                "    width: 100% !important;"+
                "  }"+
                "  .u-col > div {"+
                "    margin: 0 auto;"+
                "  }"+
                "}"+
                "body {"+
                "  margin: 0;"+
                "  padding: 0;"+
                "}"+
                ""+
                "table,"+
                "tr,"+
                "td {"+
                "  vertical-align: top;"+
                "  border-collapse: collapse;"+
                "}"+
                ""+
                "p {"+
                "  margin: 0;"+
                "}"+
                ""+
                ".ie-container table,"+
                ".mso-container table {"+
                "  table-layout: fixed;"+
                "}"+
                ""+
                "* {"+
                "  line-height: inherit;"+
                "}"+
                ""+
                "a[x-apple-data-detectors='true'] {"+
                "  color: inherit !important;"+
                "  text-decoration: none !important;"+
                "}"+
                ""+
                "</style>"+
                "  "+
                "  "+
                ""+
                "<!--[if !mso]><!--><link href=\"https://fonts.googleapis.com/css?family=Cabin:400,700&display=swap\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->"+
                ""+
                "</head>"+
                ""+
                "<body class=\"clean-body\" style=\"margin: 0;padding: 0;-webkit-text-size-adjust: 100%;background-color: #f9f9f9;color: #000000\">"+
                "  <!--[if IE]><div class=\"ie-container\"><![endif]-->"+
                "  <!--[if mso]><div class=\"mso-container\"><![endif]-->"+
                "  <table style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;min-width: 320px;Margin: 0 auto;background-color: #f9f9f9;width:100%\" cellpadding=\"0\" cellspacing=\"0\">"+
                "  <tbody>"+
                "  <tr style=\"vertical-align: top\">"+
                "    <td style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                "    <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\" style=\"background-color: #f9f9f9;\"><![endif]-->"+
                "    "+
                ""+
                "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">"+
                "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->"+
                "      "+
                "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                "  <div style=\"width: 100% !important;\">"+
                "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                "  "+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:20px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"+
                "  <tr>"+
                "    <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">"+
                "      "+
                "      <img align=\"center\" border=\"0\" src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-4.png?alt=media&token=98a8dbf6-6629-43af-9437-072ec060b344\" alt=\"Image\" title=\"Image\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 32%;max-width: 179.2px;\" width=\"179.2\"/>"+
                "      "+
                "    </td>"+
                "  </tr>"+
                "</table>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                "  </div>"+
                "</div>"+
                "<!--[if (mso)|(IE)]></td><![endif]-->"+
                "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                "    </div>"+
                "  </div>"+
                "</div>"+
                ""+
                ""+
                ""+
                "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #003399;\">"+
                "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #003399;\"><![endif]-->"+
                "      "+
                "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                "  <div style=\"width: 100% !important;\">"+
                "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                "  "+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:40px 10px 10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">"+
                "  <tr>"+
                "    <td style=\"padding-right: 0px;padding-left: 0px;\" align=\"center\">"+
                "      "+
                "      <img align=\"center\" border=\"0\" src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-5.png?alt=media&token=4511aec3-a779-48e3-8a34-ccdb214aeb4e\" alt=\"Image\" title=\"Image\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: inline-block !important;border: none;height: auto;float: none;width: 26%;max-width: 150.8px;\" width=\"150.8\"/>"+
                "      "+
                "    </td>"+
                "  </tr>"+
                "</table>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "  <div style=\"color: #e5eaf5; line-height: 140%; text-align: center; word-wrap: break-word;\">"+
                "    <p style=\"font-size: 14px; line-height: 140%;\"><strong>T H A N K S   F O R   S I G N I N G   U P !</strong></p>"+
                "  </div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:0px 10px 31px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "  <div style=\"color: #e5eaf5; line-height: 140%; text-align: center; word-wrap: break-word;\">"+
                "    <p style=\"font-size: 14px; line-height: 140%;\"><span style=\"font-size: 28px; line-height: 39.2px;\"><strong><span style=\"line-height: 39.2px; font-size: 28px;\">Verify Your E-mail Address </span></strong></span></p>"+
                "  </div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                "  </div>"+
                "</div>"+
                "<!--[if (mso)|(IE)]></td><![endif]-->"+
                "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                "    </div>"+
                "  </div>"+
                "</div>"+
                ""+
                ""+
                ""+
                "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #ffffff;\">"+
                "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #ffffff;\"><![endif]-->"+
                "      "+
                "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                "  <div style=\"width: 100% !important;\">"+
                "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                "  "+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:33px 55px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "  <div style=\"line-height: 160%; text-align: center; word-wrap: break-word;\">"+
                "    <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 22px; line-height: 35.2px;\">Hi, " + name + " </span></p>"+
                "<p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 18px; line-height: 28.8px;\">You're almost ready to get started. Please click on the button below to verify your email address and enjoy services with us! </span></p>"+
                "  </div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "<div align=\"center\">"+
                "  <!--[if mso]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;font-family:'Cabin',sans-serif;\"><tr><td style=\"font-family:'Cabin',sans-serif;\" align=\"center\"><v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" href=\"\" style=\"height:46px; v-text-anchor:middle; width:234px;\" arcsize=\"8.5%\" stroke=\"f\" fillcolor=\"#ff6600\"><w:anchorlock/><center style=\"color:#FFFFFF;font-family:'Cabin',sans-serif;\"><![endif]-->"+
                "    <a href=\"" + link + "\" target=\"_blank\" style=\"box-sizing: border-box;display: inline-block;font-family:'Cabin',sans-serif;text-decoration: none;-webkit-text-size-adjust: none;text-align: center;color: #FFFFFF; background-color: #ff6600; border-radius: 4px; -webkit-border-radius: 4px; -moz-border-radius: 4px; width:auto; max-width:100%; overflow-wrap: break-word; word-break: break-word; word-wrap:break-word; mso-border-alt: none;\">"+
                "      <span style=\"display:block;padding:14px 44px 13px;line-height:120%;\"><span style=\"font-size: 16px; line-height: 19.2px;\"><strong><span style=\"line-height: 19.2px; font-size: 16px;\">VERIFY YOUR EMAIL</span></strong></span></span>"+
                "    </a>"+
                "  <!--[if mso]></center></v:roundrect></td></tr></table><![endif]-->"+
                "</div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:33px 55px 60px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "  <div style=\"line-height: 160%; text-align: center; word-wrap: break-word;\">"+
                "    <p style=\"line-height: 160%; font-size: 14px;\"><span style=\"font-size: 18px; line-height: 28.8px;\">Thanks,</span></p>"+
                "<p style=\"line-height: 160%; font-size: 14px;\"><span style=\"font-size: 18px; line-height: 28.8px;\">The Reviewia Team</span></p>"+
                "  </div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                "  </div>"+
                "</div>"+
                "<!--[if (mso)|(IE)]></td><![endif]-->"+
                "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                "    </div>"+
                "  </div>"+
                "</div>"+
                ""+
                ""+
                ""+
                "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #e5eaf5;\">"+
                "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #e5eaf5;\"><![endif]-->"+
                "      "+
                "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                "  <div style=\"width: 100% !important;\">"+
                "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                "  "+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:41px 55px 18px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "  <div style=\"color: #003399; line-height: 160%; text-align: center; word-wrap: break-word;\">"+
                "    <p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 20px; line-height: 32px;\"><strong>Get in touch</strong></span></p>"+
                "<p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 16px; line-height: 25.6px; color: #000000;\"></span></p>"+
                "<p style=\"font-size: 14px; line-height: 160%;\"><span style=\"font-size: 16px; line-height: 25.6px; color: #000000;\">Info@Reviewia.com</span></p>"+
                "  </div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px 10px 33px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "<div align=\"center\">"+
                "  <div style=\"display: table; max-width:146px;\">"+
                "  <!--[if (mso)|(IE)]><table width=\"146\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"border-collapse:collapse;\" align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse; mso-table-lspace: 0pt;mso-table-rspace: 0pt; width:146px;\"><tr><![endif]-->"+
                "  "+
                "    "+
                "    <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->"+
                "    <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">"+
                "      <tbody><tr style=\"vertical-align: top\"><td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                "        <a href=\"https://facebook.com/\" title=\"Facebook\" target=\"_blank\">"+
                "          <img src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-2.png?alt=media&token=c3ecb376-59ec-4245-ab29-7c8f4e126218\" alt=\"Facebook\" title=\"Facebook\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">"+
                "        </a>"+
                "      </td></tr>"+
                "    </tbody></table>"+
                "    <!--[if (mso)|(IE)]></td><![endif]-->"+
                "    "+
                "    <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 17px;\" valign=\"top\"><![endif]-->"+
                "    <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 17px\">"+
                "      <tbody><tr style=\"vertical-align: top\"><td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                "        <a href=\"https://linkedin.com/\" title=\"LinkedIn\" target=\"_blank\">"+
                "          <img src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-3.png?alt=media&token=77b93198-dfaa-4957-9297-e1b111945f66\" alt=\"LinkedIn\" title=\"LinkedIn\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">"+
                "        </a>"+
                "      </td></tr>"+
                "    </tbody></table>"+
                "    <!--[if (mso)|(IE)]></td><![endif]-->"+
                "    "+
                "    <!--[if (mso)|(IE)]><td width=\"32\" style=\"width:32px; padding-right: 0px;\" valign=\"top\"><![endif]-->"+
                "    <table align=\"left\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"32\" height=\"32\" style=\"border-collapse: collapse;table-layout: fixed;border-spacing: 0;mso-table-lspace: 0pt;mso-table-rspace: 0pt;vertical-align: top;margin-right: 0px\">"+
                "      <tbody><tr style=\"vertical-align: top\"><td align=\"left\" valign=\"middle\" style=\"word-break: break-word;border-collapse: collapse !important;vertical-align: top\">"+
                "        <a href=\"https://email.com/\" title=\"Email\" target=\"_blank\">"+
                "          <img src=\"https://firebasestorage.googleapis.com/v0/b/blog-8c3fd.appspot.com/o/image-1.png?alt=media&token=fd742c96-e5d5-4256-9d6c-d983d0c3a637\" alt=\"Email\" title=\"Email\" width=\"32\" style=\"outline: none;text-decoration: none;-ms-interpolation-mode: bicubic;clear: both;display: block !important;border: none;height: auto;float: none;max-width: 32px !important\">"+
                "        </a>"+
                "      </td></tr>"+
                "    </tbody></table>"+
                "    <!--[if (mso)|(IE)]></td><![endif]-->"+
                "    "+
                "    "+
                "    <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                "  </div>"+
                "</div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                "  </div>"+
                "</div>"+
                "<!--[if (mso)|(IE)]></td><![endif]-->"+
                "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                "    </div>"+
                "  </div>"+
                "</div>"+
                ""+
                ""+
                ""+
                "<div class=\"u-row-container\" style=\"padding: 0px;background-color: transparent\">"+
                "  <div class=\"u-row\" style=\"Margin: 0 auto;min-width: 320px;max-width: 600px;overflow-wrap: break-word;word-wrap: break-word;word-break: break-word;background-color: #003399;\">"+
                "    <div style=\"border-collapse: collapse;display: table;width: 100%;background-color: transparent;\">"+
                "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding: 0px;background-color: transparent;\" align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:600px;\"><tr style=\"background-color: #003399;\"><![endif]-->"+
                "      "+
                "<!--[if (mso)|(IE)]><td align=\"center\" width=\"600\" style=\"width: 600px;padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\" valign=\"top\"><![endif]-->"+
                "<div class=\"u-col u-col-100\" style=\"max-width: 320px;min-width: 600px;display: table-cell;vertical-align: top;\">"+
                "  <div style=\"width: 100% !important;\">"+
                "  <!--[if (!mso)&(!IE)]><!--><div style=\"padding: 0px;border-top: 0px solid transparent;border-left: 0px solid transparent;border-right: 0px solid transparent;border-bottom: 0px solid transparent;\"><!--<![endif]-->"+
                "  "+
                "<table style=\"font-family:'Cabin',sans-serif;\" role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">"+
                "  <tbody>"+
                "    <tr>"+
                "      <td style=\"overflow-wrap:break-word;word-break:break-word;padding:10px;font-family:'Cabin',sans-serif;\" align=\"left\">"+
                "        "+
                "  <div style=\"color: #fafafa; line-height: 180%; text-align: center; word-wrap: break-word;\">"+
                "    <p style=\"font-size: 14px; line-height: 180%;\"><span style=\"font-size: 16px; line-height: 28.8px;\">Copyrights © Reviewia All Rights Reserved</span></p>"+
                "  </div>"+
                ""+
                "      </td>"+
                "    </tr>"+
                "  </tbody>"+
                "</table>"+
                ""+
                "  <!--[if (!mso)&(!IE)]><!--></div><!--<![endif]-->"+
                "  </div>"+
                "</div>"+
                "<!--[if (mso)|(IE)]></td><![endif]-->"+
                "      <!--[if (mso)|(IE)]></tr></table></td></tr></table><![endif]-->"+
                "    </div>"+
                "  </div>"+
                "</div>"+
                ""+
                ""+
                "    <!--[if (mso)|(IE)]></td></tr></table><![endif]-->"+
                "    </td>"+
                "  </tr>"+
                "  </tbody>"+
                "  </table>"+
                "  <!--[if mso]></div><![endif]-->"+
                "  <!--[if IE]></div><![endif]-->"+
                "</body>"+
                "</html>";
    }
}
