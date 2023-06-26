package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.project.graph.Graph;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.json.JSONObject;;

import java.util.SortedMap;
import java.util.TreeMap;

public class MovesMatrixLoading extends LoadingContent {
    //public static final int NUMBER_CELLS_BY_COORD = 10000;
    public static final int NUMBER_CELLS_BY_COORD = 5000;
    //public static final int NUMBER_CELLS_BY_COORD = 1000;
    protected MovesMatrixLoading.MMLResult mmlResult;
    public static class MMLResult extends LoadingContent.Result {
        public SortedMap<Long, Node> nodes;
        public SortedMap<Long, Road> roads;
        public int[][] startMatrix;

        public int[][] endMatrix;
        public int maxStartValue;

        public int maxEndValue;
    }

    public MovesMatrixLoading() {
        super(new MovesMatrixLoading.MMLResult());

        mmlResult = (MovesMatrixLoading.MMLResult) result;
    }

    @Override
    protected final void init() {
        percentage = 0.f;
        step = "Request data of Parma";
        String data = SceneConstants.requestData();

        step = "Parse nodes";
        percentage = 0.1f;

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

        step = "Get csv of scooters moves";
        percentage = 0.2f;

        Vector2f diff = new Vector2f(boundaries.z - boundaries.x, boundaries.w - boundaries.y);

        Vector2i numbers_cells = new Vector2i(
                (int)(Math.ceil(diff.x * NUMBER_CELLS_BY_COORD)),
                (int)(Math.ceil(diff.y * NUMBER_CELLS_BY_COORD))
        );

        mmlResult.startMatrix = new int[numbers_cells.y][numbers_cells.x];
        mmlResult.endMatrix = new int[numbers_cells.y][numbers_cells.x];

        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, "Noleggi_Parma_2022");
        String paths = DataFolder.getFileContent("scooters-path.txt");

        if (csvFile != null && paths != null) {
            step = "Scooter path parsing : 0 / " + csvFile.size();
            percentage = 0.5f;

            String[] splitPath = paths.split(";");
            float scooterLoadingPercentage = 0.01f;

            for (int i = 0; i < csvFile.size(); ++i) {
                Vector2f startPosition = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 2)),
                        Float.parseFloat(csvFile.getData(i, 1))
                );

                Vector2f endPosition  = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 5)),
                        Float.parseFloat(csvFile.getData(i, 4))
                );

                if (SceneConstants.inBoundaries(boundaries, startPosition)
                    && (SceneConstants.inBoundaries(boundaries, endPosition))) {

                    Vector2i startCellPosition = new Vector2i(
                            (int) ((startPosition.x - boundaries.x) * NUMBER_CELLS_BY_COORD),
                            (int) ((startPosition.y - boundaries.y) * NUMBER_CELLS_BY_COORD)
                    );

                    if (++mmlResult.startMatrix[startCellPosition.y][startCellPosition.x] > mmlResult.maxStartValue)
                        mmlResult.maxStartValue = mmlResult.startMatrix[startCellPosition.y][startCellPosition.x];

                    Vector2i endCellPosition = new Vector2i(
                            (int) ((endPosition.x - boundaries.x) * NUMBER_CELLS_BY_COORD),
                            (int) ((endPosition.y - boundaries.y) * NUMBER_CELLS_BY_COORD)
                    );

                    if (++mmlResult.endMatrix[endCellPosition.y][endCellPosition.x] > mmlResult.maxEndValue)
                        mmlResult.maxEndValue = mmlResult.endMatrix[endCellPosition.y][endCellPosition.x];

                }

                if (i > scooterLoadingPercentage * csvFile.size()){
                    scooterLoadingPercentage += 0.01f;

                    step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                            + " / " + csvFile.size();
                    percentage = 0.5f + 0.5f * scooterLoadingPercentage;
                }
            }
        }

        step = "Finished";
        percentage = 1;
    }
}
