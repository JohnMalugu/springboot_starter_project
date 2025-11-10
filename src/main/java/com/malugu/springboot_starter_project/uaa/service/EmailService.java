package com.malugu.springboot_starter_project.uaa.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tz.go.ega.utils.TokenConfigurationProperties;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final TokenConfigurationProperties tokenConfigurationProperties;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${app.name}")
	private String appName;

	@Value("${app.base-url}")
	private String appBaseUrl;

	@Value("${app.website-url}")
	private String appWebsiteUrl;


	/**
	 * Sends a password reset email to the specified recipient.
	 *
	 * @param toEmail    The recipient's email address.
	 * @param username   The username associated with the reset request.
	 * @param resetToken The signed password reset token.
	 * @throws MessagingException If there's an error creating or sending the email.
	 */
	public void sendPasswordResetEmail(String toEmail, String username, String resetToken) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setFrom(fromEmail);
		helper.setTo(toEmail);
		helper.setSubject("Password Reset Request for Your " + appName + " Account");

		String resetLink = appBaseUrl + "/auth/reset-password?token=" + resetToken; // Assuming a front-end form
																							// URL

		String htmlContent = generatePasswordResetEmailHtml(username, resetLink);
		helper.setText(htmlContent, true); // true indicates HTML content

		mailSender.send(message);
	}

	private String generatePasswordResetEmailHtml(String username, String resetLink) {
		// This is the HTML content from the immersive document.
		// Replace placeholders with actual values.
		String htmlTemplate = """
				<!DOCTYPE html>
				<html lang="en">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>Password Reset Request</title>
				    <style>
				        /* Basic Reset & Body Styles */
				        body {
				            margin: 0;
				            padding: 0;
				            font-family: 'Inter', sans-serif;
				            background-color: #f4f7f6;
				            color: #333333;
				            -webkit-text-size-adjust: 100%;
				            -ms-text-size-adjust: 100%;
				            width: 100% !important;
				        }
				        table {
				            border-collapse: collapse;
				            mso-table-lspace: 0pt;
				            mso-table-rspace: 0pt;
				        }
				        td {
				            padding: 0;
				        }
				        img {
				            border: 0;
				            height: auto;
				            line-height: 100%;
				            outline: none;
				            text-decoration: none;
				            -ms-interpolation-mode: bicubic;
				        }
				        a {
				            text-decoration: none;
				            color: #1a73e8;
				        }

				        /* Container & Content Area */
				        .container {
				            max-width: 600px;
				            margin: 20px auto;
				            background-color: #ffffff;
				            border-radius: 12px;
				            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
				            overflow: hidden;
				        }
				        .header {
				            background-color: #1a73e8;
				            padding: 30px 20px;
				            text-align: center;
				            border-top-left-radius: 12px;
				            border-top-right-radius: 12px;
				        }
				        .header h1 {
				            margin: 0;
				            color: #ffffff;
				            font-size: 28px;
				            font-weight: 700;
				        }
				        .content {
				            padding: 40px 30px;
				            line-height: 1.6;
				            text-align: left;
				        }
				        .content p {
				            margin-bottom: 15px;
				            font-size: 16px;
				            color: #555555;
				        }
				        .content strong {
				            color: #333333;
				        }

				        /* Call to Action Button */
				        .button-container {
				            text-align: center;
				            margin-top: 30px;
				            margin-bottom: 30px;
				        }
				        .button {
				            display: inline-block;
				            padding: 15px 30px;
				            background-color: #1a73e8;
				            color: #ffffff;
				            font-size: 18px;
				            font-weight: 600;
				            text-decoration: none;
				            border-radius: 8px;
				            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
				            transition: background-color 0.3s ease;
				        }
				        .button:hover {
				            background-color: #155bb5;
				        }

				        /* Footer */
				        .footer {
				            background-color: #e9ecef;
				            padding: 20px 30px;
				            text-align: center;
				            font-size: 12px;
				            color: #777777;
				            border-bottom-left-radius: 12px;
				            border-bottom-right-radius: 12px;
				        }
				        .footer p {
				            margin: 0;
				            line-height: 1.5;
				        }
				        .footer a {
				            color: #777777;
				            text-decoration: underline;
				        }

				        /* Responsive Adjustments */
				        @media only screen and (max-width: 600px) {
				            .container {
				                margin: 10px;
				                border-radius: 0;
				            }
				            .header {
				                padding: 20px 15px;
				            }
				            .header h1 {
				                font-size: 24px;
				            }
				            .content {
				                padding: 30px 20px;
				            }
				            .content p {
				                font-size: 15px;
				            }
				            .button {
				                padding: 12px 25px;
				                font-size: 16px;
				            }
				            .footer {
				                padding: 15px 20px;
				            }
				        }
				    </style>
				</head>
				<body>
				    <table width="100%" border="0" cellspacing="0" cellpadding="0">
				        <tr>
				            <td align="center">
				                <table class="container" width="100%" border="0" cellspacing="0" cellpadding="0">
				                    <!-- Header -->
				                    <tr>
				                        <td class="header">
				                            <h1>Password Reset Request</h1>
				                        </td>
				                    </tr>
				                    <!-- Content -->
				                    <tr>
				                        <td class="content">
				                            <p>Hello <strong>[USERNAME]</strong>,</p>
				                            <p>We received a request to reset the password for your account. If you did not make this request, please safely ignore this email.</p>
				                            <p>To reset your password, please click the button below. This link is valid for <strong>[TOKEN_VALID_TIME]</strong>.</p>

				                            <div class="button-container">
				                                <a href="[RESET_LINK]" class="button">Reset Your Password</a>
				                            </div>

				                            <p>If the button above does not work, you can copy and paste the following link into your web browser:</p>
				                            <p style="word-break: break-all; font-size: 14px; color: #1a73e8;">[RESET_LINK]</p>

				                            <p>For security reasons, this link will expire shortly. If you need to reset your password again, please visit our website and request a new link.</p>
				                            <p>Thank you,</p>
				                            <p>The [YOUR_APP_NAME] Team</p>
				                        </td>
				                    </tr>
				                    <!-- Footer -->
				                    <tr>
				                        <td class="footer">
				                            <p>&copy; [YEAR] [YOUR_APP_NAME]. All rights reserved.</p>
				                            <p>This is an automated email, please do not reply.</p>
				                            <p><a href="[YOUR_WEBSITE_URL]">Visit our website</a></p>
				                        </td>
				                    </tr>
				                </table>
				            </td>
				        </tr>
				    </table>
				</body>
				</html>
				""";

		return htmlTemplate.replace("[USERNAME]", username).replace("[RESET_LINK]", resetLink)
				.replace("[YOUR_APP_NAME]", appName).replace("[YOUR_WEBSITE_URL]", appWebsiteUrl)
				.replace("[YEAR]", String.valueOf(LocalDate.now().getYear()))
				.replace("[TOKEN_VALID_TIME]",
						tokenConfigurationProperties.getLifespan().getPasswordResetLifespan().toHours() + " hours");
	}
}
