package com.p3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectiveFunctions {

    // Private constructor to prevent instantiation
    private ObjectiveFunctions() {
        throw new UnsupportedOperationException("ObjectiveFunctions is a utility class and should not be instantiated.");
    }

    public static double edgeValue(Individual individual, List<Set<Integer>> segments) {
        double edgeValue = 0.0;
        List<List<Integer>> pixels = individual.getPixels();
        int imageHeight = individual.getImageHeight();
        int imageLength = individual.getImageLength();

        for (int i = 0; i < pixels.size(); i++) {
            // j should iterate through the neigbours of i
            for (Integer j : individual.getNeighboringPixelIndexes(i, imageHeight, imageLength)) {
                if (!inSameSegment(i, j, segments)) {
                    edgeValue += euclideanDistance(pixels.get(i), pixels.get(j));
                }
            }
        }

        return edgeValue;
    }

    private static boolean inSameSegment(int i, int j, List<Set<Integer>> segments) {
        for (Set<Integer> segment : segments) {
            if (segment.contains(i) && segment.contains(j)) {
                return true;
            }
        }
        return false;
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

