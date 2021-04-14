package com.george200150.bsc.util;

import com.george200150.bsc.model.Bitmap;

public class BitmapFormatAdapter {
    public static Bitmap convertColorToRGB(Bitmap bitmap) {
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();
        int[] androidPixels = bitmap.getPixels();
        int[] pixels = new int[3 * h * w];
        int index = 0;
        for (int intPix : androidPixels) {
            int r = (intPix >> 16) & 0xff;
            int g = (intPix >> 8) & 0xff;
            int b = intPix & 0xff;

            pixels[index] = r;
            pixels[index + 1] = g;
            pixels[index + 2] = b;
            index += 3;
        }

        bitmap.setPixels(pixels);
        return bitmap;
    }

    public static Bitmap convertRGBToColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = bitmap.getPixels();

        //convert flat RGB to Color(R,G,B)
        int[] array = new int[3 * width * height];
        int index = 0;
        int arrayIndex = 0;
        while (index + 2 < pixels.length) {
            int red = pixels[index];
            int green = pixels[index + 1];
            int blue = pixels[index + 2];
            array[arrayIndex] = rgb(red, green, blue);
            index += 3;
            arrayIndex += 1;
        }

        bitmap.setPixels(array);
        return bitmap;
    }

    private static int rgb(int red, int green, int blue) {
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }
}
