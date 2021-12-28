package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class MLActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    //ActivityMlactivityBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityMlactivityBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_mlactivity);
//        binding.ml.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                   requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
//               } else {
//                   openGallery();
//               }
//           }
//       });


    }

    private void openGallery() {

        Intent gallery = new Intent("android.media.action.IMAGE_CAPTURE");
//        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            try {
                returnRes(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                openGallery();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void returnRes(Bitmap bitmap) throws IOException {
        TensorImage image = TensorImage.fromBitmap(bitmap);
        ObjectDetector.ObjectDetectorOptions options = ObjectDetector
                .ObjectDetectorOptions
                .builder()
                .setMaxResults(5)
                .setScoreThreshold(0.5f)
                .build();
        ObjectDetector detector = ObjectDetector.createFromFileAndOptions(getApplicationContext(), "model_unquant.tflite", options);
        List<Detection> results = detector.detect(image);
        debugPrint(results);

    }
    private void debugPrint(List<Detection> results ) {
        for (Detection res: results) {
            RectF box = res.getBoundingBox();
            Log.d("ML", "Detected object");
            for (Category cat: res.getCategories()){
                Log.d("ML","Label: " +cat.getLabel()+ " Confidence" + cat.getScore());
            }
        }
    }

}