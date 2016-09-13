package com.example.gary.natureallv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.File;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.model.people.Person;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CameraPicActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    private static final int IMAGE_PERMISSION_REQUEST_CODE = 12;
    public static String URL = "http://192.168.1.15/myDocs/mainProject/res/connection.php";
    public static final String IMAGE_CAPTURE_FOLDER = "C:/xampp/htdocs/myDocs/mainProject/res/images/";
    public static final int LOCATION_REQUEST_CODE = 10;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_PIC_REQUEST = 2;
    public static final int  UPLOAD_REQUEST_CODE = 15;
    final int CAMERA_REQUEST = 25;
    final int GALLERY_REQUEST = 35;
    private ImageView ivPicSelected;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public final static int MILLISECONDS_PER_SECOND = 1000;
    public final static int MINUTE = 60 * MILLISECONDS_PER_SECOND;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private boolean permissionIsGranted = false;
    private TextView tvImageName;
    private TextView tvLatValue;
    private TextView tvGPSCoords;
    private double longitude;
    private double latitude;
    private String encoded_string, image_name;
    private Bitmap bitmap;
    private  File file;
    private Uri file_uri;
    private Uri _ImageFileUri;
    private Uri imageUri;
    private Button btnUploadPic;
    private Button btnGetGps;
    private Button btnLaunchCamera;
    private Button btnLaunchGallery;
    private Parcelable picUri;
    private static String _bytes64String, _imgFileName;
    private LocationManager locationManager;
    private LocationListener listener;
    private String capturedImage;
    String selectedPhoto;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_pic);
        _imgFileName = String.valueOf(System.currentTimeMillis());
        ivPicSelected = (ImageView) findViewById(R.id.ivPicSelected);
        tvGPSCoords = (TextView) findViewById(R.id.tvLongValue);
        btnUploadPic = (Button) findViewById(R.id.btnUploadPic);
        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        btnUploadPic.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                getFileUri();
//            }
//        });
        btnGetGps = (Button)findViewById(R.id.btnGetGps);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnLaunchCamera = (Button) findViewById(R.id.btnLaunchCamera);
        btnLaunchGallery = (Button) findViewById(R.id.btnLaunchGallery);

        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    cameraPhoto.addToGallery();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong when taking photo",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        btnLaunchGallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });

        btnUploadPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (selectedPhoto == null ||selectedPhoto.equals("")){
                    Toast.makeText(getApplicationContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(1024, 1024).getBitmap();
                    String encodedImage = ImageBase64.encode(bitmap);
                    Log.d(TAG, encodedImage);

                    HashMap<String,String> postData = new HashMap<String, String>();
                    postData.put("image", encodedImage);
                    PostResponseAsyncTask task = new PostResponseAsyncTask(CameraPicActivity.this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if(s.contains("upload_successful")){
                                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Error while uploading.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    task.execute("http://192.168.1.9/myDocs/mainProject/upload2.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Toast.makeText(getApplicationContext(), "URL error", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Toast.makeText(getApplicationContext(), "Protocol error", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Toast.makeText(getApplicationContext(), "Encoding error", Toast.LENGTH_SHORT).show();

                        }
                    });

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong when encoding photo",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        });



/**
 * This method is called when requesting the location listener
 */
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvGPSCoords.setText("");
                tvGPSCoords.append(location.getLongitude() + " " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    /**
     * Here the granting of permissions are handled
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                configure_button();}
                break;
            default:
                break;

            case  CAMERA_PIC_REQUEST:{
               //THIS MAY HAVE TO COME OUT CHECK THIS SOON!!
                    if (grantResults [0]== PackageManager.PERMISSION_GRANTED){
                        //invokeCamera();
                    }
                    else{Toast.makeText(this, getString(R.string.unable_to_invoke), Toast.LENGTH_LONG).show();

                    }
                }
                break;
            case IMAGE_GALLERY_REQUEST:{
                if (grantResults [0]== PackageManager.PERMISSION_GRANTED){
                    openImage();
                }
                else{
                    Toast.makeText(this, getString(R.string.unable_to_invoke), Toast.LENGTH_LONG).show();
                }
                break;

            }


        }
    }

    /**
     * Here the run time permissions are sought for using devices gps
     */
    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        btnGetGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });
    }



