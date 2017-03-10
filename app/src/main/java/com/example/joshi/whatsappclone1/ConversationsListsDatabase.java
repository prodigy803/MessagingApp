package com.example.joshi.whatsappclone1;

/**
 * Created by joshi on 10-03-2017.
 */

public class ConversationsListsDatabase {
    String ConversationWith;
    public ConversationsListsDatabase(){}
    public ConversationsListsDatabase(String ConversationWith){
        this.ConversationWith = ConversationWith;
    }
    public String getConversationWith(){return this.ConversationWith;}
    public void setConversationWith(String ConversationWith){this.ConversationWith=ConversationWith;}
}
