package MobilityViewer.mightylib.graphics.renderer._3D;

import MobilityViewer.mightylib.graphics.renderer.Renderer;
import MobilityViewer.mightylib.util.math.Color4f;

public class ModelRenderer extends Renderer {
    public ModelRenderer(String shaderName, String modelPath, String texture) {
        super(shaderName, true);
        shape = OBJLoader.loadObjTexturedModel(modelPath);
        switchToTextureMode(texture);
    }

    public ModelRenderer(String shaderName, String modelPath, Color4f color) {
        super(shaderName, true);
        shape = OBJLoader.loadObjColoredModel(modelPath);
        switchToColorMode(color);
    }
}
