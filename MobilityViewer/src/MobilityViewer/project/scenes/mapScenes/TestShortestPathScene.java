package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.scenes.loadingContent.ShortestPathLoading;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.graph.*;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class TestShortestPathScene extends SceneMap<ShortestPathLoading, ShortestPathLoading.SPLResult> {
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;
    private RoadRenderer pathRenderer;

    public TestShortestPathScene() {
        super(new ShortestPathLoading());
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        /// SCENE INFORMATION ///
    }

    private void createNewPath(){
        System.out.println("Do Dijkstra algorithm");

        Long[] key = new Long[loadingResult.nodes.size()];
        loadingResult.nodes.keySet().toArray(key);

        Node from = loadingResult.nodes.get(key[ThreadLocalRandom.current().nextInt(0, loadingResult.nodes.size())]);
        Node to = loadingResult.nodes.get(key[ThreadLocalRandom.current().nextInt(0, loadingResult.nodes.size())]);

        List<Node> path = Dijkstra.findShortestPath(loadingResult.graph, from, to);

        SortedMap<Long, Node> pathNodes = new TreeMap<>();
        Node previous = path.get(0), next;
        previous = new Node(previous.getId(), previous.getPosition().x, previous.getPosition().y);
        pathNodes.put(previous.getId(), previous);

        for (int i = 1; i < path.size(); ++i){
            next = path.get(i);
            next = new Node(next.getId(), next.getPosition().x, next.getPosition().y);
            pathNodes.put(next.getId(), next);

            previous.add(next);
            next.add(previous);

            previous = next;
        }

        pathRenderer.init(pathNodes);
        pathRenderer.updateNodes(pathNodes, boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);
    }

    public void zoom(Vector2f factor){
        mapCamera.setZoomLevel(new Vector2f(mapCamera.getZoomLevel()).mul(factor));
        nodeRenderer.updateNodes(loadingResult.nodes.values(), boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
        roadRenderer.updateNodes(loadingResult.nodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
    }

    @Override
    public void endLoading(){
        nodeRenderer = new NodeRenderer<>(mapCamera);
        nodeRenderer.init(loadingResult.nodes.values());
        nodeRenderer.updateNodes(loadingResult.nodes.values(), boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        roadRenderer = new RoadRenderer(mapCamera);
        roadRenderer.init(loadingResult.nodes);
        roadRenderer.updateNodes(loadingResult.nodes, boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        pathRenderer = new RoadRenderer(mapCamera);
        pathRenderer.setColor(ColorList.Green());

        createNewPath();
    }


    @Override
    public void updateAfterLoading() {
        super.unloadAfterLoading();
        checkQuit();

        if (mouseManager.getMouseScroll().y != 0) {
            mapCamera.setZoomReference(
                    new Vector2f(
                            mouseManager.posX() / windowSize.x,
                            mouseManager.posY() / windowSize.y
                    ));

            if (inputManager.getState(ActionId.SHIFT))
                zoom(new Vector2f(1, 1 + mainContext.getMouseManager().getMouseScroll().y * 0.1f));
            else
                zoom(new Vector2f(1 + mainContext.getMouseManager().getMouseScroll().y * 0.1f));
        }

        if (inputManager.inputPressed(ActionId.ENTER))
            createNewPath();

        if (inputManager.inputReleased(ActionId.RIGHT_CLICK))
            isDragging = false;

        if (isDragging) {
            mapCamera.moveXinZoom( -mouseManager.posX() + mouseManager.oldPosX());
            mapCamera.moveYinZoom( -mouseManager.posY() + mouseManager.oldPosY());
        }

        if (inputManager.inputPressed(ActionId.RIGHT_CLICK)){
            isDragging = true;
        }

        if (inputManager.inputPressed(ActionId.SHIFT)) {
            mapCamera.setZoomReference(EDirection.None);
            zoom(new Vector2f(1.01f));
        }

        move(mapCamera, inputManager);
    }

    @Override
    public void displayAL() {
        nodeRenderer.display();
        roadRenderer.display();
        pathRenderer.display();
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        nodeRenderer.unload();
        roadRenderer.unload();
        pathRenderer.unload();
    }
}

