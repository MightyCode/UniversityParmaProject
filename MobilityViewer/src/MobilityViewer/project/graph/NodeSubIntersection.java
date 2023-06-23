package MobilityViewer.project.graph;

public class NodeSubIntersection extends NodeContainer<Node, NodeIntersection>{
    public NodeSubIntersection(Node node, NodeIntersection parent1, NodeIntersection parent2) {
        super(node);

        add(parent1);
        add(parent2);
    }
    public NodeIntersection getParent1(){
        return get(0);
    }

    public NodeIntersection getParent2(){
        return get(1);
    }

    @Override
    public boolean add(NodeIntersection node){
        return false;
    }
}
