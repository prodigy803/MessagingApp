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
import java.util.List;

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

        final DatabaseHandler db = new DatabaseHandler(getActivity());

        //Initiating the Download Of any messages that we may receive:
        mDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    //Log.i("DataSnapShot key",dataSnapshot.getKey());//this is the sender,ie, opposite party
                    //Log.i("PostSnapShot Key",postSnapshot.getKey());//this is the current User, apun log
                    //Log.i("Message",String.valueOf(postSnapshot.child("Message").getValue()));//this is the Message from the server
                    String sender = dataSnapshot.getKey().replace(",",".");
                    String receiver = postSnapshot.getKey().replace(",",".");
                    String message = String.valueOf(postSnapshot.child("Message").getValue());
                    db.addConversationItem(new ConversationsListsDatabase("Conversation With "+sender));
                    usernames.add(String.valueOf(db.getAllConversationsNames()));
                    adapter.notifyDataSetChanged();

                    if(receiver.matches(user.getEmail())){
                        String[] parts = message.split("/");
                        int lengthOfParts = parts.length;
                        for(int i=0;i<lengthOfParts;i++){
                            if(i%2==0){
                                //Log.i("Message",parts[i]);//all the messages
                                db.addMessage(new MessageInSql(sender, receiver, parts[i]));
                            }
                        }
                        mDatabase.child(sender.replace(".",",")).setValue(null);
                    }
                    //To check if properly entered, uncomment the following lines
                    List<ConversationsListsDatabase> conversationNames = db.getAllConversationsNames();
                    for (ConversationsListsDatabase cn : conversationNames) {
                        String log = "ConversationItem "+cn.getConversationWith();
                        // Writing Contacts to log
                        Log.d("Name: ", log);
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
        return view;
    }

}

