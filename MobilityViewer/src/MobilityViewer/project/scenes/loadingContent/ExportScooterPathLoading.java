package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.project.graph.*;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExportScooterPathLoading extends LoadingContent{
    protected ExportScooterPathLoading.EXPLResult explResult;

    public static class EXPLResult extends LoadingContent.Result { }

    public ExportScooterPathLoading() {
        super(new ExportScooterPathLoading.EXPLResult());

        explResult = (ExportScooterPathLoading.EXPLResult) result;
    }

    @Override
    protected final void init() {
        percentage = 0.f;
        step = "Request data of Parma";

        SortedMap<Long, Node> nodes;

        String data = SceneConstants.requestData();

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
        for (Node node : nodes.values())
            graph.add(node);

        nodes.clear();
        for (Node node : graph.getNodes())
            nodes.put(node.getId(), node);

        percentage = 0.0004f;
        step = "Reduce graph";

        ReducedGraph reducedGraph = ReducedGraph.constructFrom(graph);

        System.out.println(graph.size() + " " + reducedGraph.size());

        float scooterPathCreationPercentage = 0.01f;

        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, "Noleggi_Parma_2022");
        StringBuilder export = new StringBuilder();

        step = "Scooter path creation : 0 / " + csvFile.size();
        percentage = 0.0005f;

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

                Node position = new Node(-1, startPosition.x, startPosition.y);
                Node startNode = findClosest(nodes, position);

                position = new Node(-1, endPosition.x, endPosition.y);
                Node[] arrayNodes = findNClosest(nodes, position, 2);
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
        DataFolder.saveFile(export.toString(), "scooters-path.txt");

        step = "Finished";
        percentage = 1f;
    }


    private Node findClosest(SortedMap<Long, Node> nodes, Node position){
        float min = Float.POSITIVE_INFINITY;
        Node minNode = nodes.get(nodes.firstKey());

        for (Node other : nodes.values()){
            float dist = other.getDist(position);
            if (min > dist) {
                min = dist;
                minNode = other;
            }
        }

        return minNode;
    }

    private Node[] findNClosest(SortedMap<Long, Node> nodes, Node position, int N) {
        float[] distances = new float[N];
        Arrays.fill(distances, Float.POSITIVE_INFINITY);
        Node[] result = new Node[N];
        for (Node other : nodes.values()){
            float dist = other.getDist(position);

            for (int i = 0; i < N; ++i){
                if (distances[i] > dist){
                    for (int j = N - 1; j >= i + 1; --j){
                        distances[j] = distances[j - 1];
                        result[j] = result[j - 1];
                    }

                    distances[i] = dist;
                    result[i] = other;

                    break;
                }
            }
        }

        return result;
    }
}
