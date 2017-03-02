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
import java.util.Map;

public class ChatAcitivity extends AppCompatActivity  {

    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth auth;
    public String receiverEmail,senderEmail,receiverEmailFromChatFragment,senderEmailFromChatFragment,chatmessage="",KeyToMessageToBeSent;

    ListView chatListView;
    ArrayList<String> list;
    ArrayAdapter arrayAdapter;
    Boolean DataFromProfile;

    EditText chatEditText;
    public void sendChat(View view){

        chatEditText = (EditText)findViewById(R.id.chatEditText);
        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        senderEmail = user.getEmail();//here we get the current users email, who is about to send te email.
        //This module essentially pushes the data given by the user to the firebase database.
        //Here we are essentially pushing the data in one continuous string.
        final String messageToBeSent = chatEditText.getText().toString();
        Log.i("String passed",messageToBeSent);//messageToBeSent is proper
        Log.i("Boolean IS", String.valueOf(DataFromProfile));
        if(DataFromProfile) {
            chatmessage = chatmessage.concat("/" + senderEmail.toString() + "/" + messageToBeSent);
            Message message1 = new Message(senderEmail,receiverEmail, chatmessage);
            //mDatabase.child(senderEmail.replace(".", ",")).push().setValue(message1);
            //mDatabase.child(receiverEmail.replace(".", ",")).push().setValue(message1);
            //
            String key = mDatabase.child(senderEmail.replace(".", ",")).push().getKey();
            Log.i("Key is",key);
            Map<String, Object> postValues = message1.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            //childUpdates.put("/Active-Messages/" + key, postValues);
            childUpdates.put(senderEmail.replace(".", ",") + "/" + key, postValues);
            childUpdates.put(receiverEmail.replace(".", ",") + "/" + key, postValues);
            mDatabase.updateChildren(childUpdates);

            DataFromProfile=false;

        }else{
            //That is when we are coming from the Fragment Chat Activity or When we post the second message in the chat app:
            //Here are the steps:
            //First, we check for all the similar receiver emails. If there are any repeats, we simply delete those.

            mDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //Log.i("The chat Message","Does it work");//It Does
                    for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        Message message = postSnapShot.getValue(Message.class);
                        Map<String, Object> postValues = message.toMap();
                        chatmessage = chatmessage.concat("/" + senderEmail.toString() + "/" + messageToBeSent);
                        Message message1 = new Message(senderEmail,receiverEmail, chatmessage);
                        String key = String.valueOf(postSnapShot.getKey());
                        Map<String, Object> postValues1 = message1.toMap();
                        Map<String, Object> childUpdates1 = new HashMap<>();
                        childUpdates1.put(senderEmail.replace(".", ",") + "/" + key, postValues1);
                        childUpdates1.put(receiverEmail.replace(".", ",") + "/" + key, postValues1);
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




        }/*else{

            Message message = new Message(senderEmailFromChatFragment, receiverEmailFromChatFragment,chatmessage);
            senderEmailFromChatFragment = senderEmailFromChatFragment.replace(".",",");

            senderEmail = senderEmailFromChatFragment;
            receiverEmail = receiverEmailFromChatFragment;
            mDatabase.child(senderEmailFromChatFragment).push().setValue(message);//Sets the edit text as "" everytime you click send
            list.add(chatEditText.getText().toString());
            arrayAdapter.notifyDataSetChanged();
            chatEditText.setText("");
        }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);
        setTitle("Chat with" + receiverEmail);

        //Checking if the user is presnet or not
        //s
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

        chatListView = (ListView)findViewById(R.id.chatListView);
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        chatListView.setAdapter(arrayAdapter);

        //Getting intent data from other apps
        Intent intent = getIntent();
        //First time chat initialization:
        receiverEmail = intent.getStringExtra("ReceiverEmail");
        senderEmail = intent.getStringExtra("SenderEmail");
        DataFromProfile = intent.getExtras().getBoolean("DataFromProfile");

        //Log.i("Display Boolean Value", String.valueOf(DataFromProfile));

        //This happens when the chat already exists
        receiverEmailFromChatFragment = intent.getStringExtra("userEmailFromChatFragment");
        senderEmailFromChatFragment = user.getEmail(); //THis gives the current users email,userEmailFromChatFragment gives the opp part ka email
        mDatabase = FirebaseDatabase.getInstance().getReference("Messages").child(user.getDisplayName().replace(".",","));
         //Customizing the Chat
        mDatabase.addChildEventListener(new ChildEventListener() {//We are using this to read data:
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                    Message message = postSnapShot.getValue(Message.class);
                    String MessageString = message.returnMessage();
                    Log.i("Ultimate String is",MessageString);
                    String PresentUser = user.getEmail();
                    String EmailOfReceiver = message.returnReceiverEmail();
                    Log.i("Ultimate String 2 is",EmailOfReceiver);
                    Log.i("Ultimate String 2 is",PresentUser);
                    if(PresentUser.matches(EmailOfReceiver)==false){//This is for our test case
                        Log.i("Do The Emails Match","Nope");
                        String s1 = MessageString;//Here, manipulate, the string and extract the specific substrings
                        String[] parts = s1.split("/");
                        //get the senders email from the Message String
                        int i = parts.length;
                        for(int j =0;j<i-1;j=j+2){
                                if(parts[j]==user.getEmail()){
                                    list.add(parts[j]);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                                else{
                                    list.add(">"+parts[j]);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                        }
                        Log.i("Legnth of String", String.valueOf(i));
                    }
                    else{
                        EmailOfReceiver=message.returnSenderEmail();
                        Log.i("Do The Emails Match","Yup");
                        list.add("");
                        arrayAdapter.notifyDataSetChanged();
                        chatEditText.setText("");
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
    }
}
