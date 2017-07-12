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
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halftone.halftone.R;
import com.halftone.halftone.content.Post;
import com.halftone.halftone.content.PostImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExploreFragment extends Fragment {

    private View myView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ImageView nahView, yesView;
    private TextView usernameView;

    private ImageView imageBitmap;

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

        // usernameView = (TextView) myView.findViewById(R.id.usernameLabel);
        showNextPhoto();
        nahView = (ImageView) myView.findViewById(R.id.nah);
        nahView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add to dislike
                addToUserLikes("nah");
            }
        });
        yesView = (ImageView) myView.findViewById(R.id.yes);
        yesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add to like
                addToUserLikes("yes");
            }
        });

        imageBitmap = (ImageView) myView.findViewById(R.id.postImage);

        // TextView locationLabel = (TextView) myView.findViewById(R.id.locationLabel);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // TextView usernameLabel = (TextView) myView.findViewById(R.id.usernameLabel);
        // getUsername( firebaseAuth.getCurrentUser().getUid() );
        //System.out.println( username.substring(0, username.indexOf("@") ) );
        // Show user name here instead
        // usernameLabel.setText( firebaseAuth.getCurrentUser().getEmail().substring( 0, 5 ) );

        return myView;
    }

    public void getUsername(final String uid){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usernames");
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if( postSnapshot.getValue().equals( currentSelectedPost.getPost().getUid() ) ){
                        // System.out.println(postSnapshot.getChildrenCount());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private PostImage currentSelectedPost;

    public void addToUserLikes(String likeDislike){
        if( currentSelectedPost != null){
            DatabaseReference postDR = databaseReference.child("UserLikes").child(likeDislike).child(firebaseAuth.getCurrentUser().getUid());
            DatabaseReference postImageDR = postDR.push();
            postImageDR.setValue(
                    currentSelectedPost
            );
            // manage showing the same image twice
            // TO BE IMPLEMENTED!!!!

            // Show next image
            showNextPhoto();
        }
    }

    public void showNextPhoto(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("PostImages");
        final List<PostImage> postBitmaps = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if( !postSnapshot.getKey().equals( firebaseAuth.getCurrentUser().getUid() ) ){
                        for( DataSnapshot snap : postSnapshot.getChildren() ){
                            postBitmaps.add(snap.getValue(PostImage.class));
                        }
                    }
                }
                if( postBitmaps.size() > 0 ){
                    // show image
                    currentSelectedPost = getRandomPost( postBitmaps );
                    imageBitmap.setImageBitmap(StringToBitMap(currentSelectedPost.getPostBitmap()));
                    // usernameView.setText(currentSelectedPost.getPost().getUsername());
                }else{
                    // show no images left dialog here
                    // TO BE IMPLEMENTED
                    Toast.makeText(getActivity(), "No images left", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public PostImage getRandomPost(List<PostImage> postImages){
        return postImages.get( getRandomIndex( postImages.size() ) );
    }

    Random random = new Random();
    public int getRandomIndex(int max){
        return random.nextInt(max);
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
