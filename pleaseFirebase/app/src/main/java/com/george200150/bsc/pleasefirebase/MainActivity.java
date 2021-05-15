package com.george200150.bsc.pleasefirebase;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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
    private android.graphics.Bitmap result;
    private Button submitBtn;
    private Button saveBtn;
    private Button button_ResNeSt;
    private Button button_MDEQ;
    private Button button_PyConv;
    private Button button_DNL;
    private Button button_HANet;
    private int disabledColor;
    private int enabledColor;

    static final int REQUEST_TAKE_PHOTO = 11;
    static final int STORAGE_PERMISSION_CODE = 1;

    private String currentPhotoPath;
    private APIService mAPIService;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private Button button_camera;
    private Button button_upload;
    private ForwardMessage message;


    public void handleFirebaseNotification(String payload) {
        // unregister from events until next image submit
        getApplicationContext().unregisterReceiver(receiver);

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
                    saveBtn.setVisibility(View.VISIBLE);
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
                Toast.makeText(getApplicationContext(), R.string.mssg_error_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBitmap(int width, int height, int[] pixels) {
        result = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        BitmapDrawable drawable = new BitmapDrawable(result);
        drawable.setFilterBitmap(false);
        imageView.setImageDrawable(drawable);
    }

    private void prepareSendPhoto() {
        if (photo != null) {
            result = null;
            submitBtn.setEnabled(false);
            submitBtn.setBackgroundColor(disabledColor);

            // prepare to receive an Intent from FirbaseMessagingService
            getApplicationContext().registerReceiver(receiver, filter);

            message = createForwardMessage(photo);

            saveBtn.setVisibility(View.GONE);
            changeInterface(View.VISIBLE, View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitBtn = (Button) findViewById(R.id.btn_submit);
        saveBtn = (Button) findViewById(R.id.btn_save);

        button_ResNeSt = (Button) findViewById(R.id.button_ResNeSt);
        button_MDEQ = (Button) findViewById(R.id.button_MDEQ);
        button_PyConv = (Button) findViewById(R.id.button_PyConv);
        button_DNL = (Button) findViewById(R.id.button_DNL);
        button_HANet = (Button) findViewById(R.id.button_HANet);

        button_camera = (Button) findViewById(R.id.button);
        button_upload = (Button) findViewById(R.id.button_upload);
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
                    Toast.makeText(context, R.string.mssg_error_notification, Toast.LENGTH_LONG).show();
                }
            }
        };
        filter = new IntentFilter(SubscriptionMessages.NOTIFICATION_ARRIVED);

        imageView.setOnClickListener(view -> changeImage());

        button_camera.setOnClickListener(this::dispatchTakePictureIntent);
        button_upload.setOnClickListener(this::dispatchOpenFileIntent);
        submitBtn.setOnClickListener(view -> prepareSendPhoto());
        saveBtn.setOnClickListener(view -> saveResult());
        button_ResNeSt.setOnClickListener(view -> sendPost(0));
        button_MDEQ.setOnClickListener(view -> sendPost(1));
        button_PyConv.setOnClickListener(view -> sendPost(2));
        button_DNL.setOnClickListener(view -> sendPost(3));
        button_HANet.setOnClickListener(view -> sendPost(4));

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    private void changeImage() {
        if (result == null) {
            return;
        }
        if (((BitmapDrawable) imageView.getDrawable()).getBitmap().equals(result)) {
            imageView.setImageBitmap(photo);
        } else {
            imageView.setImageBitmap(result);
        }
    }

    private void saveResult() {
        if (photo != null) {
            try {
                MediaStore.Images.Media.insertImage(getContentResolver(), result, "Depth_" + System.nanoTime(), "result");
                Toast.makeText(this, R.string.mssg_saved_file, Toast.LENGTH_SHORT).show();
                saveBtn.setVisibility(View.GONE);
            } catch (Exception ignored) {
                Toast.makeText(this, R.string.mssg_error_save_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ForwardMessage createForwardMessage(android.graphics.Bitmap androidBitmap) {
        Bitmap bitmap = new Bitmap();

        int h = androidBitmap.getHeight();
        int w = androidBitmap.getWidth();

        bitmap.setHeight(h);
        bitmap.setWidth(w);

        int[] androidPixels = new int[h * w];
        androidBitmap.getPixels(androidPixels, 0, w, 0, 0, w, h);

        bitmap.setPixels(androidPixels);

        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        ForwardMessage forwardMessage = new ForwardMessage(bitmap, token, null);
        subscribeToTopic(token.getMessage());
        return forwardMessage;
    }

    private void changeInterface(int firstGroupVisibility, int secondGroupVisibility) {
        button_ResNeSt.setVisibility(firstGroupVisibility);
        button_MDEQ.setVisibility(firstGroupVisibility);
        button_PyConv.setVisibility(firstGroupVisibility);
        button_DNL.setVisibility(firstGroupVisibility);
        button_HANet.setVisibility(firstGroupVisibility);

        //imageView + button_camera + button_upload + btn_submit (w/out btn_save)
        button_camera.setVisibility(secondGroupVisibility);
        button_upload.setVisibility(secondGroupVisibility);
        submitBtn.setVisibility(secondGroupVisibility);
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
                Toast.makeText(this, R.string.mssg_permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.mssg_permission_denied, Toast.LENGTH_SHORT).show();
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
        Intent openFileIntent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(openFileIntent, STORAGE_PERMISSION_CODE);
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

        if (maxSize >= width && maxSize >= height) { // no need to resize
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

            ContentResolver cR = this.getContentResolver();
            String type = cR.getType(selectedImage);
            if (type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/x-ms-bmp")) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                getResizedBitmap(picturePath);
            } else {
                Toast.makeText(this, R.string.mssg_error_bad_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getResizedBitmap(String currentPhotoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

        // ResNeSt does not support any larger images - could send bigger images and handle methods
        // on server side, but there would be too much latency to send a 4000x3000 pic and resize it
        // on server to 400x300 just for nothing.
        photo = this.resizeBitmap(bitmap, 500);
        imageView.setImageBitmap(photo);
    }

    public void sendPost(int methodCode) {
        message.setMethod(new Method(methodCode));

        changeInterface(View.GONE, View.VISIBLE);
        imageView.setVisibility(View.GONE);
        progress_loader.setVisibility(View.VISIBLE);

        mAPIService.sendBitmapPOST(message).enqueue(new Callback<Token>() {
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
