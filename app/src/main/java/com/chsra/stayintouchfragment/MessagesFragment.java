package com.chsra.stayintouchfragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesFragment extends Fragment {
    Controller controller;
    Conversation new_conversation;
    User contact;
    Firebase ref;
    Firebase conversationRef;
    ArrayList<Message> messages = new ArrayList<>();
    TextView msg;
    User user;
    ListView list;
    MessagesAdapter adapter;
    public MessagesFragment(){}

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
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());

        ref = new Firebase(MainActivity.FIREBASE_ENDPOINT);
        final Firebase mRef = ref.child("messages");
        conversationRef = ref.child("Conversations");
        list = (ListView) getActivity().findViewById(R.id.messages_list);
        final AuthData authData = ref.getAuth();

        if (authData != null) {

            contact = new User();
            if(new_conversation.getUser1().equals(authData.getProviderData().get("email"))){
                contact.setEmail(new_conversation.getUser2());
            }
            else {
                contact.setEmail(new_conversation.getUser1());
            }

            ref.child("users").orderByChild("email").equalTo(contact.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        contact = snapshot.getValue(User.class);
                        Log.d("demo",contact.toString());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            ref.child("users").orderByChild("uid").equalTo(authData.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        user = snapshot.getValue(User.class);

                    }

                    adapter = new MessagesAdapter(getActivity(), R.layout.layout_message, messages, user, contact, mRef);
                    list.setAdapter(adapter);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            conversationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (new_conversation.getKey() == null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Conversation conversation = snapshot.getValue(Conversation.class);
                            if ((conversation.getUser1().equals(user.getEmail()) && conversation.getUser2().equals(contact.getEmail())) || (conversation.getUser1().equals(contact.getEmail()) && conversation.getUser2().equals(user.getEmail()))) {
                                new_conversation = conversation;
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);

                        if ((message.getSender().equals(user.getUid()) && message.getReceiver().equals(contact.getUid()) ||
                                (message.getSender().equals(contact.getUid()) && message.getReceiver().equals(user.getUid())))) {

                            if (message.getReceiver().equals(user.getUid())) {

                                message.setMessage_read(true);
                                mRef.child(message.getUid()).setValue(message);
                            }
                            adapter.add(message);
                        }
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            getActivity().findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msg = (TextView) getActivity().findViewById(R.id.message_text);
                    String text = msg.getText().toString();

                    if (text.isEmpty()) {
                        Toast.makeText(getActivity(), "Please Insert a Message", Toast.LENGTH_SHORT).show();
                    } else if (text.length() > 140) {
                        Toast.makeText(getActivity(), "Message cannot be longer than 140 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        if (new_conversation.getKey()==null) {
                            Firebase conv = conversationRef.push();
                            new_conversation.setKey(conv.getKey());
                            conv.setValue(new_conversation);
                        }
                        Message message = new Message();
                        message.setMessage_text(msg.getText().toString());
                        message.setSender(authData.getUid());
                        message.setReceiver(contact.getUid());
                        message.setMessage_read(false);
                        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());
                        message.setTimestamp(timeStamp);

                        Firebase newMessage = ref.child("messages").push();
                        message.setUid(newMessage.getKey());
                        message.setConversation_id(new_conversation.getKey());
                        newMessage.setValue(message, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                msg.setText(null);
                            }
                        });
                    }
                }
            });
        }
    }

    public void setConversation(Conversation conversation){
        this.new_conversation = conversation;
    }

    public void setContact(User contact){
        this.contact = contact;
    }
}

