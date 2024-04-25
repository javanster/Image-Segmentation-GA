package com.p3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.*;
import java.util.Set;

/**
 * The ImageReader class provides utility methods for reading and writing images.
 */
public class ImageReaderWriter {

    // Private constructor to prevent instantiation
    private ImageReaderWriter() {
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
     * @param outputPath  The path where the image will be saved.
     * @param individual  The individual containing the segments.
     * @param isWhite     A boolean indicating whether the image should be saved with white or original background.
     */
    public static void writeImageWithSegments(String outputPath, Individual individual, boolean isWhite) {
        List<Set<Integer>> segments = individual.getSegments();
        int width = Parameters.IMAGE.getImageWith();
        int height = Parameters.IMAGE.getImageHeight();
        List<List<Integer>> pixels = Parameters.IMAGE.getPixels();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        int x;
        int y;
        int borderColor;

        // Draw pixels
        for (int i = 0; i < pixels.size(); i++) {
            List<Integer> pixel = pixels.get(i);
            x = i % width;
            y = i / width;
            if (isWhite) {
                g.setColor(Color.WHITE);
            } else {
                Color color = new Color(pixel.get(0), pixel.get(1), pixel.get(2));
                g.setColor(color);
            }
            g.fillRect(x, y, 1, 1);
        }

        // Draw segment borders
        if (isWhite) {
            g.setColor(Color.BLACK);
            borderColor = Color.BLACK.getRGB();
        } else {
            g.setColor(Color.GREEN);
            borderColor = Color.GREEN.getRGB();
        }

        boolean nextToBorderPixel;
        Map<Integer, Integer> segmentMap;
        Integer indexSegment;
        int numberOfNeighborsInSegment;
        Color color;

        for (Set<Integer> segment : segments) {
            for (int index : segment) {
                List<Integer> neighbors = individual.getNeighboringPixelIndexes(index, height, width);
                
                if (neighbors.size() < 8) {
                    x = index % width;
                    y = index / width;
                    g.drawRect(x, y, 1, 1);
                    continue;
                }

                nextToBorderPixel = false;
                segmentMap = individual.getSegmentMap();
                indexSegment = segmentMap.get(index);
                numberOfNeighborsInSegment = 0;

                for (int pixelNeighbor : neighbors) {
                    if (segmentMap.get(pixelNeighbor).equals(indexSegment)) {
                        numberOfNeighborsInSegment++;
                    } else {                        
                        x = pixelNeighbor % width;
                        y = pixelNeighbor / width;

                        color = new Color(image.getRGB(x, y));
                        
                        if (color.getRGB() == borderColor) {
                            nextToBorderPixel = true;
                            break;
                        }
                    }
                }
                if (!nextToBorderPixel && numberOfNeighborsInSegment < 8) {
                    x = index % width;
                    y = index / width;
                    
                    g.drawRect(x, y, 1, 1);
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
        List<List<Integer>> pixels = ImageReaderWriter.getImagePixels(imagePath);

        int[] dimensions = ImageReaderWriter.getImageDimensions(imagePath);

        int width = dimensions[0];
        int height = dimensions[1];

        String outputPath = "test.png";
        writeImage(pixels, width, height, outputPath);
    }
}

