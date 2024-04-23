package com.p3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

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
 * @param image The image the individual is based on.
 */
public class Individual {
    
    private List<Integer> chromosome;
    private List<Set<Integer>> segments;
    private Map<Integer, Integer> segmentMap;

    private Double edgeValue;
    private Double connectivityMeasure;
    private Double overallDeviation;


    public Individual(int numTrees) {
        int imageHeight = Parameters.IMAGE.getImageHeight();
        int imageLength = Parameters.IMAGE.getImageLength();

        this.edgeValue = null;
        this.connectivityMeasure = null;
        this.overallDeviation = null;

        List<List<Edge>> adjacencyList = this.getAdjacencyList(Parameters.IMAGE);
        this.chromosome = this.getChromosomeFromMST(adjacencyList, imageHeight, imageLength, numTrees);

        // List<Integer> chromosome = new ArrayList<>();
        // Random random = new Random();
        // for (int i = 0; i < imageHeight * imageLength; i++) {
        //     chromosome.add(random.nextInt(9));
        // }
        // this.chromosome = chromosome;

        this.setSegments();
        this.setSegmentMap();
    }

    public Individual(List<Integer> chromosome, Individual parent) {
        this.chromosome = chromosome;
        this.setSegments();
        this.setSegmentMap();
    }

    
    public List<Integer> getChromosome() {
        return new ArrayList<>(this.chromosome);
    }

    public void setChromosome(List<Integer> chromosome) {
        this.chromosome = chromosome;
        this.setSegments();
        this.setSegmentMap();
    }

    public List<Set<Integer>> getSegments() {
        return this.segments;
    }

    public Map<Integer, Integer> getSegmentMap() {
        return this.segmentMap;
    }

    public double getEdgeValue() {
        if (this.edgeValue == null) {
            this.edgeValue = ObjectiveFunctions.edgeValue(this);
        }
        return this.edgeValue;
    }

    public double getConnectivityMeasure() {
        if (this.connectivityMeasure == null) {
            this.connectivityMeasure = ObjectiveFunctions.connectivityMeasure(this);
        }
        return this.connectivityMeasure;
    }

    public double getOverallDeviation() {
        if (this.overallDeviation == null) {
            this.overallDeviation = ObjectiveFunctions.overallDeviation(this);
        }
        return this.overallDeviation;
    }

    public Double getWeightedFitness() {
        return Parameters.EDGE_WEIGHT * this.getEdgeValue() - Parameters.CONNECTIVITY_WEIGHT * this.getConnectivityMeasure() - Parameters.DEVIATION_WEIGHT * this.getOverallDeviation();
    }

    /**
     * Resets the objective values of the individual, so that they are recalculated 
     * the next time they are accessed.
     */
    public void resetObjectiveValues() {
        this.edgeValue = null;
        this.connectivityMeasure = null;
        this.overallDeviation = null;
    }

    /**
     * Represents an edge in a graph.
     */
    class Edge implements Comparable<Edge> {
        int from;
        int to;
        double weight;

        /**
         * Constructs a new Edge object.
         *
         * @param from   the source vertex of the edge
         * @param to     the destination vertex of the edge
         * @param weight the weight of the edge
         */
        public Edge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        /**
         * Compares this edge with another edge based on their weights.
         *
         * @param other the other edge to compare with
         * @return a negative integer, zero, or a positive integer as this edge is less than, equal to, or greater than the other edge
         */
        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    /**
     * Returns the adjacency list representation of the image.
     * The weight of the edge between two pixels is the Euclidean 
     * distance between the RGB values of the pixels.
     *
     * @param image The image the individual is based on.
     * @return the adjacency list representation of the image
     */
    private List<List<Edge>> getAdjacencyList(Image image) {
        List<List<Integer>> pixels = image.getPixels();
        int imageHeight = image.getImageHeight();
        int imageLength = image.getImageLength();

        List<List<Edge>> adjacencyList = new ArrayList<>(pixels.size());

        for (int i = 0; i < pixels.size(); i++) {
            List<Integer> pixel = pixels.get(i);
            List<Integer> neighbors = this.getNeighboringPixelIndexes(i, imageHeight, imageLength);

            List<Edge> edges = new ArrayList<>();
            for (int neighborIndex : neighbors) {
                List<Integer> neighbor = pixels.get(neighborIndex);
                double weight = ObjectiveFunctions.euclideanDistance(pixel, neighbor);
                edges.add(new Edge(i, neighborIndex, weight));
            }
            adjacencyList.add(edges);
        }

        return adjacencyList;
    }

