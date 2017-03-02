package com.example.joshi.whatsappclone1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InviteFriendsFragment extends Fragment {
    EditText inviteAndSearchFriendsEditText;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    Button searchForUsers;
    String resultText,userSearchedEmail;
    EditText acceptUsername;
    ListView listview;
    public InviteFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        final ArrayList<ArrayList<String>> usernames = new ArrayList<ArrayList<String>>();

        mDatabase = FirebaseDatabase.getInstance().getReference("User Information");
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    //finish();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_invite_friends, container, false);

        //Code for searching for users

        listview =(ListView)view.findViewById(R.id.emailsOfFriendsRequested);
        searchForUsers = (Button)view.findViewById(R.id.searchForUsers);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        acceptUsername = (EditText)view.findViewById(R.id.acceptUsername);
        ListView userListView = (ListView)view.findViewById(R.id.emailsOfFriendsRequested);
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
        userListView.setAdapter(adapter);

        searchForUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernames.clear();
                adapter.notifyDataSetChanged();

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                            Users users = postSnapShot.getValue(Users.class);
                            final String UsernameToBeSearched = acceptUsername.getText().toString();
//                            Log.i("Names",users.returnNameFromUsers());

                            if(UsernameToBeSearched.matches(users.returnNameFromUsers())){
                                usernames.add(users.returnEmailFromUsers().toString());
                                adapter.notifyDataSetChanged();
                                Log.i("User Exists","True");
                                Log.i("Key of the user is",dataSnapshot.getKey());//This returns User Information
                                Log.i("User ID",user.getUid());
                                userSearchedEmail = users.returnEmailFromUsers();
                                Log.i("User email is",users.returnEmailFromUsers());
                            }else{
                                Log.i("User Exists","False");
                            }
                        }
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String s = (String) listview.getItemAtPosition(position);
                            Intent intent = new Intent(getActivity(), Profile.class);
                            intent.putExtra("userEmail",s);
                            startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

}
