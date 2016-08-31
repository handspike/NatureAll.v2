package com.example.gary.natureallv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.io.File;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraPicActivity extends AppCompatActivity {

    public static String URL = "http://192.168.1.15/myDocs/mainProject/res/connection.php";
    public static final String IMAGE_CAPTURE_FOLDER = "C:/xampp/htdocs/myDocs/mainProject/res/images/";
    public static final int CAMERA_REQUEST = 10;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_PIC_REQUEST = 2;
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
    private Button btnUploadPic;
    private Button btnGetGps;
    private Parcelable picUri;
    private static String _bytes64String, _imgFileName;
    private LocationManager locationManager;
    private LocationListener listener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_pic);
        _imgFileName = String.valueOf(System.currentTimeMillis());
        ivPicSelected = (ImageView) findViewById(R.id.ivPicSelected);

        tvGPSCoords = (TextView) findViewById(R.id.tvLongValue);
        btnUploadPic = (Button) findViewById(R.id.btnUploadPic);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnUploadPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getFileUri();
            }
        });
        btnGetGps = (Button)findViewById(R.id.btnGetGps);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

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





    /**
     * Method called when btnFromCamera is clicked.
     * @param view
     */
    public void btnFromCameraClicked(View view){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);//
        //String pictureName = getPictureName();//
        //File imageFile = new File(pictureDirectory, pictureName);//
        //Uri pictureUri = Uri.fromFile(imageFile);//
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);//
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    /**
     * This method is invoked when the user presses the From Gallery button.
     * @param view
     */
    public void onBtnGalleryClicked(View view){
        //Invoke the image gallery with an implicit intent.
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

    private String getPictureName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = sdf.format(new Date());
        return "natureall" + timeStamp +".jpg";
    }

    private void getFileUri() {
        image_name = getPictureName();
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + image_name);
        file_uri = Uri.fromFile(file);



    }

//    public void onBtnUploadClick(View view){
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        getFileUri();
//        i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
//        startActivityForResult(i, 10);
//        makeRequest();
//
//    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                ivPicSelected.setImageBitmap(cameraImage);


            }
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                //declare a stream to read the image data.
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    ivPicSelected.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Unable to open the image", Toast.LENGTH_LONG).show();
                }

            }


//            if(requestCode == 10 && resultCode == RESULT_OK) {
//                new Encode_image().execute();
//            }

        }
    }
/*Possible way to recover pics after saving to external storage*/
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//
//        outState.putParcelable("picUri", picUri);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);

//        picUri= savedInstanceState.getParcelable("picUri");

//    }
    /*End of/Possible way to recover pics after saving to external storage*/

//    private class Encode_image extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//
//            byte [] array = stream.toByteArray();
//            encoded_string = Base64.encodeToString(array, 0);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            makeRequest();
//        }
//    }

//    private void makeRequest() {
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.1.15/myDocs/mainProject/res/connection.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams ()throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("encoded_string", encoded_string);
//                map.put("image_name", image_name);
//                return map;
//            }
//
//        };
//        requestQueue.add(request);
//    }





}



