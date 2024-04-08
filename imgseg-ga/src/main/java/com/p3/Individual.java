package com.p3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Represents an individual in the population. Each individual has a graph representation (undirected)
 * of an image. The number of genes in the chromosome are equal to the number of pixels in the image.
 * Each gene represents the direction of the edge between the pixel and its neighbor. One chromosome
 * can contain several underected graphs, which each correspond to a segmentation of the image.
 * 
 * The constructor instantiates an individual by creating a minimum spanning tree (MST) of the image,
 * i.e. the chromosome contains only one segment.
 * 
 * Inspired by:
 * Ripon, Kazi Shah Nawaz & Ali, Lasker & Newaz, Sarfaraz & Ma, Jinwen. (2017). A Multi-Objective
 * Evolutionary Algorithm for Color Image Segmentation. 10.1007/978-3-319-71928-3_17
 * 
 * @param pixels A list of pixels in the image. Each pixel is represented as a list of three integers (RGB values).
 * @param imageHeight The height of the image in pixels.
 * @param imageLength The length of the image in pixels.
 */
public class Individual {
    
    private List<Integer> chromosome;


    public Individual(List<List<Integer>> pixels, int imageHeight, int imageLength) {

        Map<Set<Integer>, Double> edgeWeights = new HashMap<>();

        for (int i = 0; i < pixels.size(); i++) {
            List<Integer> pixel = pixels.get(i);
            List<Integer> neighbors = this.getNeighboringPixelIndexes(i, imageHeight, imageLength);

            for (int neighborIndex : neighbors) {
                List<Integer> neighbor = pixels.get(neighborIndex);
                Set<Integer> edge = Set.of(i, neighborIndex);
                if (!edgeWeights.containsKey(edge)) {
                    edgeWeights.put(edge, this.euclideanDistance(pixel, neighbor));
                }
            }
        }

        this.chromosome = this.getChromosomeFromMST(edgeWeights, imageHeight, imageLength);
    }

    /**
     * Returns the chromosome of the individual.
     * 
     * @return The chromosome of the individual.
     */
    public List<Integer> getChromosome() {
        return this.chromosome;
    }

    /**
     * Creates a chromosome from a minimum spanning tree (MST) of the image. The MST is created using
     * Prim's algorithm, where the weight of the edge between two pixels is the Euclidean distance between
     * the RGB values of the pixels.
     * 
     * @param edgeWeights A map where the keys are sets of two pixel indexes and the values are the weights of the edges between the pixels.
     * @param imageHeight The height of the image in pixels.
     * @param imageLength The length of the image in pixels.
     * @return The chromosome of the individual.
     */
    private List<Integer> getChromosomeFromMST(Map<Set<Integer>, Double> edgeWeights, int imageHeight, int imageLength) {
        int pixelCount = imageHeight * imageLength;
        List<Integer> chromosome = new ArrayList<>(Collections.nCopies(pixelCount, 0));
        
        Random random = new Random();
        int randomPixelIndex = random.nextInt(pixelCount);

        List<Integer> visitedIndexes = new ArrayList<>();
        visitedIndexes.add(randomPixelIndex);

        while (visitedIndexes.size() < pixelCount) {
            int originPixelIndex = visitedIndexes.get(0);
            double minWeight = Double.MAX_VALUE;
            int minPixelIndex = -1;
            for (int pixelIndex : visitedIndexes) {
                for (Set<Integer> key : edgeWeights.keySet()) {
                    List<Integer> keyList = new ArrayList<>(key);
                    if (key.contains(pixelIndex) && (!(visitedIndexes.contains(keyList.get(0)) && visitedIndexes.contains(keyList.get(1))))) {
                        double weight = edgeWeights.get(key);
                        if (weight < minWeight) {
                            originPixelIndex = pixelIndex;
                            minWeight = weight;
                            minPixelIndex = keyList.get(0) != pixelIndex ? keyList.get(0) : keyList.get(1);
                        }
                    }
                }
            }
            edgeWeights.remove(Set.of(originPixelIndex, minPixelIndex));
            visitedIndexes.add(minPixelIndex);
            if (chromosome.get(originPixelIndex) == 0) {
                int graphDirection = getGraphDirection(originPixelIndex, minPixelIndex, imageHeight, imageLength);
                chromosome.set(originPixelIndex, graphDirection);
            } else {
                int graphDirection = getGraphDirection(minPixelIndex, originPixelIndex, imageHeight, imageLength);
                chromosome.set(minPixelIndex, graphDirection);
            }
        }
        return chromosome;

    }

