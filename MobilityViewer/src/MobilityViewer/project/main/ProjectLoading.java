package MobilityViewer.project.main;

import static org.lwjgl.glfw.GLFW.*;

import MobilityViewer.mightylib.inputs.InputManager;
import MobilityViewer.mightylib.inputs.inputType.ActionInput;
import MobilityViewer.mightylib.inputs.inputType.EInputType;
import MobilityViewer.mightylib.inputs.inputType.InputOr;
import MobilityViewer.mightylib.inputs.inputType.InputSimple;
import MobilityViewer.mightylib.main.Context;
import MobilityViewer.mightylib.main.IProjectLoading;
import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVLoader;

class ProjectLoading implements IProjectLoading {
    @Override
    public void init(){
        Resources resources = Resources.getInstance();
        resources.Loaders.add(new CSVLoader());
    }

    @Override
    public void contextLoading(Context context) {
        InputManager inputManager = context.getInputManager();
        setKeybinds(inputManager);
    }

    public void setKeybinds(InputManager inputManager){
        ActionInput[] configurations = {
                new ActionInput(ActionId.ESCAPE, "ESCAPE",
                        new InputSimple(GLFW_KEY_ESCAPE, EInputType.Keyboard)),

                new ActionInput(ActionId.MOVE_LEFT, "MOVE LEFT",
                        new InputOr(
                            new InputSimple(GLFW_KEY_A, EInputType.Keyboard),
                            new InputSimple(GLFW_KEY_LEFT, EInputType.Keyboard)
                        )),

                new ActionInput(ActionId.MOVE_RIGHT, "MOVE RIGHT",
                        new InputOr(
                                new InputSimple(GLFW_KEY_D, EInputType.Keyboard),
                                new InputSimple(GLFW_KEY_RIGHT, EInputType.Keyboard)
                        )),

                new ActionInput(ActionId.MOVE_UP, "MOVE UP",
                        new InputOr(
                                new InputSimple(GLFW_KEY_W, EInputType.Keyboard),
                                new InputSimple(GLFW_KEY_UP, EInputType.Keyboard)
                        )),

                new ActionInput(ActionId.MOVE_DOWN, "MOVE DOWN",
                        new InputOr(
                                new InputSimple(GLFW_KEY_S, EInputType.Keyboard),
                                new InputSimple(GLFW_KEY_DOWN, EInputType.Keyboard)
                        )),

                new ActionInput(ActionId.MOVE_FORWARD, "MOVE FORWARD",
                        new InputSimple(GLFW_KEY_SPACE, EInputType.Keyboard)),

                new ActionInput(ActionId.MOVE_BACKWARD, "MOVE BACKWARD",
                        new InputSimple(GLFW_KEY_LEFT_CONTROL, EInputType.Keyboard)),

                new ActionInput(ActionId.SHIFT, "SHIFT",
                        new InputSimple(GLFW_KEY_LEFT_SHIFT, EInputType.Keyboard)),

                new ActionInput(ActionId.ENTER, "ENTER",
                        new InputSimple(GLFW_KEY_ENTER, EInputType.Keyboard)),

                new ActionInput(ActionId.SELECT_UP, "SELECT UP",
                        new InputOr(
                                new InputSimple(GLFW_KEY_W, EInputType.Keyboard),
                                new InputSimple(GLFW_KEY_UP, EInputType.Keyboard)
                        )),

                new ActionInput(ActionId.SELECT_DOWN, "SELECT DOWN",
                        new InputOr(
                                new InputSimple(GLFW_KEY_S, EInputType.Keyboard),
                                new InputSimple(GLFW_KEY_DOWN, EInputType.Keyboard)
                        )),

                new ActionInput(ActionId.LEFT_CLICK, "LEFT CLICK",
                        new InputSimple(GLFW_MOUSE_BUTTON_1, EInputType.Mouse)),

                new ActionInput(ActionId.RIGHT_CLICK, "RIGHT CLICK",
                        new InputSimple(GLFW_MOUSE_BUTTON_2, EInputType.Mouse)),

                new ActionInput(ActionId.SHOW_HIDE_HELP, "SHOW_HIDE_HELP CLICK",
                        new InputSimple(GLFW_KEY_H, EInputType.Keyboard)),

                new ActionInput(ActionId.SHOW_HIDE_MAP, "SHOW_HIDE_MAP CLICK",
                        new InputSimple(GLFW_KEY_T, EInputType.Keyboard)),

                new ActionInput(ActionId.SWITCH, "SWITCH",
                        new InputSimple(GLFW_KEY_SPACE, EInputType.Keyboard)),
        };

        inputManager.init(configurations);
    }
}