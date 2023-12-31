package MobilityViewer.mightylib.inputs.inputType;

import MobilityViewer.mightylib.inputs.InputManager;

public interface IInput {
    boolean getState(InputManager inputManager);

    boolean inputPressed(InputManager inputManager);

    boolean inputReleased(InputManager inputManager);
}
