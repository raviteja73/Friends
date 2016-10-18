package com.chsra.stayintouchfragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chsra on 27-Apr-16.
 */
public class MessagesAdapter extends ArrayAdapter {
    List<Message> mData;
    Context mContext;
    int mResource;
    User user;
    ImageView imageView;
    Firebase mRef;
    User contact;

    public MessagesAdapter(Context context, int resource, List objects, User user, User contact, Firebase ref) {
        super(context, resource, objects);
        this.mData = objects;
        this.mContext = context;
        this.mResource = resource;
        this.user = user;
        this.contact = contact;
        Firebase.setAndroidContext(mContext);
        mRef = ref;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.mResource, parent, false);
        }
        final Message msg = mData.get(position);
        TextView textView;
        textView = (TextView) convertView.findViewById(R.id.messages_title);
        if (msg.getSender().equals(user.getUid())) {
            textView.setText(user.getFullName());
        } else {
            textView.setText(contact.getFullName());
        }
        textView = (TextView) convertView.findViewById(R.id.message_content);
        textView.setText(msg.getMessage_text());
        textView = (TextView) convertView.findViewById(R.id.message_time);
        textView.setText(msg.getTimestamp());
        imageView = (ImageView) convertView.findViewById(R.id.delete_image);
        imageView.setVisibility(View.INVISIBLE);
        if (msg.getSender().equals(user.getUid())) {
            convertView.setBackgroundColor(Color.parseColor("#FFD6D1D1"));
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.delete).resize(40, 40).into(imageView);
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Query qref = mRef.orderByChild("timestamp").equalTo(msg.getTimestamp());
                final int[] count = {1};
                qref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message match_msg = dataSnapshot.getValue(Message.class);
                        if (match_msg.getMessage_text().equals(msg.getMessage_text()) && count[0] == 1) {
                            dataSnapshot.getRef().removeValue();
                            count[0] = 0;
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
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
        return convertView;
    }
}
