package MobilityViewer.project.graph;

import MobilityViewer.mightylib.util.math.MightyMath;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Node extends PositionListNode<Node> {

    protected Vector2f position;

    public Node(long id, float x, float y){
        super(id);

        position = new Vector2f(x, y);
    }

    public float getDist(Node n){
        return getPosition().distance(n.getPosition());
    }

    @Override
    public Vector2f getPositionInBoundaries(Vector4f boundaries, Vector4f rendererDest){
        return new Vector2f(
                MightyMath.mapf(position.x, boundaries.x, boundaries.z, rendererDest.x, rendererDest.z),

                MightyMath.mapf(position.y, boundaries.y, boundaries.w, rendererDest.w, rendererDest.y)
        );
    }

    @Override
    public Vector2f getPosition() {
        return new Vector2f(position);
    }
}
