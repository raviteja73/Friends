package com.chsra.stayintouchfragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


public class ConversationsFragment extends Fragment {
    Controller controller;
    ArrayList<Conversation> conversations = new ArrayList<>();
    Firebase rootRef;
    Firebase convRef;
    Firebase userRef;
    Firebase mRef;
    ListView listView;
    AuthData authData;
    ConversationsAdapter adapter;
    User loggedInUser;

    public ConversationsFragment(){}

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
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        rootRef = new Firebase(MainActivity.FIREBASE_ENDPOINT);
        convRef = rootRef.child("Conversations");
        userRef = rootRef.child("users");
        mRef = rootRef.child("messages");
        authData = rootRef.getAuth();


        userRef.orderByChild("uid").equalTo(authData.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    loggedInUser = snapshot.getValue(User.class);
                    Log.d("d", "user " + loggedInUser.toString());
                }
                ImageView image = (ImageView) getActivity().findViewById(R.id.nav_imageView);
                byte[] decodedString = Base64.decode(loggedInUser.getPic(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                image.setImageBitmap(decodedByte);
                TextView textView = (TextView) getActivity().findViewById(R.id.nav_fullname);
                textView.setText(loggedInUser.getFullName());
                try {
                    adapter = new ConversationsAdapter(getActivity(), R.layout.conversation_row, conversations, loggedInUser, rootRef);
                    adapter.setNotifyOnChange(true);

                    listView = (ListView) getActivity().findViewById(R.id.conversations_listview);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Conversation conversation = adapter.getItem(position);
                            controller.goToMessages(conversation);
                        }
                    });

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            String[] s = {"Archive Conversation", "Delete Conversatiion"};
                            final Conversation conversation = conversations.get(position);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setItems(s, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (which == 0) {
                                        if (conversation.getUser1().equals(authData.getProviderData().get("email"))) {
                                            conversation.setArchivedByUser1(true);
                                        } else if (conversation.getUser2().equals(authData.getProviderData().get("email"))) {
                                            conversation.setIsArchivedByUser2(true);
                                        }
                                        convRef.child(conversation.getKey()).setValue(conversation);
                                    } else if (which == 1) {
                                        if (conversation.getDeletedBy() != null) {
                                            if ((conversation.getDeletedBy().equals(conversation.getUser1())) || conversation.getDeletedBy().equals(conversation.getUser2())) {
                                                convRef.child(conversation.getKey()).removeValue();
                                                mRef.orderByChild("conversation_id").equalTo(conversation.getKey()).getRef().removeValue();
                                            }
                                        } else {
                                            conversation.setDeletedBy(authData.getProviderData().get("email").toString());
                                            convRef.child(conversation.getKey()).setValue(conversation);
                                        }
                                    } else {

                                    }
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return true;
                        }
                    });
                }catch (Exception e){

                }




                convRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        adapter.clear();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Conversation conversation = postSnapshot.getValue(Conversation.class);
                            if (conversation.isDisplayable(loggedInUser)) {
                                adapter.add(conversation);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }

                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }
}
