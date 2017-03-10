package com.example.joshi.whatsappclone1;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Toolbar toolbar1;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //System Critical References.
        mDatabase = FirebaseDatabase.getInstance().getReference("User Information");
        auth = FirebaseAuth.getInstance();

        //Fragment Coding starts
        toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar1);

        //the following commands enable or disable the <- button on the main activity page.
        toolbar1.setNavigationIcon(null);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        //Fragment Coding ends

        //Logs out the user if accidentally goes to main page with out authentication.
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) {
                            // user auth state is changed - user is null
                            // launch login activity
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                };

        //Retrieving and displaying hello to the user and where the user is from.
        /* Hello to user bit
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                    Users users = postSnapShot.getValue(Users.class);
                    String string = "Hello " + users.returnNameFromUsers() + ".";
                    helloToUser.setText(string);
                    //Users users = dataSnapshot.getValue(Users.class);
                }
                //Users users = dataSnapshot.getValue(Users.class);
                //System.out.println(users.getName());
                //helloToUser.setText(users.name);
                //Log.i("SuccessFull ?","True");
                //Users users = dataSnapshot.getKey().matches();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/

    }


    //Fragment COde
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ChatFragment(), "Existing Chats");
        adapter.addFrag(new InviteFriendsFragment(), "Chat Rooms");
        adapter.addFrag(new CheckOutFragment(), "Logout");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    //This method
    public void signOut() {
        auth.signOut();
    }
    //has to be added to all the fragments
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }
}
