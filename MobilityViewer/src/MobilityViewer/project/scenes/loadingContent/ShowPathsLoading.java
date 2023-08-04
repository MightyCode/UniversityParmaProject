package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.resources.data.JSONFile;
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

public class ShowPathsLoading extends LoadingContent{
    protected ShowPathsLoading.BMResult bmResult;

    public static class BMResult extends LoadingContent.Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
        public SortedMap<Long, Node>[] paths;

        public ArrayList<Node> firstNodes;
        public ArrayList<Node> lastNodes;

        public Graph graph;
    }

    private final String resourceCategory;

    public ShowPathsLoading(String resourceCategory) {
        super(new ShowPathsLoading.BMResult());

        bmResult = (ShowPathsLoading.BMResult) result;
        this.resourceCategory = resourceCategory;
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

        JSONFile displayableResources = Resources.getInstance().getResource(JSONFile.class, "displayableResources");
        JSONObject resources = displayableResources.getObject().getJSONObject(resourceCategory);

        CSVFile bikesPath = Resources.getInstance().getResource(CSVFile.class, resources.getString("file"));

        bmResult.paths = new SortedMap[bikesPath.size()];

        int number = 0;

        for (int i = 0; i < bikesPath.size(); ++i){
            String bikePath = bikesPath.getData(i, 0);
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

        step = "Finished";
        percentage = 1f;
    }
}
