package com.p3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.HashSet;
import java.util.Optional;
import java.util.Arrays;

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
    private List<List<Integer>> pixels;
    private int imageHeight;
    private int imageLength;


    public Individual(List<List<Integer>> pixels, int imageHeight, int imageLength) {

        this.pixels = pixels;
        this.imageHeight = imageHeight;
        this.imageLength = imageLength;
        Map<Set<Integer>, Double> edgeWeights = new HashMap<>();

        for (int i = 0; i < pixels.size(); i++) {
            List<Integer> pixel = pixels.get(i);
            List<Integer> neighbors = this.getNeighboringPixelIndexes(i, imageHeight, imageLength);

            for (int neighborIndex : neighbors) {
                List<Integer> neighbor = pixels.get(neighborIndex);
                Set<Integer> edge = Set.of(i, neighborIndex);
                if (!edgeWeights.containsKey(edge)) {
                    edgeWeights.put(edge, ObjectiveFunctions.euclideanDistance(pixel, neighbor));
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

    public void setChromosome(List<Integer> chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * Returns the pixels of the image.
     * 
     * @return The pixels of the image.
     */
    public List<List<Integer>> getPixels() {
        return this.pixels;
    }

    /**
     * Returns the height of the image in pixels.
     * 
     * @return The height of the image in pixels.
     */
    public int getImageHeight() {
        return this.imageHeight;
    }

    /**
     * Returns the length of the image in pixels.
     * 
     * @return The length of the image in pixels.
     */
    public int getImageLength() {
        return this.imageLength;
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
    public int getGraphDirection(int originPixelIndex, int targetPixel, int imageHeight, int imageLength) {
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
    public List<Integer> getNeighboringPixelIndexes(int pixelIndex, int imageHeight, int imageLength) {
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

    public List<Set<Integer>> getSegments() {
        Map<Integer, Set<Integer>> pixelToSegment = new HashMap<>();
        for (int i = 0; i < chromosome.size(); i++) {
            if (!pixelToSegment.containsKey(i)) {
                Set<Integer> segment = new HashSet<>();
                Stack<Integer> stack = new Stack<>();
                stack.push(i);
    
                while (!stack.isEmpty()) {
                    int current = stack.pop();
                    segment.add(current);
    
                    // Add neighbors to the stack based on the value in the chromosome
                    int neighbor = getNeighborFromGraph(current, this.imageHeight, this.imageLength, chromosome.get(current));
                    if (neighbor != -1 && !segment.contains(neighbor)) {
                        stack.push(neighbor);
                    }
    
                    // If the current pixel is connected to an existing segment, merge the segments
                    if (neighbor != -1 && pixelToSegment.containsKey(neighbor)) {
                        segment.addAll(pixelToSegment.get(neighbor));
                        pixelToSegment.put(neighbor, segment);
                    }
                }
    
                // Add the segment to the map for each pixel in the segment
                for (int pixel : segment) {
                    pixelToSegment.put(pixel, segment);
                }
            }
        }
    
        // Create a list of unique segments from the map values
        List<Set<Integer>> segments = new ArrayList<>(new HashSet<>(pixelToSegment.values()));
    
        return segments;
    }

    private int getNeighborFromGraph(int pixelIndex, int imageHeight, int imageLength, int direction) {
        int row = pixelIndex / imageLength;
        int col = pixelIndex % imageLength;

        switch (direction) {
            case 1: // right
                if (col + 1 < imageLength) {
                    return pixelIndex + 1;
                }
                break;
            case 2: // left
                if (col - 1 >= 0) {
                    return pixelIndex - 1;
                }
                break;
            case 3: // up
                if (row - 1 >= 0) {
                    return pixelIndex - imageLength;
                }
                break;
            case 4: // down
                if (row + 1 < imageHeight) {
                    return pixelIndex + imageLength;
                }
                break;
            case 5: // top right
                if (row - 1 >= 0 && col + 1 < imageLength) {
                    return pixelIndex - imageLength + 1;
                }
                break;
            case 6: // bottom right
                if (row + 1 < imageHeight && col + 1 < imageLength) {
                    return pixelIndex + imageLength + 1;
                }
                break;
            case 7: // top left
                if (row - 1 >= 0 && col - 1 >= 0) {
                    return pixelIndex - imageLength - 1;
                }
                break;
            case 8: // bottom left
                if (row + 1 < imageHeight && col - 1 >= 0) {
                    return pixelIndex + imageLength - 1;
                }
                break;
            default:
                return -1;
        }

        return -1;
    }

    public static void main(String[] args) {
        List<List<Integer>> pixels = List.of(
            List.of(1, 2, 3),
            List.of(4, 5, 6),
            List.of(7, 8, 9),
            List.of(10, 11, 12)
            // List.of(7, 8, 9),
            // List.of(10, 11, 12)
        );
        int imageHeight = 2;
        int imageLength = 2;
        Individual individual = new Individual(pixels, imageHeight, imageLength);
        // List<Integer> chromosome = individual.getChromosome();
        // System.out.println(chromosome);
        List<Integer> chromosome = List.of(0, 8, 1, 7);
        individual.setChromosome(chromosome);
        List<Set<Integer>> segments = individual.getSegments();
        System.out.println(segments);
    }
}
