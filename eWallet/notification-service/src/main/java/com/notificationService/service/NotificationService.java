package com.notificationService.service;

import com.example.CommonConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private  JavaMailSender mailSender;

    @KafkaListener(topics = CommonConstants.TRANSACTION_COMPLETED_TOPIC, groupId = "EWallet_Group")
    public void sendEmailAfterTransactionUpdate(ConsumerRecord<String, String> record) throws ParseException, JsonProcessingException {

        String message = record.value();
        log.info("Received message: {}", message);

        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(message);

        String senderEmail = (String) data.get(CommonConstants.TRANSACTION_COMPLETED_TOPIC_SENDER_EMAIL);
        String senderMessage = (String) data.get(CommonConstants.TRANSACTION_COMPLETED_TOPIC_SENDER_MESSAGE);
        String receiverEmail = (String) data.get(CommonConstants.TRANSACTION_COMPLETED_TOPIC_RECEIVER_EMAIL);
        String receiverMessage = (String) data.get(CommonConstants.TRANSACTION_COMPLETED_TOPIC_RECEIVER_MESSAGE);

        try {
            if (senderEmail != null && senderMessage != null) {
                sendEmail(senderEmail, "Transaction successful", senderMessage);
                log.info("Sent email to sender: {}", senderEmail);
            }

            if (receiverEmail != null && receiverMessage != null) {
                sendEmail(receiverEmail, "Money Received", receiverMessage);
                log.info("Sent email to receiver: {}", receiverEmail);
            }
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }

    }

    private void sendEmail(String toEmail,
                           String subject,
                           String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("avneeshnamdeoapple@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }


}
