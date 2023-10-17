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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class AddUpdateHikesActivity extends AppCompatActivity {

    private Calendar selectedDateTime;
    private ImageView imageHike;
    private TextInputEditText nameHike, location, dateHike, lengthHike, descriptionHike;
    private boolean isEditMode = false;
    private RadioGroup levelRadioGroup, parkingRadioGroup;
    private Button btn_addHike;

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
    String id, name, location_db, date, description, level, parkingAvailable, length, createdAt, lastUpdated;;
    private MyDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_hikes);

        //Test time picker
        selectedDateTime = Calendar.getInstance();
        //
        actionBar = getSupportActionBar();
        //title
        actionBar.setTitle("Add a hike");
        //back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageHike = findViewById(R.id.imageHike);
        nameHike = findViewById(R.id.nameHike);
        location = findViewById(R.id.location);
        dateHike = findViewById(R.id.dateHike);
        lengthHike = findViewById(R.id.lengthHike);
        descriptionHike = findViewById(R.id.descriptionHike);
        parkingRadioGroup = findViewById(R.id.parkingRadioGroup);
        levelRadioGroup = findViewById(R.id.levelRadioGroup);
        btn_addHike = findViewById(R.id.btn_addHike);


        //get data from intent
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        //on click date, pop up calender
        dateHike.setOnTouchListener(new View.OnTouchListener() {
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
        dateHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //set data to views
        if(isEditMode){
            //Update hike
            actionBar.setTitle("Update Hike");
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("HIKENAME");
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"));
            location_db = intent.getStringExtra("LOCATION");
            date = intent.getStringExtra("DATE");
            length = intent.getStringExtra("LENGTH");
            level = intent.getStringExtra("LEVEL");
            description = intent.getStringExtra("DESCRIPTION");
            parkingAvailable = intent.getStringExtra("PARKING");
            createdAt = intent.getStringExtra("CREATEDAT");
            lastUpdated = intent.getStringExtra("LASTUPDATED");
            //set data to views
            nameHike.setText(name);
            location.setText(location_db);
            dateHike.setText(date);
            lengthHike.setText(length);
            descriptionHike.setText(description);
            //set data to radio level, parking radio group
            int levelRadioCount = levelRadioGroup.getChildCount();
            for(int i = 0; i < levelRadioCount; i++){
                RadioButton levelButton = (RadioButton) levelRadioGroup.getChildAt(i);
                if(levelButton.getText().toString().equals(level)){
                    levelButton.setChecked(true);
                    break;
                }
            }
            int parkingRadioCount = parkingRadioGroup.getChildCount();
            for(int i = 0; i < parkingRadioCount; i++){
                RadioButton parkingButton = (RadioButton) parkingRadioGroup.getChildAt(i);
                if(parkingButton.getText().toString().equals(parkingAvailable)){
                    parkingButton.setChecked(true);
                    break;
                }
            }
            //set data to images
            if(imageUri.toString().equals("null")){
                //no image, set to default image
                imageHike.setImageResource(R.drawable.ic_image_hike);
            } else {
                imageHike.setImageURI(imageUri);
            }
        } else {
            //Create hike
            actionBar.setTitle("Add Hike");
        }

        //init dbHelper
        dbHelper = new MyDbHelper(this);
        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

        //click image view to show image pick dialog
        imageHike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //show image pick dialog
                imagePickDialog();
            }
        });

        //click add/update button to add/update the hike
        btn_addHike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                dateHike.setText(selectedDateTimeString);
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), false);

            timePickerDialog.show();
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void inputData(){
        //get input data
        name = nameHike.getText().toString().trim();
        location_db = location.getText().toString().trim();
        date = dateHike.getText().toString().trim();
        description = descriptionHike.getText().toString().trim();
        length = lengthHike.getText().toString();

        int selectedLevelId = levelRadioGroup.getCheckedRadioButtonId();
        if(selectedLevelId != -1)
        {
            RadioButton radioButton = findViewById(selectedLevelId);
            level = radioButton.getTag().toString();
        }
        int selectedParking = parkingRadioGroup.getCheckedRadioButtonId();
        if(selectedParking != -1)
        {
            RadioButton radioButton = findViewById(selectedParking);
            parkingAvailable = radioButton.getTag().toString();
        }
        String timestamp = "" + System.currentTimeMillis();
        if(isEditMode){
            //update hike
            dbHelper.updateHike(
                    ""+id,
                    ""+name,
                    ""+location_db,
                    ""+date,
                    ""+length,
                    ""+description,
                    ""+parkingAvailable,
                    ""+level,
                    ""+imageUri,
                    ""+createdAt,
                    ""+timestamp
            );
            Toast.makeText(this, "Update Hike Successfully", Toast.LENGTH_SHORT).show();
        } else {
            //create new hike
            dbHelper.insertHike(
                    ""+name,
                    ""+location_db,
                    ""+date,
                    ""+length,
                    ""+description,
                    ""+parkingAvailable,
                    ""+level,
                    ""+imageUri,
                    ""+timestamp,
                    ""+timestamp
            );
        }

        Toast.makeText(this, "Successfully Add Hike: " + name, Toast.LENGTH_SHORT).show();
    }

    private void imagePickDialog(){
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
            imageHike.setImageURI(croppedImageUri);
        } else {
            Exception error = result.getError();
            Toast.makeText(this, "Error: " + error , Toast.LENGTH_SHORT).show();
        }
    });
}