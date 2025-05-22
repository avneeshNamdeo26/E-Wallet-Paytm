package com.example.service;

import com.example.CommonConstants;
import com.example.UserIdentifier;
import com.example.model.Wallet;
import com.example.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = CommonConstants.USER_CREATED_TOPIC, groupId = "EWallet_Group")
    public void createWallet(String message) throws ParseException {

        JSONObject data = (JSONObject) new JSONParser().parse(message);

        Long userId = (Long) data.get(CommonConstants.USER_CREATED_TOPIC_USERID);
        String phoneNumber = (String) data.get(CommonConstants.USER_CREATED_TOPIC_PHONE_NUMBER);
        String identifierKey = (String) data.get(CommonConstants.USER_CREATED_TOPIC_IDENTIFIER_KEY);
        String identifierValue = (String) data.get(CommonConstants.USER_CREATED_TOPIC_IDENTIFIER_VALUE);

        Wallet wallet = Wallet.builder()
                        .useId(userId).phoneNumber(phoneNumber)
                        .userIdentifier(UserIdentifier.valueOf(identifierKey))
                        .identifierValue(identifierValue)
                        .balance(10.0)
                        .build();
        walletRepository.save(wallet);
    }

    @KafkaListener(topics = CommonConstants.TRANSACTION_CREATED_TOPIC, groupId = "EWallet_Group")
    public void updateWalletForTransaction(String message) throws ParseException, JsonProcessingException {

        JSONObject data = (JSONObject) new JSONParser().parse(message);

        String senderId = (String) data.get(CommonConstants.TRANSACTION_CREATED_TOPIC_SENDER);
        String receiverId = (String) data.get(CommonConstants.TRANSACTION_CREATED_TOPIC_RECEIVER);
        Double amount = (Double) data.get(CommonConstants.TRANSACTION_CREATED_TOPIC_AMOUNT);
        String transactionId = (String) data.get(CommonConstants.TRANSACTION_CREATED_TOPIC_TRANSACTIONId);


        //Check if sender's and receiver's wallets are in active state
        Wallet senderWallet = walletRepository.findByPhoneNumber(senderId);
        Wallet receiverWallet = walletRepository.findByPhoneNumber(receiverId);

        //publish the event after validating and updating wallets of sender and receiver
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.TRANSACTION_CREATED_TOPIC_SENDER,senderId);
        jsonObject.put(CommonConstants.TRANSACTION_CREATED_TOPIC_RECEIVER,receiverId);
        jsonObject.put(CommonConstants.TRANSACTION_CREATED_TOPIC_AMOUNT,amount);
        jsonObject.put(CommonConstants.TRANSACTION_CREATED_TOPIC_TRANSACTIONId,transactionId);

        if(senderWallet == null || receiverWallet == null || senderWallet.getBalance()<amount){
            jsonObject.put(CommonConstants.WALLET_UPDATED_TOPIC_STATUS,CommonConstants.WALLET_UPDATE_STATUS_FAILED);
        }
        else{
            //debit from sender
            walletRepository.updateWallet(senderId,0-amount);

            //credit in receiver
            walletRepository.updateWallet(receiverId, amount);
            jsonObject.put("walletUpdateStatus",CommonConstants.WALLET_UPDATE_STATUS_SUCCESS);
        }
        kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC,
                objectMapper.writeValueAsString(jsonObject));
    }

}
