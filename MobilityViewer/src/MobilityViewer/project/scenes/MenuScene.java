package MobilityViewer.project.scenes;

import MobilityViewer.mightylib.graphics.GUI.BackgroundlessButton;
import MobilityViewer.mightylib.graphics.GUI.GUIList;
import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.graphics.text.Text;
import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.JSONFile;
import MobilityViewer.mightylib.resources.texture.BasicBindableObject;
import MobilityViewer.mightylib.resources.texture.TextureParameters;
import MobilityViewer.mightylib.scene.Scene;
import MobilityViewer.mightylib.sounds.SoundSourceCreationInfo;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.display.StrSelector;
import MobilityViewer.project.main.ActionId;
import MobilityViewer.project.scenes.mapScenes.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class MenuScene extends Scene {
    private enum ETypeSelector {
        general, path, startEnd
    }

    private static final SoundSourceCreationInfo CREATION_INFO_SELECT = new SoundSourceCreationInfo();
    static {
        CREATION_INFO_SELECT.name = "select";
        CREATION_INFO_SELECT.gainNode = "noise";
        CREATION_INFO_SELECT.gain = 1f;
    }

    private HashMap<ETypeSelector, GUIList> guiLists;
    private ETypeSelector currentTypeSelected;

    private StrSelector typeSelector;
    private Text currentTypeSelectedText;

    private JSONFile displayableResources;
    private String[] resourceCategories;

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
                .setText("Vehicles simulation");

        showScooterSimulation.Text.copyTo(showScooterSimulation.OverlapsText);
        showScooterSimulation.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Vehicles simulation<-");

        BackgroundlessButton movesMatrixScene = parmaScene.copy();
        movesMatrixScene.Text.setPosition(new Vector2f(windowSize.x * 0.75f, windowSize.y * 0.15f))
                .setText("Moves matrix start/end");

        movesMatrixScene.Text.copyTo(movesMatrixScene.OverlapsText);
        movesMatrixScene.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Moves matrix start/end<-");

        BackgroundlessButton fullMatrixScene = parmaScene.copy();
        fullMatrixScene.Text.setPosition(new Vector2f(windowSize.x * 0.75f, windowSize.y * 0.3f))
                .setText("Full matrix start/end");

        fullMatrixScene.Text.copyTo(fullMatrixScene.OverlapsText);
        fullMatrixScene.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Full matrix start/end<-");

        BackgroundlessButton showBikeMovements = parmaScene.copy();
        showBikeMovements.Text.setPosition(new Vector2f(windowSize.x * 0.75f, windowSize.y * 0.45f))
                .setText("Show vehicle path individually");

        showBikeMovements.Text.copyTo(showBikeMovements.OverlapsText);
        showBikeMovements.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Show vehicles path individually<-");

        BackgroundlessButton deleteData = parmaScene.copy();
        deleteData.Text.setPosition(new Vector2f(windowSize.x * 0.3f, windowSize.y * 0.9f))
                .setText("Data options");

        deleteData.Text.copyTo(deleteData.OverlapsText);
        deleteData.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Data options<-");

        BackgroundlessButton buttonQuit = parmaScene.copy();
        buttonQuit.Text.setPosition(new Vector2f(windowSize.x * 0.6f, windowSize.y * 0.9f))
                .setText("Quit");

        buttonQuit.Text.copyTo(buttonQuit.OverlapsText);
        buttonQuit.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Quit<-");

        displayableResources =  Resources.getInstance().getResource(JSONFile.class, "displayableResources");
        HashSet<String> keys = new HashSet<>(displayableResources.getObject().keySet());
        keys.add("general");
        resourceCategories = keys.toArray(new String[]{});
        typeSelector = new StrSelector(resourceCategories, mainContext);

        String baseType = args.length > 0 ? args[0] : "general";

        if (args.length >= 1)
            System.out.println("First argument : " + args[0]);
        if (args.length >= 2)
            System.out.println("Second argument : " + args[1]);


        for (int i = 0; i < resourceCategories.length; ++i){
            if (resourceCategories[i].equalsIgnoreCase(baseType))
                typeSelector.setIndex(i);
        }

        currentTypeSelectedText = new Text();
        currentTypeSelectedText.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.05f))
                .setFontSize(35);

        guiLists = new HashMap<>();

        GUIList generalGui = new GUIList(mainContext.getInputManager(), mainContext.getMouseManager());
        generalGui.setupActionInputValues(ActionId.SELECT_UP, ActionId.SELECT_DOWN);
        generalGui.ShouldLoop = false;
        generalGui.GUIs.put(0, parmaScene);
        generalGui.GUIs.put(1, constructReducedGraph);
        generalGui.GUIs.put(2, testShortestPathButton);
        generalGui.GUIs.put(-1, buttonQuit);
        generalGui.GUIs.put(-2, deleteData);
        guiLists.put(ETypeSelector.general, generalGui);

        GUIList pathGui = new GUIList(mainContext.getInputManager(), mainContext.getMouseManager());
        pathGui.setupActionInputValues(ActionId.SELECT_UP, ActionId.SELECT_DOWN);
        pathGui.ShouldLoop = false;
        pathGui.GUIs.put(5, movesMatrixScene);
        pathGui.GUIs.put(6, fullMatrixScene);
        pathGui.GUIs.put(7, showBikeMovements);
        pathGui.GUIs.put(-1, buttonQuit);
        pathGui.GUIs.put(-2, deleteData);
        guiLists.put(ETypeSelector.path, pathGui);

        GUIList startEndGUi = new GUIList(mainContext.getInputManager(), mainContext.getMouseManager());
        startEndGUi.setupActionInputValues(ActionId.SELECT_UP, ActionId.SELECT_DOWN);
        startEndGUi.ShouldLoop = false;
        startEndGUi.GUIs.put(3, exportPathScene);
        startEndGUi.GUIs.put(4, showScooterSimulation);
        startEndGUi.GUIs.put(5, movesMatrixScene);
        startEndGUi.GUIs.put(6, fullMatrixScene);
        startEndGUi.GUIs.put(-1, buttonQuit);
        startEndGUi.GUIs.put(-2, deleteData);
        guiLists.put(ETypeSelector.startEnd, startEndGUi);

        listUpdated();
    }

    public void update() {
        super.update();
        typeSelector.update();
        if (typeSelector.isUpdated())
            listUpdated();

        GUIList guiList = guiLists.get(currentTypeSelected);
        guiList.update();

        if ((mainContext.getInputManager().getState(ActionId.ENTER) && !guiList.isMouseSelecting())
            || (mainContext.getInputManager().inputPressed(ActionId.LEFT_CLICK) && guiList.isMouseSelecting())){
            Integer id = guiList.getSelected();
            if (id != null) {
                switch (id) {
                    case 0:
                        sceneManagerInterface.setNewScene(new ShowParmaScene(), new String[]{});
                        break;
                    case 1:
                        sceneManagerInterface.setNewScene(new ShowReduceGraphScene(), new String[]{});
                        break;
                    case 2:
                        sceneManagerInterface.setNewScene(new TestShortestPathScene(), new String[]{});
                        break;
                    case 3:
                        sceneManagerInterface.setNewScene(new ExportVehiclesPathScene(typeSelector.getSelected()),
                                new String[]{typeSelector.getSelected()});
                        break;
                    case 4:
                        sceneManagerInterface.setNewScene(new ShowVehicleSimulationScene(typeSelector.getSelected()),
                                new String[]{typeSelector.getSelected()});
                        break;
                    case 5:
                        sceneManagerInterface.setNewScene(new ShowMovesMatrixScene(), new String[]{typeSelector.getSelected()});
                        break;
                    case 6:
                        sceneManagerInterface.setNewScene(new ShowFullMatrixScene(), new String[]{typeSelector.getSelected()});
                        break;
                    case 7:
                        sceneManagerInterface.setNewScene(new ShowPathsScene(typeSelector.getSelected()),
                                new String[]{typeSelector.getSelected()});
                        break;
                    case -1:
                        sceneManagerInterface.exit(0);
                        break;
                    case -2:
                        sceneManagerInterface.setNewScene(new DeleteDataScene(), new String[]{});
                        break;
                }
            }
        }
    }

    private void listUpdated(){
        if (typeSelector.getSelected().equalsIgnoreCase("general")) {
            currentTypeSelected = ETypeSelector.general;

            currentTypeSelectedText.setText("General scenes");
            return;
        }

        JSONObject objectSelected = displayableResources.getObject().getJSONObject(typeSelector.getSelected());

        currentTypeSelectedText.setText(objectSelected.getString("name"));

        String type = objectSelected.getString("type");

        if (type.equalsIgnoreCase("path"))
            currentTypeSelected = ETypeSelector.path;
        else if (type.equalsIgnoreCase("start/end"))
            currentTypeSelected = ETypeSelector.startEnd;
        else
            currentTypeSelected = null;
    }

    public void display() {
        super.setVirtualScene();
        clear();

        typeSelector.display();
        guiLists.get(currentTypeSelected).display();

        currentTypeSelectedText.display();

        super.setAndDisplayRealScene();
    }


    public void unload() {
        super.unload();

        currentTypeSelectedText.unload();
        typeSelector.unload();

        for (GUIList guiList : guiLists.values())
            guiList.unload();
    }
}
