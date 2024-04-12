package com.p3;

import java.util.List;

/**
 * The Image class represents an image read from a file and provides methods for accessing its properties.
 * Contains a list of pixels and the dimensions of the image. Uses the ImageReader class to read the image.
 * 
 * @param filePath The path to the image file.
 */
public class Image {
    
    private List<List<Integer>> pixels;
    private int imageHeight;
    private int imageLength;
    
    public Image(String filePath) {
        this.pixels = ImageReader.getImagePixels(filePath);
        this.imageHeight = ImageReader.getImageDimensions(filePath)[0];
        this.imageLength = ImageReader.getImageDimensions(filePath)[1];
    }

    /**
     * Returns the list of pixels in the image.
     * 
     * @return The list of pixels in the image.
     */
    public List<List<Integer>> getPixels() {
        return pixels;
    }

    /**
     * Returns the height of the image.
     * 
     * @return The height of the image.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Returns the length of the image.
     * 
     * @return The length of the image.
     */
    public int getImageLength() {
        return imageLength;
    }

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Image image = new Image(imagePath);
        System.out.println(image.imageHeight);
        System.out.println(image.imageLength);
        System.out.println(image.pixels.size());
    }
}
