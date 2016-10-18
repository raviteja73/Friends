package com.chsra.stayintouchfragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    Controller controller;
    Firebase ref;
    AuthData authData;
    String to64;
    TextView textView;
    EditText editText;
    User user;
    String oldEmail;
    String oldPass;
    public ProfileFragment(){}

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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        ref = new Firebase(MainActivity.FIREBASE_ENDPOINT);


        getActivity().findViewById(R.id.user_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });


        authData = ref.getAuth();
        ref.child("users").orderByChild("uid").equalTo(authData.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        displayDetails(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });


        //update user details
        getActivity().findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user.setPic(to64);
                editText = (EditText) getActivity().findViewById(R.id.name_box);
                user.setFullName(editText.getText().toString());
                editText = (EditText) getActivity().findViewById(R.id.phone_number);
                user.setPhoneNumber(editText.getText().toString());

                EditText emailField = (EditText) getActivity().findViewById(R.id.email);
                String email = emailField.getText().toString();//email.setText(email.getText().toString());
                EditText passField = (EditText) getActivity().findViewById(R.id.password);
                final String pass = passField.getText().toString();//pass.setText(pass.getText().toString());

                oldEmail = user.getEmail();
                oldPass = user.getPassword();

                user.setEmail(email);
                user.setPassword(pass);

                if(!oldEmail.equals(email)) {
                    ref.changeEmail(oldEmail, oldPass, email, new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            changePassword(pass);
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            Log.d("d", "Error " + firebaseError.getMessage());
                            Toast.makeText(getActivity(), "Error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
                } else if (!oldPass.equals(pass)) {
                    changePassword(pass);
                } else{
                    updateUser();
                }
            }
        });


        getActivity().findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                controller.goToConversations();
            }
        });
    }


    private void changePassword(String newPass){
        if(!oldPass.equals(newPass)) {
            ref.changePassword(user.getEmail(), oldPass, newPass, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    updateUser();
                }

                @Override
                public void onError(FirebaseError firebaseError) {

                    Toast.makeText(getActivity(), "Error: " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateUser();
        }
    }


    private void updateUser() {
        ref.child("users").child(user.getUid()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                controller.goToConversations();
            }
        });
    }



    //Get image from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView image = (ImageView) getActivity().findViewById(R.id.user_image);
        try {
            if (requestCode == requestCode && resultCode == getActivity().RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                Log.d("d", "Image URI: " + selectedImage);

                Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                image.setImageBitmap(bm);

                //Encoding image to base64
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                to64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("d", "Encoded String: " + to64);

            }
        }catch (IOException e){
            Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_LONG).show();
        }

    }


    public void displayDetails(DataSnapshot dataSnapshot){
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            user = snapshot.getValue(User.class);

        }

        ImageView image = (ImageView) getActivity().findViewById(R.id.user_image);
        if(user.getPic() != null && !user.getPic().equals("")) {
            byte[] decodedString = Base64.decode(user.getPic(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
        } else {
            image.setImageResource(R.drawable.default_profile);
        }
        textView = (TextView) getActivity().findViewById(R.id.name_label);
        textView.setText(user.getFullName());
        editText = (EditText) getActivity().findViewById(R.id.name_box);
        editText.setText(user.getFullName());
        editText = (EditText) getActivity().findViewById(R.id.email);
        editText.setText(user.getEmail());
        editText = (EditText) getActivity().findViewById(R.id.phone_number);
        editText.setText(user.getPhoneNumber());
        editText = (EditText) getActivity().findViewById(R.id.password);
        editText.setText(user.getPassword());
    }
}



