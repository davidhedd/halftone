package com.halftone.halftone.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halftone.halftone.R;
import com.halftone.halftone.content.PostImage;
import com.halftone.halftone.users.Username;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private View myView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_explore, container, false);

        TextView locationLabel = (TextView) myView.findViewById(R.id.locationLabel);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        TextView usernameLabel = (TextView) myView.findViewById(R.id.usernameLabel);
        getUsername( firebaseAuth.getCurrentUser().getUid() );
        //System.out.println( username.substring(0, username.indexOf("@") ) );
        // Show user name here instead
        usernameLabel.setText( firebaseAuth.getCurrentUser().getEmail().substring( 0, 5 ) );

        printAllBitmaps();

        return myView;
    }

    public void getUsername(final String uid){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usernames");
        // final List<PostImage> postBitmaps = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if( postSnapshot.getKey().equals( uid ) ) {
                        System.out.println("yes");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void printAllBitmaps(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("PostImages");
        final List<PostImage> postBitmaps = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if( postSnapshot.getKey().equals( firebaseAuth.getCurrentUser().getUid() ) ){
                        PostImage postImage = postSnapshot.getValue(PostImage.class);
                        postBitmaps.add(postImage);
                        //if( counter == 1 ){
                            // show images
                            ImageView imageBitmap = (ImageView) myView.findViewById(R.id.postImage);
                            imageBitmap.setImageBitmap(StringToBitMap(postImage.getPostBitmap()));
                        //}
                        counter++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}