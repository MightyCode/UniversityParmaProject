package MobilityViewer.project.scenes;

import MobilityViewer.mightylib.graphics.GUI.BackgroundlessButton;
import MobilityViewer.mightylib.graphics.GUI.GUIList;
import MobilityViewer.mightylib.graphics.text.ETextAlignment;
import MobilityViewer.mightylib.resources.texture.BasicBindableObject;
import MobilityViewer.mightylib.resources.texture.TextureParameters;
import MobilityViewer.mightylib.scene.Scene;
import MobilityViewer.mightylib.sounds.SoundSourceCreationInfo;
import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class DeleteDataScene extends Scene {
    private static final SoundSourceCreationInfo CREATION_INFO_SELECT = new SoundSourceCreationInfo();
    static {
        CREATION_INFO_SELECT.name = "select";
        CREATION_INFO_SELECT.gainNode = "noise";
        CREATION_INFO_SELECT.gain = 1f;
    }

    private GUIList guiList;

    public void init(String[] args) {
        super.init(args, new BasicBindableObject().setQualityTexture(TextureParameters.REALISTIC_PARAMETERS));
        /// SCENE INFORMATION ///

        main3DCamera.setPos(new Vector3f(0, 0, 0));
        setClearColor(52, 189, 235, 1f);

        /// RENDERERS ///

        Vector2i windowSize = mainContext.getWindow().getInfo().getSizeCopy();

        BackgroundlessButton allDataButton = new BackgroundlessButton(mainContext);
        allDataButton.Text.setFont("bahnschrift")
                .setAlignment(ETextAlignment.Center)
                .setReference(EDirection.None)
                .setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.15f))
                .setFontSize(30)
                .setText("Delete All Data");

        allDataButton.Text.copyTo(allDataButton.OverlapsText);
        allDataButton.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Delete All Data<-");

        BackgroundlessButton parmaMapButton = allDataButton.copy();
        parmaMapButton.Text.setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.3f))
                .setText("Delete geographic data only");

        parmaMapButton.Text.copyTo(parmaMapButton.OverlapsText);
        parmaMapButton.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Delete geographic data only<-");

        BackgroundlessButton scooterPathButton = allDataButton.copy();
        scooterPathButton.Text.setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.45f))
                .setText("Delete generated paths only");

        scooterPathButton.Text.copyTo(scooterPathButton.OverlapsText);
        scooterPathButton.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Delete generated paths only<-");

        BackgroundlessButton buttonQuit = allDataButton.copy();
        buttonQuit.Text.setPosition(new Vector2f(windowSize.x * 0.5f, windowSize.y * 0.9f))
                .setText("Go back to main menu");

        buttonQuit.Text.copyTo(buttonQuit.OverlapsText);
        buttonQuit.OverlapsText.setColor(new Color4f(0.3f))
                .setText("->Go back to main menu<-");

        guiList = new GUIList(mainContext.getInputManager(), mainContext.getMouseManager());
        guiList.setupActionInputValues(ActionId.SELECT_UP, ActionId.SELECT_DOWN);
        guiList.GUIs.put(0, allDataButton);
        guiList.GUIs.put(1, parmaMapButton);
        guiList.GUIs.put(2, scooterPathButton);
        guiList.GUIs.put(-1, buttonQuit);
        guiList.ShouldLoop = false;
    }


    public void update() {
        super.update();

        if (mainContext.getInputManager().inputPressed(ActionId.ESCAPE))
            sceneManagerInterface.setNewScene(new MenuScene(), new String[]{""});

        guiList.update();

        if ((mainContext.getInputManager().getState(ActionId.ENTER) && !guiList.isMouseSelecting())
                || (mainContext.getInputManager().inputPressed(ActionId.LEFT_CLICK) && guiList.isMouseSelecting())){
            Integer id = guiList.getSelected();
            if (id != null) {
                switch (id) {
                    case 0:
                        DataFolder.emptyDataFolder();
                        break;
                    case 1:
                        DataFolder.deleteFile("parma.txt");
                        break;
                    case 2:
                        DataFolder.deleteFile("scooters-path.txt");
                        break;
                    case -1:
                        sceneManagerInterface.setNewScene(new MenuScene(), new String[]{""});
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
