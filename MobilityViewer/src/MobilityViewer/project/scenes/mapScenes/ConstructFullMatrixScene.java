package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.display.MatrixRenderer;
import MobilityViewer.project.scenes.loadingContent.FullMatrixLoading;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

public class ConstructFullMatrixScene extends SceneMap<FullMatrixLoading, FullMatrixLoading.FMLResult> {
    private MatrixRenderer matrixRenderer;

    private Text currentMatrixDisplayed;

    public ConstructFullMatrixScene() {
        super(new FullMatrixLoading());
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        /// SCENE INFORMATION ///
    }

    public void zoom(Vector2f factor){
        mapCamera.setZoomLevel(new Vector2f(mapCamera.getZoomLevel()).mul(factor));
    }

    @Override
    public void endLoading(){
        matrixRenderer = new MatrixRenderer(mapCamera,
                loadingResult.matrix.length * loadingResult.matrix[0].length);
        matrixRenderer.updateNodes(loadingResult.matrix, loadingResult.maxValue, displayBoundaries);

        System.out.println("Matrix of size : " + (loadingResult.matrix.length * loadingResult.matrix[0].length));

        currentMatrixDisplayed = new Text();
        currentMatrixDisplayed.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.50f, windowSize.y * 0.05f))
                .setFontSize(30)
                .setText("Full matrix");
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
    }

    @Override
    public void displayAL() {
        matrixRenderer.display();

        currentMatrixDisplayed.display();
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        matrixRenderer.unload();

        currentMatrixDisplayed.unload();
    }
}
