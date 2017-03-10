package com.example.joshi.whatsappclone1;

/**
 * Created by joshi on 08-03-2017.
 */
//Associated with DatabaseHandler2
//this class is needed for storing the data to the local database:
public class MessageInSql {
    String SenderEmail,ReceiverEmail,Message;

    String Table_Name;
    public MessageInSql(){

    }
    public MessageInSql(String senderEmail,String receiverEmail,String Message){
        this.SenderEmail=senderEmail;
        this.ReceiverEmail = receiverEmail;
        this.Message = Message;
    }
    public void setTableName(String nameOfTable){
        this.Table_Name = nameOfTable ;
    }
    public void setSenderEmail(String senderEmail){
        this.SenderEmail = senderEmail;
    }
    public void setReceiverEmail(String receiverEmail){
        this.ReceiverEmail = receiverEmail;
    }
    public void setMessage(String message){
        this.Message = message;
    }
    public String getSenderEmail(){
        return this.SenderEmail;
    }
    public String getReceiverEmail(){
        return this.ReceiverEmail;
    }
    public String getMessage(){
        return this.Message;
    }
}
