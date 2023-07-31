package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.GUI.BackgroundlessButton;
import MobilityViewer.mightylib.graphics.GUI.GUIList;
import MobilityViewer.mightylib.graphics.renderer._2D.shape.RectangleRenderer;
import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.main.GameTime;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.mightylib.util.math.MathTime;
import MobilityViewer.project.TickTranslator;
import MobilityViewer.project.scenes.loadingContent.ShowVehicleLoading;
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

public class ShowVehicleSimulation extends SceneMap<ShowVehicleLoading, ShowVehicleLoading.SSLResult> {
    private static final long BASE_TIME_SPEED = 1000000;

    public static TickTranslator TRANSLATOR;

    private NodeRenderer<Node> nodeRenderer;
    private RoadRenderer roadRenderer;

    private long startTime;
    private long endTime;
    private long currentTime;

    private float scooterSize;

    private boolean playing;
    private RectangleRenderer UIBackground;
    private Text currentTimeText, speedText;
    private Slider timeSlider, speedSlider;
    private GUIList chooseTime;

    private BackgroundlessButton timeInYear, timeInMonth, timeInWeek, timeInDay;

    private String pattern;

    public ShowVehicleSimulation() {
        super(new ShowVehicleLoading(),
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

        TRANSLATOR = new TickTranslator.BaseTickTranslator();

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
        this.endTime = MathTime.datetimeStrToTick("2022-12-31 23:59:59");
        currentTime = 0;

        createTimeSlider(currentTime);

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

        Scooter.initRenderer(mapCamera);

        chooseTime = new GUIList(inputManager, mouseManager);

        timeInYear = new BackgroundlessButton(mainContext);
        timeInYear.Text.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.Down)
                .setPosition(new Vector2f(windowSize.x * 0.95f, windowSize.y * 0.8f))
                .setFontSize(20)
                .setText("Time in year");

        timeInYear.Text.copyTo(timeInYear.OverlapsText);
        timeInYear.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Time in year<-");

        timeInMonth = timeInYear.copy();
        timeInMonth.Text.setPosition(new Vector2f(windowSize.x * 0.95f, windowSize.y * 0.85f))
                .setText("Time in month");

        timeInMonth.Text.copyTo(timeInMonth.OverlapsText);
        timeInMonth.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Time in month<-");

        timeInWeek = timeInYear.copy();
        timeInWeek.Text.setPosition(new Vector2f(windowSize.x * 0.95f, windowSize.y * 0.9f))
                .setText("Time in week");

        timeInWeek.Text.copyTo(timeInWeek.OverlapsText);
        timeInWeek.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Time in week<-");

        timeInDay = timeInYear.copy();
        timeInDay.Text.setPosition(new Vector2f(windowSize.x * 0.95f, windowSize.y * 0.95f))
                .setText("Time in day");

        timeInDay.Text.copyTo(timeInDay.OverlapsText);
        timeInDay.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Time in day<-");

        chooseTime.GUIs.put(0, timeInYear);
        chooseTime.GUIs.put(1, timeInMonth);
        chooseTime.GUIs.put(2, timeInWeek);
        chooseTime.GUIs.put(3, timeInDay);

        setColor();

        pattern = "dd MMMM yyyy, H 'h' m 'm' s 's'";
    }

    public void switchToTranslator(TickTranslator tt){
        TRANSLATOR = tt;
        currentTime = tt.convert(currentTime);
        createTimeSlider(currentTime);

        setColor();
    }

    public void setColor(){
        timeInYear.Text.setColor(ColorList.Black());
        timeInYear.OverlapsText.setColor(ColorList.Black());

        timeInMonth.Text.setColor(ColorList.Black());
        timeInMonth.OverlapsText.setColor(ColorList.Black());

        timeInWeek.Text.setColor(ColorList.Black());
        timeInWeek.OverlapsText.setColor(ColorList.Black());

        timeInDay.Text.setColor(ColorList.Black());
        timeInDay.OverlapsText.setColor(ColorList.Black());

        if (TRANSLATOR instanceof TickTranslator.BaseTickTranslator){
            timeInYear.Text.setColor(ColorList.Red());
            timeInYear.OverlapsText.setColor(ColorList.Red());
        } else if (TRANSLATOR instanceof TickTranslator.MonthTickTranslator) {
            timeInMonth.Text.setColor(ColorList.Red());
            timeInMonth.OverlapsText.setColor(ColorList.Red());
        } else if (TRANSLATOR instanceof TickTranslator.WeekTickTranslator) {
            timeInWeek.Text.setColor(ColorList.Red());
            timeInWeek.OverlapsText.setColor(ColorList.Red());
        } else if (TRANSLATOR instanceof TickTranslator.DayTickTranslator) {
            timeInDay.Text.setColor(ColorList.Red());
            timeInDay.OverlapsText.setColor(ColorList.Red());
        }
    }

    public void createTimeSlider(long init){
        timeSlider = new HorizontalSlider(main2DCamera, inputManager, mouseManager,
                new Vector2f(windowSize.x * 0.2f, windowSize.y * 0.9f),
                new Vector2f(windowSize.x * 0.6f, windowSize.y * 0.1f),
                0,
                TRANSLATOR.duration()
        );

        timeSlider.setValue(init);
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

        chooseTime.update();
        if ((mainContext.getInputManager().inputPressed(ActionId.LEFT_CLICK) && chooseTime.isMouseSelecting())) {
            Integer id = chooseTime.getSelected();
            if (id != null) {
                switch (id) {
                    default:
                    case 0:
                        switchToTranslator(new TickTranslator.BaseTickTranslator());
                        pattern = "dd MMMM yyyy, H 'h' m 'm' s 's'";
                        break;
                    case 1:
                        switchToTranslator(new TickTranslator.MonthTickTranslator());
                        pattern = "dd '-' H 'h' m 'm' s 's'";
                        break;
                    case 2:
                        switchToTranslator(new TickTranslator.WeekTickTranslator());
                        pattern = "EEEE H 'h' m 'm' s 's'";
                        break;
                    case 3:
                        switchToTranslator(new TickTranslator.DayTickTranslator());
                        pattern = "H 'h' m 'm' s 's'";
                        break;
                }
            }
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

            if (currentTime >= TRANSLATOR.duration())
                currentTime = TRANSLATOR.duration() - BASE_TIME_SPEED;
        }

        if (inputManager.getState(ActionId.OBJECT_SIZE_UP)){
            scooterSize *= 1.005f;
            Scooter.Size = scooterSize / mapCamera.getZoomLevel().x;
        }

        if (inputManager.getState(ActionId.OBJECT_SIZE_DOWN)){
            scooterSize /= 1.005f;
            Scooter.Size = scooterSize / mapCamera.getZoomLevel().x;
        }

        if (playing) {
            Scooter.beforeUpdate();

            for (Scooter scooter : loadingResult.scooters)
                scooter.update(currentTime, boundaries, displayBoundaries);

            currentTimeText.setText(MathTime.tickToCustomizedStr(currentTime + startTime, pattern) +
                    "\n" + Scooter.getNumberDrawn() + " scooters");
        }
    }

    @Override
    public void displayAL() {
        nodeRenderer.display();
        roadRenderer.display();

        Scooter.display();

        UIBackground.display();
        currentTimeText.display();
        timeSlider.display();

        speedText.display();
        speedSlider.display();

        chooseTime.display();
    }


    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        UIBackground.unload();
        currentTimeText.unload();
        speedText.unload();

        nodeRenderer.unload();
        roadRenderer.unload();
        speedSlider.unload();

        Scooter.unload();

        timeSlider.unload();

        chooseTime.unload();
    }
}
