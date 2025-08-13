package com.example.dongyucar.management.util;


import com.example.dongyucar.management.controller.dto.ContactRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactEmailUtil {

    private final JavaMailSender mailSender;

    public void sendContactEmail(ContactRequest request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo("badasslee7@naver.com");
        helper.setFrom("badasslee7@naver.com");
        helper.setSubject("새로운 온라인 문의가 도착했습니다.");
        String content = getString(request);

        helper.setText(content, true);

        mailSender.send(message);
    }

    private static String getString(final ContactRequest request) {
        return "<h2>온라인 문의 내용</h2>" +
                "<p><strong>성명:</strong> " + request.getCustomerName() + "</p>" +
                "<p><strong>연락처:</strong> " + request.getCustomerPhone() + "</p>" +
                "<p><strong>차종:</strong><br>" + request.getDesiredModel() + "</p>";
    }
}
