package com.p3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageReader {

    // Private constructor to prevent instantiation
    private ImageReader() {
        throw new UnsupportedOperationException("ImageReader is a utility class and should not be instantiated.");
    }

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

    public static int[] getImageDimensions(String imagePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new int[]{image.getWidth(), image.getHeight()};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[]{};
    }

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

    // main
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