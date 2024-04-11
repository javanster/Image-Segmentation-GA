package com.p3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectiveFunctions {

    // Private constructor to prevent instantiation
    private ObjectiveFunctions() {
        throw new UnsupportedOperationException("ObjectiveFunctions is a utility class and should not be instantiated.");
    }

    public static double edgeValue(List<Integer> solution) {
        return 0.0;
    }

    /**
     * Returns the Euclidean distance between two pixels in the image. The Euclidean distance is calculated
     * as the square root of the sum of the squared differences of the RGB values of the pixels.
     * 
     * @param pixel1 The RGB values of the first pixel.
     * @param pixel2 The RGB values of the second pixel.
     * @return The Euclidean distance between the two pixels.
     */
    public static double euclideanDistance(List<Integer> pixel1, List<Integer> pixel2) {
        return Math.sqrt(Math.pow(pixel1.get(0) - pixel2.get(0), 2) + 
            Math.pow(pixel1.get(1) - pixel2.get(1), 2) + 
            Math.pow(pixel1.get(2) - pixel2.get(2), 2));
    }
}

