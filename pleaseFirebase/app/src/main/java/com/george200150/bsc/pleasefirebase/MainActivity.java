package com.george200150.bsc.pleasefirebase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.george200150.bsc.pleasefirebase.model.Bitmap;
import com.george200150.bsc.pleasefirebase.model.ForwardMessage;
import com.george200150.bsc.pleasefirebase.model.Token;
import com.george200150.bsc.pleasefirebase.service.APIService;
import com.george200150.bsc.pleasefirebase.util.ApiUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static TextView mResponseTv2; // TODO: CREATED MEMORY LEAK JUST FOR TESTING PURPOSES !!!
    private TextView mResponseTv;
    private static ImageView imageView; // TODO: new memory leak... (testing...)
    private android.graphics.Bitmap photo;
    private Button button;
    private Button button_upload;

    static final int REQUEST_TAKE_PHOTO = 11;
    static final int STORAGE_PERMISSION_CODE = 1;

    private String currentPhotoPath;
    private APIService mAPIService;

    private static Context mContext; // TODO: CREATED MEMORY LEAK JUST FOR TESTING PURPOSES !!!

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    private static int height;
    private static int width;
    private static List<Integer> ListOfAllPixelsToBeGathered = new ArrayList<>();


    public static void doToast(String payload, String count, String size) { // TODO: CREATED MEMORY LEAK JUST FOR TESTING PURPOSES !!!
        int index = Integer.parseInt(count);
        Log.d(TAG, "doToast: int index = " + index);
        mResponseTv2.setText(Integer.toString(index));

        String[] sizes = size.split(",");
        height = Integer.parseInt(sizes[0]);
        width = Integer.parseInt(sizes[1]);
        Log.d(TAG, "doToast: int height = " + height);
        Log.d(TAG, "doToast: int width = " + width);

        Log.d(TAG, "P A Y L O A D = " + payload);
//        String[] pixels = content.split(",");
//        Toast.makeText(MainActivity.getContext(), payload, Toast.LENGTH_LONG).show();
        String[] RGB = payload.split(",");

        int indexRGB = 0;
        while (indexRGB + 2 < RGB.length) {
            int red = Integer.parseInt(RGB[indexRGB]);
            int green = Integer.parseInt(RGB[indexRGB+1]);
            int blue = Integer.parseInt(RGB[indexRGB+2]);

            ListOfAllPixelsToBeGathered.add(red);
            ListOfAllPixelsToBeGathered.add(green);
            ListOfAllPixelsToBeGathered.add(blue);

            indexRGB += 3;
        }
    }


    public static void f() {
        List<Integer> intPixels = ListOfAllPixelsToBeGathered;

        int[] primitives = intPixels.stream().mapToInt(Integer::intValue).toArray();
        Log.d(TAG, "doToast: primitives.len = " + primitives.length);

        //convert sparse RGB to Color(R,G,B)
        int[] array = new int[3 * width * height];
        int index = 0;
        int arrayIndex = 0;
        while (index + 2 < primitives.length) {
            int red = primitives[index];
            int green = primitives[index + 1];
            int blue = primitives[index + 2];
            array[arrayIndex] = Color.rgb(red, green, blue);
            index += 3;
            arrayIndex += 1;
        }

        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565);
        bitmap.setPixels(array, 0, width, 0, 0, width, height);

        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        drawable.setFilterBitmap(false);
        imageView.setImageDrawable(drawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setContext(this);
        setContentView(R.layout.activity_main);

        Button submitBtn = (Button) findViewById(R.id.btn_submit);
        button = (Button) findViewById(R.id.button);
        button_upload = (Button) findViewById(R.id.button_upload);
        imageView = (ImageView) findViewById(R.id.imageView);
        mResponseTv = (TextView) findViewById(R.id.tv_response);
        mResponseTv2 = (TextView) findViewById(R.id.tv_response2);

        mAPIService = ApiUtils.getAPIService();

        button.setOnClickListener(view -> dispatchTakePictureIntent(view));

        button_upload.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            }
            dispatchOpenFileIntent(view);
        });

        submitBtn.setOnClickListener(view -> {
            if (photo != null) {
                sendPost(photo);
                imageView.setImageBitmap(photo);
            }
        });
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
        Toast.makeText(this, R.string.mssg_error_submitting_post, Toast.LENGTH_SHORT).show();
    }

    private void dispatchOpenFileIntent(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, STORAGE_PERMISSION_CODE);
    }

    private void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public android.graphics.Bitmap getResizedBitmap(android.graphics.Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

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

            BitmapFactory.Options options = new BitmapFactory.Options();
            android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

            System.out.println(currentPhotoPath);
            Log.d(TAG, "onActivityResult: currentPhotoPath = " + currentPhotoPath);
            photo = this.getResizedBitmap(bitmap, 500); // resize, big pictures are hard to be transported
            imageView.setImageBitmap(photo);
        } else if (requestCode == STORAGE_PERMISSION_CODE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            // String picturePath contains the path of selected Image

            System.out.println(picturePath);
            Log.d(TAG, "onActivityResult: picturePath = " + picturePath);

            // WET code
            BitmapFactory.Options options = new BitmapFactory.Options();
            android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);

            photo = this.getResizedBitmap(bitmap, 500); // resize, big pictures are hard to be transported
            imageView.setImageBitmap(photo);
        }
    }

    public void sendPost(android.graphics.Bitmap androidBitmap) {
        Bitmap bitmap = new Bitmap();

        int h = androidBitmap.getHeight();
        int w = androidBitmap.getWidth();
        Log.d(TAG, "sendPost: HEIGHT = " + h); // TODO: WHY DOES BITMAP HAVE 2x1 AND THERE ARE MORE PIXELS (3x2)
        Log.d(TAG, "sendPost: WIDTH = " + w);

        bitmap.setHeight(h);
        bitmap.setWidth(w);

        int[] androidPixels = new int[h * w];
        androidBitmap.getPixels(androidPixels, 0, w, 0, 0, w, h);

        int[] pixels = new int[3 * h * w]; // reduce bitmap dimensionality even more
        int index = 0;
        for (int intPix : androidPixels) {
            int r = (intPix >> 16) & 0xff;
            int g = (intPix >> 8) & 0xff;
            int b = intPix & 0xff;

            pixels[index] = (int) r;
            pixels[index + 1] = (int) g;
            pixels[index + 2] = (int) b;
            index += 3;
        }
        bitmap.setPixels(pixels);

        Integer method = 0; // TODO: customise backend ML architecture.

        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        ForwardMessage forwardMessage = new ForwardMessage(bitmap, token, method);
        subscribeToTopic(token.getMessage());

        mAPIService.sendBitmapPOST(forwardMessage).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Token token = response.body();
                    Log.i(TAG, "post submitted to API." + token.toString());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "Unable to submit post to API: {}", t);
            }
        });
    }

    private void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        Log.d(TAG, "subscribeToTopic: " + topic);
    }

    public void showResponse(String response) {
        if (mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
//        mResponseTv.setText(response);
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
