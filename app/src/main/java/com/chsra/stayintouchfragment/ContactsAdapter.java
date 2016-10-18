package com.chsra.stayintouchfragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by chsra on 27-Apr-16.
 */
public class ContactsAdapter extends ArrayAdapter{
    List<User> users;
    Context context;
    int resource;
    Firebase ref;
    AuthData authData;
    ImageView phoneIcon;

    public ContactsAdapter(Context context, int resource, List<User> users, Firebase ref, AuthData authData) {
        super(context, resource, users);
        this.users = users;
        this.context = context;
        this.resource = resource;
        this.ref = ref;
        this.authData = authData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }

        final View thisView = convertView;
        final User contact = this.users.get(position);



        ImageView pic = (ImageView) convertView.findViewById(R.id.conversationImage);

        if(contact.getPic() != null && !contact.getPic().isEmpty()) {
            byte[] decodedString = Base64.decode(contact.getPic(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            pic.setImageBitmap(decodedByte);
        } else {
            pic.setImageResource(R.drawable.default_profile);
        }

        TextView name = (TextView) convertView.findViewById(R.id.contactName);
        name.setText(contact.getFullName());

        if(contact.getPhoneNumber() != null && !contact.getPhoneNumber().isEmpty()){
            phoneIcon = (ImageView) convertView.findViewById(R.id.CallIcon);
            phoneIcon.setVisibility(View.VISIBLE);

            phoneIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhoneNumber()));
                    thisView.getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
