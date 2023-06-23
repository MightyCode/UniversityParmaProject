package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.GraphUtil;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

public class ShortestPathLoading extends LoadingContent{

    protected ShortestPathLoading.SPLResult splResult;

    public static class SPLResult extends Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;

        public Graph graph;
    }

    public ShortestPathLoading() {
        super(new ShortestPathLoading.SPLResult());

        splResult = (ShortestPathLoading.SPLResult) result;
    }

    @Override
    protected final void init() {
        step = "Request data of Parma";
        percentage = 0.f;

        String data = SceneConstants.requestData();

        step = "Parse nodes";
        percentage = 0.5f;

        if (data == null)
            return;
        splResult.nodes = new TreeMap<>();
        splResult.roads = new TreeMap<>();

        JSONObject jsonObject = new JSONObject(data);

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        SceneConstants.parseNode(jsonObject, splResult.nodes, boundaries);

        step = "Create graph, eliminate disconnected nodes and parse roads";
        percentage = 0.75f;

        SceneConstants.parseRoad(jsonObject, splResult.roads, splResult.nodes, boundaries);

        splResult.graph = new Graph();
        splResult.graph.addAll(splResult.nodes.values());
        splResult.graph = GraphUtil.eliminateNotConnectedNodes(splResult.graph);

        splResult.nodes.clear();
        for (Node node : splResult.graph.getNodes())
            splResult.nodes.put(node.getId(), node);

        percentage = 1;
    }
}