    /**
     * Returns the direction of the edge from the origin pixel to the target pixel (Moore neighborhood).
     * The direction is represented as an integer from 1 to 8, where 1 is right, 2 is left, 3 is up, 4 is down,
     * 5 is top right, 6 is bottom right, 7 is top left, and 8 is bottom left.
     * 
     * @param originPixelIndex The index of the origin pixel in the image.
     * @param targetPixel The index of the target pixel in the image.
     * @param imageHeight The height of the image in pixels.
     * @param imageLength The length of the image in pixels.
     * @return The direction of the edge from the origin pixel to the target pixel.
     */
    private int getGraphDirection(int originPixelIndex, int targetPixel, int imageHeight, int imageLength) {
        int originRow = originPixelIndex / imageLength;
        int originCol = originPixelIndex % imageLength;
        
        int targetRow = targetPixel / imageLength;
        int targetCol = targetPixel % imageLength;
        
        if (targetRow == originRow - 1) {
            if (targetCol == originCol - 1) {
                return 7; // target is directly top left of origin
            } else if (targetCol == originCol) {
                return 3; // target is directly above origin
            } else if (targetCol == originCol + 1) {
                return 5; // target is directly top right of origin
            }
        } else if (targetRow == originRow) {
            if (targetCol == originCol - 1) {
                return 2; // target is directly left of origin
            } else if (targetCol == originCol + 1) {
                return 1; // target is directly right of origin
            }
        } else if (targetRow == originRow + 1) {
            if (targetCol == originCol - 1) {
                return 8; // target is directly bottom left of origin
            } else if (targetCol == originCol) {
                return 4; // target is directly below origin
            } else if (targetCol == originCol + 1) {
                return 6; // target is directly bottom right of origin
            }
        }
        
        // If none of the above conditions match, the target is not a Moore neighbor of the origin
        throw new IllegalArgumentException("Target pixel is not a Moore neighbor of the origin pixel");
    }

    /**
     * Returns the indexes of the neighboring pixels of the pixel at index pixelIndex in the image (Moore neighborhood).
     * 
     * @param pixelIndex The index of the pixel in the image.
     * @param imageHeight The height of the image in pixels.
     * @param imageLength The length of the image in pixels.
     * @return A list of the indexes of the neighboring pixels of the pixel at index pixelIndex.
     */
    private List<Integer> getNeighboringPixelIndexes(int pixelIndex, int imageHeight, int imageLength) {
        if (pixelIndex < 0 || pixelIndex >= imageHeight * imageLength) {
            throw new IllegalArgumentException("Invalid pixel index");
        }

        List<Integer> neighbors = new ArrayList<>();
        int row = pixelIndex / imageLength;
        int col = pixelIndex % imageLength;

        int[] offsets = {-1, 0, 1};

        for (int rowOffset : offsets) {
            for (int colOffset : offsets) {
                int newRow = row + rowOffset;
                int newCol = col + colOffset;

                if (newRow >= 0 && newRow < imageHeight && newCol >= 0 && newCol < imageLength) {
                    int neighborIndex = newRow * imageLength + newCol;
                    if (neighborIndex != pixelIndex) {
                        neighbors.add(neighborIndex);
                    }
                }
            }
        }

        return neighbors;
    }
    

    /**
     * Returns the Euclidean distance between two pixels in the image. The Euclidean distance is calculated
     * as the square root of the sum of the squared differences of the RGB values of the pixels.
     * 
     * @param pixel1 The RGB values of the first pixel.
     * @param pixel2 The RGB values of the second pixel.
     * @return The Euclidean distance between the two pixels.
     */
    private double euclideanDistance(List<Integer> pixel1, List<Integer> pixel2) {
        return Math.sqrt(Math.pow(pixel1.get(0) - pixel2.get(0), 2) + 
            Math.pow(pixel1.get(1) - pixel2.get(1), 2) + 
            Math.pow(pixel1.get(2) - pixel2.get(2), 2));
    }

}
