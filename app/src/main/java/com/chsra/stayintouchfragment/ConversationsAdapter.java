package com.chsra.stayintouchfragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

public class ConversationsAdapter extends ArrayAdapter<Conversation> {
    List<Conversation> conversations;
    Activity context;
    int resource;
    User loggedInUser;
    Firebase ref;

    User contact;

    public ConversationsAdapter(Activity context, int resource, List<Conversation> conversations, User loggedInUser, Firebase ref) {
        super(context, resource, conversations);
        this.conversations = conversations;
        this.context = context;
        this.resource = resource;
        this.loggedInUser = loggedInUser;
        this.ref = ref;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }

        final View thisView = convertView;
        final Conversation conversation = this.conversations.get(position);

        String contact_mail;



        if(conversation.getUser1().equals(loggedInUser.getEmail())){
            contact_mail = conversation.getUser2();
        } else {
            contact_mail = conversation.getUser1();
        }




        ref.child("users").orderByChild("email").equalTo(contact_mail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getValue(User.class).equals(loggedInUser)) {
                        contact = snapshot.getValue(User.class);

                    }
                }

                ImageView pic = (ImageView) thisView.findViewById(R.id.conversationImage);

                if (contact.getPic() != null) {
                    if (!contact.getPic().isEmpty()) {

                        byte[] decodedString = Base64.decode(contact.getPic(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        pic.setImageBitmap(decodedByte);
                    }
                } else {
                    pic.setImageResource(R.drawable.default_profile);
                }

                TextView name = (TextView) thisView.findViewById(R.id.contactName);

                name.setText(contact.getFullName());

                ref.child("messages").
                        orderByChild("receiver").
                        equalTo(loggedInUser.getUid()).
                        addValueEventListener(new ValueEventListener() {
                            ImageView unreadNotifier = (ImageView) thisView.findViewById(R.id.unreadNotification);

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    Message message = postSnapshot.getValue(Message.class);

                                    if (message.getSender().equals(contact.getUid()) && !message.isMessage_read()) {

                                        unreadNotifier.setVisibility(View.VISIBLE);
                                        break;
                                    } else {
                                        unreadNotifier.setVisibility(View.INVISIBLE);
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

        return convertView;
    }
}

