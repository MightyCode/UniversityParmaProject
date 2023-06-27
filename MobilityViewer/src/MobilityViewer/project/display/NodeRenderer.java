package MobilityViewer.project.display;

import MobilityViewer.mightylib.graphics.renderer.Renderer;
import MobilityViewer.mightylib.graphics.renderer.Shape;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.project.graph.PositionListNode;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.*;

public class NodeRenderer<T extends PositionListNode<?>> {
    private static final int EBO_SHIFT = 6;
    private static final int VBO_SHIFT = 8;
    private final Renderer renderer;
    private float[] vbo;
    private final int vboPositionIndex;

    private float nodeSize;

    public NodeRenderer(Camera2D referenceCamera){
        renderer = new Renderer("colorShape2D", true);
        renderer.switchToColorMode(ColorList.Red());
        renderer.getShape().setEboStorage(Shape.DYNAMIC_STORE);
        renderer.getShape().setEbo(new int[0]);
        renderer.setReferenceCamera(referenceCamera);

        vboPositionIndex = renderer.getShape().addVboFloat(new float[0], 2, Shape.DYNAMIC_STORE);

        nodeSize = 2;
    }

    public void setColor(Color4f color){
        renderer.switchToColorMode(color);
    }

    public void setNodeSize(float nodeSize){
        this.nodeSize = nodeSize;
    }

    public float getNodeSize() {
        return nodeSize;
    }

    public void init(Collection<T> nodes){
        int [] ebo = new int[EBO_SHIFT  * nodes.size()];
        int [] eboValues = new int[]{ 0, 1, 2, 0, 2, 3 };

        for (int i = 0; i < nodes.size(); ++i) {
            for (int j = 0; j < EBO_SHIFT; ++j)
                ebo[EBO_SHIFT * i + j] = eboValues[j] + 4 * i;
        }

        renderer.getShape().setEbo(ebo);
        vbo = new float[VBO_SHIFT * nodes.size()];

        System.out.println("Number of nodes : " + nodes.size());
    }

    public void updateNodes(Collection<T> nodes, Vector4f boundaries, Vector4f displayBoundaries, float zoomLevel){
        float nodeSize = this.nodeSize / zoomLevel;

        int i = 0;
        Vector4f temp = new Vector4f();
        Vector2f position;

        for (PositionListNode<?> node : nodes){
            position = node.getPositionInBoundaries(boundaries, displayBoundaries);

            temp.x = position.x - nodeSize / 2;
            temp.z = position.x + nodeSize / 2;
            temp.y = position.y - nodeSize / 2;
            temp.w = position.y + nodeSize / 2;

            vbo[i * VBO_SHIFT + 0] = temp.x; vbo[i * VBO_SHIFT + 1] = temp.w;
            vbo[i * VBO_SHIFT + 2] = temp.x; vbo[i * VBO_SHIFT + 3] = temp.y;
            vbo[i * VBO_SHIFT + 4] = temp.z; vbo[i * VBO_SHIFT + 5] = temp.y;
            vbo[i * VBO_SHIFT + 6] = temp.z; vbo[i * VBO_SHIFT + 7] = temp.w;
            ++i;
        }

        renderer.getShape().updateVbo(vbo, vboPositionIndex);
    }

    public void display() {
        renderer.display();
    }

    public void unload() {
        renderer.unload();
    }
}
