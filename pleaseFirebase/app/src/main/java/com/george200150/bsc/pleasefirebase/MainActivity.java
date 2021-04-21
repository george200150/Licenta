package com.george200150.bsc.pleasefirebase;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.george200150.bsc.pleasefirebase.model.Bitmap;
import com.george200150.bsc.pleasefirebase.model.ForwardMessage;
import com.george200150.bsc.pleasefirebase.model.Method;
import com.george200150.bsc.pleasefirebase.model.SubscriptionMessages;
import com.george200150.bsc.pleasefirebase.model.Token;
import com.george200150.bsc.pleasefirebase.service.APIService;
import com.george200150.bsc.pleasefirebase.util.ApiUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public TextView mResponseTv2;
    private TextView mResponseTv;
    private ProgressBar progress_loader;
    private ImageView imageView;
    private android.graphics.Bitmap photo;
    private Button submitBtn;
    private EditText textNet;
    private Method method;
    private static int disabledColor;
    private static int enabledColor;

    static final int REQUEST_TAKE_PHOTO = 11;
    static final int STORAGE_PERMISSION_CODE = 1;

    private String currentPhotoPath;
    private static APIService mAPIService;
    private BroadcastReceiver receiver;
    private IntentFilter filter;


    public void handleFirebaseNotification(String payload) {
        getApplicationContext().unregisterReceiver(receiver); // unregister from events until next image submit

        mResponseTv2.setText(payload);
        mAPIService.sendFetchPOST(payload).enqueue(new Callback<Bitmap>() {
            @Override
            public void onResponse(Call<Bitmap> call, Response<Bitmap> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = response.body();
                    submitBtn.setEnabled(true);
                    submitBtn.setBackgroundColor(enabledColor);
                    imageView.setVisibility(View.VISIBLE);
                    progress_loader.setVisibility(View.GONE);
                    displayBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getPixels());
                } else { // thrown exception via Advice
                    String errorMessage = "ERROR!";
                    try {
                        errorMessage = response.errorBody().string().split(":")[1]; // remove package name
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Bitmap> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "SOMETHING UNEXPECTED HAPPENED...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBitmap(int width, int height, int[] pixels) {
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        drawable.setFilterBitmap(false);
        imageView.setImageDrawable(drawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitBtn = (Button) findViewById(R.id.btn_submit);
        textNet = (EditText) findViewById(R.id.textNet);
        Button button = (Button) findViewById(R.id.button);
        Button button_upload = (Button) findViewById(R.id.button_upload);
        imageView = (ImageView) findViewById(R.id.imageView);
        progress_loader = (ProgressBar) findViewById(R.id.progress_loader);
        mResponseTv = (TextView) findViewById(R.id.tv_response);
        mResponseTv2 = (TextView) findViewById(R.id.tv_response2);

        mAPIService = ApiUtils.getAPIService();

        disabledColor = ResourcesCompat.getColor(getResources(), R.color.colorAccentDisabled, null);
        enabledColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String payload = intent.getStringExtra("RESOURCE");
                if (payload != null) {
                    handleFirebaseNotification(payload);
                } else {
                    Toast.makeText(context, "Notification was empty!", Toast.LENGTH_LONG).show();
                }
            }
        };
        filter = new IntentFilter(SubscriptionMessages.NOTIFICATION_ARRIVED);

        button.setOnClickListener(this::dispatchTakePictureIntent);
        button_upload.setOnClickListener(this::dispatchOpenFileIntent);
        submitBtn.setOnClickListener(view -> {
            if (photo != null) {
                submitBtn.setEnabled(false);
                submitBtn.setBackgroundColor(disabledColor);

                getApplicationContext().registerReceiver(receiver, filter); // prepare to receive an Intent from FirbaseMessagingService

                sendPost(photo); // todo: do in background task

                imageView.setImageBitmap(photo);
                imageView.setVisibility(View.GONE);
                progress_loader.setVisibility(View.VISIBLE);
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            getApplicationContext().unregisterReceiver(receiver);
        } catch (RuntimeException e) { // avoid memory leaks (maybe also resubscribe onStart)
            e.printStackTrace();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed for accessing the Gallery.")
                    .setPositiveButton("Ok", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showErrorMessage() {
        submitBtn.setEnabled(true);
        submitBtn.setBackgroundColor(enabledColor);
        imageView.setVisibility(View.VISIBLE);
        progress_loader.setVisibility(View.GONE);
        Toast.makeText(this, R.string.mssg_error_submitting_post, Toast.LENGTH_SHORT).show();
    }

    private void dispatchOpenFileIntent(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, STORAGE_PERMISSION_CODE);
    }

    private void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private android.graphics.Bitmap resizeBitmap(android.graphics.Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (maxSize >= width && maxSize >= height ) { // no need to resize
            return image;
        }

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = Math.max(1, (int) (width / bitmapRatio));
        } else {
            height = maxSize;
            width = Math.max(1, (int) (height * bitmapRatio));
        }
        return android.graphics.Bitmap.createScaledBitmap(image, width, height, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            getResizedBitmap(currentPhotoPath);
        } else if (requestCode == STORAGE_PERMISSION_CODE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            getResizedBitmap(picturePath);
        }
    }

    private void getResizedBitmap(String currentPhotoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

        String networkName = textNet.getText().toString().toLowerCase();

        // set maximum size according to the used network
        if (networkName.equals("resnest")) {
            photo = this.resizeBitmap(bitmap, 500); // ResNeSt does not support any larger images
            method = new Method(0);
        } else if (networkName.equals("mdeq")) {
            photo = this.resizeBitmap(bitmap, 2048); // MDEQ does. (long network delay)
            method = new Method(1);
        } else {
            Toast.makeText(this, "Please select the Network first!", Toast.LENGTH_SHORT).show();
            method = new Method(-1);
            return;
        }
        imageView.setImageBitmap(photo);
    }

    public void sendPost(android.graphics.Bitmap androidBitmap) {
        Bitmap bitmap = new Bitmap();

        int h = androidBitmap.getHeight();
        int w = androidBitmap.getWidth();

        bitmap.setHeight(h);
        bitmap.setWidth(w);

        int[] androidPixels = new int[h * w];
        androidBitmap.getPixels(androidPixels, 0, w, 0, 0, w, h);

        bitmap.setPixels(androidPixels);

        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        ForwardMessage forwardMessage = new ForwardMessage(bitmap, token, method);
        subscribeToTopic(token.getMessage());

        mAPIService.sendBitmapPOST(forwardMessage).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                showErrorMessage();
            }
        });
    }

    private void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public void showResponse(String response) {
        if (mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
