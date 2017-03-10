package com.example.joshi.whatsappclone1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
//Associated with MessagesInSql
public class DatabaseHandler extends SQLiteOpenHelper{
    // Database Version

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Conversations";
    // Table Columns names
    String SenderEmail = "SenderEmail";
    String ReceiverEmail = "ReceiverEmail";
    String Message = "Message";
    String Conversation = "Conversation";

    // Table name
    String Table_Name2 = "Conversations";
    String Table_Name1="Messages";
    //Table Creation
    String CREATE_Conversation_TABLE = "CREATE TABLE IF NOT EXISTS '"+ Table_Name1 +"' ("+ SenderEmail +" VARCHAR ,"+ ReceiverEmail +" VARCHAR,"+ Message +" VARCHAR );";

    String CREATE_ConversationsLists_TABLE = "CREATE IF NOT EXISTS TABLE "+Table_Name2+" (Conversation1 VARCHAR)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_Conversation_TABLE);
        db.execSQL(CREATE_ConversationsLists_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name1);
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name2);
        // Create tables again
        onCreate(db);
    }
    void addMessage(MessageInSql messageInSql){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SenderEmail,messageInSql.getSenderEmail());
        values.put(ReceiverEmail,messageInSql.getReceiverEmail());
        values.put(Message,messageInSql.getMessage());
        db.insert(Table_Name1,null,values);
        db.close();
    }
    public List<MessageInSql> getAllMessages(){
        SQLiteDatabase db = this.getWritableDatabase();

        List<MessageInSql> messages = new ArrayList<MessageInSql>();
        String selectQuery = "SELECT * FROM " + Table_Name1;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                MessageInSql messageInSql = new MessageInSql();
                messageInSql.setSenderEmail(cursor.getString(0));
                messageInSql.setReceiverEmail(cursor.getString(1));
                messageInSql.setMessage(cursor.getString(2));
                messages.add(messageInSql);
            }while(cursor.moveToNext());
        }
        return messages;
    }

    void addConversationItem(ConversationsListsDatabase conversationsListsDatabase) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Conversation, conversationsListsDatabase.getConversationWith()); // Conversation Name
        // Inserting Row
        db.insert(Table_Name2, null, values);
        db.close(); // Closing database connection
    }
    // Getting All Conversations
    public List<ConversationsListsDatabase> getAllConversationsNames() {
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        List<ConversationsListsDatabase> conversationsList = new ArrayList<ConversationsListsDatabase>();
        String selectQuery = "SELECT  * FROM "+ Table_Name2;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ConversationsListsDatabase conversationsListsDatabase = new ConversationsListsDatabase();
                conversationsListsDatabase.setConversationWith(cursor.getString(0));
                // Adding contact to list
                conversationsList.add(conversationsListsDatabase);
            } while (cursor.moveToNext());
        }

        // return contact list
        return conversationsList;
    }
}
