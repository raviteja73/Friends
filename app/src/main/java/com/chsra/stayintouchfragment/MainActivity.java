package com.chsra.stayintouchfragment;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Controller {

    static String FIREBASE_ENDPOINT = "https://stay-in-touch.firebaseio.com/";
    static String FRAGMENT_TAG_LOGIN = "login_fragment";
    static String FRAGMENT_TAG_SIGNUP = "signup_fragment";
    static String FRAGMENT_TAG_CONVERSATIONS = "conversations_fragment";
    static String FRAGMENT_TAG_CONTACTS = "contacts_fragment";
    static String FRAGMENT_TAG_PROFILE = "profile_fragment";
    static String FRAGMENT_TAG_MESSAGES = "messages_fragment";
    static String FRAGMENT_TAG_CONTACT = "contact_fragment";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getFragmentManager().beginTransaction().add(R.id.fragment_container, new LoginFragment(), FRAGMENT_TAG_LOGIN).commit();
    }


    @Override
    public void onBackPressed(){
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.drawer_conversations:
                Log.d("d", "conversation drawer clicked");
                goToConversations();
                break;
            case R.id.drawer_contacts:
                Log.d("d", "contacts drawer clicked");
                goToContacts();
                break;
            case R.id.drawer_profile:
                Log.d("d", "profile drawer clicked");
                goToProfile();
                break;
            case R.id.drawer_logout:
                Log.d("d", "logout drawer clicked");
                Firebase fbRef = new Firebase(FIREBASE_ENDPOINT);
                fbRef.unauth();
                goToLogin();
                break;
            case R.id.drawer_archives:
                break;
            case R.id.drawer_exit:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void goToSignup() {
        getSupportActionBar().setTitle(R.string.page_title_signup);

        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new SignupFragment(), FRAGMENT_TAG_SIGNUP).
                addToBackStack(null).
                commit();
    }

    @Override
    public void goToLogin() {
        getSupportActionBar().setTitle(R.string.page_title_login);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new LoginFragment(), FRAGMENT_TAG_LOGIN).
                commit();
    }

    @Override
    public void goToConversations() {
        getSupportActionBar().setTitle(R.string.page_title_conversations);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ConversationsFragment(), FRAGMENT_TAG_CONVERSATIONS).
                commit();
    }

    @Override
    public void goToContacts() {
        getSupportActionBar().setTitle(R.string.page_title_contacts);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ContactsFragment(), FRAGMENT_TAG_CONTACTS).
                commit();
    }

    @Override
    public void goToProfile() {
        getSupportActionBar().setTitle(R.string.page_title_profile);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, new ProfileFragment(), FRAGMENT_TAG_PROFILE).
                commit();
    }

    @Override
    public void goToMessages(Conversation conversation) {
        getSupportActionBar().setTitle(R.string.page_title_messages);
        MessagesFragment f = new MessagesFragment();
        f.setConversation(conversation);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, f, FRAGMENT_TAG_MESSAGES).
                addToBackStack(null).
                commit();
    }


    @Override
    public void goToContact(User contact) {
        getSupportActionBar().setTitle(contact.getFullName());
        ContactFragment f = new ContactFragment();
        f.setContact(contact);
        getFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container, f, FRAGMENT_TAG_CONTACT).
                addToBackStack(null).
                commit();
    }

    @Override
    public void closeCurrent() {
        getFragmentManager().popBackStack();
    }
}
