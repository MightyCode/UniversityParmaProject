package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.mightylib.util.math.MightyMath;
import MobilityViewer.project.scenes.loadingContent.ParmaShowPointLoading;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.graph.*;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

public class ShowParmaScene extends SceneMap<ParmaShowPointLoading, ParmaShowPointLoading.PSPResult> {
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private Text[] bikeStationsName;
    private float fontSize;
    private boolean showStations;

    public ShowParmaScene() {
        super(new ParmaShowPointLoading(), "Show / Hide stations : space\n");
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        /// SCENE INFORMATION ///
        fontSize = 20;
        showStations = true;
    }

    public void zoom(Vector2f factor){
        mapCamera.setZoomLevel(new Vector2f(mapCamera.getZoomLevel()).mul(factor));
        nodeRenderer.updateNodes(
                loadingResult.nodes.values(), boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
        roadRenderer.updateNodes(
                loadingResult.nodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);

        for (Text text : bikeStationsName)
            text.setFontSize(fontSize / mapCamera.getZoomLevel().x);
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

        CSVFile bikeStations = Resources.getInstance().getResource(CSVFile.class, "coordinate parma bike sharing");

        Text reference = new Text();
        reference.setReferenceCamera(mapCamera);
        reference.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setFontSize(fontSize)
                .setText("");

        bikeStationsName = new Text[bikeStations.size()];
        for (int i = 0; i < bikeStations.size(); ++i){
            bikeStationsName[i] = reference.createCopy();

            Vector2f position = new Vector2f(
                    Float.parseFloat(bikeStations.getData(i, bikeStations.getColumnIndex("Y"))),
                    Float.parseFloat(bikeStations.getData(i, bikeStations.getColumnIndex("X")))
            );

            bikeStationsName[i]
                    .setText(bikeStations.getData(i, bikeStations.getColumnIndex("NOME")))
                    .setPosition(
                            new Vector2f(
                                    MightyMath.mapf(position.x, boundaries.x, boundaries.z,
                                            displayBoundaries.x, displayBoundaries.z),

                                    MightyMath.mapf(position.y, boundaries.y, boundaries.w,
                                            displayBoundaries.w, displayBoundaries.y)
                            )
                    );
        }

        reference.unload();
    }

    @Override
    public void updateAfterLoading() {
        checkQuit();

        if (inputManager.inputPressed(ActionId.SWITCH)) {
            showStations = !showStations;
        }

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
        nodeRenderer.display();
        roadRenderer.display();

        if (showStations) {
            for (Text text : bikeStationsName)
                text.display();
        }
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        nodeRenderer.unload();
        roadRenderer.unload();

        for (Text text : bikeStationsName)
            text.unload();
    }
}
