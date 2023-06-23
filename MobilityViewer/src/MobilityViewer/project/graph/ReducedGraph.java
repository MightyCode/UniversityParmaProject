package MobilityViewer.project.graph;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class ReducedGraph extends ListNode<NodeIntersection> {
    public static class ReducedGraphSubNode extends ListNode<NodeSubIntersection>{
        public ReducedGraphSubNode() {
            super(-1);
        }
    }

    private final ReducedGraphSubNode subNodeGraph;

    private ReducedGraph() {
        super(-1);
        subNodeGraph = new ReducedGraphSubNode();
    }

    protected void init(Graph graph) {
        subNodeGraph.clear();

        Queue<Node> nodeToCheck = new LinkedList<>();
        Set<Node> subNodes = new HashSet<>();

        Node currentNode, next, previous, temp;
        NodeIntersection currentIntersection, nextIntersection;
        NodeSubIntersection subIntersection;

        for (Node node : graph.getNodes()) {
            if (node.size() != 2) {
                nodeToCheck.add(node);
                break;
            }
        }

        int total = 0;

        while(!nodeToCheck.isEmpty()){
            currentNode = nodeToCheck.poll();

            if (containsId(currentNode.getId())){
                currentIntersection = getById(currentNode.getId());
            } else {
                currentIntersection = new NodeIntersection(currentNode);
                add(currentIntersection);
            }

            for (Node neighbour : currentNode.getNodes()){
                subNodes.clear();

                next = neighbour;
                previous = currentNode;

                while (next.size() == 2){
                    subNodes.add(next);
                    temp = next;

                    next = next.get( (next.get(0) == previous) ? 1: 0);

                    previous = temp;
                }

                // Next is the second intersection
                if (containsId(next.getId())){
                    nextIntersection = getById(next.getId());
                } else {
                    nextIntersection = new NodeIntersection(next);
                    add(nextIntersection);
                    nodeToCheck.add(next);
                }

                NodeIntersectionList intersectionList = new NodeIntersectionList(currentIntersection, nextIntersection);
                for (Node node : subNodes){
                    if (subNodeGraph.containsId(node.getId())){
                        subIntersection = subNodeGraph.getById(node.getId());
                    } else {
                        subIntersection = new NodeSubIntersection(node, currentIntersection, nextIntersection);
                        subNodeGraph.add(subIntersection);
                    }

                    intersectionList.add(subIntersection);

                    ++total;
                }
                currentIntersection.add(intersectionList);
            }
        }

        System.out.println((size() + total / 2) + " / " + graph.size());
    }

    public static ReducedGraph constructFrom(Graph graph) {
        ReducedGraph reducedGraph = new ReducedGraph();
        reducedGraph.init(graph);
        return reducedGraph;
    }

    public ReducedGraphSubNode getSubNodeGraph(){
        return subNodeGraph;
    }
}