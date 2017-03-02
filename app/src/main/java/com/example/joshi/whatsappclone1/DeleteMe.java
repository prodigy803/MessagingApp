package com.example.joshi.whatsappclone1;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by joshi on 01-03-2017.
 */

public class DeleteMe {
    private DatabaseReference mDatabase;
    public String Key;
    public String email1;
    public String email2;
    public DeleteMe() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public  DeleteMe(String Key,String email1,String email2){
        this.Key = Key;
        this.email1=email1;
        this.email2=email2;
        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
    }
    public void deleteAccount(){
        if(email1.matches(email2)){
            mDatabase.child(Key).removeValue();
        }else{
            Log.i("The Critical","Was False");
        }
    }


}
