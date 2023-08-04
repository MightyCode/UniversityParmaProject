package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.resources.data.JSONFile;
import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.json.JSONObject;;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class ShowMatrixLoading extends LoadingContent {
    public static final int[] NUMBER_CELLS = new int[]{ 200, 200 };
    protected ShowMatrixLoading.MMLResult mmlResult;
    public static class MMLResult extends LoadingContent.Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;

        public HashMap<String, int[][]> startMatrices;
        public HashMap<String, Integer> minStartValues;
        public HashMap<String, Integer> maxStartValues;

        public HashMap<String, int[][]> endMatrices;
        public HashMap<String, Integer> minEndValues;
        public HashMap<String, Integer> maxEndValues;

        public HashMap<String, int[][]> combineMatrices;
        public HashMap<String, Integer> minCombineValues;
        public HashMap<String, Integer> maxCombineValues;

        public HashMap<String, int[][]> absoluteMatrices;
        public HashMap<String, Integer> minAbsoluteValues;
        public HashMap<String, Integer> maxAbsoluteValues;
    }

    public ShowMatrixLoading() {
        super(new ShowMatrixLoading.MMLResult());

        mmlResult = (ShowMatrixLoading.MMLResult) result;
    }

    @Override
    protected final void init() {
        percentage = 0.f;
        step = "Request data of Parma";

        mmlResult.startMatrices = new HashMap<>();
        mmlResult.minStartValues = new HashMap<>();
        mmlResult.maxStartValues = new HashMap<>();

        mmlResult.endMatrices = new HashMap<>();
        mmlResult.minEndValues = new HashMap<>();
        mmlResult.maxEndValues = new HashMap<>();

        mmlResult.combineMatrices = new HashMap<>();
        mmlResult.minCombineValues = new HashMap<>();
        mmlResult.maxCombineValues = new HashMap<>();

        mmlResult.absoluteMatrices = new HashMap<>();
        mmlResult.minAbsoluteValues = new HashMap<>();
        mmlResult.maxAbsoluteValues = new HashMap<>();

        JSONFile displayableResources =  Resources.getInstance().getResource(JSONFile.class, "displayableResources");
        String[] resourceCategories = displayableResources.getObject().keySet().toArray(new String[]{});

        for (String key : resourceCategories){
            if (key.equals("general"))
                continue;

            mmlResult.startMatrices.put(key, new int[NUMBER_CELLS[1]][NUMBER_CELLS[0]]);
            mmlResult.minStartValues.put(key, 0);
            mmlResult.maxStartValues.put(key, 0);

            mmlResult.endMatrices.put(key, new int[NUMBER_CELLS[1]][NUMBER_CELLS[0]]);
            mmlResult.minEndValues.put(key, 0);
            mmlResult.maxEndValues.put(key, 0);

            mmlResult.combineMatrices.put(key, new int[NUMBER_CELLS[1]][NUMBER_CELLS[0]]);
            mmlResult.minCombineValues.put(key, 0);
            mmlResult.maxCombineValues.put(key, 0);

            mmlResult.absoluteMatrices.put(key, new int[NUMBER_CELLS[1]][NUMBER_CELLS[0]]);
            mmlResult.minAbsoluteValues.put(key, 0);
            mmlResult.maxAbsoluteValues.put(key, 0);
        }

        String data = SceneConstants.requestData();

        step = "Parse nodes";
        percentage = 0.1f;

        if (interrupted())
            return;

        if (data == null)
            return;

        mmlResult.nodes = new TreeMap<>();
        mmlResult.roads = new TreeMap<>();

        JSONObject jsonObject = new JSONObject(data);

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        SceneConstants.parseNode(jsonObject, mmlResult.nodes, boundaries);

        step = "Create graph and parse roads";
        percentage = 0.15f;

        Graph graph = new Graph();
        for (Node node : mmlResult.nodes.values()) {
            graph.add(node);
        }

        SceneConstants.parseRoad(jsonObject, mmlResult.roads, mmlResult.nodes, boundaries);

        Vector2f boundariesSize = new Vector2f(boundaries.z - boundaries.x, boundaries.w - boundaries.y);

        step = "Get csv of scooters moves";
        percentage = 0.2f;

        JSONObject fileObject = displayableResources.getObject();
        Vector2f startPosition = new Vector2f(), endPosition = new Vector2f();
        int numbResource = 0;

        for (String key : resourceCategories){
            if (key.equals("general"))
                continue;

            System.out.println("Bug");

            JSONObject resourceObject = fileObject.getJSONObject(key);

            CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, resourceObject.getString("file"));

            if (csvFile != null) {
                step = "Scooter path parsing : 0 / " + csvFile.size();
                percentage = 0.2f + (0.8f / mmlResult.maxAbsoluteValues.size() * numbResource);

                float scooterLoadingPercentage = 0.01f;

                for (int i = 0; i < csvFile.size(); ++i) {
                    fillPosition(startPosition, endPosition, resourceObject, csvFile, i);

                    fillWithPositions(startPosition, endPosition, key, boundaries, boundariesSize);

                    if (i > scooterLoadingPercentage * csvFile.size()){
                        scooterLoadingPercentage += 0.01f;

                        step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                                + " / " + csvFile.size();
                        percentage = 0.2f + (0.8f / mmlResult.maxAbsoluteValues.size() * numbResource)
                                + (0.8f / mmlResult.maxAbsoluteValues.size()) * scooterLoadingPercentage;
                    }
                }
            }

            ++numbResource;
        }

        step = "Finished";
        percentage = 1;
    }

    public void fillPosition(Vector2f startPosition, Vector2f endPosition, JSONObject object, CSVFile csvFile, int index) {
        String type = object.getString("type");
        if (type.equalsIgnoreCase("start/end")) {
            JSONObject info = object.getJSONObject("info");

            startPosition.x = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("startLongitude").getInt("col")));

            startPosition.y = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("startLatitude").getInt("col")));

            endPosition.x = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("endLongitude").getInt("col")));

            endPosition.y = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("endLatitude").getInt("col")));

        } else if (type.equalsIgnoreCase("path")) {
            String temp = csvFile.getData(index, 0);

            String[] positions = temp.split(",");

            String[] position = positions[0].trim().split(" ");

            startPosition.x = Float.parseFloat(position[0]);
            startPosition.y = Float.parseFloat(position[1]);

            position = positions[positions.length - 1].trim().split(" ");

            endPosition.x = Float.parseFloat(position[0]);
            endPosition.y = Float.parseFloat(position[1]);
        }
    }

    public void fillWithPositions(Vector2f startPosition, Vector2f endPosition,
                                  String type, Vector4f boundaries, Vector2f diff){
        if (SceneConstants.inBoundaries(boundaries, startPosition)
                && (SceneConstants.inBoundaries(boundaries, endPosition))) {

            Vector2i startCellPosition = new Vector2i(
                    (int) ((startPosition.x - boundaries.x) / diff.x * ShowMatrixLoading.NUMBER_CELLS[0]),
                    (int) ((diff.y - (startPosition.y - boundaries.y)) / diff.y * ShowMatrixLoading.NUMBER_CELLS[1])
            );

            Vector2i endCellPosition = new Vector2i(
                    (int) ((endPosition.x - boundaries.x) / diff.x * ShowMatrixLoading.NUMBER_CELLS[0]),
                    (int) ((diff.y - (endPosition.y - boundaries.y)) / diff.y * ShowMatrixLoading.NUMBER_CELLS[1])
            );

            // Start Matrix

            if (++mmlResult.startMatrices.get(type)
                    [startCellPosition.y][startCellPosition.x] > mmlResult.maxStartValues.get(type))
                mmlResult.maxStartValues.put(type,
                        mmlResult.startMatrices.get(type)[startCellPosition.y][startCellPosition.x]);

            // End Matrix

            if (++mmlResult.endMatrices.get(type)[endCellPosition.y][endCellPosition.x] > mmlResult.maxEndValues.get(type))
                mmlResult.maxEndValues.put(type,
                        mmlResult.endMatrices.get(type)[endCellPosition.y][endCellPosition.x]);

            // Combine matrix

            if (--mmlResult.combineMatrices.get(type)
                    [startCellPosition.y][startCellPosition.x] < mmlResult.minCombineValues.get(type))
                mmlResult.minCombineValues.put(type,
                        mmlResult.combineMatrices.get(type)[startCellPosition.y][startCellPosition.x]);

            if (++mmlResult.combineMatrices.get(type)
                    [endCellPosition.y][endCellPosition.x] > mmlResult.maxCombineValues.get(type))
                mmlResult.maxCombineValues.put(type,
                        mmlResult.combineMatrices.get(type)[endCellPosition.y][endCellPosition.x]);

            // Absolute matrix
            if (++mmlResult.absoluteMatrices.get(type)
                    [startCellPosition.y][startCellPosition.x] > mmlResult.maxAbsoluteValues.get(type))
                mmlResult.maxAbsoluteValues.put(type,
                        mmlResult.absoluteMatrices.get(type)[startCellPosition.y][startCellPosition.x]);

            if (++mmlResult.absoluteMatrices.get(type)[endCellPosition.y][endCellPosition.x] > mmlResult.maxAbsoluteValues.get(type))
                mmlResult.maxAbsoluteValues.put(type,
                        mmlResult.absoluteMatrices.get(type)[endCellPosition.y][endCellPosition.x]);
        }
    }
}
