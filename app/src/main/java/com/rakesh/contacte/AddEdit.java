package com.rakesh.contacte;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddEdit extends AppCompatActivity {
    EditText editName;
    EditText editNumber;
    String name, number;
    int id;
    Intent i;
    Toolbar bar;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageButton imageSelect;
    String userChosenTask;
    DataBaseHandler db;
    ByteArrayOutputStream bytes;
    Bitmap thumbnail = null;
    Bundle editData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        db = new DataBaseHandler(this);
        editName = (EditText)findViewById(R.id.editName);
        editNumber = (EditText)findViewById(R.id.editNumber);
        imageSelect = (ImageButton) findViewById(R.id.imageSelect);
        bar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(bar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("");
        }
        thumbnail = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_contact);
        bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        imageSelect.setImageBitmap(thumbnail);

        i = new Intent(this, MainActivity.class);

        editData = getIntent().getExtras();
        if(editData!=null) {
            editName.setText(editData.getString("name"));
            editNumber.setText(editData.getString("number"));
            id = editData.getInt("id");
            ByteArrayInputStream imageStream = new ByteArrayInputStream(editData.getByteArray("image"));
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            imageSelect.setImageBitmap(theImage);
        }

        imageSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editName.getText().toString().trim();
                number = editNumber.getText().toString().trim();
                if(name.isEmpty()||number.isEmpty()) {
                    Snackbar.make(view, "Please fill the name and number", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    if(id!=0) {
                        db.delContact(db.getContact(id));
                    }
                    byte[] imageInByte = bytes.toByteArray();
                    db.addContact(new Contact(name, number, imageInByte));
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(editData!=null) {
            db.delContact(db.getContact(id));
        }
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEdit.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(AddEdit.this);

                if (items[item].equals("Take Photo")) {
                    userChosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    System.exit(0);
                }
                break;
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        bytes = new ByteArrayOutputStream();
        if (thumbnail!=null){
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        }
        imageSelect.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        imageSelect.setImageBitmap(thumbnail);
    }
}
