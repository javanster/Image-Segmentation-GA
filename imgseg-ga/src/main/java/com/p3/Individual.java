package com.p3;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * WORK IN PROGRESS
 * 
 */
public class Individual {
    
    private List<Integer> chromosome;


    public Individual(List<List<Integer>> pixels, int height, int length) {

    }

    private List<Integer> getNeighboringPixelIndexes(int i, int height, int length) {
        if (i < 0 || i >= height * length) {
            throw new IllegalArgumentException("Invalid pixel index");
        }

        List<Integer> neighbors = new ArrayList<>();
        int row = i / length;
        int col = i % length;

        int[] offsets = {-1, 0, 1};

        for (int rowOffset : offsets) {
            for (int colOffset : offsets) {
                int newRow = row + rowOffset;
                int newCol = col + colOffset;

                if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < length) {
                    int neighborIndex = newRow * length + newCol;
                    if (neighborIndex != i) {
                        neighbors.add(neighborIndex);
                    }
                }
            }
        }

        return neighbors;
    }
    

    private double euclideanDistance(List<Integer> pixel1, List<Integer> pixel2) {
        return Math.sqrt(Math.pow(pixel1.get(0) - pixel2.get(0), 2) + 
            Math.pow(pixel1.get(1) - pixel2.get(1), 2) + 
            Math.pow(pixel1.get(2) - pixel2.get(2), 2));
    }

}
