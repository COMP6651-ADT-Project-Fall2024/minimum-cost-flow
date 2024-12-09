package graph;

import constants.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {

    private Map<Integer, List<Edge>> adjacencyList;
    private Set<Integer> vertices;
    private int source;
    private int sink;

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.vertices = new HashSet<>();
    }

    public void loadGraphFromMatrices(int[][] adjacencyMatrix, int[][] cap, int[][] unitCost, int source, int sink) {
        int n = adjacencyMatrix.length;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (adjacencyMatrix[i][j] == 1) {
                    addEdge(i, j, cap[i][j], unitCost[i][j]);
                }
            }
        }
    }

    public void loadGraphFromFile(String fileName, int source, int sink) throws IOException {
        this.source = source;
        this.sink = sink;
        fileName = Constants.RESOURCES_PATH + fileName ;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line = br.readLine())!=null){
                if(line.trim().isEmpty()){
                    continue;
                }

                String[] tokens = line.trim().split("\\s+");
                if(tokens.length !=4){
                    throw new IllegalArgumentException("Invalid line format: "+ line);
                }
                int src = Integer.parseInt(tokens[0]);
                int dest = Integer.parseInt(tokens[1]);
                int capacity = Integer.parseInt(tokens[2]);
                int cost = Integer.parseInt(tokens[3]);
                addEdge(src, dest, capacity, cost);

            }
        }
    }

    private void addEdge(int source, int destination, int capacity, int cost){
        Edge forwardEdge = new Edge(source, destination, capacity, cost);
        Edge backwardEdge = new Edge(destination, source, 0, -cost);
        forwardEdge.setResidual(backwardEdge);
        backwardEdge.setResidual(forwardEdge);
        adjacencyList.putIfAbsent(source, new ArrayList<>());
        adjacencyList.putIfAbsent(destination, new ArrayList<>());
        adjacencyList.get(source).add(forwardEdge);
        adjacencyList.get(destination).add(backwardEdge);
        vertices.add(source);
        vertices.add(destination);

    }

    public List<Edge> getEdgesFromVertex(int vertex){
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    public int getSource(){
        return source;
    }

    public int getSink(){
        return sink;
    }

    public Set<Integer> getVertices(){
        return vertices;
    }

    public static class Edge{
        private int source;
        private int destination;
        private int capacity;
        private int cost;
        private int flow;
        private Edge residual;  // maybe should be an int

        public Edge(int source, int destination, int capacity, int cost) {
            this.source = source;
            this.destination = destination;
            this.capacity = capacity;
            this.cost = cost;
            this.flow = 0;
        }

        public int remainingCapacity(){
            return capacity - flow;
        }

        public void augmentFlow(int amount){
            flow += amount;
            residual.flow -=amount;
        }

        public int getSource() {
            return source;
        }
        public int getDestination() {
            return destination;
        }
        public int getCapacity() {
            return capacity;
        }
        public int getCost() {
            return cost;
        }
        public int getFlow() {
            return flow;
        }
        public Edge getResidual() {
            return residual;
        }
        public void setResidual(Edge residual) {
            this.residual = residual;
        }

        @Override
        public String toString() {
            return String.format("Edge(%d -> %d | Capacity: %d | Cost: %d | Flow: %d)",
                    source, destination, capacity, cost, flow);
        }
    }
}
