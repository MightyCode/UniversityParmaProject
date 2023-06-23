package MobilityViewer.project.graph;

import java.util.*;

public class Dijkstra {
    /*private final Map<Long, Float> shortestDistances; // Cache to store shortest distances
    private final Set<Long> visitedNodes;

    public Dijkstra() {
        shortestDistances = new HashMap<>();
        visitedNodes = new HashSet<>();
    }

    public List<Node> findShortestPath(Graph graph, Node source, Node destination) {
        if (!graph.contains(source) || !graph.contains(destination)) {
            throw new IllegalArgumentException("Source or destination node not found in the graph.");
        }

        // Check the cache for the shortest distance
        long cacheKey = getCacheKey(source.getId(), destination.getId());
        if (shortestDistances.containsKey(cacheKey)) {
            return reconstructPath(graph, source, destination);
        }

        shortestDistances.clear();
        visitedNodes.clear();

        // Initialize the shortest distances with infinity except for the source node
        for (Node node : graph.getNodes()) {
            if (node == source) {
                shortestDistances.put(node.getId(), 0f);
            } else {
                shortestDistances.put(node.getId(), Float.POSITIVE_INFINITY);
            }
        }

        // Visit nodes in order of their shortest distance
        while (!visitedNodes.contains(destination.getId())) {
            Node currentNode = getClosestUnvisitedNode(graph);
            visitedNodes.add(currentNode.getId());

            // Update the shortest distances of neighboring nodes
            for (Node neighbor : currentNode.getNodes()) {
                if (!visitedNodes.contains(neighbor.getId())) {
                    float distance = currentNode.getDist(neighbor) + shortestDistances.get(currentNode.getId());
                    if (distance < shortestDistances.get(neighbor.getId())) {
                        shortestDistances.put(neighbor.getId(), distance);
                    }
                }
            }
        }

        // Cache the shortest distance
        shortestDistances.put(cacheKey, shortestDistances.get(destination.getId()));

        return reconstructPath(graph, source, destination);
    }

    private Node getClosestUnvisitedNode(Graph graph) {
        Node closestNode = null;
        float shortestDistance = Float.POSITIVE_INFINITY;

        for (Node node : graph.getNodes()) {
            if (!visitedNodes.contains(node.getId()) && shortestDistances.get(node.getId()) < shortestDistance) {
                shortestDistance = shortestDistances.get(node.getId());
                closestNode = node;
            }
        }

        return closestNode;
    }

    private List<Node> reconstructPath(Graph graph, Node source, Node destination) {
        List<Node> path = new ArrayList<>();
        Node currentNode = destination;

        while (currentNode != null && currentNode != source) {
            path.add(0, currentNode);
            float currentDistance = shortestDistances.get(currentNode.getId());

            for (Node neighbor : currentNode.getNodes()) {
                if (currentDistance - neighbor.getDist(currentNode) == shortestDistances.get(neighbor.getId())) {
                    currentNode = neighbor;
                    break;
                }
            }
        }

        if (currentNode == source) {
            path.add(0, source);
        } else {
            path.clear();
        }

        return path;
    }

    private long getCacheKey(long sourceId, long destinationId) {
        return (sourceId << 32) | destinationId;
    }*/

    private static Map<String, List<Node>> cache = new HashMap<>();

    public static List<Node> findShortestPath(Graph graph, Node startNode, Node endNode) {
        String cacheKey = startNode.getId() + "-" + endNode.getId();
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        Map<Node, Float> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        Set<Node> visited = new HashSet<>();

        for (Node node : graph.getNodes()) {
            distances.put(node, Float.MAX_VALUE);
            previousNodes.put(node, null);
        }

        distances.put(startNode, 0f);
        priorityQueue.offer(startNode);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();

            if (currentNode.equals(endNode)) {
                List<Node> shortestPath = reconstructPath(previousNodes, endNode);
                cache.put(cacheKey, shortestPath);
                return shortestPath;
            }

            visited.add(currentNode);

            for (Node neighbor : currentNode.getNodes()) {
                if (visited.contains(neighbor))
                    continue;

                float newDistance = distances.get(currentNode) + currentNode.getDist(neighbor);

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousNodes.put(neighbor, currentNode);
                    priorityQueue.offer(neighbor);
                }
            }
        }

        return new ArrayList<>();
    }

    private static List<Node> reconstructPath(Map<Node, Node> previousNodes, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(0, currentNode);
            currentNode = previousNodes.get(currentNode);
        }

        return path;
    }
}