    /**
     * Creates a chromosome from several minimum spanning trees (MST) of the image. The MST is created using
     * Prim's algorithm, where the weight of the edge between two pixels is the Euclidean distance between
     * the RGB values of the pixels. 
     * The MSTs together contains all pixels of the image
     * 
     * @param edgeWeights A map where the keys are sets of two pixel indexes and the values are the weights of the edges between the pixels.
     * @param imageHeight The height of the image in pixels.
     * @param imageLength The length of the image in pixels.
     * @return The chromosome of the individual.
     */
    private List<Integer> getChromosomeFromMST(List<List<Edge>> adjacencyList, int imageHeight, int imageLength, int numTrees) {
        int pixelCount = imageHeight * imageLength;
        List<Integer> chromosome = new ArrayList<>(Collections.nCopies(pixelCount, 0));

        Random random = new Random();
        Set<Integer> visitedIndexes = new HashSet<>();
        PriorityQueue<Edge> queue = new PriorityQueue<>();

        for (int i = 0; i < numTrees; i++) {
            int randomPixelIndex = random.nextInt(pixelCount);
            visitedIndexes.add(randomPixelIndex);
            addEdgesToQueue(queue, randomPixelIndex, adjacencyList, visitedIndexes);
        }

        while (visitedIndexes.size() < pixelCount) {
            Edge minEdge = queue.poll();
            while (minEdge != null && visitedIndexes.contains(minEdge.to)) {
                minEdge = queue.poll();
            }
            if (minEdge == null) {
                break;
            }

            int originPixelIndex = minEdge.from;
            int minPixelIndex = minEdge.to;
            visitedIndexes.add(minPixelIndex);
            addEdgesToQueue(queue, minPixelIndex, adjacencyList, visitedIndexes);

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
     * Adds the edges of a given node to a priority queue, if the destination node has not been visited.
     *
     * @param queue       The priority queue to add the edges to.
     * @param node        The node whose edges are to be added to the queue.
     * @param adjacencyList The adjacency list representing the graph.
     * @param visited     The set of visited nodes.
     */
    private void addEdgesToQueue(PriorityQueue<Edge> queue, int node, List<List<Edge>> adjacencyList, Set<Integer> visited) {
        for (Edge edge : adjacencyList.get(node)) {
            if (!visited.contains(edge.to)) {
                queue.add(edge);
            }
        }
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

 
    /**
     * Sets the segments of the individual based on the chromosome and image.
     * Each segment is represented as a set of pixels.
     */
    private void setSegments() {
        int pixelCount = this.chromosome.size();
        DisjointSet ds = new DisjointSet(pixelCount);

        for (int i = 0; i < pixelCount; i++) {
            int neighbor = getNeighborFromGraph(i, Parameters.IMAGE.getImageHeight(), Parameters.IMAGE.getImageLength(), chromosome.get(i));
            if (neighbor != -1) {
                ds.union(i, neighbor);
            }
        }

        Map<Integer, Set<Integer>> segmentsMap = new HashMap<>();
        for (int i = 0; i < pixelCount; i++) {
            int root = ds.find(i);
            segmentsMap.computeIfAbsent(root, k -> new HashSet<>()).add(i);
        }

        this.segments = new ArrayList<>(segmentsMap.values());
    }

    /**
     * Sets the segment map for the individual.
     * The segment map is a mapping of each pixel to its corresponding segment index.
     * This method iterates over each segment and assigns the segment index to each pixel in the segment.
     */
    private void setSegmentMap() {
        this.segmentMap = new HashMap<>();
        for (int i = 0; i < this.segments.size(); i++) {
            for (int pixel : this.segments.get(i)) {
                this.segmentMap.put(pixel, i);
            }
        }
    }

    /**
     * Returns the index of the neighboring pixel of the pixel at index pixelIndex in the image based on the direction.
     * The direction is represented as an integer from 1 to 8, where 1 is right, 2 is left, 3 is up, 4 is down,
     * 5 is top right, 6 is bottom right, 7 is top left, and 8 is bottom left.
     * 
     * @param pixelIndex The index of the pixel in the image.
     * @param imageHeight The height of the image in pixels.
     * @param imageLength The length of the image in pixels.
     * @param direction The direction of the neighboring pixel.
     * @return The index of the neighboring pixel of the pixel at index pixelIndex based on the direction.
     */
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

    class DisjointSet {
        private int[] parent;
        private int[] rank;
    
        public DisjointSet(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }
    
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }
    
        public void union(int x, int y) {
            int xRoot = find(x);
            int yRoot = find(y);
            if (xRoot == yRoot) {
                return;
            }
            if (rank[xRoot] < rank[yRoot]) {
                parent[xRoot] = yRoot;
            } else if (rank[xRoot] > rank[yRoot]) {
                parent[yRoot] = xRoot;
            } else {
                parent[yRoot] = xRoot;
                rank[xRoot]++;
            }
        }
    }

    public static void main(String[] args) {
        String imagePath = "training_images/118035/Test image.jpg";
        Image image = new Image(imagePath);
        Parameters.IMAGE = image;
        Individual individual = new Individual(5);
        System.out.println(individual.getSegmentMap());
    }
}
