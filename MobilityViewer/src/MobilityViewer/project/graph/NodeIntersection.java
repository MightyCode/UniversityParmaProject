package MobilityViewer.project.graph;
public class NodeIntersection extends NodeContainer<Node, NodeIntersectionList> {
    public NodeIntersection(Node node) {
        super(node);
    }

    public float getDist(NodeIntersection intersection){
        return getById(intersection.getId()).dist();
    }
}
