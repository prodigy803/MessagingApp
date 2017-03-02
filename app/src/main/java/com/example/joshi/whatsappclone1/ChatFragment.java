package com.example.joshi.whatsappclone1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//This fragment has the all the live chats with a list view
public class ChatFragment extends Fragment {
    private DatabaseReference mDatabase;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Boolean Value = true;

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        userEmail = userEmail.replace(".",",");
        final ListView listview =(ListView)view.findViewById(R.id.ExistingChatsListView);
        final ArrayList<String> usernames = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1 , usernames){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.BLACK);

                return view;
            }
        };
        listview.setAdapter(adapter);

        //Information Available at this point:
        //We can get the current User ID, email(Main important), Name.

        //mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        //mDatabase = FirebaseDatabase.getInstance().getReference("Messages").getRoot();With root, you go to the Literal root of the cllass
        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");



        mDatabase.addChildEventListener(new ChildEventListener() {//We are using this to read data:
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Message message = postSnapShot.getValue(Message.class);
                    String key = String.valueOf(postSnapShot.getKey());
                    Log.i("Key",key);//Returns me the emails of the dudes:
                    Log.i("Message",message.returnMessage());
                    Log.i("Sender",message.returnSenderEmail());
                    Log.i("Receiver",message.returnReceiverEmail());
                    Log.i("User Email",user.getEmail());
                    //We have to change this bit
                    String s3 = "Conversation With "+ message.returnSenderEmail();


                    if (user.getEmail().matches(message.returnReceiverEmail())){ ///Under Review
                        String s1 = message.returnMessage();
                        String s2 = s1.substring(s.indexOf("/") + 1, s.indexOf("/"));
                        //get the senders email from the Message String
                        Log.i("Sender Email is",s2);
                        usernames.add("Conversation With " + s2);
                        adapter.notifyDataSetChanged();

                        //Log.i("IF Statement"," Executed");
                        //Log.i("Active user is",message.returnSenderEmail());
                    }
                    else if(user.getEmail().matches(message.returnSenderEmail())){
                        //usernames.add("Conversation With " + receiversEmail);
                        Log.i("Receiver Email is",message.returnReceiverEmail());
                        usernames.add("Conversation With " + message.returnReceiverEmail());
                        adapter.notifyDataSetChanged();
                    }

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String Splitter = "Conversation With ";
                            String s = (String) listview.getItemAtPosition(position);
                            s = s.replaceAll(Splitter, "");
                            Log.i("Split String", s);
                            Intent intent = new Intent(getActivity(), ChatAcitivity.class);
                            intent.putExtra("userEmailFromChatFragment", s);
                            intent.putExtra("DataFromProfile", false);
                            startActivity(intent);
                        }
                    });
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
        return view;
    }
}

