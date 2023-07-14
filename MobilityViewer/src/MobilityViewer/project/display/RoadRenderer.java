package MobilityViewer.project.display;

import MobilityViewer.mightylib.graphics.renderer.Renderer;
import MobilityViewer.mightylib.graphics.renderer.Shape;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.project.graph.Node;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.SortedMap;

public class RoadRenderer {
    private static final int EBO_SHIFT = 6;
    private static final int VBO_SHIFT = 8;
    private final Renderer renderer;
    float[] vbo;
    private final int vboPositionIndex;

    private int roadNumber;

    private float roadSize;

    public RoadRenderer(Camera2D referenceCamera){
        renderer = new Renderer("colorShape2D", true);
        renderer.switchToColorMode(ColorList.Blue());
        renderer.getShape().setEboStorage(Shape.DYNAMIC_STORE);
        renderer.getShape().setEbo(new int[0]);
        renderer.setReferenceCamera(referenceCamera);

        vboPositionIndex = renderer.getShape().addVboFloat(new float[0], 2, Shape.DYNAMIC_STORE);

        roadNumber = 0;
        roadSize = 2;
    }

    public void setColor(Color4f color){
        renderer.switchToColorMode(color);
    }

    public void init(SortedMap<Long, Node> nodes){
        roadNumber = 0;
        for (Map.Entry<Long, Node> entry: nodes.entrySet()){
            for (Node node : entry.getValue().getNodes()){
                if (node.getId() < entry.getValue().getId())
                    ++roadNumber;
            }
        }

        int [] ebo = new int[EBO_SHIFT  * roadNumber];
        int [] eboValues = new int[]{ 0, 1, 2, 0, 2, 3 };

        for (int i = 0; i < roadNumber; ++i) {
            for (int j = 0; j < EBO_SHIFT; ++j)
                ebo[EBO_SHIFT * i + j] = eboValues[j] + 4 * i;
        }

        System.out.println("Number of road : " + roadNumber);

        renderer.getShape().setEbo(ebo);
        vbo = new float[VBO_SHIFT * roadNumber];
    }

    public void setRoadSize(float size){
        this.roadSize = size;
    }

    public void updateNodes(SortedMap<Long, Node> nodes, Vector4f boundaries, Vector4f displayBoundaries, float zoomLevel){
        float roadSize = this.roadSize / zoomLevel;

        int i = 0;
        Vector4f temp1 = new Vector4f(), temp2 = new Vector4f();

        Vector2f position1, position2, result = new Vector2f();
        for (Map.Entry<Long, Node> entry: nodes.entrySet()){
            for (Node node : entry.getValue().getNodes()){
                if (node.getId() >= entry.getValue().getId())
                    continue;

                position1 = entry.getValue().getPositionInBoundaries(boundaries, displayBoundaries);
                position2 = node.getPositionInBoundaries(boundaries, displayBoundaries);

                result = position1.sub(position2, result).perpendicular().normalize();

                temp1.x = position1.x - result.x * roadSize / 2;
                temp1.y = position1.y - result.y * roadSize / 2;

                temp1.z = position1.x + result.x * roadSize / 2;
                temp1.w = position1.y + result.y * roadSize / 2;

                temp2.x = position2.x + result.x * roadSize / 2;
                temp2.y = position2.y + result.y * roadSize / 2;

                temp2.z = position2.x - result.x * roadSize / 2;
                temp2.w = position2.y - result.y * roadSize / 2;

                vbo[i * VBO_SHIFT + 0] = temp1.x; vbo[i * VBO_SHIFT + 1] = temp1.y;
                vbo[i * VBO_SHIFT + 2] = temp1.z; vbo[i * VBO_SHIFT + 3] = temp1.w;
                vbo[i * VBO_SHIFT + 4] = temp2.x; vbo[i * VBO_SHIFT + 5] = temp2.y;
                vbo[i * VBO_SHIFT + 6] = temp2.z; vbo[i * VBO_SHIFT + 7] = temp2.w;
                ++i;
            }
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
