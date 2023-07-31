package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.project.graph.*;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

public class ReducedGraphLoading extends LoadingContent{
    protected ReducedGraphLoading.RGLResult rglResult;

    public static class RGLResult extends LoadingContent.Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
        public ReducedGraph reducedGraph;
        public SortedMap<Long, Node> reducedNodes;

        public SortedMap<Long, Node> reducedSubNodes;
    }

    public ReducedGraphLoading() {
        super(new ReducedGraphLoading.RGLResult());

        rglResult = (ReducedGraphLoading.RGLResult) result;
    }

    @Override
    protected final void init() {
        step = "Request data of Parma";
        percentage = 0.f;
        Vector4f boundaries = SceneConstants.BOUNDARIES;

        String data = SceneConstants.requestData();

        if (interrupted())
            return;

        step = "Parse nodes";
        percentage = 0.2f;

        if (data == null)
            return;

        rglResult.nodes = new TreeMap<>();
        rglResult.roads = new TreeMap<>();

        JSONObject jsonObject = new JSONObject(data);

        SceneConstants.parseNode(jsonObject, rglResult.nodes, boundaries);

        step = "Parse roads";
        percentage = 0.4f;

        SceneConstants.parseRoad(jsonObject, rglResult.roads, rglResult.nodes, boundaries);

        step = "Simplify graph";
        percentage = 0.6f;

        Graph graph = new Graph();
        graph.addAll(rglResult.nodes.values());
        graph = GraphUtil.eliminateNotConnectedNodes(graph);

        step = "Reduce graph";
        percentage = 0.8f;

        rglResult.nodes.clear();
        for (Node node : graph.getNodes())
            rglResult.nodes.put(node.getId(), node);

        rglResult.reducedGraph = ReducedGraph.constructFrom(graph);

        graph = rglResult.reducedGraph.createCorresponding();

        rglResult.reducedNodes = new TreeMap<>();
        for (Node node : graph.getNodes())
            rglResult.reducedNodes.put(node.getId(), node);

        graph = rglResult.reducedGraph.createCorrespondingSubGraph();
        rglResult.reducedSubNodes = new TreeMap<>();
        for (Node node : graph.getNodes())
            rglResult.reducedSubNodes.put(node.getId(), node);

        step = "Finished";
        percentage = 1f;
    }
}
