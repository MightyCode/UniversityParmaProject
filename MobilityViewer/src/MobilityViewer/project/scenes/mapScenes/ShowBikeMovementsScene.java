package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.ProjectUtil;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.graph.Dijkstra;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.loadingContent.BikeMovementLoading;
import org.joml.Vector2f;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ShowBikeMovementsScene extends SceneMap<BikeMovementLoading, BikeMovementLoading.BMResult> {
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private RoadRenderer pathRenderer;
    private int currentPathDisplayed;

    private Text currentPathDisplayedText;

    private boolean showDijkstraOnTop;
    private RoadRenderer dijkstraRenderer;

    public ShowBikeMovementsScene() {
        super(new BikeMovementLoading(),
                "Show / Not show dijkstra on top : space\n" +
                        "Next path : enter\n");
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        /// SCENE INFORMATION ///
        currentPathDisplayed = 0;
    }

    public void zoom(Vector2f factor){
        mapCamera.setZoomLevel(new Vector2f(mapCamera.getZoomLevel()).mul(factor));
        nodeRenderer.updateNodes(
                loadingResult.nodes.values(), boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
        roadRenderer.updateNodes(
                loadingResult.nodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
    }

    @Override
    public void endLoading(){
        nodeRenderer = new NodeRenderer<>(mapCamera);
        nodeRenderer.init(loadingResult.nodes.values());
        nodeRenderer.updateNodes(
                loadingResult.nodes.values(), boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        roadRenderer = new RoadRenderer(mapCamera);
        roadRenderer.init(loadingResult.nodes);
        roadRenderer.updateNodes(
                loadingResult.nodes, boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        pathRenderer = new RoadRenderer(mapCamera);

        dijkstraRenderer = new RoadRenderer(mapCamera);
        dijkstraRenderer.setColor(ColorList.Coral());
        showDijkstraOnTop = false;

        currentPathDisplayedText = new Text();
        currentPathDisplayedText.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.Up)
                .setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.01f))
                .setFontSize(30)
                .setText("");

        updatePathRenderer();
    }

    public void updatePathRenderer(){
        currentPathDisplayedText.setText("Path displayed : " + (currentPathDisplayed + 1) + "/" + loadingResult.paths.length);

        pathRenderer.setColor(ColorList.Green());
        pathRenderer.init(loadingResult.paths[currentPathDisplayed]);
        pathRenderer.updateNodes(loadingResult.paths[currentPathDisplayed],
                boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        Vector2f startPosition = loadingResult.firstNodes.get(currentPathDisplayed).getPosition();

        Vector2f endPosition = loadingResult.lastNodes.get(currentPathDisplayed).getPosition();

        Node position = new Node(-1, startPosition.x, startPosition.y);
        Node startNode = ProjectUtil.findClosest(loadingResult.nodes, position);
        position = new Node(-1, endPosition.x, endPosition.y);
        Node[] arrayNodes = ProjectUtil.findNClosest(loadingResult.nodes, position, 2);
        Node endNode;
        if (arrayNodes[0].getId() == startNode.getId()) {
            if (position.getDist(startNode) / 2f > position.getDist(arrayNodes[1]))
                endNode = arrayNodes[0];
            else
                endNode = arrayNodes[1];
        } else {
            endNode = arrayNodes[0];
        }
        List<Node> path = null;
        if (startNode != endNode) {
            path = Dijkstra.findShortestPath(loadingResult.graph, startNode, endNode);
        }
        SortedMap<Long, Node> pathNodes = new TreeMap<>();
        Node previous = path.get(0), next;
        previous = new Node(previous.getId(), previous.getPosition().x, previous.getPosition().y);
        pathNodes.put(previous.getId(), previous);
        for (int i = 1; i < path.size(); ++i) {
            next = path.get(i);
            next = new Node(next.getId(), next.getPosition().x, next.getPosition().y);
            pathNodes.put(next.getId(), next);
            previous.add(next);
            next.add(previous);
            previous = next;
        }

        dijkstraRenderer.init(pathNodes);
        dijkstraRenderer.updateNodes(pathNodes, boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);
    }

    @Override
    public void updateAfterLoading() {
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

        if (inputManager.inputPressed(ActionId.ENTER)){
            currentPathDisplayed += 1;
            if (currentPathDisplayed == loadingResult.paths.length)
                currentPathDisplayed = 0;

            updatePathRenderer();
        }

        if (inputManager.inputPressed(ActionId.SWITCH))
            showDijkstraOnTop = !showDijkstraOnTop;


        move(mapCamera, inputManager);
    }

    @Override
    public void displayAL() {
        nodeRenderer.display();
        roadRenderer.display();

        if (showDijkstraOnTop){
            pathRenderer.display();
            dijkstraRenderer.display();
        } else {
            dijkstraRenderer.display();
            pathRenderer.display();
        }


        currentPathDisplayedText.display();
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        nodeRenderer.unload();
        roadRenderer.unload();
        pathRenderer.unload();
        currentPathDisplayedText.unload();
        dijkstraRenderer.unload();
    }
}

