package com.george200150.bsc.pleasefirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.george200150.bsc.pleasefirebase.model.Bitmap;
import com.george200150.bsc.pleasefirebase.model.ForwardMessage;
import com.george200150.bsc.pleasefirebase.model.Pixel;
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
    private ImageView imageView;
    private android.graphics.Bitmap photo;
    private Button button;

    static final int REQUEST_TAKE_PHOTO = 1;

    private String currentPhotoPath;
    private APIService mAPIService;

    private static Context mContext; // TODO: CREATED MEMORY LEAK JUST FOR TESTING PURPOSES !!!

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void doToast(String PLANT) { // TODO: CREATED MEMORY LEAK JUST FOR TESTING PURPOSES !!!
        mResponseTv2.setText(PLANT);
        Toast.makeText(MainActivity.getContext(), PLANT, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MainActivity.setContext(this);
        setContentView(R.layout.activity_main);

        final EditText titleEt = (EditText) findViewById(R.id.et_title);
        final EditText bodyEt = (EditText) findViewById(R.id.et_body);
        Button submitBtn = (Button) findViewById(R.id.btn_submit);
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        mResponseTv = (TextView) findViewById(R.id.tv_response);
        mResponseTv2 = (TextView) findViewById(R.id.tv_response2);

        mAPIService = ApiUtils.getAPIService();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(view);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photo != null) {
                    sendPost(photo);
                    imageView.setImageBitmap(photo);
                }
            }
        });
    }

    public void showErrorMessage() {
        Toast.makeText(this, R.string.mssg_error_submitting_post, Toast.LENGTH_SHORT).show();
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
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return android.graphics.Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

            photo = this.getResizedBitmap(bitmap, 500); // resize, big pictures are hard to be transported
            imageView.setImageBitmap(photo);
        }
    }

    public void sendPost(android.graphics.Bitmap androidBitmap) {
        Bitmap bitmap = new Bitmap();

        int h = androidBitmap.getHeight();
        int w = androidBitmap.getWidth();
        Log.d(TAG, "sendPost: HEIGHT = " + h);
        Log.d(TAG, "sendPost: WIDTH = " + w);

        bitmap.setHeight(h);
        bitmap.setWidth(w);

        int[] androidPixels = new int[h * w];
        androidBitmap.getPixels(androidPixels, 0, w, 0, 0, w, h);

        // build Pixel object from int pixel
        List<Pixel> pixels = new ArrayList<>();
        for (int intPix : androidPixels) {
            int r = (intPix >> 16) & 0xff;
            int g = (intPix >> 8) & 0xff;
            int b = intPix & 0xff;

            Pixel pixel = new Pixel();
            pixel.setR(r);
            pixel.setG(g);
            pixel.setB(b);
            pixels.add(pixel);
        }

        // TESTING... //
        /*int[] revertedPixels = new int[h*w];
        int index = 0;
        for (Pixel pixel : pixels){
            int rgbPix = 0xFFFFFF;
            rgbPix += pixel.getR() << 16;
            rgbPix += pixel.getG() << 8;
            rgbPix += pixel.getB();
            revertedPixels[index] = rgbPix;
            index ++;
        }*/

        // TESTING... //
        /*DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.graphics.Bitmap revertedBitmap = android.graphics.Bitmap.createBitmap(metrics, w, h, android.graphics.Bitmap.Config.valueOf("ARGB_8888"));
        revertedBitmap.setPixels(revertedPixels, 0, w, 0, 0, w, h);
        imageView.setImageBitmap(revertedBitmap);*/

        bitmap.setPixels(pixels);

        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        ForwardMessage forwardMessage = new ForwardMessage(bitmap, token);
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
