package MobilityViewer.mightylib.scene;

import MobilityViewer.mightylib.graphics.shader.ShaderManager;
import MobilityViewer.mightylib.resources.texture.BasicBindableObject;
import MobilityViewer.mightylib.resources.texture.IGLBindable;
import MobilityViewer.mightylib.resources.texture.TextureParameters;
import MobilityViewer.mightylib.main.Context;
import MobilityViewer.mightylib.main.ContextManager;
import MobilityViewer.mightylib.graphics.renderer._2D.VirtualSceneRenderer;
import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.util.math.Color4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    protected final Resources resources;
    protected final ShaderManager shaderManager;
    protected final Context mainContext;

    protected Camera3D main3DCamera;
    protected Camera2D main2DCamera;

    protected SceneManagerInterface sceneManagerInterface;
    private VirtualSceneRenderer scRenderer;

    public Scene(Camera3DCreationInfo info){
        resources = Resources.getInstance();
        shaderManager = ShaderManager.getInstance();
        mainContext = ContextManager.getInstance().getMainContext();

        Camera3DCreationInfo parameters;

        if (info == null){
            parameters = new Camera3DCreationInfo();
            parameters.fov = 120f;
            parameters.initialPosition = new Vector3f(0, 0, 0);
        } else {
            parameters = info;
        }

        main3DCamera = mainContext.createCamera(parameters);
        main2DCamera = new Camera2D(mainContext.getWindow().getInfo(), new Vector2f(0,0));
        shaderManager.setCameras(main2DCamera, main3DCamera);

        sceneManagerInterface = null;
    }

    public Scene(){
        this(null);
    }

    public void init(String[] args, IGLBindable bindable){
        dispose();

        scRenderer = new VirtualSceneRenderer(mainContext.getWindow().getInfo(), bindable);
    }

    public void init(String[] args){
        init(args, new BasicBindableObject().setQualityTexture(TextureParameters.REALISTIC_PARAMETERS));
    }

    public void setSceneManagerInterface(SceneManagerInterface sceneManagerInterface){
        this.sceneManagerInterface = sceneManagerInterface;
    }

    public void update(){}

    public void dispose(){}

    public void display(){}


    protected void setVirtualScene(){
        dispose();
        scRenderer.bindFrameBuff();
        mainContext.getWindow().setVirtualViewport();
    }

    protected void setAndDisplayRealScene(){
        scRenderer.unbindFrameBuff();
        mainContext.getWindow().setRealViewport();

        scRenderer.display();
    }

    protected void clear(){ glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); }

    protected void setClearColor(Color4f color) { glClearColor(color.getR(), color.getG(), color.getB(), color.getA());}
    protected void setClearColor(float color1, float color2, float color3, float alpha){    glClearColor(color1, color2, color3, alpha); }
    protected void setClearColor(int color1, int color2, int color3, float alpha){          glClearColor((float)color1/255, (float)color2/255, (float)color3/255, alpha); }
    protected void setClearColor(float color1, float color2, float color3){                 glClearColor(color1, color2, color3, 1f);}
    protected void setClearColor(float color, float alpha){                                 glClearColor(color, color, color, alpha);}
    protected void setClearColor(float color){                                              glClearColor(color, color, color, 1f);}

    public void unload() {
        scRenderer.unload();
    }
}
