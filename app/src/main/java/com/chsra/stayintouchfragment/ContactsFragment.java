package com.chsra.stayintouchfragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {
    Controller controller;
    Firebase fbRef;
    Firebase usersRef;
    Firebase messagesRef;
    ArrayList<User> users = new ArrayList<>();
    AuthData authData;
    ContactsAdapter adapter;
    static String USER_KEY = "user";
    public ContactsFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // make sure activity implements interface
        try{
            controller = (Controller) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity should implement interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
        usersRef = fbRef.child("users");
        messagesRef = fbRef.child("messages");
        authData = fbRef.getAuth();
        if (authData != null) {

            ListView listView = (ListView) getActivity().findViewById(R.id.conversations);
            adapter = new ContactsAdapter(getActivity(), R.layout.contact_row, users, messagesRef, authData);
            adapter.setNotifyOnChange(true);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User user = users.get(position);
                    Conversation conversation = new Conversation();
                    conversation.setUser1((String) authData.getProviderData().get("email"));
                    conversation.setUser2(user.getEmail());

                    controller.goToMessages(conversation);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    User contact = users.get(position);
                    controller.goToContact(contact);
                    return false;
                }
            });

            displayContacts();
        } else {

        }
    }

    private void displayContacts(){
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                adapter.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (!user.getUid().equals(authData.getUid())) {
                        adapter.add(user);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}

