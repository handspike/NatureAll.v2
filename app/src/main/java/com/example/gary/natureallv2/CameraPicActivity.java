package com.example.gary.natureallv2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraPicActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView ivPicSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_pic);
        ivPicSelected = (ImageView) findViewById(R.id.ivPicSelected);
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




    }



