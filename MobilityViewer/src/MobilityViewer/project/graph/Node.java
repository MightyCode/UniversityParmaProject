package MobilityViewer.project.graph;

import MobilityViewer.mightylib.util.math.MightyMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;

public class Node extends PositionListNode<Node> {

    protected Vector2f position;
    private final HashMap<Node, Float> overrideDistances;

    public Node(long id, float x, float y){
        super(id);

        overrideDistances = new HashMap<>();
        position = new Vector2f(x, y);
    }

    public float getDist(Node n){
        if (overrideDistances.containsKey(n))
            return overrideDistances.get(n);

        return getPosition().distance(n.getPosition());
    }

    @Override
    public Vector2f getPositionInBoundaries(Vector4f boundaries, Vector4f rendererDest){
        return new Vector2f(
                MightyMath.mapf(position.x, boundaries.x, boundaries.z, rendererDest.x, rendererDest.z),

                MightyMath.mapf(position.y, boundaries.y, boundaries.w, rendererDest.w, rendererDest.y)
        );
    }

    public void add(Node neighbour, float distance){
        super.add(neighbour);

        overrideDistances.put(neighbour, distance);
    }

    @Override
    public Node copy() {
        return new Node(getId(), position.x, position.y);
    }

    @Override
    public Vector2f getPosition() {
        return new Vector2f(position);
    }
}
