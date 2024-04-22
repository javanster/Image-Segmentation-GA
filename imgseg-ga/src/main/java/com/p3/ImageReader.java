package com.p3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.Set;

/**
 * The ImageReader class provides utility methods for reading and writing images.
 */
public class ImageReader {

    // Private constructor to prevent instantiation
    private ImageReader() {
        throw new UnsupportedOperationException("ImageReader is a utility class and should not be instantiated.");
    }

    /**
     * Reads the pixels of an image from the specified file path.
     * Each pixel is represented as a list of three integers (RGB values).
     *
     * @param imagePath the path to the image file
     * @return a list of lists representing the RGB values of each pixel in the image
     */
    public static List<List<Integer>> getImagePixels(String imagePath) {
        List<List<Integer>> pixels = new ArrayList<>();

        try {
            BufferedImage image = ImageIO.read(new File(imagePath));

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int color = image.getRGB(x, y);

                    int red = (color >> 16) & 0xff;
                    int green = (color >> 8) & 0xff;
                    int blue = color & 0xff;

                    List<Integer> pixel = new ArrayList<>();
                    pixel.add(red);
                    pixel.add(green);
                    pixel.add(blue);

                    pixels.add(pixel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Image pixels are read successfully.");
        return pixels;
    }

    /**
     * Retrieves the dimensions (width and height) of an image from the specified file path.
     *
     * @param imagePath the path to the image file
     * @return an array containing the width and height of the image
     */
    public static int[] getImageDimensions(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new int[]{image.getHeight(), image.getWidth()};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[]{};
    }

    /**
     * Writes an image using the specified pixel values, width, height, and output path.
     *
     * @param pixels     a list of lists representing the RGB values of each pixel in the image
     * @param width      the width of the image
     * @param height     the height of the image
     * @param outputPath the path to write the output image file
     */
    public static void writeImage(List<List<Integer>> pixels, int width, int height, String outputPath) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int pixelIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Integer> pixel = pixels.get(pixelIndex++);
                int red = pixel.get(0);
                int green = pixel.get(1);
                int blue = pixel.get(2);

                int rgb = (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, rgb);
            }
        }

        try {
            ImageIO.write(image, "jpg", new File(outputPath));
            System.out.println("Image is written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes an image with segments to the specified output path.
     *
     * @param pixels      The list of pixels representing the image.
     * @param width       The width of the image.
     * @param height      The height of the image.
     * @param outputPath  The path where the image will be saved.
     * @param individual  The individual containing the segments.
     */
    public static void writeImageWithSegments(String outputPath, Individual individual) {
        List<Set<Integer>> segments = individual.getSegments();
        int width = Parameters.IMAGE.getImageLength();
        int height = Parameters.IMAGE.getImageHeight();
        List<List<Integer>> pixels = Parameters.IMAGE.getPixels();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Draw pixels
        for (int i = 0; i < pixels.size(); i++) {
            List<Integer> pixel = pixels.get(i);
            int x = i % width;
            int y = i / width;
            Color color = new Color(pixel.get(0), pixel.get(1), pixel.get(2));
            g.setColor(color);
            g.fillRect(x, y, 1, 1);
        }

        // Draw segment borders
        g.setColor(Color.GREEN);
        for (Set<Integer> segment : segments) {
            for (int index : segment) {
                int x = index % width;
                int y = index / width;

                // Check neighboring pixels
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        int neighborIndex = ny * width + nx;

                        // If the neighboring pixel is not in the same segment, draw a border
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height && !segment.contains(neighborIndex)) {
                            g.drawRect(x, y, 1, 1);
                        }
                    }
                }
            }
        }

        g.dispose();

        // Save image
        try {
            ImageIO.write(image, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        List<List<Integer>> pixels = ImageReader.getImagePixels(imagePath);

        int[] dimensions = ImageReader.getImageDimensions(imagePath);

        int width = dimensions[0];
        int height = dimensions[1];

        String outputPath = "test.png";
        writeImage(pixels, width, height, outputPath);
    }
}

