package com.posit.posit.global.sms;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {
    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.sms-from}")
    private String from;

    public void sendSms(String to, String code) {

        DefaultMessageService messageService =
                SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);

        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setText("[POSiT] 인증번호는 [" + code + "] 입니다. (3분 유효)");

        try {
            messageService.send(message);
        } catch (SolapiMessageNotReceivedException e) {
            throw new RuntimeException("SMS 발송 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("SMS 전송 중 오류 발생: " + e.getMessage());
        }
    }
}
