package com.example.workit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.room.Database;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Profile extends AppCompatActivity {

    public static final String TAG = "TAG";
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    TextView profile_name, dob, edit, sex;
    ImageView profile_pic;
    DatePickerDialog.OnDateSetListener setListener;
    EditText first, last, height, weight;
    String _first, _last, _dob, _sex, _height, _weight, _goal, _image;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String email;
    String currentPhotoPath;
    Context con = this;
    Button edit_pic;
    boolean pic_taken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        email = fAuth.getCurrentUser().getEmail();

        profile_pic = findViewById(R.id.userIV);
        profile_name = findViewById(R.id.name);
        first = findViewById(R.id.edt_first_name);
        edit = findViewById(R.id.edit_profile);
        last = findViewById(R.id.edt_last_name);
        dob = findViewById(R.id.edt_dob);
        sex = findViewById(R.id.edt_sex);
        height = findViewById(R.id.edt_height);
        weight = findViewById(R.id.edt_weight);
        edit_pic = findViewById(R.id.edt_pic);

        edit_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });

        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Profile.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                mBuilder.setTitle("");
                Spinner spinner = (Spinner) mView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Profile.this,
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sex.setText(spinner.getSelectedItem().toString());
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Profile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month,
                        day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int _month = preferences.getInt("month", 1) - 1;
                datePickerDialog.updateDate(preferences.getInt("year", 2000),
                        _month, preferences.getInt("day", 1));
                datePickerDialog.show();
            }
        });
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                dob.setText(date);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("day", dayOfMonth);
                editor.putInt("month", month);
                editor.putInt("year", year);
                editor.apply();
            }
        };
    }

    public void back (View view) {
        finish();
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {

        DocumentReference documentReference = fstore.collection("users").document(email);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                first.setText(value.getString("firstName"));
                last.setText(value.getString("lastName"));
                dob.setText(value.getString("dob"));
                sex.setText(value.getString("sex"));
                height.setText(value.getString("height"));
                weight.setText(value.getString("weight"));
                _image = value.getString("picture");
//                if (_image == null) {
//                    Uri getImage = Uri.parse("android.resource://com.example.workit/drawable/mt_img");
//                    profile_pic.setImageURI(getImage);
//                }
//                else {
//                    Uri getImage = Uri.parse(_image);
//                    profile_pic.setImageURI(getImage);
//                }
                profile_name.setText(value.getString("firstName") + " " + value.getString("lastName"));
            }
        });

        _first = first.getText().toString();
        _last = last.getText().toString();
        _dob = dob.getText().toString();
        _sex = sex.getText().toString();
        _height = height.getText().toString();
        _weight = weight.getText().toString();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
        _image = preferences.getString("profile_img", "");
        Uri getImage = Uri.parse(_image);
        profile_pic.setImageURI(getImage);
    }

    public void takePic() {
        new AlertDialog.Builder(con)
                .setTitle("Choose")
                .setMessage("Take a new picture or choose one from gallery")
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askCameraPermissions();
                    }
                })
                .setNegativeButton("From Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gallery();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void gallery() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                Uri image = Uri.fromFile(f);
                profile_pic.setImageURI(image);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("profile_img", image.toString());
                editor.apply();

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(image);
                this.sendBroadcast(mediaScanIntent);
                pic_taken = true;
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                profile_pic.setImageURI(contentUri);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("profile_img", contentUri.toString());
                editor.apply();
                pic_taken = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void edit(View view) {
        String edit_text = edit.getText().toString();
        if (edit_text.equals("Edit Profile")) {
            edit.setText("Done");
            edit.setTextColor(Color.BLACK);
            first.setEnabled(true);
            first.setTextColor(Color.BLACK);
            last.setEnabled(true);
            last.setTextColor(Color.BLACK);
            dob.setEnabled(true);
            dob.setTextColor(Color.BLACK);
            sex.setEnabled(true);
            sex.setTextColor(Color.BLACK);
            height.setEnabled(true);
            height.setTextColor(Color.BLACK);
            weight.setEnabled(true);
            weight.setTextColor(Color.BLACK);
            edit_pic.setVisibility(View.VISIBLE);
        }
        else {
            boolean check = false;
            if (isFirstChanged())
                check = true;
            if (isLastChanged())
                check = true;
            if (isDobChanged())
                check = true;
            if (isSexChanged())
                check = true;
            if (isHeightChanged())
                check = true;
            if (isWeightChanged())
                check = true;
            if (pic_taken)
                check = true;
            if (check) {
                profile_name.setText(_first + " " + _last);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
                _first = preferences.getString("first", "John");
                _last = preferences.getString("last", "Doe");
                _dob = preferences.getString("dob", "");
                email = preferences.getString("Email", "");
                _sex = preferences.getString("sex", "");
                _height = preferences.getString("height", "5'11");
                _weight = preferences.getString("weight", "180");
                _goal = preferences.getString("goal", "");
                _image = preferences.getString("profile_img", "");

                DocumentReference documentReference = fstore.collection("users").document(email);
                Map<String, Object> user = new HashMap<>();
                user.put("firstName", _first);
                user.put("lastName", _last);
                user.put("email", email);
                user.put("dob", _dob);
                user.put("sex", _sex);
                user.put("height", _height);
                user.put("weight", _weight);
                user.put("goal", _goal);
                user.put("picture", _image);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess, user profile is created for " + email);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
                Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Data is same and cannot be updated", Toast.LENGTH_LONG).show();
            }
            first.setEnabled(false);
            first.setTextColor(Color.WHITE);
            last.setEnabled(false);
            last.setTextColor(Color.WHITE);
            dob.setEnabled(false);
            dob.setTextColor(Color.WHITE);
            sex.setEnabled(false);
            sex.setTextColor(Color.WHITE);
            height.setEnabled(false);
            height.setTextColor(Color.WHITE);
            weight.setEnabled(false);
            weight.setTextColor(Color.WHITE);
            edit.setText("Edit Profile");
            edit.setTextColor(Color.WHITE);
            edit_pic.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isWeightChanged() {
        if (!_weight.equals(weight.getText().toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("weight", weight.getText().toString());
            editor.apply();
            _weight = weight.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isHeightChanged() {
        if (!_height.equals(height.getText().toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("height", height.getText().toString());
            editor.apply();
            _height = height.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isSexChanged() {
        if (!_sex.equals(sex.getText().toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("sex", sex.getText().toString());
            editor.apply();
            _sex = sex.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isDobChanged() {
        if (!_dob.equals(dob.getText().toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("dob", dob.getText().toString());
            editor.apply();
            _dob = dob.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isLastChanged() {
        if (!_last.equals(last.getText().toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("last", last.getText().toString());
            editor.apply();
            _last = last.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isFirstChanged() {
        if (!_first.equals(first.getText().toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Profile.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("first", first.getText().toString());
            editor.apply();
            _first = first.getText().toString();
            return true;
        }
        else {
            return false;
        }
    }
}