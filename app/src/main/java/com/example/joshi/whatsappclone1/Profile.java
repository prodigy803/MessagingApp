package com.example.joshi.whatsappclone1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
        private FirebaseAuth.AuthStateListener mAuthListener;
        private FirebaseAuth auth;
        private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //we get the email for the Invite and Search Fragment
        //In this activity, there is a checkbox, which is ticked if the guy searching the profile and the guy whose profile is getting
        //reviewed. We will first create a database of all the friends
        //If the users UID is in that Datalist, then we will mark the checkbox as Checked, Else we will not.
        //The Profile also leads to another activity, the main chat activity, where the user is able to send and receive messages.
        //The chats conversation can then be displayed on the chats bar on the main activity. Hence, Chats have to have a seperate database
        //table where we can

        mDatabase = FirebaseDatabase.getInstance().getReference("User Information");
        auth = FirebaseAuth.getInstance();

        //Checking if the user is logged on or not.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Profile.this, LoginActivity.class));
                    finish();
                }
            }
        };
        final Intent intent = getIntent();
        final String emailOfSearchedPerson = intent.getStringExtra("userEmail");
        TextView UserEmail = (TextView)findViewById(R.id.UserNameProfleTextView);
        UserEmail.setText(emailOfSearchedPerson);
        Button StartConversationButton = (Button)findViewById(R.id.StartConversationButton);
        StartConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent1 = new Intent(Profile.this,ChatAcitivity.class);
                intent1.putExtra("ReceiverEmail",emailOfSearchedPerson);
                intent1.putExtra("SenderEmail",user.getEmail());
                intent1.putExtra("DataFromProfile",true);
                startActivity(intent1);
            }
        });
    }
}
