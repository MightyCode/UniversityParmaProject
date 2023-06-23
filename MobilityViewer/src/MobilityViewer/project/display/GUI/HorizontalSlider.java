package MobilityViewer.project.display.GUI;

import MobilityViewer.mightylib.graphics.renderer._2D.shape.RectangleRenderer;
import MobilityViewer.mightylib.inputs.InputManager;
import MobilityViewer.mightylib.inputs.MouseManager;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.MightyMath;
import org.joml.Vector2f;

public class HorizontalSlider extends Slider {
    public HorizontalSlider(Camera2D referenceCamera, InputManager inputManager, MouseManager mouseManager,
                            Vector2f position, Vector2f size, float minValue, float maxValue){
        super(referenceCamera, inputManager, mouseManager, position, size, minValue, maxValue, 0);
    }


    @Override
    protected void setBarSize(RectangleRenderer bar, Vector2f sliderSize){
        bar.setSizePix(sliderSize.x * 1,sliderSize.y * 0.1f);
    }

    @Override
    protected void setBarPosition(RectangleRenderer bar, Vector2f sliderPosition, Vector2f sliderSize){
        bar.setPosition(
                new Vector2f(sliderPosition.x,
                        sliderPosition.y + sliderSize.y * 0.5f - bar.scale().y * 0.5f)
        );
    }

    @Override
    protected void setButtonSize(RectangleRenderer button, Vector2f sliderSize){
        float ref = Math.min(sliderSize.x, sliderSize.y);

        button.setSizePix(ref *  0.28f, ref * 0.32f);
    }

    @Override
    protected void setBasicButtonPosition(RectangleRenderer button, Vector2f sliderPosition, Vector2f sliderSize){
        button.setPosition(
          new Vector2f(
                  sliderPosition.x + sliderSize.x * 0.5f - button.scale().x * 0.5f,
                sliderPosition.y + sliderSize.y * 0.5f - button.scale().y * 0.5f));
    }

    @Override
    protected void setButtonPositionWithCurrentValue(RectangleRenderer button, Vector2f buttonBoundaries){
        button.setPosition(
                new Vector2f(
                        (float)MightyMath.mapd(getCurrentValue(), getMinValue(), getMaxValue(),
                                buttonBoundaries.x, buttonBoundaries.y),
                        button.position().y
                )
        );
    }

    @Override
    protected double returnCurrentValue(RectangleRenderer button, Vector2f buttonBoundaries){
        return MightyMath.mapd(button.position().x, buttonBoundaries.x, buttonBoundaries.y, getMinValue(), getMaxValue());
    }

    protected void setButtonPositionDrag(RectangleRenderer button,
                                                   Vector2f buttonBoundaries, Vector2f mousePosition){
        Vector2f newPosition = new Vector2f(
                mousePosition.x - button.scale().x * 0.5f,
                button.position().y
        );

        if (newPosition.x < buttonBoundaries.x){
            newPosition.x = buttonBoundaries.x;
        } else if (newPosition.x > buttonBoundaries.y) {
            newPosition.x = buttonBoundaries.y;
        }

        button.setPosition(newPosition);
    }
}
