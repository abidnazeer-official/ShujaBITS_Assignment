package com.example.shujabits_assignment;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddImageActivity extends AppCompatActivity {
    private ImageView imageViewAddImage;
    private EditText editTextAddTitle, editTextAddDescription;
    private Button buttonSave;
    ActivityResultLauncher<Intent>activityResultLauncherForSelectImage;
    private Bitmap selectedImage;
    private Bitmap scaledImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        getSupportActionBar().setTitle("Add Image");
        registerActivityForSelectImage();

        imageViewAddImage = findViewById(R.id.ImageViewAddImage);
        editTextAddTitle = findViewById(R.id.editTextAddTitle);
        editTextAddDescription = findViewById(R.id.editTextAddDescription);
        buttonSave = findViewById(R.id.btnSave);

        imageViewAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(AddImageActivity.this
                        , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                    ActivityCompat.requestPermissions(AddImageActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , 1);

                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    activityResultLauncherForSelectImage.launch(intent);
                }
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedImage == null){
                    Toast.makeText(AddImageActivity.this
                            ,"Please select an Image!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddImageActivity.this,"Image Saved Successfully",Toast.LENGTH_SHORT).show();

                    String title = editTextAddTitle.getText().toString();
                    String description = editTextAddDescription.getText().toString();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    scaledImage = makeSmall(selectedImage,300);
                    scaledImage.compress(Bitmap.CompressFormat.PNG, 50,outputStream);
                    byte[] image = outputStream.toByteArray();

                    Intent intent = new Intent();
                    intent.putExtra("title",title);
                    intent.putExtra("description",description);
                    intent.putExtra("image",image);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){

            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            activityResultLauncherForSelectImage.launch(intent);
        }
    }
    public void registerActivityForSelectImage(){

        activityResultLauncherForSelectImage
                = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                      int resultCode = result.getResultCode();
                      Intent data = result.getData();

                      if (resultCode == RESULT_OK && data != null){

                          try {
                              selectedImage = MediaStore.Images.Media
                                      .getBitmap(getContentResolver(),data.getData());

                              imageViewAddImage.setImageBitmap(selectedImage);
                          } catch (IOException e) {
                              throw new RuntimeException(e);
                          }
                      }
                    }
        });
    }
    public Bitmap makeSmall(Bitmap image,int maxSize){

        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = (float) width / (float) height;

        if (ratio > 1){

            width = maxSize;
            height = (int) (width / ratio);
        }else {

            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}