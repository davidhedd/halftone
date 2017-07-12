package com.halftone.halftone.layout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import com.halftone.halftone.content.Post;
import com.halftone.halftone.content.PostImage;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    private View myView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private List<String> likesBitmaps;

    private TextView likeView;

    public static LikesFragment newInstance() {
        return new LikesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_likes, container, false);
        // likesBitmaps = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        // System.out.println(firebaseAuth.getCurrentUser().getUid());

        likeView = (TextView) myView.findViewById(R.id.likeView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        getLikes();

        return myView;
    }

    public void getLikes(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserLikes").child("yes");
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                likeView.setText("");
                String likes = "";
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if( postSnapshot.getKey().equals( firebaseAuth.getCurrentUser().getUid() ) ) {
                        for( DataSnapshot list : postSnapshot.getChildren() ){
                            Post post = list.child("post").getValue(Post.class);
                            likes += post.getUid() + "\n";
                        }
                    }
                }
                System.out.println(likes);

                likeView.setText(likes);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}