package MobilityViewer.mightylib.graphics.renderer._2D;

import MobilityViewer.mightylib.graphics.renderer._2D.shape.RectangleRenderer;
import MobilityViewer.mightylib.main.WindowInfo;
import MobilityViewer.mightylib.resources.texture.IGLBindable;

public class VirtualSceneRenderer extends RectangleRenderer {
    private final FrameBuffer frameBuffer;

    public VirtualSceneRenderer(WindowInfo info,  IGLBindable bindable){
        super("postProcessing");
        frameBuffer = new FrameBuffer(info, bindable);
        this.shape.updateVbo(new float[]{
               -1, 1,
               -1, -1,
               1, -1,
               1, 1
        }, positionIndex);
    }

    public FrameBuffer getFrameBuffer(){
        return frameBuffer;
    }


    public void bindFrameBuff(){
        frameBuffer.bindFrameBuffer();
    }


    public void unbindFrameBuff(){
        frameBuffer.unbindFrameBuffer();
        frameBuffer.bindRenderTexture();
    }


    public void unload(){
        super.unload();
        frameBuffer.unload();
    }
}