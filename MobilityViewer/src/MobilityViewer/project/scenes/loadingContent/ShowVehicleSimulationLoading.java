package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.resources.data.JSONFile;
import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.mightylib.util.math.MathTime;
import MobilityViewer.project.display.Vehicle;
import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ShowVehicleSimulationLoading extends LoadingContent {

    protected ShowVehicleSimulationLoading.SSLResult sslResult;

    public static class SSLResult extends Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
        public ArrayList<Vehicle> scooters;
    }

    private final String resourceCategory;

    public ShowVehicleSimulationLoading(String resourceCategory) {
        super(new ShowVehicleSimulationLoading.SSLResult());

        sslResult = (ShowVehicleSimulationLoading.SSLResult) result;
        this.resourceCategory = resourceCategory;
    }

    @Override
    protected final void init() {
        step = "Request data of Parma";
        percentage = 0.f;

        String data = SceneConstants.requestData();

        step = "Parse nodes";
        percentage = 0.025f;

        if (interrupted())
            return;

        if (data == null)
            return;

        sslResult.nodes = new TreeMap<>();
        sslResult.roads = new TreeMap<>();

        JSONObject jsonObject = new JSONObject(data);

        Vector4f boundaries = SceneConstants.BOUNDARIES;
        SceneConstants.parseNode(jsonObject, sslResult.nodes, boundaries);

        step = "Parse roads";
        percentage = 0.050f;

        Graph graph = new Graph();
        for (Node node : sslResult.nodes.values()) {
            graph.add(node);
        }

        SceneConstants.parseRoad(jsonObject, sslResult.roads, sslResult.nodes, boundaries);

        step = "Get scooters csv";
        percentage = 0.075f;

        System.out.println("Get csv and create scooters");

        sslResult.scooters = new ArrayList<>();

        JSONFile displayableResources = Resources.getInstance().getResource(JSONFile.class, "displayableResources");
        JSONObject resources = displayableResources.getObject().getJSONObject(resourceCategory);
        JSONObject info = resources.getJSONObject("info");

        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, resources.getString("file"));
        String paths = DataFolder.getFileContent(resources.getString("file") + "-path.txt");

        if (interrupted())
            return;

        if (csvFile != null && paths != null) {
            String[] splitPath = paths.split(";");
            step = "Scooter path parsing : 0 / " + splitPath.length + " (" + csvFile.size() + ")";
            percentage = 0.2f;

            String startDate, endDate;
            Vehicle current;

            float scooterLoadingPercentage = 0.01f;

            long startTime = MathTime.datetimeStrToTick("2022-01-01 00:00:00");

            int indexInFile = 0;
            for (int i = 0; i < csvFile.size(); ++i) {
                if (interrupted())
                    return;

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
                    startDate = csvFile.getData(i, info.getJSONObject("startTime").getInt("col"));
                    endDate = csvFile.getData(i, info.getJSONObject("endTime").getInt("col"));

                    long startTick = MathTime.datetimeStrToTick(startDate) - startTime;
                    long duration = MathTime.datetimeStrToTick(endDate) - startTime - startTick;
                    current = new Vehicle(startTick, duration);

                    String[] pathIdNodes = splitPath[indexInFile].split(",");
                    List<Node> path = new ArrayList<>();
                    if (pathIdNodes.length >= 2) {
                        for (String idNode : pathIdNodes)
                            path.add(sslResult.nodes.get(Long.parseLong(idNode.trim())));

                        current.init(path);
                        sslResult.scooters.add(current);
                    }

                    ++indexInFile;
                }

                if (i > scooterLoadingPercentage * csvFile.size()) {
                    scooterLoadingPercentage += 0.01f;

                    step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * splitPath.length + 1)
                            + " / " + splitPath.length + " (" + csvFile.size() + ")";
                    percentage = 0.2f + 0.8f * scooterLoadingPercentage;
                }
            }
        }

        step = "Finished";
        percentage = 1;
    }
}
