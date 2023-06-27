package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.renderer._2D.shape.RectangleRenderer;
import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.main.GameTime;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.mightylib.util.math.MathTime;
import MobilityViewer.project.scenes.loadingContent.ScooterSimulationLoading;
import MobilityViewer.project.display.GUI.HorizontalSlider;
import MobilityViewer.project.display.GUI.Slider;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.display.Scooter;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ShowScootersSimulation extends SceneMap<ScooterSimulationLoading, ScooterSimulationLoading.SSLResult> {
    private static final float BASE_TIME_SPEED = 10000000;
    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private long startTime;
    private long currentTime;

    private float scooterSize;

    private boolean playing;
    private RectangleRenderer UIBackground;
    private Text currentTimeText, speedText;
    private Slider timeSlider, speedSlider;

    public ShowScootersSimulation() {
        super(new ScooterSimulationLoading(),
                "Object size up / down : u / j\n");
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        scooterSize = 10f;
    }

    @Override
    public void endLoading(){
        playing = true;

        currentTimeText = new Text();
        currentTimeText.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.LeftUp)
                .setPosition(new Vector2f(windowSize.x * 0.02f, windowSize.y * 0.02f))
                .setFontSize(40)
                .setText("20 January");

        speedText = new Text();
        speedText.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.70f, windowSize.y * 0.05f))
                .setFontSize(20)
                .setText("x1");

        this.startTime = MathTime.datetimeStrToTick("2022-01-01 00:00:00");
        currentTime = 0;

        timeSlider = new HorizontalSlider(main2DCamera, inputManager, mouseManager,
                new Vector2f(windowSize.x * 0.2f, windowSize.y * 0.9f),
                new Vector2f(windowSize.x * 0.6f, windowSize.y * 0.1f),
                0,
                MathTime.datetimeStrToTick("2022-12-31 23:59:59") -  startTime
        );

        speedSlider = new HorizontalSlider(main2DCamera, inputManager, mouseManager,
                new Vector2f(windowSize.x * 0.78f, windowSize.y * 0f),
                new Vector2f(windowSize.x * 0.2f, windowSize.y * 0.1f),
                0,
                7
        );

        speedSlider.setValue(0);

        displayBoundaries = new Vector4f(0, 0, windowSize.x, windowSize.y);
        boundaries = SceneConstants.BOUNDARIES;

        UIBackground = new RectangleRenderer("colorShape2D");
        UIBackground.switchToColorMode(new Color4f(0.8f, 0.8f, 0.8f, 0.8f));
        UIBackground.setPosition(new Vector2f());
        UIBackground.setSizePix(windowSize.x, windowSize.y * 0.1f);

        nodeRenderer = new NodeRenderer<>(mapCamera);
        nodeRenderer.init(loadingResult.nodes.values());
        nodeRenderer.updateNodes(
                loadingResult.nodes.values(), boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        roadRenderer = new RoadRenderer(mapCamera);
        roadRenderer.init(loadingResult.nodes);
        roadRenderer.updateNodes(
                loadingResult.nodes, boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        for (Scooter scooter : loadingResult.scooters)
            scooter.initRenderer(mapCamera);
    }


    public void zoom(Vector2f factor){
        mapCamera.setZoomLevel(new Vector2f(mapCamera.getZoomLevel()).mul(factor));
        nodeRenderer.updateNodes(loadingResult.nodes.values(), boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
        roadRenderer.updateNodes(loadingResult.nodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);

        Scooter.Size = scooterSize / mapCamera.getZoomLevel().x;
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

        if (inputManager.inputPressed(ActionId.RIGHT_CLICK))
            isDragging = true;


        if (inputManager.inputPressed(ActionId.SHIFT)) {
            mapCamera.setZoomReference(EDirection.None);
            zoom(new Vector2f(1.01f));
        }

        SceneMap.moves(mapCamera, inputManager);

        speedSlider.update();
        if (speedSlider.isGettingDragged())
            speedText.setText("x" + ((int)Math.pow(10, speedSlider.getCurrentValue())));


        timeSlider.update();
        if (playing) {
            if (timeSlider.isGettingDragged())
                currentTime = (long) timeSlider.getCurrentValue();
            else {
                currentTime += GameTime.DeltaTime() * Math.pow(10, speedSlider.getCurrentValue()) * BASE_TIME_SPEED;
                timeSlider.setValue(currentTime);
            }

            currentTimeText.setText(MathTime.tickToStr(currentTime + startTime));
        }

        for (Scooter scooter : loadingResult.scooters)
            scooter.update(currentTime, boundaries, displayBoundaries);

        if (inputManager.getState(ActionId.OBJECT_SIZE_UP)){
            scooterSize *= 1.005f;
            Scooter.Size = scooterSize / mapCamera.getZoomLevel().x;
        }

        if (inputManager.getState(ActionId.OBJECT_SIZE_DOWN)){
            scooterSize /= 1.005f;
            Scooter.Size = scooterSize / mapCamera.getZoomLevel().x;
        }
    }

    @Override
    public void displayAL() {
        nodeRenderer.display();
        roadRenderer.display();

        for (Scooter scooter : loadingResult.scooters)
            scooter.display(currentTime);

        UIBackground.display();
        currentTimeText.display();
        timeSlider.display();


        speedText.display();
        speedSlider.display();
    }


    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        UIBackground.unload();
        currentTimeText.unload();
        speedText.unload();

        nodeRenderer.unload();
        roadRenderer.unload();
        speedSlider.unload();

        for (Scooter scooter : loadingResult.scooters)
            scooter.unload();

        timeSlider.unload();
    }
}
