package com.george200150.bsc.persistence;

import com.george200150.bsc.exception.ImageLoadException;
import com.george200150.bsc.exception.ImageSaveException;
import com.george200150.bsc.model.Bitmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ImageDAO {
    public static String saveImage(int width, int height, int[] flattenedPixels) {
        String timeStamp = Long.toString(System.currentTimeMillis());
        String pathname = "client_images\\" + timeStamp + "_" + Arrays.hashCode(flattenedPixels) + "_image";

        writeArray(pathname + ".txt", flattenedPixels);
        writeArray(pathname + ".csv", new int[]{width, height});

        return pathname;
    }

    private static void writeArray(String filename, int[] x) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename))) {
            for (int value : x) {
                outputWriter.write(Integer.toString(value));
                outputWriter.newLine();
            }
            outputWriter.flush();
        } catch (IOException e) {
            throw new ImageSaveException("Image failed to be saved!");
        }
    }

    public static Bitmap readImage(String pathname) {
        int[] pixels = readArray(pathname + ".txt");
        int[] whValues = readArray(pathname + ".csv");

        int width = whValues[0];
        int height = whValues[1];

        return new Bitmap(height, width, pixels);
    }

    private static int[] readArray(String pathname) {
        try (Scanner scanner = new Scanner(new File(pathname))) {
            List<Integer> readIntegersList = new ArrayList<>();
            while (scanner.hasNextInt()) {
                readIntegersList.add(scanner.nextInt());
            }
            return readIntegersList.stream().mapToInt(Integer::valueOf).toArray();
        } catch (FileNotFoundException e) {
            throw new ImageLoadException("Could not read memory location!");
        }
    }
}
