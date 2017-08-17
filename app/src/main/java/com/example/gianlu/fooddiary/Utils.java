package com.example.gianlu.fooddiary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by gianlu on 13/08/2017.
 */

public class Utils {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public void copyAppDbToDownloadFolder(Activity curr_activity, Context curr_context, String dbName) throws IOException {
        // check System permission
        permissionCheck(curr_activity);

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String backupFileName = "Food Diary " + currentDateTimeString + ".db";
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), backupFileName); // for example "my_data_backup.db"
        File currentDB = curr_context.getDatabasePath(dbName); //databaseName=your current application database name, for example "my_data.db"
        copyFile(currentDB, backupDB);
    }

    public void importAppDbFromFile(Activity curr_activity, final Context curr_context, final String dbName) throws IOException {
        // check system permission
        permissionCheck(curr_activity);

        // get file from dialog box
        File mPath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        FileDialog fileDialog = new FileDialog(curr_activity, mPath, ".db");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {

            //copy file into app data and overwrite db
            public void fileSelected(File importDbFile) {
                Log.d(getClass().getName(), "selected file " + importDbFile.toString());
                copyFile(importDbFile, curr_context.getDatabasePath(dbName));
            }
        });

        fileDialog.showDialog();
    }

    private boolean copyFile(File origin_file, File destination_file) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(origin_file);
        } catch (FileNotFoundException e) {
            Log.i("Error", " Origin file not found");
            e.printStackTrace();
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destination_file);
        } catch (FileNotFoundException e) {
            Log.i("Error", " Can't create destination file");
            e.printStackTrace();
            return false;
        }
        try {
            fos.getChannel().transferFrom(fis.getChannel(), 0, fis.getChannel().size());
        } catch (IOException e) {
            Log.i("Error", " I/O Exception when copying file");
            e.printStackTrace();
            return false;
        }
        try {
            fis.close();
        } catch (IOException e) {
            Log.i("Error", " I/O Exception when copying file");
            e.printStackTrace();
            return false;
        }
        try {
            fos.close();
        } catch (IOException e) {
            Log.i("Error", " I/O Exception when closing destination file");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private void permissionCheck(Activity a){
        int permissionCheck = ContextCompat.checkSelfPermission(a,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(a,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(a,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



        }
}