package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.display.MatrixRenderer;
import MobilityViewer.project.display.TypeSelector;
import MobilityViewer.project.main.ETypeData;
import MobilityViewer.project.scenes.loadingContent.FullMatrixLoading;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

import java.util.HashMap;

public class ConstructFullMatrixScene extends SceneMap<FullMatrixLoading, FullMatrixLoading.FMLResult> {
    private HashMap<ETypeData, MatrixRenderer> matricesRenderer;

    private Text currentMatrixDisplayed;

    private TypeSelector<ETypeData> typeSelector;

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
        matricesRenderer = new HashMap<>();
        for (ETypeData key : ETypeData.values()){
            matricesRenderer.put(key, new MatrixRenderer(mapCamera,
                    loadingResult.matrices.get(key).length * loadingResult.matrices.get(key)[0].length));

            matricesRenderer.get(key).updateNodes(loadingResult.matrices.get(key),
                    loadingResult.minValues.get(key), loadingResult.maxValues.get(key), displayBoundaries);
        }

        typeSelector = new TypeSelector<>(ETypeData.values(), mainContext);

        currentMatrixDisplayed = new Text();
        currentMatrixDisplayed.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.50f, windowSize.y * 0.05f))
                .setFontSize(30)
                .setColor(ColorList.LightGreen())
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

        typeSelector.update();
    }

    @Override
    public void displayAL() {
        matricesRenderer.get(typeSelector.getSelected()).display();
        currentMatrixDisplayed.display();

        typeSelector.display();
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();
        for (ETypeData key : ETypeData.values()){
            matricesRenderer.get(key).unload();
        }

        currentMatrixDisplayed.unload();
        typeSelector.unload();
    }
}
