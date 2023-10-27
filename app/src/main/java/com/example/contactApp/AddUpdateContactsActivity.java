package com.example.contactApp;

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
import android.widget.Toast;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddUpdateContactsActivity extends AppCompatActivity {

    private Calendar selectedDateTime;
    private ImageView imageContact;
    private TextInputEditText nameContact, dobContact, emailContact;
    private boolean isEditMode = false;
    private Button btn_addContact;

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
    String id, name, dob, email;
    private MyDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_contact);

        //Test time picker
        selectedDateTime = Calendar.getInstance();
        //
        actionBar = getSupportActionBar();
        //title
        actionBar.setTitle("Add a contact");
        //back button
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageContact = findViewById(R.id.imageContact);
        nameContact = findViewById(R.id.nameContact);
        emailContact = findViewById(R.id.email);
        dobContact = findViewById(R.id.dob);
        btn_addContact = findViewById(R.id.btn_addContact);

        //get data from intent
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        //on click date, pop up calender
        dobContact.setOnTouchListener(new View.OnTouchListener() {
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
        dobContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //set data to views
        if(isEditMode){
            //Update contact
            actionBar.setTitle("Update Contact");
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("CONTACT_NAME");
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"));
            dob = intent.getStringExtra("DOB");
            email = intent.getStringExtra("EMAIL");
            //set data to views
            nameContact.setText(name);
            dobContact.setText(dob);
            emailContact.setText(email);

            //set data to images
            if(imageUri.toString().equals("null")){
                //no image, set to default image
                imageContact.setImageResource(R.drawable.ic_image_contact);
            } else {
                imageContact.setImageURI(imageUri);
            }
        } else {
            //Create
            actionBar.setTitle("Add Contact");
        }

        //init dbHelper
        dbHelper = new MyDbHelper(this);
        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

        //click image view to show image pick dialog
        imageContact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //show image pick dialog
                imagePickDialog();
            }
        });

        //click add/update button to add/update
        btn_addContact.setOnClickListener(new View.OnClickListener(){
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

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String selectedDateString = format.format(selectedDateTime.getTime());
            dobContact.setText(selectedDateString);
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void inputData(){
        //get input data
        name = nameContact.getText().toString().trim();
        dob = dobContact.getText().toString().trim();
        email = emailContact.getText().toString().trim();
        //validate
        if(validateInput())
        {
            if(isEditMode){
                //update
                dbHelper.updateContact(
                        ""+id,
                        ""+name,
                        ""+imageUri,
                        ""+dob,
                        ""+email
                );
                Toast.makeText(this, "Update Contact Successfully", Toast.LENGTH_SHORT).show();
            } else {
                //create new
                dbHelper.addContact(
                        ""+name,
                        ""+imageUri,
                        ""+dob,
                        ""+email
                );
            }
            Toast.makeText(this, "Successfully Add Contact: " + name, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(){
        boolean validateResult = true;
        if(name.trim().isEmpty())
        {
            nameContact.setError("Name is required!");
            validateResult = false;
        }
        return validateResult;
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
            imageContact.setImageURI(croppedImageUri);
        } else {
            Exception error = result.getError();
            Toast.makeText(this, "Error: " + error , Toast.LENGTH_SHORT).show();
        }
    });
}