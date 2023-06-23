package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.mightylib.util.math.MathTime;
import MobilityViewer.project.display.Scooter;
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

public class ScooterSimulationLoading extends LoadingContent{

    protected ScooterSimulationLoading.SSLResult sslResult;

    public static class SSLResult extends Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
        public ArrayList<Scooter> scooters;
    }

    public ScooterSimulationLoading() {
        super(new ScooterSimulationLoading.SSLResult());

        sslResult = (ScooterSimulationLoading.SSLResult) result;
    }

    @Override
    protected final void init() {
        step = "Request data of Parma";
        percentage = 0.f;

        String data = SceneConstants.requestData();

        step = "Parse nodes";
        percentage = 0.025f;

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
        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, "Noleggi_Parma_2022");
        String paths = DataFolder.getFileContent("scooters-path.txt");

        if (csvFile != null && paths != null) {
            String[] splitPath = paths.split(";");
            step = "Scooter path parsing : 0 / " + splitPath.length + " (" + csvFile.size() + ")";
            percentage = 0.2f;

            String startDate, endDate;
            Scooter current;

            float scooterLoadingPercentage = 0.01f;

            long startTime = MathTime.datetimeStrToTick("2022-01-01 00:00:00");

            int indexInFile = 0;
            for (int i = 0; i < csvFile.size(); ++i) {
                Vector2f startPosition = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 2)),
                        Float.parseFloat(csvFile.getData(i, 1))
                );

                Vector2f endPosition = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 5)),
                        Float.parseFloat(csvFile.getData(i, 4))
                );

                if (SceneConstants.inBoundaries(boundaries, startPosition)
                        && (SceneConstants.inBoundaries(boundaries, endPosition))) {
                    startDate = csvFile.getData(i, 0);
                    endDate = csvFile.getData(i, 3);

                    long startTick = MathTime.datetimeStrToTick(startDate) - startTime;
                    long duration = MathTime.datetimeStrToTick(endDate) - startTime - startTick;
                    current = new Scooter(startTick, duration);

                    String[] pathIdNodes = splitPath[indexInFile].split(",");
                    List<Node> path = new ArrayList<>();
                    if (pathIdNodes.length >= 2) {
                        for (String idNode : pathIdNodes) {
                            path.add(sslResult.nodes.get(Long.parseLong(idNode.trim())));
                        }

                        current.init(path);
                        sslResult.scooters.add(current);
                    }

                    ++indexInFile;
                }

                if (i > scooterLoadingPercentage * csvFile.size()){
                    scooterLoadingPercentage += 0.01f;

                    step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                            + " / " + splitPath.length + " (" + csvFile.size() + ")";;
                    percentage = 0.2f + 0.8f * scooterLoadingPercentage;
                }
            }
        }

        step = "Finished";
        percentage = 1;
    }
}
