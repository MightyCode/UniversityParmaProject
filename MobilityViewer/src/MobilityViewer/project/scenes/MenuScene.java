package MobilityViewer.project.scenes;

import MobilityViewer.mightylib.graphics.GUI.BackgroundlessButton;
import MobilityViewer.mightylib.graphics.GUI.GUIList;
import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.resources.texture.BasicBindableObject;
import MobilityViewer.mightylib.resources.texture.TextureParameters;
import MobilityViewer.mightylib.scene.Scene;
import MobilityViewer.mightylib.sounds.SoundSourceCreationInfo;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.mapScenes.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MenuScene extends Scene {
    private static final SoundSourceCreationInfo CREATION_INFO_SELECT = new SoundSourceCreationInfo();
    static {
        CREATION_INFO_SELECT.name = "select";
        CREATION_INFO_SELECT.gainNode = "noise";
        CREATION_INFO_SELECT.gain = 1f;
    }

    private GUIList guiList;

    private static List<Result> parseJsonResponse(String jsonResponse) {
        List<Result> results = new ArrayList<>();

        System.out.println(jsonResponse);

        return results;
    }

    private static class Result {
        private boolean success;
        private String result;

        public Result(boolean success, String result) {
            this.success = success;
            this.result = result;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getResult() {
            return result;
        }
    }


    public void init(String[] args) {
        super.init(args, new BasicBindableObject().setQualityTexture(TextureParameters.REALISTIC_PARAMETERS));
        /// SCENE INFORMATION ///

        main3DCamera.setPos(new Vector3f(0, 0, 0));
        setClearColor(52, 189, 235, 1f);

        /// RENDERERS ///

        Vector2i windowSize = mainContext.getWindow().getInfo().getSizeCopy();

        BackgroundlessButton parmaScene = new BackgroundlessButton(mainContext);
        parmaScene.Text.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.25f, windowSize.y * 0.15f))
                .setFontSize(30)
                .setText("Parma map");

        parmaScene.Text.copyTo(parmaScene.OverlapsText);
        parmaScene.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Parma map<-");

        BackgroundlessButton constructReducedGraph = parmaScene.copy();
        constructReducedGraph.Text.setPosition(new Vector2f(windowSize.x * 0.25f, windowSize.y * 0.3f))
                .setText("Parma reduced graph");

        constructReducedGraph.Text.copyTo(constructReducedGraph.OverlapsText);
        constructReducedGraph.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Parma reduced graph<-");



        BackgroundlessButton testShortestPathButton = parmaScene.copy();
        testShortestPathButton.Text.setPosition(new Vector2f(windowSize.x * 0.25f, windowSize.y * 0.45f))
                .setText("Test shortest path algorithm");

        testShortestPathButton.Text.copyTo(testShortestPathButton.OverlapsText);
        testShortestPathButton.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Test shortest path algorithm<-");


        BackgroundlessButton exportPathScene = parmaScene.copy();
        exportPathScene.Text.setPosition(new Vector2f(windowSize.x * 0.25f, windowSize.y * 0.6f))
                .setText("Export scooters path");

        exportPathScene.Text.copyTo(exportPathScene.OverlapsText);
        exportPathScene.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Export scooters path<-");


        BackgroundlessButton showScooterSimulation = parmaScene.copy();
        showScooterSimulation.Text.setPosition(new Vector2f(windowSize.x * 0.25f, windowSize.y * 0.75f))
                .setText("Scooter simulation");

        showScooterSimulation.Text.copyTo(showScooterSimulation.OverlapsText);
        showScooterSimulation.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Scooter simulation<-");

        BackgroundlessButton movesMatrixScene = parmaScene.copy();
        movesMatrixScene.Text.setPosition(new Vector2f(windowSize.x * 0.75f, windowSize.y * 0.15f))
                .setText("Moves matrix start/end");

        movesMatrixScene.Text.copyTo(movesMatrixScene.OverlapsText);
        movesMatrixScene.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Moves matrix start/end<-");

        BackgroundlessButton buttonQuit = parmaScene.copy();
        buttonQuit.Text.setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.9f))
                .setText("Quit");

        buttonQuit.Text.copyTo(buttonQuit.OverlapsText);
        buttonQuit.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Quit<-");

        guiList = new GUIList(mainContext.getInputManager(), mainContext.getMouseManager());
        guiList.setupActionInputValues(ActionId.SELECT_UP, ActionId.SELECT_DOWN);
        guiList.GUIs.put(0, parmaScene);
        guiList.GUIs.put(1, constructReducedGraph);
        guiList.GUIs.put(2, testShortestPathButton);
        guiList.GUIs.put(3, exportPathScene);
        guiList.GUIs.put(4, showScooterSimulation);
        guiList.GUIs.put(5, movesMatrixScene);
        guiList.GUIs.put(6, buttonQuit);
        guiList.ShouldLoop = false;
    }


    public void update() {
        super.update();

        guiList.update();

        if ((mainContext.getInputManager().getState(ActionId.ENTER) && !guiList.isMouseSelecting())
        || (mainContext.getInputManager().inputPressed(ActionId.LEFT_CLICK) && guiList.isMouseSelecting())){
            Integer id = guiList.getSelected();
            if (id != null) {
                switch (id) {
                    case 0:
                        sceneManagerInterface.setNewScene(new ShowParmaScene(), new String[]{""});
                        break;
                    case 1:
                        sceneManagerInterface.setNewScene(new ConstructReduceGraphScene(), new String[]{""});
                        break;
                    case 2:
                        sceneManagerInterface.setNewScene(new TestShortestPathScene(), new String[]{""});
                        break;
                    case 3:
                        sceneManagerInterface.setNewScene(new ExportScootersPathScene(), new String[]{""});
                        break;
                    case 4:
                        sceneManagerInterface.setNewScene(new ShowScootersSimulation(), new String[]{""});
                        break;
                    case 5:
                        sceneManagerInterface.setNewScene(new MovesMatrixScene(), new String[]{""});
                        break;
                    case 6:
                        sceneManagerInterface.exit(0);
                        break;
                }
            }
        }
    }

    public void display() {
        super.setVirtualScene();
        clear();

        guiList.display();

        super.setAndDisplayRealScene();
    }


    public void unload() {
        super.unload();

        guiList.unload();
    }
}
