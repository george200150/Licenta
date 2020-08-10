package com.george200150.bsc.pleasefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
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

    private APIService mAPIService;

    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }
    public static void setContext(Context context) {
        mContext = context;
    }

    public static void doToast(String PLANT){
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
                //android.graphics.Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                //if (bitmap != null) {
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



    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            this.photo = imageBitmap;
        }
    }

    public void sendPost(android.graphics.Bitmap androidBitmap) { // TODO: if camera is rotated other than landscape, w <-> h and python crashes
        Bitmap bitmap = new Bitmap();

        int h = androidBitmap.getHeight();
        int w = androidBitmap.getWidth();
        Log.d(TAG, "sendPost: HEIGHT = " + h);
        Log.d(TAG, "sendPost: WIDTH = " + w);

        bitmap.setHeight(h);
        bitmap.setWidth(w);

        int[] androidPixels = new int[h*w];
        androidBitmap.getPixels(androidPixels, 0, w, 0, 0, w, h);

        // build Pixel object from int pixel
        List<Pixel> pixels = new ArrayList<>();
        for (int intPix : androidPixels){
            int r = (intPix >> 16) & 0xff;
            int g = (intPix >> 8) & 0xff;
            int b = intPix & 0xff;

            Pixel pixel = new Pixel();
            pixel.setR(r);
            pixel.setG(g);
            pixel.setB(b);
            pixels.add(pixel);
        }
        //

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

//        // inline stub for integration testing
//        Bitmap bitmap = new Bitmap();
//        /*bitmap.setHeight(9);
//        bitmap.setWidth(9);*/
//        bitmap.setHeight(3);
//        bitmap.setWidth(3);
//        List<Pixel> pixels = new ArrayList<>();
//        Pixel white = new Pixel();
//        white.setR(255);
//        white.setG(255);
//        white.setB(255);
//
//        Pixel black = new Pixel();
//        black.setR(0);
//        black.setG(0);
//        black.setB(0);
//
//        pixels.add(white);
//        pixels.add(black);
//        pixels.add(white);
//        pixels.add(black);
//        pixels.add(white);
//        pixels.add(black);
//        pixels.add(white);
//        pixels.add(black);
//        pixels.add(white);

        /*pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);*/

        bitmap.setPixels(pixels);
        // change of plan - token will be generated by the client

        Token token = new Token(bitmap.hashCode() + "_TOKEN_" + System.nanoTime());
        ForwardMessage forwardMessage = new ForwardMessage(bitmap, token);
        subscribeToTopic(token.getMessage());

        mAPIService.sendBitmapPOST(forwardMessage).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if(response.isSuccessful()) {
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

    private void subscribeToTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        Log.d(TAG, "subscribeToTopic: " + topic);
    }

    public void showResponse(String response) {
        if(mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);
    }
}
