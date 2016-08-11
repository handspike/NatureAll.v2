package com.example.gary.natureallv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraPicActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int CAMERA_REQUEST = 10;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView ivPicSelected;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    public final static int MILLISECONDS_PER_SECOND = 1000;
    public final static int MINUTE = 60 * MILLISECONDS_PER_SECOND;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private boolean permissionIsGranted = false;
    private TextView tvLatValue;
    private TextView tvLongValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_pic);
        ivPicSelected = (ImageView) findViewById(R.id.ivPicSelected);
        tvLatValue = (TextView) findViewById(R.id.tvLatValue);
        tvLongValue = (TextView) findViewById(R.id.tvLongValue);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //initialise the location request with the accuracy and frequency with which we want location updates
        locationRequest = new LocationRequest();
        locationRequest.setInterval(MINUTE);
        locationRequest.setFastestInterval(15 * MILLISECONDS_PER_SECOND);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }




    /**
     * Method called when btnFromCamera is clicked.
     * @param view
     */
    public void btnFromCameraClicked(View view){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);//
//        String pictureName = getPictureName();//
//        File imageFile = new File(pictureDirectory, pictureName);//
//        Uri pictureUri = Uri.fromFile(imageFile);//
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);//
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST){
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                ivPicSelected.setImageBitmap(cameraImage);


            }
            if (requestCode == IMAGE_GALLERY_REQUEST){
                Uri imageUri = data.getData();
                //declare a stream to read the image data.
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    ivPicSelected.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Unable to open the image",Toast.LENGTH_LONG ).show();
                }

            }
        }
    }


    @Override
    public void onConnected(Bundle bundle){requestLocationUpdates();}

    private void requestLocationUpdates(){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                }
                return;
            }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (permissionIsGranted) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (permissionIsGranted) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionIsGranted = true;
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                }
                else{
                    permissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                    tvLatValue.setText("Lat permission denied");
                    tvLongValue.setText("Long permission denied");
                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionIsGranted){
        if (googleApiClient.isConnected())
            requestLocationUpdates();
    }}


    @Override
    protected void onPause() {
        super.onPause();
        if(permissionIsGranted) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (permissionIsGranted){
    Toast.makeText(this, "Location Changed:" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
    }
    }

}



