package MobilityViewer.project.scenes.mapScenes;

import MobilityViewer.mightylib.util.math.ColorList;
import MobilityViewer.mightylib.util.math.EDirection;
import MobilityViewer.project.scenes.loadingContent.ShowReducedGraphLoading;
import MobilityViewer.project.display.NodeRenderer;
import MobilityViewer.project.display.RoadRenderer;
import MobilityViewer.project.graph.*;
import MobilityViewer.project.main.ActionId;
import org.joml.Vector2f;

public class ShowReduceGraphScene extends SceneUsingMap<ShowReducedGraphLoading, ShowReducedGraphLoading.RGLResult> {
    private NodeRenderer<NodeIntersection> nodeRenderer;

    private NodeRenderer<NodeSubIntersection> subNodeRenderer;
    private RoadRenderer roadRenderer;

    private boolean showSubNodeRendererBelow;

    private RoadRenderer reducedNodeRenderer;

    private RoadRenderer reducedSubNodeRenderer;
    private int thingsToShow;

    public ShowReduceGraphScene(){
        super(new ShowReducedGraphLoading(),
                "Object size up / down : u / j\n" +
                "Switch display order : space\n");
    }

    @Override
    public void initialize(String[] args) {
        super.initialize(args);
        showSubNodeRendererBelow = true;
        thingsToShow = 0;
    }

    @Override
    public void endLoading(){
        /// SCENE INFORMATION ///
        roadRenderer = new RoadRenderer(mapCamera);
        roadRenderer.init(loadingResult.nodes);
        roadRenderer.updateRoads(loadingResult.nodes, boundaries,
                displayBoundaries, main2DCamera.getZoomLevel().x);

        nodeRenderer = new NodeRenderer<>(mapCamera);
        nodeRenderer.setColor(ColorList.Green());
        nodeRenderer.setNodeSize(10);
        nodeRenderer.init(loadingResult.reducedGraph.getNodes());
        nodeRenderer.updateNodes(loadingResult.reducedGraph.getNodes(), boundaries,
                displayBoundaries, main2DCamera.getZoomLevel().x);

        subNodeRenderer = new NodeRenderer<>(mapCamera);
        subNodeRenderer.setColor(ColorList.Red());
        subNodeRenderer.setNodeSize(5);
        subNodeRenderer.init(loadingResult.reducedGraph.getSubNodeGraph().getNodes());
        subNodeRenderer.updateNodes(loadingResult.reducedGraph.getSubNodeGraph().getNodes(),
                boundaries, displayBoundaries, main2DCamera.getZoomLevel().x);

        reducedNodeRenderer = new RoadRenderer(mapCamera);
        reducedNodeRenderer.init(loadingResult.reducedNodes);
        reducedNodeRenderer.updateRoads(loadingResult.reducedNodes, boundaries,
                displayBoundaries, main2DCamera.getZoomLevel().x);

        reducedSubNodeRenderer = new RoadRenderer(mapCamera);
        reducedSubNodeRenderer.init(loadingResult.reducedSubNodes);
        reducedSubNodeRenderer.updateRoads(loadingResult.reducedSubNodes, boundaries,
                displayBoundaries, main2DCamera.getZoomLevel().x);
    }

    public void zoom(Vector2f factor){
        mapCamera.setZoomLevel(new Vector2f(mapCamera.getZoomLevel()).mul(factor));
        nodeRenderer.updateNodes(loadingResult.reducedGraph.getNodes(),
                boundaries, displayBoundaries, mapCamera.getZoomLevel().x);

        subNodeRenderer.updateNodes(loadingResult.reducedGraph.getSubNodeGraph().getNodes(),
                boundaries, displayBoundaries, mapCamera.getZoomLevel().x);

        roadRenderer.updateRoads(loadingResult.nodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);

        reducedNodeRenderer.updateRoads(loadingResult.reducedNodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);

        reducedSubNodeRenderer.updateRoads(loadingResult.reducedSubNodes, boundaries, displayBoundaries, mapCamera.getZoomLevel().x);
    }

    @Override
    public void updateAfterLoading() {
        checkQuit();

        if (mouseManager.getMouseScroll().y != 0) {
            mapCamera.setZoomReference(
                    new Vector2f(
                         mouseManager.posX() / windowSize.x,
                        mouseManager.posY() / windowSize.y
                    ));

            if (inputManager.getState(ActionId.SHIFT))
                zoom(new Vector2f(1, 1 + mainContext.getMouseManager().getMouseScroll().y * 0.1f));
            else
                zoom(new Vector2f(1 + mainContext.getMouseManager().getMouseScroll().y * 0.1f));
        }

        if (inputManager.inputReleased(ActionId.RIGHT_CLICK))
            isDragging = false;

        if (isDragging) {
            mapCamera.moveXinZoom( -mouseManager.posX() + mouseManager.oldPosX());
            mapCamera.moveYinZoom( -mouseManager.posY() + mouseManager.oldPosY());
        }

        if (inputManager.inputPressed(ActionId.RIGHT_CLICK))
            isDragging = true;

        if (inputManager.inputPressed(ActionId.SHIFT)) {
            mapCamera.setZoomReference(EDirection.None);
            zoom(new Vector2f(1.01f));
        }

        move(mapCamera, inputManager);

        if (inputManager.getState(ActionId.OBJECT_SIZE_UP)){
            nodeRenderer.setNodeSize(nodeRenderer.getNodeSize() * 1.005f);
            subNodeRenderer.setNodeSize(subNodeRenderer.getNodeSize() * 1.005f);
            zoom(new Vector2f(1));
        }

        if (inputManager.getState(ActionId.OBJECT_SIZE_DOWN)){
            nodeRenderer.setNodeSize(nodeRenderer.getNodeSize() / 1.005f);
            subNodeRenderer.setNodeSize(subNodeRenderer.getNodeSize() / 1.005f);
            zoom(new Vector2f(1));
        }

        if (inputManager.inputPressed(ActionId.SWITCH))
            showSubNodeRendererBelow = !showSubNodeRendererBelow;

        if (inputManager.inputPressed(ActionId.ENTER)) {
            if (++thingsToShow > 2)
                thingsToShow = 0;
        }
    }


    @Override
    public void displayAL() {
        if (showSubNodeRendererBelow) {
            subNodeRenderer.display();
            nodeRenderer.display();
        } else {
            nodeRenderer.display();
            subNodeRenderer.display();
        }

        switch (thingsToShow){
            case 0:
                roadRenderer.display(); break;
            case 1:
                reducedNodeRenderer.display(); break;
            case 2:
                reducedSubNodeRenderer.display(); break;
        }
    }

    @Override
    public void unloadAfterLoading() {
        super.unloadAfterLoading();

        nodeRenderer.unload();
        roadRenderer.unload();
        subNodeRenderer.unload();
        reducedSubNodeRenderer.unload();
    }
}
