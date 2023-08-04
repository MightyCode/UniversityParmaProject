package MobilityViewer.project.display;

import MobilityViewer.mightylib.graphics.renderer.Renderer;
import MobilityViewer.mightylib.graphics.renderer.Shape;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.scenes.mapScenes.ShowVehicleSimulationScene;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a vehicle in space and in time
 */
public class Vehicle {
    private static int ID_COUNTER = 0;
    public static float Size = 10f;

    private static final int EBO_SHIFT = 6;
    private static int [] EBO_ORDER = new int[]{ 0, 1, 2, 0, 2, 3 };
    private static final int VBO_SHIFT = 8;

    private static Renderer renderer;
    private static int vboVertexId;
    private static final int NUMBER_MAX_TO_DRAW = 5000;
    private static final int[] order = new int[NUMBER_MAX_TO_DRAW * EBO_SHIFT];
    private static final float[] positions = new float[NUMBER_MAX_TO_DRAW * VBO_SHIFT];
    private static final Vector4f temp = new Vector4f();
    private static int toDrawThisFrame = 0;
    public static int getNumberDrawn(){
        return toDrawThisFrame;
    }

    private final int id = ID_COUNTER++;
    private final ArrayList<Node> path;
    private float[] distances;
    private long[] nodeTime;

    private final long startTime;
    private final long timeToAchievePath;

    private Vector2f position;

    public Vehicle(long startTime, long timeToAchievePath){
        this.startTime = startTime;
        this.timeToAchievePath = timeToAchievePath;
        path = new ArrayList<>();
    }

    public void init(List<Node> path){
        this.path.clear();
        this.path.addAll(path);

        if (path.size() <= 1){
            distances = new float[0];
            nodeTime = new long[0];

            return;
        }

        distances = new float[path.size() - 1];
        float total = 0;
        nodeTime = new long[path.size() - 1];

        for (int i = 0; i < path.size() - 1; ++i){
            distances[i] = path.get(i).getDist(path.get(i + 1));
            total += distances[i];
        }

        for (int i = 0; i < path.size() - 1; ++i){
            nodeTime[i] = (long)(timeToAchievePath * ((double) distances[i]) / (double) total);
        }
    }

    /**
     * Static method so it should be called once.
     * @param referenceCamera camera given by dev
     */
    public static void initRenderer(Camera2D referenceCamera){
        // Setup renderer
        renderer = new Renderer("colorShape2D", true);
        renderer.switchToColorMode(ColorList.Orange());
        renderer.getShape().setEboStorage(Shape.STREAM_STORE);
        renderer.getShape().setEbo(order);
        renderer.setReferenceCamera(referenceCamera);

        vboVertexId = renderer.getShape().addVboFloat(positions, 2, Shape.STREAM_STORE);
    }

    public void update(long currentTime, Vector4f boundaries, Vector4f rendererDest){
        long startTime = ShowVehicleSimulationScene.TRANSLATOR.convert(this.startTime);
        if (currentTime < startTime || currentTime >= startTime + timeToAchievePath)
            return;

        if (toDrawThisFrame * VBO_SHIFT >= positions.length){
            System.out.println("Number of vehicule that can be draw this frame exceeds the limit.");
            return;
        }

        long now = currentTime - startTime;

        int i = 0;
        while (now >= nodeTime[i] && i < nodeTime.length - 1){
            now -= nodeTime[i];
            ++i;
        }

        Vector2f position1 = path.get(i).getPositionInBoundaries(boundaries, rendererDest);
        Vector2f position2 = path.get(i + 1).getPositionInBoundaries(boundaries, rendererDest);
        position = position1.add(
                position2.sub(
                        position1, new Vector2f()).mul(now * 1.0f / nodeTime[i], new Vector2f())
                        , new Vector2f());

        temp.x = position.x - Size / 2;
        temp.z = position.x + Size / 2;
        temp.y = position.y - Size / 2;
        temp.w = position.y + Size / 2;

        //System.out.println(temp.x + " " + temp.y + " " + temp.z + " " + temp.w);

        positions[toDrawThisFrame * VBO_SHIFT + 0] = temp.x; positions[toDrawThisFrame * VBO_SHIFT + 1] = temp.w;
        positions[toDrawThisFrame * VBO_SHIFT + 2] = temp.x; positions[toDrawThisFrame * VBO_SHIFT + 3] = temp.y;
        positions[toDrawThisFrame * VBO_SHIFT + 4] = temp.z; positions[toDrawThisFrame * VBO_SHIFT + 5] = temp.y;
        positions[toDrawThisFrame * VBO_SHIFT + 6] = temp.z; positions[toDrawThisFrame * VBO_SHIFT + 7] = temp.w;

        for (i = 0; i < EBO_ORDER.length; ++i){
            order[toDrawThisFrame * EBO_SHIFT + i] = EBO_ORDER[i] + 4 * toDrawThisFrame;
        }

        toDrawThisFrame++;
    }

    public static void beforeUpdate(){
        toDrawThisFrame = 0;
    }

    public static void display() {
        renderer.getShape().updateEbo(order, 0);
        renderer.getShape().setEboNumberIndex(toDrawThisFrame * EBO_SHIFT);
        renderer.getShape().updateVbo(positions, vboVertexId);

        renderer.display();
    }

    public static void unload(){
        renderer.unload();
    }
}


