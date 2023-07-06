package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

public class ParmaShowPointLoading extends LoadingContent {

    protected PSPResult pspResult;

    public static class PSPResult extends Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
    }

    public ParmaShowPointLoading() {
        super(new PSPResult());

        pspResult = (PSPResult) result;
    }

    @Override
    protected final void init() {
        percentage = 0.f;
        step = "Request data of Parma";
        String data = SceneConstants.requestData();

        if (interrupted())
            return;

        step = "Parse nodes";
        percentage = 0.33f;

        if (data == null)
            return;

        pspResult.nodes = new TreeMap<>();
        pspResult.roads = new TreeMap<>();

        JSONObject jsonObject = new JSONObject(data);

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        SceneConstants.parseNode(jsonObject, pspResult.nodes, boundaries);

        if (interrupted())
            return;

        step = "Create graph and parse roads";
        percentage = 0.66f;

        Graph graph = new Graph();
        for (Node node : pspResult.nodes.values()) {
            graph.add(node);

            if (interrupted())
                return;
        }

        SceneConstants.parseRoad(jsonObject, pspResult.roads, pspResult.nodes, boundaries);

        step = "Finished";
        percentage = 1f;
    }
}
