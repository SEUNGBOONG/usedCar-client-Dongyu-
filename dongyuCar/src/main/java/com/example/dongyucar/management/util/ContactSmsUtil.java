package com.example.dongyucar.management.util;

import com.example.dongyucar.aligo.AligoSmsUtil;
import com.example.dongyucar.management.controller.dto.ContactRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactSmsUtil {

    private final AligoSmsUtil aligoSmsUtil;

    @Value("${aligo.contact.receiver:01048744447}")
    private String receiver;

    public void sendContactSms(ContactRequest request) {
        aligoSmsUtil.sendSms(receiver, buildMessage(request));
    }

    private String buildMessage(ContactRequest request) {
        return "[승계 상담 신청]\n"
                + "이름: " + nullSafe(request.getCustomerName()) + "\n"
                + "전화번호: " + nullSafe(request.getCustomerPhone()) + "\n"
                + "차종: " + nullSafe(request.getDesiredModel());
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
