package com.example.shujabits_assignment;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private FloatingActionButton fab;
    private MyImagesViewModel myImagesViewModel;
    private ActivityResultLauncher<Intent>activityResultLauncherForAddImage;
    private ActivityResultLauncher<Intent>activityResultLauncherForUpdateImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("ShujaBITS Assignment (ABID)");
        setContentView(R.layout.activity_main);

        registerActivityForUpdateImage();
        registerActivityForAddImage();
        rv = findViewById(R.id.rv);
        fab = findViewById(R.id.fab);
        rv.setLayoutManager(new LinearLayoutManager(this));
        MyImagesAdapter adapter = new MyImagesAdapter();
        rv.setAdapter(adapter);

        myImagesViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(MyImagesViewModel.class);
        myImagesViewModel.getAllImages().observe(MainActivity.this, new Observer<List<MyImages>>() {
            @Override
            public void onChanged(List<MyImages> myImages) {

                adapter.setImagesList(myImages);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,"Please Select an Image",Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(MainActivity.this, AddImageActivity.class);

                activityResultLauncherForAddImage.launch(intent);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0
                , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(MainActivity.this,"Image Deleted Successfully",Toast.LENGTH_SHORT).show();

                myImagesViewModel.delete(adapter.getPosition(viewHolder.getBindingAdapterPosition()));
            }
        }).attachToRecyclerView(rv);

        adapter.setListener(new MyImagesAdapter.onImageClickListener() {
            @Override
            public void onImageClick(MyImages myImages) {

                Intent intent = new Intent(MainActivity.this,UpdateImageActivity.class);
                intent.putExtra("id",myImages.getImage_id());
                intent.putExtra("title",myImages.getImage_title());
                intent.putExtra("description",myImages.getImage_description());
                intent.putExtra("image",myImages.getImage());
                activityResultLauncherForUpdateImage.launch(intent);
            }
        });
    }
    public void registerActivityForUpdateImage() {

        activityResultLauncherForUpdateImage =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                        , new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {

                                int resultCode = result.getResultCode();
                                Intent data = result.getData();

                                if (resultCode == RESULT_OK && data != null) {

                                    String title = data.getStringExtra("updateTitle");
                                    String description = data.getStringExtra("updateDescription");
                                    byte[] image = data.getByteArrayExtra("image");
                                    int id = data.getIntExtra("id", -1);

                                    MyImages myImages = new MyImages(title, description, image);
                                    myImages.setImage_id(id);
                                    myImagesViewModel.update(myImages);
                                }
                            }
                        });
    }
    public void registerActivityForAddImage() {

            activityResultLauncherForAddImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == RESULT_OK && data != null) {

                        String title = data.getStringExtra("title");
                        String description = data.getStringExtra("description");
                        byte[] image = data.getByteArrayExtra("image");

                        MyImages myImages = new MyImages(title, description, image);
                        myImagesViewModel.insert(myImages);

                    }
                }


            });

    }
    }

