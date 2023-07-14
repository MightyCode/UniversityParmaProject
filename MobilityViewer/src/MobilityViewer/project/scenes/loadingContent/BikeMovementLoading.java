package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector4f;
import org.json.JSONObject;

;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class BikeMovementLoading extends LoadingContent{
    protected BikeMovementLoading.BMResult bmResult;

    public static class BMResult extends LoadingContent.Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
        public SortedMap<Long, Node>[] paths;

        public ArrayList<Node> firstNodes;
        public ArrayList<Node> lastNodes;

        public Graph graph;
    }

    public BikeMovementLoading() {
        super(new BikeMovementLoading.BMResult());

        bmResult = (BikeMovementLoading.BMResult) result;
    }

    @Override
    protected final void init() {
        percentage = 0.f;
        step = "Request data of Parma";
        String data = SceneConstants.requestData(true);

        if (interrupted())
            return;

        step = "Parse nodes";
        percentage = 0.2f;

        if (data == null)
            return;

        bmResult.nodes = new TreeMap<>();
        bmResult.roads = new TreeMap<>();
        bmResult.firstNodes = new ArrayList<>();
        bmResult.lastNodes = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(data);

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        SceneConstants.parseNode(jsonObject, bmResult.nodes, boundaries);

        if (interrupted())
            return;

        step = "Create graph and parse roads";
        percentage = 0.4f;

        bmResult.graph = new Graph();
        for (Node node : bmResult.nodes.values()) {
            bmResult.graph.add(node);

            if (interrupted())
                return;
        }

        SceneConstants.parseRoad(jsonObject, bmResult.roads, bmResult.nodes, boundaries);

        step = "Parsing paths";
        percentage = 0.6f;

        CSVFile bikesPath = Resources.getInstance().getResource(CSVFile.class, "bikes-path");

        int number = 0;

        for (int i = 0; i < bikesPath.size(); ++i){
            if (bikesPath.getData(i, 0).toLowerCase().startsWith("linestring"))
                ++number;
        }

        bmResult.paths = new SortedMap[number];

        number = 0;

        for (int i = 0; i < bikesPath.size(); ++i){
            String bikePath = bikesPath.getData(i, 0);
            if (bikePath.toLowerCase().startsWith("linestring")) {
                bikePath = bikePath.toLowerCase().replace("linestring", "").replace("(", "")
                        .replace(")", "");

                String[] positions = bikePath.split(",");

                bmResult.paths[number] = new TreeMap<>();
                Node previous = null;

                for (int j = 0; j < positions.length; ++j){
                    String[] axis = positions[j].trim().split(" ");

                    Node current = new Node(j, Float.parseFloat(axis[0]), Float.parseFloat(axis[1]));
                    if (j == 0)
                        bmResult.firstNodes.add(current);

                    bmResult.paths[number].put(current.getId(), current);

                    if (previous != null){
                        previous.add(current);
                        current.add(previous);
                    }

                    previous = current;
                }

                bmResult.lastNodes.add(previous);

                ++number;
            }
        }

        step = "Finished";
        percentage = 1f;
    }
}
