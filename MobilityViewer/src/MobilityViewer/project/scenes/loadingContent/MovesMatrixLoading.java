package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.main.ETypeData;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.json.JSONObject;;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class MovesMatrixLoading extends LoadingContent {
    public static final int[] NUMBER_CELLS = new int[]{ 200, 200 };
    protected MovesMatrixLoading.MMLResult mmlResult;
    public static class MMLResult extends LoadingContent.Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;

        public HashMap<ETypeData, int[][]> startMatrices;
        public HashMap<ETypeData, Integer> minStartValues;
        public HashMap<ETypeData, Integer> maxStartValues;

        public HashMap<ETypeData, int[][]> endMatrices;
        public HashMap<ETypeData, Integer> minEndValues;
        public HashMap<ETypeData, Integer> maxEndValues;

        public HashMap<ETypeData, int[][]> combineMatrices;
        public HashMap<ETypeData, Integer> minCombineValues;
        public HashMap<ETypeData, Integer> maxCombineValues;

        public HashMap<ETypeData, int[][]> absoluteMatrices;
        public HashMap<ETypeData, Integer> minAbsoluteValues;
        public HashMap<ETypeData, Integer> maxAbsoluteValues;
    }

    public MovesMatrixLoading() {
        super(new MovesMatrixLoading.MMLResult());

        mmlResult = (MovesMatrixLoading.MMLResult) result;
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

        for (ETypeData key : ETypeData.values()){
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

        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, "Noleggi_Parma_2022");
        ETypeData type = ETypeData.Scooter;

        Vector2f startPosition, endPosition;

        if (csvFile != null) {
            step = "Scooter path parsing : 0 / " + csvFile.size();
            percentage = 0.33f;
            float scooterLoadingPercentage = 0.01f;

            for (int i = 0; i < csvFile.size(); ++i) {
                 startPosition = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 2)),
                        Float.parseFloat(csvFile.getData(i, 1))
                );

                endPosition = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 5)),
                        Float.parseFloat(csvFile.getData(i, 4))
                );

                fillWithPositions(startPosition, endPosition, type, boundaries, boundariesSize);

                if (i > scooterLoadingPercentage * csvFile.size()){
                    scooterLoadingPercentage += 0.01f;

                    step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                            + " / " + csvFile.size();
                    percentage = 0.33f + 0.33f * scooterLoadingPercentage;
                }
            }
        }

        csvFile = Resources.getInstance().getResource(CSVFile.class, "bikes-path");
        type = ETypeData.Bike;

        String temp;
        String[] positions;
        String[] position;

        if (csvFile != null) {
            step = "Bike path parsing : 0 / " + csvFile.size();
            percentage = 0.36f;
            float scooterLoadingPercentage = 0.01f;

            for (int i = 0; i < csvFile.size(); ++i) {

                temp = csvFile.getData(i, 0);

                temp = temp.trim().toLowerCase().replace("linestring(", "")
                        .replace(")", "");

                if (!temp.contains("point") && !temp.contains("multi")) {
                    positions = temp.split(",");

                    position = positions[0].trim().split(" ");

                    startPosition = new Vector2f(
                            Float.parseFloat(position[0]),
                            Float.parseFloat(position[1])
                    );

                    position = positions[positions.length - 1].trim().split(" ");

                    endPosition = new Vector2f(
                            Float.parseFloat(position[0]),
                            Float.parseFloat(position[1])
                    );

                    fillWithPositions(startPosition, endPosition, type, boundaries, boundariesSize);
                }

                if (i > scooterLoadingPercentage * csvFile.size()) {
                    scooterLoadingPercentage += 0.01f;

                    step = "Bike path parsing : " + (int) (scooterLoadingPercentage * csvFile.size())
                            + " / " + csvFile.size();
                    percentage = 0.66f + 0.34f * scooterLoadingPercentage;
                }
            }

            step = "Finished";
            percentage = 1;
        }
    }

    public void fillWithPositions(Vector2f startPosition, Vector2f endPosition,
                                  ETypeData type, Vector4f boundaries, Vector2f diff){
        if (SceneConstants.inBoundaries(boundaries, startPosition)
                && (SceneConstants.inBoundaries(boundaries, endPosition))) {

            Vector2i startCellPosition = new Vector2i(
                    (int) ((startPosition.x - boundaries.x) / diff.x * MovesMatrixLoading.NUMBER_CELLS[0]),
                    (int) ((diff.y - (startPosition.y - boundaries.y)) / diff.y * MovesMatrixLoading.NUMBER_CELLS[1])
            );

            Vector2i endCellPosition = new Vector2i(
                    (int) ((endPosition.x - boundaries.x) / diff.x * MovesMatrixLoading.NUMBER_CELLS[0]),
                    (int) ((diff.y - (endPosition.y - boundaries.y)) / diff.y * MovesMatrixLoading.NUMBER_CELLS[1])
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
