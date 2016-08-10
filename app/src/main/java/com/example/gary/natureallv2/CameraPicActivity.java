package com.example.gary.natureallv2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CameraPicActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
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
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_REQUEST){
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data");
                ivPicSelected.setImageBitmap(cameraImage);
                

            }
        }
    }
}
