package com.example.joshi.whatsappclone1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAcitivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth auth;
    public String receiverEmail, senderEmail, receiverEmailFromChatFragment, senderEmailFromChatFragment, chatmessage = "", KeyToMessageToBeSent;

    ListView chatListView;
    ArrayList<String> list;
    ArrayAdapter arrayAdapter;
    Boolean DataFromProfile;

    EditText chatEditText;

    public void sendChat(View view) {

        chatEditText = (EditText) findViewById(R.id.chatEditText);
        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String messageToBeSent = chatEditText.getText().toString();
        senderEmail = user.getEmail();
        //here we get the current users email, who is about to send te email.
        //This module essentially pushes the data given by the user to the firebase database.
        //Here we are essentially pushing the data in one continuous string.
        Log.i("String passed", messageToBeSent);//messageToBeSent is proper
        Log.i("Boolean IS", String.valueOf(DataFromProfile));
        DatabaseHandler db = new DatabaseHandler(this);


        if (DataFromProfile) {//if is executed, if we try to connect for the first time.

            //Storing the message locally on the phone.
            Log.i("SenderEmail", senderEmail);
            Log.i("SenderEmail", receiverEmail);
            Log.i("SenderEmail", messageToBeSent);
            db.addMessage(new MessageInSql(senderEmail, receiverEmail, messageToBeSent));
            db.addConversationItem(new ConversationsListsDatabase("Conversation with "+receiverEmail));
            list.add(messageToBeSent);
            arrayAdapter.notifyDataSetChanged();
            chatEditText.setText("");

            //we are doing the following because we want the receiver to receive all the messages in a sequence.
            chatmessage = chatmessage.concat("/" + senderEmail + "/" + messageToBeSent);
            Message message1 = new Message(senderEmail, receiverEmail, chatmessage);
            Map<String, Object> postValues = message1.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(receiverEmail.replace(".", ",") + "/" + senderEmail.toString().replace(".", ","), postValues);
            mDatabase.updateChildren(childUpdates);
            DataFromProfile = false;
        } else {

            //That is when we are coming from the Fragment Chat Activity
            //Storing the data in the local database;
            db.addMessage(new MessageInSql(senderEmail, receiverEmail, messageToBeSent));
            list.add(messageToBeSent);
            arrayAdapter.notifyDataSetChanged();
            chatEditText.setText("");
            //THIS BIT IS FOR SENDING THE MESSAGE TO THE SERVER, Cause we have to read and concatenate
            // the data and thats why we are using addChildEventLister

            mDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.i("The chat Message","Does it work");//
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                        Log.i("Strings", String.valueOf(dataSnapshot.getChildren()));
                        chatmessage = chatmessage.concat("/" + senderEmail.toString() + "/" + messageToBeSent);
                        //Log.i("ChatMessagConcat",chatmessage);Its working correctly
                        Message message1 = new Message(senderEmail, receiverEmail, chatmessage);

                        Map<String, Object> postValues1 = message1.toMap();
                        Map<String, Object> childUpdates1 = new HashMap<>();

                        //We arent using key as the parameter to search anymore.
                        //childUpdates1.put(senderEmail.replace(".", ",") + "/" + key, postValues1);
                        //childUpdates1.put(receiverEmailFromChatFragment.replace(".", ",") + "/" + key, postValues1);

                        childUpdates1.put(receiverEmail.replace(".", ",") + "/" + senderEmail.toString().replace(".", ","), postValues1);
                        mDatabase.updateChildren(childUpdates1);

                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);
        setTitle("Chat with" + receiverEmail);
        final DatabaseHandler db = new DatabaseHandler(this);
        //Checking if the user is presnet or not

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ChatAcitivity.this, LoginActivity.class));
                    //finish();
                }
            }
        };

        //Basic initilialization of the App:

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        chatListView = (ListView) findViewById(R.id.chatListView);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        chatListView.setAdapter(arrayAdapter);

        //Getting intent data from other apps
        final Intent intent = getIntent();
        //Now if we are coming from the Profile Activity, we will get the following value as true
        //or false if we are coming from chatFragment
        DataFromProfile = intent.getExtras().getBoolean("DataFromProfile");

        //First time chat initialization:
        if (DataFromProfile) {
            receiverEmail = intent.getStringExtra("ReceiverEmail");
            senderEmail = user.getEmail();
        } else {
            receiverEmailFromChatFragment = intent.getStringExtra("userEmailFromChatFragment");
            senderEmailFromChatFragment = user.getEmail();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        // Here we fill up the chat:
        //Take the messages from the server, and display them here:
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    Log.i("All the Receivers",dataSnapshot.getKey());//--Works
                    //Log.i("All the Senders",postSnapShot.getKey());//--Works

                    //CorrectImplementation
                    if(dataSnapshot.getKey().replace(",",".").matches(user.getEmail())){

                        //Log.i("TheMessageIs", String.valueOf(postSnapShot.child("Message").getValue()));//gets the message
                        //String s1 = String.valueOf(postSnapShot.child("Message").getValue());
                        //Log.i("TheValueOFS1",s1);
                        //String s4 =user.getEmail();
                        //do{
                        String[] parts = String.valueOf(postSnapShot.child("Message").getValue()).split("/");
                        int lengthOfParts = parts.length;
                        for(int i=0;i<lengthOfParts;i++){
                            if(i%2==0){
                                //SendersEmail:
                                //We get the receiver:
                                Log.i("Part1",parts[i]);
                                if (DataFromProfile) {
                                    //receiverEmail = intent.getStringExtra("ReceiverEmail");
                                    //senderEmail = user.getEmail();
                                    if(parts[i].matches("")){/*BugSolved*/}
                                    else{
                                        db.addMessage(new MessageInSql(receiverEmail,senderEmail,">"+parts[i]));
                                        list.add(">"+parts[i]);
                                        arrayAdapter.notifyDataSetChanged();
                                        senderEmail.replace(".",",");
                                        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
                                        mDatabase.child(senderEmail.replace(".",",")).setValue(null);

                                    }
                                } else {
                                    //receiverEmailFromChatFragment = intent.getStringExtra("userEmailFromChatFragment");
                                    //senderEmailFromChatFragment = user.getEmail();
                                    if(parts[i].matches("")){/*BugSolved*/}
                                    else{
                                        db.addMessage(new MessageInSql(receiverEmailFromChatFragment,senderEmailFromChatFragment,">"+parts[i]));
                                        list.add(">"+parts[i]);
                                        arrayAdapter.notifyDataSetChanged();
                                        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
                                        mDatabase.child(senderEmailFromChatFragment.replace(".",",")).setValue(null);


                                    }
                                }

                            }else{
                                //Message:


                            }
                            Log.i("Part",parts[i]);
                        }
                            //String s2 = s1.substring(s4.indexOf("/"), s4.indexOf("/"));
                            //Log.i("TheFirstString",String.valueOf(s2));
                            //ArrayList<String> messages = new ArrayList<String>();
                            //Log.i("TheMessageIs",s2);
                            //messages.add(s2);
                            //int numberOfMessages = messages.size();
                            //Log.i("TheNumberOfMessgesAres", String.valueOf(numberOfMessages));
                        //    s1 = s1.replace(s2,"");
                        //}while(s1.length>= 0);
                        //for (DataSnapshot postSnapShot1 : postSnapShot.getChildren()){
                        //    Log.i("Whats THus",postSnapShot1.getKey());
                        //}

                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
            List<MessageInSql> messageInSqls = db.getAllMessages();
            Log.i("AreWeJere ","yes");
            for (MessageInSql cn : messageInSqls) {
                list.add(cn.getMessage());
                arrayAdapter.notifyDataSetChanged();
                //Log.i("Yolo",cn.getMessage());
                //Log.i("Yolo2",cn.getSenderEmail());
                //Log.i("Yolo3",cn.getReceiverEmail());
            }
    }
}