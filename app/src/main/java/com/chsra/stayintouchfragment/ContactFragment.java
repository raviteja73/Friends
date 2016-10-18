package com.chsra.stayintouchfragment;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class ContactFragment extends Fragment {
    Controller controller;
    User contact;
    User user;
    TextView textview;

    public ContactFragment(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            if(context instanceof FragmentActivity) {
                controller = (Controller) getActivity();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity should implement interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        if(contact.getPic() != null && !contact.getPic().equals("")) {
            byte[] decodedString = Base64.decode(contact.getPic(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        } else {

        }

        try {
            textview = (TextView) getActivity().findViewById(R.id.contact_name);
            textview.setText(contact.getFullName());
            textview = (TextView) getActivity().findViewById(R.id.user_name);
            textview.setText(contact.getFullName());
            textview = (TextView) getActivity().findViewById(R.id.phone_number);
            textview.setText(contact.getPhoneNumber());
            textview = (TextView) getActivity().findViewById(R.id.email);
            textview.setText(contact.getEmail());
        }
        catch (Exception e){
        }
    }


    public void setContact(User contact){
        this.contact = contact;
    }

}

