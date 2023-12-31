package MobilityViewer.project.main;
import MobilityViewer.mightylib.inputs.InputManager;

/**
 * List of static values used to characterize the action that can be done by user.
 */
public class ActionId {
    public static final int MOVE_RIGHT = InputManager.getAndIncrementId();
    public static final int MOVE_LEFT = InputManager.getAndIncrementId();
    public static final int OBJECT_SIZE_UP = InputManager.getAndIncrementId();
    public static final int OBJECT_SIZE_DOWN = InputManager.getAndIncrementId();
    public static final int MOVE_UP = InputManager.getAndIncrementId();
    public static final int MOVE_DOWN = InputManager.getAndIncrementId();

    public static final int SHIFT = InputManager.getAndIncrementId();
    public static final int LEFT_CLICK = InputManager.getAndIncrementId();
    public static final int RIGHT_CLICK = InputManager.getAndIncrementId();
    public static final int ESCAPE = InputManager.getAndIncrementId();
    public static final int ENTER = InputManager.getAndIncrementId();
    public static final int SELECT_UP = InputManager.getAndIncrementId();
    public static final int SELECT_DOWN = InputManager.getAndIncrementId();
    public static final int SHOW_HIDE_HELP = InputManager.getAndIncrementId();
    public static final int SHOW_HIDE_MAP = InputManager.getAndIncrementId();
    public static final int SWITCH = InputManager.getAndIncrementId();
}
