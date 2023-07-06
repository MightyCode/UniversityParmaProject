package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.loadingContent.BikeMovementLoading;
import org.joml.Vector2f;

public class ShowBikeMovementsScene extends SceneMap<BikeMovementLoading, BikeMovementLoading.BMResult> {
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private RoadRenderer pathRenderer;
    private int currentPathDisplayed;

    public ShowBikeMovementsScene() {
        super(new BikeMovementLoading(), "Show / Hide stations : space\n");
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
        updatePathRenderer();
    }

    public void updatePathRenderer(){
        pathRenderer.setColor(ColorList.Green());
        pathRenderer.init(loadingResult.paths[currentPathDisplayed]);
        pathRenderer.updateNodes(loadingResult.paths[currentPathDisplayed],
                boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);
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

