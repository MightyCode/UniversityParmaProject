package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.scenes.loadingContent.ExportScooterPathLoading;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.LoadingScene;
import MobilityViewer.project.scenes.MenuScene;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class ExportScootersPathScene extends LoadingScene<ExportScooterPathLoading, ExportScooterPathLoading.EXPLResult> {
    public ExportScootersPathScene(){
        super(new ExportScooterPathLoading());
    }
    private Vector2i windowSize;
    private Text done;
    private Text loadingDisplay;

    private String currentTypeResource;

    @Override
    public void initialize(String[] args) {
        /// SCENE INFORMATION ///

        currentTypeResource = args[0];

        main3DCamera.setPos(new Vector3f(0, 0, 0));
        main2DCamera.setPos(new Vector2f(0, 0));
        main2DCamera.setZoomLevel(1);

        setClearColor(200, 200, 200, 1f);

        windowSize = mainContext.getWindow().getInfo().getSizeCopy();

        loadingDisplay = new Text();
        loadingDisplay.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.5f))
                .setFontSize(40)
                .setText("Loading (0 %)");
    }

    @Override
    protected void updateBeforeLoading() {
        if (mainContext.getInputManager().inputPressed(ActionId.ESCAPE))
            sceneManagerInterface.setNewScene(new MenuScene(), new String[]{""});

        if (hasLoadingAdvance())
            loadingDisplay.setText(
                    "Loading (" + (((int)(percentage() * 10000)) / 100.0f) + " %)"
                            + "\n" + stepName()
            );
    }

    @Override
    public void updateAfterLoading(){
        if (mainContext.getInputManager().inputPressed(ActionId.ESCAPE))
            sceneManagerInterface.setNewScene(new MenuScene(), new String[]{currentTypeResource});
    }

    @Override
    protected void endLoading() {
        done = new Text();
        done.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.5f))
                .setFontSize(40)
                .setText("Done");
    }

    @Override
    protected void displayBeforeLoading() {
        super.setVirtualScene();
        clear();

        loadingDisplay.display();

        super.setAndDisplayRealScene();
    }

    @Override
    public void displayAfterLoading(){
        super.setVirtualScene();
        clear();

        done.display();

        super.setAndDisplayRealScene();
    }

    @Override
    protected void unloadBeforeLoading() {
        loadingDisplay.unload();
    }

    @Override
    public void unloadAfterLoading(){
        loadingDisplay.unload();
        done.unload();
    }
}
