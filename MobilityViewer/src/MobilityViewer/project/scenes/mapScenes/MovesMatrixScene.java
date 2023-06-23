package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.display.MatrixRenderer;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.scenes.loadingContent.MovesMatrixLoading;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

public class MovesMatrixScene extends SceneMap<MovesMatrixLoading, MovesMatrixLoading.MMLResult>
{
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private MatrixRenderer startMatrixRenderer;
    private MatrixRenderer endMatrixRenderer;

    private boolean shouldShowStartMatrix;
    private boolean shouldShowMap;

    private Text currentMatrixDisplayed;

    public MovesMatrixScene() {
        super(new MovesMatrixLoading(),
                "Switch from start / end matrix : space\n"
                         +   "Show / Hide map : t\n");
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        /// SCENE INFORMATION ///
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

        startMatrixRenderer = new MatrixRenderer(mapCamera,
                loadingResult.startMatrix.length * loadingResult.startMatrix[0].length);
        startMatrixRenderer.updateNodes(loadingResult.startMatrix, loadingResult.maxStartValue, displayBoundaries);

        endMatrixRenderer = new MatrixRenderer(mapCamera,
                loadingResult.endMatrix.length * loadingResult.endMatrix[0].length);
        endMatrixRenderer.updateNodes(loadingResult.endMatrix, loadingResult.maxEndValue, displayBoundaries);

        currentMatrixDisplayed = new Text();
        currentMatrixDisplayed.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.50f, windowSize.y * 0.05f))
                .setFontSize(30)
                .setText("Start matrix");

        shouldShowStartMatrix = true;
        shouldShowMap = true;
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

        move(mapCamera, inputManager);

        if (inputManager.inputPressed(ActionId.SWITCH)) {
            shouldShowStartMatrix = !shouldShowStartMatrix;

            if (shouldShowStartMatrix)
                currentMatrixDisplayed.setText("Start Matrix");
            else
                currentMatrixDisplayed.setText("End Matrix");
        }

        if (inputManager.inputPressed(ActionId.SHOW_HIDE_MAP))
            shouldShowMap = !shouldShowMap;
    }

    @Override
    public void displayAL() {
        if (shouldShowStartMatrix)
            startMatrixRenderer.display();
        else
            endMatrixRenderer.display();

        if (shouldShowMap) {
            nodeRenderer.display();
            roadRenderer.display();
        }

        currentMatrixDisplayed.display();
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        startMatrixRenderer.unload();
        endMatrixRenderer.unload();
        nodeRenderer.unload();
        roadRenderer.unload();

        currentMatrixDisplayed.unload();
    }
}
