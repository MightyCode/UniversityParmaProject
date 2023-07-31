package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.display.*;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.scenes.loadingContent.MovesMatrixLoading;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

import java.util.HashMap;

public class MovesMatrixScene extends SceneMap<MovesMatrixLoading, MovesMatrixLoading.MMLResult> {
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private HashMap<String, MatrixRenderer> startMatricesRenderer;
    private HashMap<String, MatrixRenderer> endMatricesRenderer;
    private HashMap<String, MatrixRenderer> combineMatricesRenderer;

    private HashMap<String, MatrixRenderer> absoluteMatricesRenderer;

    private int matrixToShow;
    private boolean shouldShowMap;
    private Text currentMatrixDisplayed;

    private StrSelector typeSelector;

    public MovesMatrixScene() {
        super(new MovesMatrixLoading(),
                "Switch from start / end matrix : space\n" +  "Show / Hide map : t\n");
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
        typeSelector = new StrSelector(resourceCategories, mainContext);
        typeSelector.setIfExists(currentResourceCategory);

        nodeRenderer = new NodeRenderer<>(mapCamera);
        nodeRenderer.setColor(new Color4f(1, 0, 0, 0.1f));
        nodeRenderer.init(loadingResult.nodes.values());
        nodeRenderer.updateNodes(
                loadingResult.nodes.values(), boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        roadRenderer = new RoadRenderer(mapCamera);
        roadRenderer.setColor(new Color4f(0, 0, 1, 0.6f));
        roadRenderer.init(loadingResult.nodes);
        roadRenderer.updateNodes(
                loadingResult.nodes, boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        matrixToShow = 0;

        startMatricesRenderer = new HashMap<>();
        endMatricesRenderer = new HashMap<>();
        absoluteMatricesRenderer = new HashMap<>();
        combineMatricesRenderer = new HashMap<>();

        for (String key : resourceCategories){
            startMatricesRenderer.put(key, new MatrixRenderer(mapCamera,
                    loadingResult.startMatrices.get(key).length * loadingResult.startMatrices.get(key)[0].length));

            startMatricesRenderer.get(key)
                    .updateNodes(loadingResult.startMatrices.get(key),
                            loadingResult.minStartValues.get(key), loadingResult.maxStartValues.get(key), displayBoundaries);

            endMatricesRenderer.put(key, new MatrixRenderer(mapCamera,
                    loadingResult.endMatrices.get(key).length * loadingResult.endMatrices.get(key)[0].length));

            endMatricesRenderer.get(key)
                    .updateNodes(loadingResult.endMatrices.get(key),
                            loadingResult.minEndValues.get(key), loadingResult.maxEndValues.get(key), displayBoundaries);

            absoluteMatricesRenderer.put(key, new MatrixRenderer(mapCamera,
                    loadingResult.absoluteMatrices.get(key).length * loadingResult.absoluteMatrices.get(key)[0].length));

            absoluteMatricesRenderer.get(key)
                    .updateNodes(loadingResult.absoluteMatrices.get(key),
                            loadingResult.minAbsoluteValues.get(key), loadingResult.maxAbsoluteValues.get(key), displayBoundaries);

            combineMatricesRenderer.put(key, new MatrixRenderer(mapCamera,
                    loadingResult.combineMatrices.get(key).length * loadingResult.combineMatrices.get(key)[0].length));

            combineMatricesRenderer.get(key).setColorMode(MatrixRenderer.EMatrixRendererMode.ColorInterval);

            combineMatricesRenderer.get(key)
                    .updateNodes(loadingResult.combineMatrices.get(key),
                            loadingResult.minCombineValues.get(key), loadingResult.maxCombineValues.get(key), displayBoundaries);
        }

        currentMatrixDisplayed = new Text();
        currentMatrixDisplayed.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setColor(ColorList.LightGreen())
                .setPosition(new Vector2f(windowSize.x * 0.50f, windowSize.y * 0.05f))
                .setFontSize(30)
                .setText("Start matrix");

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
            if (++matrixToShow >= 4){
                matrixToShow = 0;
            }

            switch (matrixToShow){
                case 0: default:
                    currentMatrixDisplayed.setText("Start Matrix"); break;
                case 1:
                    currentMatrixDisplayed.setText("End Matrix"); break;
                case 2:
                    currentMatrixDisplayed.setText("Absolute Matrix"); break;
                case 3:
                    currentMatrixDisplayed.setText("Combine Matrix"); break;
            }
        }

        if (inputManager.inputPressed(ActionId.SHOW_HIDE_MAP))
            shouldShowMap = !shouldShowMap;

        typeSelector.update();
        if (typeSelector.isUpdated())
            currentResourceCategory = typeSelector.getSelected();
    }

    @Override
    public void displayAL() {
        switch (matrixToShow){
            case 0: default:
                startMatricesRenderer.get(typeSelector.getSelected()).display(); break;
            case 1:
                endMatricesRenderer.get(typeSelector.getSelected()).display(); break;
            case 2:
                absoluteMatricesRenderer.get(typeSelector.getSelected()).display(); break;
            case 3:
                combineMatricesRenderer.get(typeSelector.getSelected()).display(); break;
        }

        if (shouldShowMap) {
            //nodeRenderer.display();
            roadRenderer.display();
        }

        currentMatrixDisplayed.display();
        typeSelector.display();
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        typeSelector.unload();

        for (String key : resourceCategories){
            startMatricesRenderer.get(key).unload();
            endMatricesRenderer.get(key).unload();
            absoluteMatricesRenderer.get(key).unload();
            combineMatricesRenderer.get(key).unload();
        }

        nodeRenderer.unload();
        roadRenderer.unload();

        currentMatrixDisplayed.unload();
    }
}
