package com.george200150.bsc.licentamobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static String SERVER_ADDRESS = "192.168.1.45"; // LAN connection.
    private static final int SERVER_PORT = 58348;

    private static final int FILE_SELECT_CODE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final short SERVER_PROCESS_AI_PIXELS = 10;

    ImageView imageView3;

    private String mImageFileLocation = "";
    private String GALLERY_LOCATION = "image_gallery";
    private File mGalleryFolder;

    public void changeIP(View view) {
        EditText ip = findViewById(R.id.ipText);
        this.SERVER_ADDRESS = ip.getText().toString();
        Toast.makeText(this, "ip set to: "+this.SERVER_ADDRESS, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("TAG", "File Uri: " + uri.toString());
                    // Get the path
                    String path = FileUtils.getPath(this, uri);
                    Log.d("TAG", "File Path: " + path);
                    // Get the file instance

                    imageView3.setImageURI(uri);
                    Bitmap bitmap =((BitmapDrawable)imageView3.getDrawable()).getBitmap();


                    int dstWidth = 28;
                    int dstHeight = 28;
                    bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);//TODO: grayscale it !!!
                    int[] pixels = extractPixels(bitmap); // todo: why 5476 pixels ???????? (I had to resize it)
                    setServerProcessAiPIXELS(pixels, bitmap.getWidth(), bitmap.getHeight());
                    //setServerProcessAi(file);

                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {

                    Bitmap bitmap = rotateImage(bitmapToGrayScale(setReducedImageSize())); //TODO: apply this to every function
                    /*int[] pixels = extractPixels(bitmap);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int imagePixelCount = width * height;//TODO: duplicate*/

                    /*for (int i = 0; i < imagePixelCount; i++){
                        int pixel = pixels[i];
                        Log.d("PIXEL", pixel + " ARGB");
                    }*/



                    int dstWidth = 28;
                    int dstHeight = 28;
                    bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                    int[] pixels = extractPixels(bitmap); // todo: why 5476 pixels ???????? (I had to resize it)
                    setServerProcessAiPIXELS(pixels, bitmap.getWidth(), bitmap.getHeight());
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void sendOneToServer(View view) {
        //ImageView one = findViewById(R.id.imageView);
        //Bitmap bitmap =((BitmapDrawable) one.getDrawable()).getBitmap();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.one);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.two);
        int dstWidth = 28;
        int dstHeight = 28;
        bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
        int[] pixels = extractPixels(bitmap); // todo: why 5476 pixels ???????? (I had to resize it)

        setServerProcessAiPIXELS(pixels, bitmap.getWidth(), bitmap.getHeight());
    }

    public void sendTwoToServer(View view) {
        /*try{
            InputStream inputStream = getResources().openRawResource(R.raw.two);
            File tempFile = File.createTempFile("pre", "suf");
            copyFile(inputStream, new FileOutputStream(tempFile));

            // Now some_file is tempFile .. do what you like
            setServerProcessAi(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't create temp file ", e);
        }*/

        //ImageView one = findViewById(R.id.imageView);
        //Bitmap bitmap =((BitmapDrawable) one.getDrawable()).getBitmap();
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.one);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.two);
        int dstWidth = 28;
        int dstHeight = 28;
        bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
        int[] pixels = extractPixels(bitmap); // todo: why 5476 pixels ???????? (I had to resize it)
        setServerProcessAiPIXELS(pixels, bitmap.getWidth(), bitmap.getHeight());
    }


    private void setServerProcessAiPIXELS(int[] pixels, int width, int height) {
        SendImagePIXELSTask task = new SendImagePIXELSTask();

        int[] dims = new int[2];
        dims[0]= width;
        dims[1]= height;

        task.doInBackground(pixels,dims);
    }


    public int[] extractPixels(Bitmap bitmap){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int imagePixelCount = width * height;

        int[] pixels = new int[imagePixelCount];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height); // The returned colors are non-premultiplied ARGB values in the ColorSpace.Named#SRGB color space.

        // pixel= -8093052  -->  A=1111 1111  R=1000 0100  G=1000 0010  B=1000 0100  -->  RGB(132, 130, 132) == light grey  (:D) yay!
        return  pixels;
    }

    public void showFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mImageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                Log.i("ORIENTATION_UNDEFINED", "0");
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                Log.i("ORIENTATION_ROTATE_90", "90");
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                Log.i("ORIENTATION_ROTATE_180", "180");
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                Log.i("ORIENTATION_ROTATE_270", "270");
                break;
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imageView3.setImageBitmap(rotatedBitmap);
        return rotatedBitmap;
    }


    private Bitmap setReducedImageSize(){
        int targetImageViewWidth = imageView3.getWidth();
        int targetImageViewHeight = imageView3.getHeight();

        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmpOptions);

        int cameraImageWidth = bmpOptions.outWidth;
        int cameraImageHeight = bmpOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmpOptions.inSampleSize = scaleFactor;
        bmpOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(mImageFileLocation, bmpOptions);
    }


    protected Bitmap bitmapToGrayScale(Bitmap colorBitmap){
        Bitmap grayscaleBitmap = Bitmap.createBitmap(
                colorBitmap.getWidth(), colorBitmap.getHeight(),
                Bitmap.Config.RGB_565);

        Canvas c = new Canvas(grayscaleBitmap);
        Paint p = new Paint();
        ColorMatrix cm = new ColorMatrix();

        cm.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        p.setColorFilter(filter);
        c.drawBitmap(colorBitmap, 0, 0, p);
        return grayscaleBitmap;
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    private final int REQUEST_CODE_ANY = 3;
    File createImageFile() throws IOException {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ANY);
        if (isStoragePermissionGranted()){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IMAGE_" + timeStamp + "_";

            File image = File.createTempFile(imageFileName,".jpg", mGalleryFolder);
            mImageFileLocation = image.getAbsolutePath();

            return image;
        }
        return null;
    }





    class SendImagePIXELSTask extends AsyncTask<int[], Void, String> {

        @Override
        protected String doInBackground(int[]... ints) {// TODO: if not found connection => DEADLOCK    // TODO: if not found connection => DEADLOCK    // TODO: if not found connection => DEADLOCK
            int[] pixelsARGB = ints[0];

            int[] dimensions = ints[1];
            int width = dimensions[0];
            int height = dimensions[1];

            for (int i=0; i<28; i++) {
                for (int j = 0; j < 28; j++)
                    System.out.print(pixelsARGB[i * 28 + j]);
                System.out.println();
            }

            Socket socket = null;
            BufferedReader reader = null;
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT); // TODO: change thins in order to send better formatted data to Python Server

                DataOutputStream socketOut = new DataOutputStream(socket.getOutputStream());     //TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO:
                socketOut.writeByte(SERVER_PROCESS_AI_PIXELS);     //TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO://TODO:
                socketOut.flush();
                socketOut.writeInt(width);
                socketOut.flush();
                socketOut.writeInt(height);
                socketOut.flush();
                for (int index = 0; index < width * height; index++) {
                    socketOut.writeInt(pixelsARGB[index]);
                    socketOut.flush();
                }

                //TODO: read if opperation has succeeded !!!

            } catch (IOException e) {
                System.err.println("Caught exception " + e.getMessage());
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                closeStreams(socket, reader);
            }
            return null;
        }
    }

    private static void closeStreams(Socket socket, BufferedReader reader) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Could not close socket!");
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Could not close reader!");
            }
        }
    }

        public void dispatchTakePictureIntent(View view) {
        /*Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);*/


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}





class FileUtils{
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}