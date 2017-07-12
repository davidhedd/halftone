package com.halftone.halftone.layout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.halftone.halftone.R;
import com.halftone.halftone.content.Post;
import com.halftone.halftone.content.PostImage;
import com.halftone.halftone.content.Tag;
import com.halftone.halftone.users.Username;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


public class UploadFragment extends Fragment implements LocationListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private LocationManager locationManager;

    private String imageBitmapLink = "";
    private String hashtagString;
    private List<String> hashtags;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextView imageBitmapLabel;
    private ImageView addPhotoIcon;
    private Button addPostButton;
    private EditText tagLabels;

    private boolean takingPhoto;

    private View myView;

    //List<String> hashtagStorage;

    private int postCounter = 0;
    private int PICK_IMAGE_REQUEST = 1;

    public static UploadFragment newInstance() {
        return new UploadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_upload, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        MainActivity.welcomeMessage++;
        imageBitmapLabel = (TextView) myView.findViewById(R.id.photoBitmapLabel);

        // Allow user to take photo
        addPhotoIcon = (ImageView) myView.findViewById(R.id.addPhotoIcon);
        addPhotoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        // Allow user to select photo from gallery

        // Edit text view for the user to input the tags for this photo
        tagLabels = (EditText) myView.findViewById(R.id.tagLabels);
        tagLabels.setSelected(false);

        // Validation for the input tags text box
        checkForTags();

        // functionality for the upload post button
        addPostButton = (Button) myView.findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imgDecodableString.equals("")) {
                    System.out.println("here");
                    DatabaseReference postDR = databaseReference.child("Posts");
                    DatabaseReference postImageDR = databaseReference.child("PostImages").child(firebaseAuth.getCurrentUser().getUid());
                    DatabaseReference tagsDR = databaseReference.child("Tags");

                    DatabaseReference postDR1 = postDR.push();
                    DatabaseReference postImageDR1 = postImageDR.push();
                    DatabaseReference tagsDR1 = tagsDR.push();

                    Post post = new Post(
                            firebaseAuth.getCurrentUser().getUid(),
                            /*getUsername(),*/
                            hashtags
                    );
                    PostImage postImage = new PostImage(
                            post,
                            imgDecodableString
                    );
                    postDR1.setValue(post);
                    postImageDR1.setValue(postImage);
                    Toast.makeText(getContext(), "Post uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            if (MainActivity.welcomeMessage <= 1) {
                getUsername();
                /*
                    Toast.makeText(getActivity(), "Welcome back " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                */
            }
        } catch (NullPointerException npe) {
            System.out.println("aw oh null pointer");
        }

        locationManager = (LocationManager) getContext()
                .getSystemService(LOCATION_SERVICE);

        //statusCheck();

        return myView;
    }

    String username = "";

    public String getUsername(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Usernames");
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    if( postSnapshot.getKey().equals( firebaseAuth.getCurrentUser().getUid() ) ) {
                        Username u = postSnapshot.getValue(Username.class);
                        username = u.getUsername();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return "";
    }

    public void convertToTags(String input){
        if( !input.contains("#") ){
            return;
        }
        String[] tempHashtags = input.split("#");
        List<String> hashtags = new ArrayList<>();
        for( int i=0; i<tempHashtags.length; i++ ){
            tempHashtags[i] = tempHashtags[i].replace(" ", "");
            if( !tempHashtags[i].equals("") ){
                hashtags.add( tempHashtags[i] );
                hashtagString += tempHashtags[i].replace(" ", "") + " ";
            }
        }
        String hash = "";
        for( String hashtag : hashtags ){
            hash += "#" + hashtag + " ";
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /** The method decodes the image file to avoid out of memory issues. Sets the
     * selected image in to the ImageView.
     *
     * @param filePath
     */
    public void decodeFile(String filePath) {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageBitmapLink = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        // imageBitmapLabel.setText(imageBitmapLink);
        System.out.println(imageBitmapLink);
    }

    String imgDecodableString = "";
    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void checkForTags(){
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if user has typed a space
                // If they have put a hash symbol
                convertToTags(s.toString());
            }
        };
        tagLabels.addTextChangedListener(fieldValidatorTextWatcher);
    }

    Bitmap bitmap = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imgDecodableString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            System.out.println("here:" + imgDecodableString);
        }

        /*try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                decodeFile(picturePath);
            } else {
                System.out.println("");
            }
        } catch (Exception e) {
            System.out.println("");
        }*/
    }

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            System.out.println("going to take photo     ");
            takingPhoto = true;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void launchGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*@SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bitmap=null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String b = getBitmapToString( bitmap );
    }
*/
}