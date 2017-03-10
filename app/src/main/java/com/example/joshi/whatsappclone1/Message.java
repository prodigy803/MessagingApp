package com.example.joshi.whatsappclone1;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshi on 28-02-2017.
 */
//this class is needed for sending the data to the server
public class Message {

    public String ReceiverEmail;
    public String SenderEmail;
    public String Message;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Message(String SenderEmail,String ReceiverEmail, String Message){
        this.SenderEmail = SenderEmail;
        this.ReceiverEmail = ReceiverEmail;
        this.Message = Message;

    }
    public String returnSenderEmail(){
        return SenderEmail;
    }

    public String returnReceiverEmail(){
        return ReceiverEmail;
    }
    public String returnMessage(){
        return Message;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Message", Message);
        //result.put("SenderEmail", SenderEmail);
        //result.put("ReceiverEmail", ReceiverEmail);
        return result;
    }
    public Map<String, Object> MessageMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Message", Message);
        return result;
    }
}