//06 Sep 'working...'

//  Temporary comment out while trying other code for camera click
//    /**
//     * Method called when btnFromCamera is clicked.
//     * @param view
//     */
//    public void btnFromCameraClicked(View view) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                invokeCamera();
//            } else {
//                String[] permissionRequested = {Manifest.permission.CAMERA};
//                requestPermissions(permissionRequested, CAMERA_PIC_REQUEST);
//
//            }
//        }
//
//    }
//
//    /**
//     * Here the native device camera is called and the image saved to sd card
//     */
//    private void invokeCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //05 Sep re allowing the u/m
//        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);//
//        String pictureName = getPictureName();//
//        File imageFile = new File(pictureDirectory, pictureName);//may need extra info here to ad gps
//        Uri pictureUri = Uri.fromFile(imageFile);//
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);//
//        //capturedImage = imageFile.getAbsolutePath();
//        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//
//    }End of Temporary comment out

    /**
     * This method is invoked when the user presses the From Gallery button.
     * @param view
     */
    public void onBtnGalleryClicked(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //Invoke the image gallery with an implicit intent.
                openImage();
            }else{
                String[] permissionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissionRequest, IMAGE_PERMISSION_REQUEST_CODE);
            }
        }


    }

    /**
     * This method opens the image gallery
     */
    private void openImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //Where to find the data.
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        //get a Uri representation.
        Uri data = Uri.parse(pictureDirectoryPath);
        //set the data and type/get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    /**
     * this method generates an individual name for the image FROM THE CAMERA INTENT ONLY!!
     * @return
     */
    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "natureall" + timeStamp +".jpg";
    }

    /**
     * this method generates a Uri to send an image to the image view
     */

    private void getFileUri() {
        image_name = getPictureName();
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + image_name);
        file_uri = Uri.fromFile(file);



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == LOCATION_REQUEST_CODE) {

            configure_button();

            }
             if (requestCode == IMAGE_GALLERY_REQUEST) {
                 imageUri = data.getData();//this may be the uri I want (placed in a field?)
                 //declare a stream to read the image data.
                 InputStream inputStream;
                 try {
                     inputStream = getContentResolver().openInputStream(imageUri);

                     Bitmap image = BitmapFactory.decodeStream(inputStream);
                     ivPicSelected.setImageBitmap(image);
                 } catch (FileNotFoundException e) {
                     Toast.makeText(this, "Unable to open the image", Toast.LENGTH_LONG).show();
                 }
                 //makeRequest();



             }
                if (requestCode == CAMERA_PIC_REQUEST){

                       // invokeCamera();
                }
            if (requestCode == UPLOAD_REQUEST_CODE){
//                InputStream inputStream;
//                try{
//                inputStream = getContentResolver().openInputStream(imageUri);
//
//                Bitmap image = BitmapFactory.decodeStream(inputStream);
//               new Encode_image().execute();}
//
//                catch (FileNotFoundException e) {
//                    Toast.makeText(this, "Unable to open the image", Toast.LENGTH_LONG).show();
//                }
                makeRequest();

            }
//TODO this has to be worked out yet
            if (requestCode == CAMERA_REQUEST){
               String photoPath =  cameraPhoto.getPhotoPath();
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    ivPicSelected.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong when loading photo",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
            if (requestCode == GALLERY_REQUEST){
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    ivPicSelected.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong when choosing photo",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            }




        }


    public void onBtnUploadClick(View view){
        Toast.makeText(this, "Upload Button Clicked", Toast.LENGTH_LONG).show();


        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //getFileUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(i, UPLOAD_REQUEST_CODE);
        makeRequest();

    }

    private class Encode_image extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = BitmapFactory.decodeFile(imageUri/*file_uri*/.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte [] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            makeRequest();
        }
    }


    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.10/myDocs/mainProject/res/connection.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams ()throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("encoded_string", encoded_string);
                map.put("image_name", image_name);
                return map;
            }

        };
        Toast.makeText(this, "At requestQueue.add", Toast.LENGTH_LONG).show();
        requestQueue.add(request);
        Toast.makeText(this, "Passed requestQueue.add", Toast.LENGTH_LONG).show();
    }


}






