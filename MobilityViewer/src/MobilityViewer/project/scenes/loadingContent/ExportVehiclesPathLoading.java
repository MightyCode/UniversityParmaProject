package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.resources.data.JSONFile;
import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.project.utils.ProjectUtil;
import MobilityViewer.project.graph.*;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Scene used to create the paths for vehicles.
 */
public class ExportVehiclesPathLoading extends LoadingContent {
    protected ExportVehiclesPathLoading.EXPLResult explResult;

    public static class EXPLResult extends LoadingContent.Result { }

    private final String resourceCategory;

    public ExportVehiclesPathLoading(String resourceCategory) {
        super(new ExportVehiclesPathLoading.EXPLResult());

        explResult = (ExportVehiclesPathLoading.EXPLResult) result;
        this.resourceCategory = resourceCategory;
    }

    @Override
    protected final void init() {
        percentage = 0.f;
        step = "Request data of Parma";

        SortedMap<Long, Node> nodes;

        String data = SceneConstants.requestData();

        if (interrupted())
            return;

        if (data == null)
            return;

        nodes = new TreeMap<>();

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        JSONObject jsonObject = new JSONObject(data);

        percentage = 0.0002f;
        step = "Parse nodes";

        SceneConstants.parseNode(jsonObject, nodes, boundaries);

        percentage = 0.0003f;
        step = "Create graph and parse roads";

        SceneConstants.parseRoad(jsonObject, new TreeMap<>(), nodes, boundaries);

        int total = 0;
        for (Node node : nodes.values())
            total += node.getNodes().size();

        System.out.println("Number nodes : " + nodes.size() + ", number connexion : " + total);
        System.out.println("Ratio : " + (total * 1.0f / nodes.size()));

        Graph graph = new Graph();
        for (Node node : nodes.values()) {
            graph.add(node);
        }

        nodes.clear();
        for (Node node : graph.getNodes())
            nodes.put(node.getId(), node);

        percentage = 0.0004f;
        step = "Reduce graph";

        ReducedGraph reducedGraph = ReducedGraph.constructFrom(graph);
        Graph correspondingReducedGraph = reducedGraph.createCorresponding();

        boolean WILL_USE_REDUCED_PATH = false;

        float scooterPathCreationPercentage = 0.01f;

        JSONFile displayableResources = Resources.getInstance().getResource(JSONFile.class, "displayableResources");
        JSONObject resources = displayableResources.getObject().getJSONObject(resourceCategory);
        JSONObject info = resources.getJSONObject("info");

        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, resources.getString("file"));
        StringBuilder export = new StringBuilder();

        if (interrupted())
            return;

        step = "Scooter path creation : 0 / " + csvFile.size();
        percentage = 0.0005f;

        for (int i = 0; i < csvFile.size(); ++i) {
            Vector2f startPosition = new Vector2f(
                    Float.parseFloat(csvFile.getData(i, info.getJSONObject("startLongitude").getInt("col"))),
                    Float.parseFloat(csvFile.getData(i, info.getJSONObject("startLatitude").getInt("col")))
            );

            Vector2f endPosition = new Vector2f(
                    Float.parseFloat(csvFile.getData(i, info.getJSONObject("endLongitude").getInt("col"))),
                    Float.parseFloat(csvFile.getData(i, info.getJSONObject("endLatitude").getInt("col")))
            );

            if (SceneConstants.inBoundaries(boundaries, startPosition)
                    && (SceneConstants.inBoundaries(boundaries, endPosition))) {

                Node position = new Node(-1, startPosition.x, startPosition.y);
                Node startNode = ProjectUtil.findClosest(nodes, position);

                position = new Node(-1, endPosition.x, endPosition.y);
                Node[] arrayNodes = ProjectUtil.findNClosest(nodes, position, 2);
                Node endNode;
                if (arrayNodes[0].getId() == startNode.getId()){
                    if (position.getDist(startNode) / 2f > position.getDist(arrayNodes[1]))
                        endNode = arrayNodes[0];
                    else
                        endNode = arrayNodes[1];
                } else {
                    endNode = arrayNodes[0];
                }

                List<Node> path = null;
                if (startNode != endNode){
                    if (WILL_USE_REDUCED_PATH)
                        path = Dijkstra.findShortestPathReduced(reducedGraph, correspondingReducedGraph, startNode, endNode);
                    else
                        path = Dijkstra.findShortestPath(graph, startNode, endNode);
                }

                if (path != null) {
                    for (int a = 0; a < path.size(); ++a) {
                        export.append(path.get(a).getId());
                        if (a != path.size() - 1)
                            export.append(",");
                    }

                    if (i != csvFile.size() - 1)
                        export.append(";");
                }
            }

            if (i > scooterPathCreationPercentage * csvFile.size()){
                scooterPathCreationPercentage += 0.01f;

                step = "Scooter path creation : " + (int)(scooterPathCreationPercentage * csvFile.size())
                            + " / " + csvFile.size();
                percentage = 0.0005f + 0.9994f * scooterPathCreationPercentage;
            }
        }

        step = "Save paths in file";
        percentage = 0.999f;
        DataFolder.saveFile(export.toString(), resources.getString("file") + "-path.txt");

        step = "Finished";
        percentage = 1f;
    }
}
