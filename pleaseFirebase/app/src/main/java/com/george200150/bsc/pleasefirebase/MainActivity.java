package com.george200150.bsc.pleasefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private APIService mAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText titleEt = (EditText) findViewById(R.id.et_title);
        final EditText bodyEt = (EditText) findViewById(R.id.et_body);
        Button submitBtn = (Button) findViewById(R.id.btn_submit);
        mResponseTv = (TextView) findViewById(R.id.tv_response);
        mResponseTv2 = (TextView) findViewById(R.id.tv_response2);

        mAPIService = ApiUtils.getAPIService();

        // TODO : DEBUG PURPOSES ONLY// TODO : DEBUG PURPOSES ONLY// TODO : DEBUG PURPOSES ONLY
        // FirebaseMessaging.getInstance().subscribeToTopic("_TOKEN_");
        // TODO : DEBUG PURPOSES ONLY// TODO : DEBUG PURPOSES ONLY// TODO : DEBUG PURPOSES ONLY

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEt.getText().toString().trim();
                String body = bodyEt.getText().toString().trim();
                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                    sendPost(title, body);
                }
            }
        });
    }

    public void showErrorMessage() {
        Toast.makeText(this, R.string.mssg_error_submitting_post, Toast.LENGTH_SHORT).show();
    }

    public void sendPost(String title, String body) {

        Bitmap bitmap = new Bitmap();
        bitmap.setHeight(3);
        bitmap.setWidth(3);
        List<Pixel> pixels = new ArrayList<>();
        Pixel white = new Pixel();
        white.setR(255);
        white.setG(255);
        white.setB(255);

        Pixel black = new Pixel();
        black.setR(0);
        black.setG(0);
        black.setB(0);

        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);
        pixels.add(black);
        pixels.add(white);

        bitmap.setPixels(pixels);
        mAPIService.sendBitmapPOST(bitmap).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if(response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Token token = response.body();
                    Log.i(TAG, "post submitted to API." + token.toString());

                    // TODO: subscribe to the "TOKEN" topic
                    String TOPIC = token.toString();

                    /////////////////////////////////////////////////////////////////////////////////////////////////////////
                    // TODO: nu da cine trebuie subscribe la topic !!!!!!!!!
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////

                    subscribeToTopic(TOPIC);
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
    }

    public void showResponse(String response) {
        if(mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);
    }
}
