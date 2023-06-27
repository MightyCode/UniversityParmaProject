package MobilityViewer.project.graph;

import java.util.*;

public class Dijkstra {
    public static List<Node> findShortestPathReduced(ReducedGraph graph, Node startNode, Node endNode){
        if (startNode == endNode)
            return new ArrayList<>();

        NodeIntersection startIntersection = null, endIntersection = null;

        for(NodeIntersection intersection : graph.getNodes()){
            if (intersection.getId() == startNode.getId())
                startIntersection = intersection;

            if (intersection.getId() == endNode.getId())
                endIntersection = intersection;

            for (NodeIntersectionList list : intersection.getNodes()){
                if (list.containsId(startNode.getId())) {
                    if (list.getReferenceNodeFrom() == endIntersection)
                        startIntersection = list.getReferenceNodeTo();
                    else
                        startIntersection = list.getReferenceNodeFrom();
                }

                if (list.containsId(endNode.getId())){
                    if (list.getReferenceNodeFrom() == startIntersection)
                        endIntersection = list.getReferenceNodeTo();
                    else
                        endIntersection = list.getReferenceNodeFrom();
                }
            }

            if (startIntersection != null && endIntersection != null)
                break;
        }

        Map<NodeIntersection, Float> distances = new HashMap<>();
        Map<NodeIntersection, NodeIntersection> previousNodes = new HashMap<>();
        PriorityQueue<NodeIntersection> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        Set<NodeIntersection> visited = new HashSet<>();

        for (NodeIntersection node : graph.getNodes()) {
            distances.put(node, Float.MAX_VALUE);
            previousNodes.put(node, null);
        }

        distances.put(startIntersection, 0f);
        priorityQueue.offer(startIntersection);

        while (!priorityQueue.isEmpty()) {
            NodeIntersection currentNode = priorityQueue.poll();

            if (currentNode.equals(endIntersection)) {
                List<NodeIntersection> inters =  reconstructPathIntersection(previousNodes, endIntersection);

                List<Node> result = new ArrayList<>();
                for(NodeIntersection intersection : inters)
                    result.add(intersection.getReferenceNode());

                return result;
            }

            visited.add(currentNode);

            for (NodeIntersectionList neighbor : currentNode.getNodes()) {
                NodeIntersection to = neighbor.getReferenceNodeTo();

                if (visited.contains(to))
                    continue;

                float newDistance = distances.get(currentNode) + currentNode.getDist(to);

                if (newDistance < distances.get(to)) {
                    distances.put(to, newDistance);
                    previousNodes.put(to, currentNode);
                    priorityQueue.offer(to);
                }
            }
        }

        return new ArrayList<>();
    }

    public static List<Node> findShortestPath(Graph graph, Node startNode, Node endNode) {
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

            if (currentNode.equals(endNode))
                return reconstructPath(previousNodes, endNode);

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

    private static List<NodeIntersection> reconstructPathIntersection(
            Map<NodeIntersection, NodeIntersection> previousNodes, NodeIntersection endNode) {
        List<NodeIntersection> path = new ArrayList<>();
        NodeIntersection currentNode = endNode;

        while (currentNode != null) {
            path.add(0, currentNode);
            currentNode = previousNodes.get(currentNode);
        }

        return path;
    }
}