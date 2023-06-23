package MobilityViewer.project.display;

import MobilityViewer.mightylib.graphics.renderer._2D.shape.RectangleRenderer;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.graph.Node;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Scooter {
    private static int ID_COUNTER = 0;
    private int id = ID_COUNTER++;
    private static final float SIZE = 10f;

    private RectangleRenderer renderer;

    private final ArrayList<Node> path;
    private float[] distances;
    private long[] nodeTime;

    private final long startTime;
    private final long timeToAchievePath;

    public Scooter(long startTime, long timeToAchievePath){
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


    public void initRenderer(Camera2D referenceCamera){
        renderer = new RectangleRenderer("colorShape2D");
        renderer.setReferenceCamera(referenceCamera);
        renderer.setReference(EDirection.None);
        renderer.switchToColorMode(ColorList.Orange());
        renderer.setSizePix(SIZE, SIZE);
    }

    public void update(long currentTime, Vector4f boundaries, Vector4f rendererDest){
        if (currentTime < startTime || currentTime >= startTime + timeToAchievePath)
            return;

        long now = currentTime - startTime;

        int i = 0;
        while (now > nodeTime[i]){
            now -= nodeTime[i];
            ++i;
        }

        Vector2f position1 = path.get(i).getPositionInBoundaries(boundaries, rendererDest);
        Vector2f position2 = path.get(i + 1).getPositionInBoundaries(boundaries, rendererDest);
        renderer.setPosition(
                position1.add(position2.sub(position1, new Vector2f()).mul(now * 1.0f / nodeTime[i], new Vector2f())
                        , new Vector2f())
        );
    }

    public void display(long currentTime){
        if (currentTime < startTime || currentTime > startTime + timeToAchievePath)
            return;

        renderer.display();
    }

    public void unload(){
        renderer.unload();
    }
}


