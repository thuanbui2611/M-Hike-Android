package com.example.m_hike;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddUpdateObservationActivity extends AppCompatActivity {
    private ImageView imageObs;
    private Calendar selectedDateTime;
    private TextInputEditText name_input, comment_input, time_input;
    private boolean isEditMode = false;
    private Button btn_addObs;
    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    //arrays of permissions
    private String[] cameraPermissions; //camera and storage
    private String[] storagePermissions; //only storage
    //variables that contain data to save
    private Uri imageUri;
    //actionbar
    private ActionBar actionBar;
    //db helper
    String id, hikeID, name, comment, time, createdAt, lastUpdated;
    private MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_observation);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add a observation");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        selectedDateTime = Calendar.getInstance();
        imageObs = findViewById(R.id.imageObs);
        name_input = findViewById(R.id.nameObs);
        comment_input = findViewById(R.id.commentObs);
        time_input = findViewById(R.id.timeObs);
        btn_addObs = findViewById(R.id.btn_submitAddObs);

        //Get data pass from HikeDetailActivity
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);
        hikeID = intent.getStringExtra("HIKE_ID");

        //on click date, pop up calender
        time_input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    showDateTimePicker();
                    return true;
                }
                return false;
            }
        });
        //on click date, prevent open keyboard
        time_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(isEditMode)
        {
            //Update
            actionBar.setTitle("Update Observation");
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            time = intent.getStringExtra("TIME");
            comment = intent.getStringExtra("COMMENT");
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"));
            createdAt = intent.getStringExtra("CREATEDAT");
            lastUpdated = intent.getStringExtra("LASTUPDATED");
            //set data to views
            name_input.setText(name);
            comment_input.setText(comment);
            time_input.setText(time);
            if(imageUri.toString().equals("null")){
                //no image, set to default image
                imageObs.setImageResource(R.drawable.ic_image_hike);
            } else {
                imageObs.setImageURI(imageUri);
            }

        } else {
            //Create
            actionBar.setTitle("Create Observation");
        }

        //init dbHelper
        dbHelper = new MyDbHelper(this);
        //init permission arrays
        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        //click on imageview to show image pick dialog
        imageObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show image pick dialog
                imagePickDialog();
            }
        });
        //click add/update button to add/update the observation
        btn_addObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

    }

    public void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker datePicker, int year, int month, int dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker timePicker, int hourOfDay, int minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);

                SimpleDateFormat format = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                String selectedDateTimeString = format.format(selectedDateTime.getTime());
                time_input.setText(selectedDateTimeString);
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), false);

            timePickerDialog.show();
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void inputData() {
        //get input data
        name = name_input.getText().toString().trim();
        time = time_input.getText().toString().trim();
        comment = comment_input.getText().toString().trim();

        String timestamp = ""+ System.currentTimeMillis();
        if(isEditMode){
            //update observation
            dbHelper.updateObservation(
                    ""+id,
                    ""+hikeID,
                    ""+name,
                    ""+time,
                    ""+comment,
                    ""+imageUri,
                    ""+createdAt,
                    ""+timestamp
            );
            Toast.makeText(this, "Update Observation Successfully", Toast.LENGTH_SHORT).show();
        } else
        {
            //create observation
            dbHelper.insertObservation(
                    ""+hikeID,
                    ""+name,
                    ""+time,
                    ""+comment,
                    ""+imageUri,
                    ""+timestamp,
                    ""+timestamp
            );
        }
        Toast.makeText(this,"Successfully Add an Observation", Toast.LENGTH_SHORT).show();
    }

    private void imagePickDialog() {
        //options to display in dialog
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //handle click
                if(i==0){
                    //camera clicked
                    if(!checkCameraPermissions())
                    {
                        requestCameraPermission();
                    }
                    else {
                        //permission already granted
                        pickFromCamera();
                    }
                } else if(i==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        //create/show dialog
        builder.create().show();
    }
    private boolean checkStoragePermission(){
        //check if storage permission is enable or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        //check if camera permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //Go back when clicking Back button of actionBar
        return super.onSupportNavigateUp();
    }

    ActivityResultLauncher<Intent> pickFromCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int resultCode = activityResult.getResultCode();
                    if(resultCode == RESULT_OK){
                        // Read the image data from the file specified by imageUri
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CropImageOptions cropImageOptions = new CropImageOptions();
                        cropImageOptions.imageSourceIncludeGallery = true;
                        cropImageOptions.imageSourceIncludeCamera = true;
                        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(imageUri, cropImageOptions);
                        cropImage.launch(cropImageContractOptions);
                    }
                }
            });

    ActivityResultLauncher<Intent> pickFromGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int resultCode = activityResult.getResultCode();
                    if(resultCode == RESULT_OK){
                        Intent data = activityResult.getData();
                        CropImageOptions cropImageOptions = new CropImageOptions();
                        cropImageOptions.imageSourceIncludeGallery = true;
                        cropImageOptions.imageSourceIncludeCamera = true;
                        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(data.getData(), cropImageOptions);
                        cropImage.launch(cropImageContractOptions);
                    }
                }
            });
    private void pickFromCamera(){
        //intent to pick image from camera, the img will return in onActivityResult method
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image description");
        //put image uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera for image
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        pickFromCameraLauncher.launch(cameraIntent);
    }
    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*"); //only image
        pickFromGalleryLauncher.launch(galleryIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //result of permission allowed/denied
        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG).show();
                    }
                }
            }

            break;
        }
    }

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(
            new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    Uri croppedImageUri = result.getUriContent();
                    imageUri = croppedImageUri;
                    //set image
                    imageObs.setImageURI(croppedImageUri);
                } else {
                    Exception error = result.getError();
                    Toast.makeText(this, "Error: " + error , Toast.LENGTH_SHORT).show();
                }
            });
}