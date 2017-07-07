package com.halftone.halftone.layout;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.halftone.halftone.R;
import com.halftone.halftone.content.Post;
import com.halftone.halftone.content.PostImage;
import com.halftone.halftone.content.Tag;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


public class UploadFragment extends Fragment implements LocationListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private LocationManager locationManager;

    private String imageBitmapLink = "";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private TextView imageBitmapLabel;
    private ImageView addPhotoIcon, addTagsIcon;
    private Button addPostButton;
    private EditText addTags;

    private boolean takingPhoto;

    private View myView;

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

        addTagsIcon = (ImageView) myView.findViewById(R.id.addTagsIcon);
        addTagsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });

        addTags = (EditText) myView.findViewById(R.id.tagLabels);
        addTags.setSelected(false);

        addPostButton = (Button) myView.findViewById(R.id.addPostButton);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageBitmapLabel.getText().equals("")) {
                    DatabaseReference postDR = databaseReference.child("Posts");
                    DatabaseReference postImageDR = databaseReference.child("PostImages").child(firebaseAuth.getCurrentUser().getUid());

                    DatabaseReference postDR1 = postDR.push();
                    DatabaseReference postImageDR1 = postImageDR.push();

                    List<Tag> tags = new ArrayList<Tag>();
                    tags.add(new Tag(1, "Test tag 1"));
                    tags.add(new Tag(2, "Test tag 2"));
                    tags.add(new Tag(3, "Test tag 3"));
                    Post post = new Post(
                            ++postCounter,
                            firebaseAuth.getCurrentUser(),
                            "Test post",
                            "This is a sample post.",
                            "lat",
                            "lng",
                            tags,
                            4
                    );
                    PostImage postImage = new PostImage(
                            post,
                            imageBitmapLabel.getText().toString()
                    );
                    postDR1.setValue(post);
                    postImageDR1.setValue(postImage);
                    Toast.makeText(getContext(), "Post uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            if (MainActivity.welcomeMessage <= 1) {
                if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getEmail().toLowerCase().contains("kristina")) {
                    Toast.makeText(getActivity(), "Hello Kristina", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Welcome back " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (NullPointerException npe) {
            System.out.println("aw oh null pointer");
        }

        /*addTagsView = (ImageView) myView.findViewById(R.id.addTagsIcon);
        addTagsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                String[] colors = {"red", "yellow", "green"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("hello")
                        .setItems(colors, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                            }
                        });
                builder.create();
            }
        });*/

        locationManager = (LocationManager) getContext()
                .getSystemService(LOCATION_SERVICE);

        //statusCheck();

        return myView;
    }

    public List<String> convertToTags(String input){
        String[] tempHashtags = input.split("#");
        List<String> hashtags = new ArrayList<>();
        for( int i=0; i<tempHashtags.length; i++ ){
            tempHashtags[i] = tempHashtags[i].replace(" ", "");
            if( !tempHashtags[i].equals("") ){
                hashtags.add( tempHashtags[i] );
            }
        }
        return hashtags;
    }



    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && takingPhoto) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                // imageBitmapLink = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                imageBitmapLabel.setText(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
        }
        try {
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
        }
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
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

    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takingPhoto = true;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void launchGallery(){
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
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

}