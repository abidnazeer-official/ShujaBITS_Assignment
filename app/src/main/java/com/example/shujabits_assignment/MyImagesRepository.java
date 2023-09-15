package com.example.shujabits_assignment;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyImagesRepository {

    private MyImagesDao myImagesDao;
    private LiveData<List<MyImages>> imageList;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MyImagesRepository(Application application){

        MyImagesDatabase database = MyImagesDatabase.getInstance(application);
        myImagesDao = database.myImagesDao();
        imageList = myImagesDao.getAllImages();
    }

    public void insert(MyImages myImages){

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                myImagesDao.insert(myImages);
            }
        });
    }

    public void delete(MyImages myImages){

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                myImagesDao.delete(myImages);
            }
        });
    }

    public void update(MyImages myImages){

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                myImagesDao.update(myImages);
            }
        });
    }


    public LiveData<List<MyImages>> getAllImages() {
        return imageList;
    }
}
