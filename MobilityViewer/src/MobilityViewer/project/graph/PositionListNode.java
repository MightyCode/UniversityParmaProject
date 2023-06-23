package MobilityViewer.project.graph;

import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class PositionListNode<T extends ListNode<?>> extends ListNode<T> {

    public PositionListNode(long id) {
        super(id);
    }

    public abstract Vector2f getPosition();

    public abstract Vector2f getPositionInBoundaries(Vector4f boundaries, Vector4f rendererDest);
}
