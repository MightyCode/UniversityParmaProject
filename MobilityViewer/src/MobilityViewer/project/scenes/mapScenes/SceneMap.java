package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.renderer._2D.shape.RectangleRenderer;
import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.inputs.InputManager;
import MobilityViewer.mightylib.inputs.MouseManager;
import MobilityViewer.mightylib.main.GameTime;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.scenes.loadingContent.LoadingContent;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.LoadingScene;
import MobilityViewer.project.scenes.MenuScene;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class SceneMap<T extends LoadingContent, K extends LoadingContent.Result> extends LoadingScene<T, K> {
    public static final float MOVE_SPEED = 600;
    public static final float SHIFT_SPEED = MOVE_SPEED * 2f;

    private Text loadingDisplay;

    protected Vector4f boundaries;
    protected Vector4f displayBoundaries;
    protected Vector2i windowSize;

    protected InputManager inputManager;
    protected MouseManager mouseManager;

    protected Camera2D mapCamera;

    protected boolean isDragging;

    private RectangleRenderer helpBackground;
    private Text helpText;
    private boolean helpVisible;

    private final String furtherInstruction;

    public SceneMap(T T) {
        this(T, "");
    }

    public SceneMap(T T, String furtherInstruction) {
        super(T);
        this.furtherInstruction = furtherInstruction;
    }

    @Override
    public void initialize(String[] args){
        main3DCamera.setPos(new Vector3f(0, 0, 0));
        main2DCamera.setPos(new Vector2f(0, 0));
        main2DCamera.setZoomLevel(1);

        setClearColor(200, 200, 200, 1f);
        mapCamera = new Camera2D(mainContext.getWindow().getInfo(), new Vector2f(0, 0));

        inputManager = mainContext.getInputManager();
        mouseManager = mainContext.getMouseManager();

        windowSize = mainContext.getWindow().getInfo().getSizeCopy();

        displayBoundaries = new Vector4f(0, 0, windowSize.x, windowSize.y);
        boundaries = SceneConstants.BOUNDARIES;

        loadingDisplay = new Text();
        loadingDisplay.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.5f))
                .setFontSize(40)
                .setText("Loading (0 %)");

        helpText = new Text();
        helpText.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Left)
                .setReference(EDirection.LeftUp)
                .setFontSize(20)
                .setPosition(new Vector2f(windowSize.x * 0.01f, windowSize.y * 0.1f))
                .setText(       "Zoom in/out (to mouse cursor) : mouse wheel\n" +
                                "Zoom only Y-axis : shift + zoom\n" +
                                "Move around : arrows or WASD / QZSD\n" +
                                "Show / hide help : h\n" +
                                furtherInstruction +
                                "Quit scene : esc"
                        );

        helpVisible = true;

        helpBackground = new RectangleRenderer("colorShape2D");
        helpBackground.switchToColorMode(new Color4f(0.8f, 0.8f, 0.8f, 1f));
        helpBackground.setPosition(helpText.leftUpPosition());
        helpBackground.setSizePix(helpText.size().x, helpText.size().y);
    }

    @Override
    protected final void updateBeforeLoading(){
        checkQuit();
        if (hasLoadingAdvance())
            loadingDisplay.setText(
                    "Loading (" + (((int)(percentage() * 10000)) / 100.0f) + " %)"
                    + "\n" + stepName()
            );
    }


    @Override
    public final void displayBeforeLoading() {
        super.setVirtualScene();
        clear();

        loadingDisplay.display();

        super.setAndDisplayRealScene();
    }

    public abstract void displayAL();

    @Override
    public final void displayAfterLoading() {
        super.setVirtualScene();
        clear();

        displayAL();

        if (mainContext.getInputManager().inputPressed(ActionId.SHOW_HIDE_HELP))
            helpVisible = !helpVisible;

        if (helpVisible) {
            helpBackground.display();
            helpText.display();
        }

        super.setAndDisplayRealScene();
    }


    protected void checkQuit(){
        if (mainContext.getInputManager().inputPressed(ActionId.ESCAPE))
            sceneManagerInterface.setNewScene(new MenuScene(), new String[]{""});
    }

    protected void move(Camera2D mapCamera, InputManager inputManager){
        float speed = MOVE_SPEED;
        if (inputManager.getState(ActionId.SHIFT))
            speed = SHIFT_SPEED;

        speed *= GameTime.DeltaTime();

        if (inputManager.getState(ActionId.MOVE_LEFT)){
            mapCamera.moveXinZoom(-speed);
        }

        if (inputManager.getState(ActionId.MOVE_RIGHT)){
            mapCamera.moveXinZoom(speed);
        }

        if (inputManager.getState(ActionId.MOVE_UP)){
            mapCamera.moveYinZoom(-speed);
        }

        if (inputManager.getState(ActionId.MOVE_DOWN)){
            mapCamera.moveYinZoom(speed);
        }
    }

    public static void moves(Camera2D mapCamera, InputManager inputManager){
        float speed = MOVE_SPEED;
        if (inputManager.getState(ActionId.SHIFT))
            speed = SHIFT_SPEED;

        speed *= GameTime.DeltaTime();

        if (inputManager.getState(ActionId.MOVE_LEFT)){
            mapCamera.moveXinZoom(-speed);
        }

        if (inputManager.getState(ActionId.MOVE_RIGHT)){
            mapCamera.moveXinZoom(speed);
        }

        if (inputManager.getState(ActionId.MOVE_UP)){
            mapCamera.moveYinZoom(-speed);
        }

        if (inputManager.getState(ActionId.MOVE_DOWN)){
            mapCamera.moveYinZoom(speed);
        }
    }

    @Override
    public final void unloadBeforeLoading() {
        loadingDisplay.unload();
        helpText.unload();
        helpBackground.unload();
    }

    @Override
    public void unloadAfterLoading() {
        loadingDisplay.unload();
        helpText.unload();
        helpBackground.unload();
    }
}
