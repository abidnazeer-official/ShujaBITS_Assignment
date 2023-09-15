package com.example.shujabits_assignment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MyImagesViewModel extends AndroidViewModel {
    MyImagesRepository repository;
    private LiveData<List<MyImages>> imageList;
    public MyImagesViewModel(@NonNull Application application) {
        super(application);

        repository = new MyImagesRepository(application);
        imageList = repository.getAllImages();
    }

    public void insert(MyImages myImages){
        repository.insert(myImages);
    }

    public void delete(MyImages myImages){
        repository.delete(myImages);
    }

    public void update(MyImages myImages){
        repository.update(myImages);
    }

    public LiveData<List<MyImages>> getAllImages() {
        return imageList;
    }

    }
