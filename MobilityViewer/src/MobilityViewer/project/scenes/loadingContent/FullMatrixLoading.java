package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.project.main.ETypeData;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.HashMap;

public class FullMatrixLoading extends LoadingContent {

    public static final int[] NUMBER_CELLS = new int[]{ 12, 15 };

    protected FullMatrixLoading.FMLResult fmlResult;
    public static class FMLResult extends Result {
        public HashMap<ETypeData, int[][]> matrices;
        public HashMap<ETypeData, Integer> minValues;
        public HashMap<ETypeData, Integer> maxValues;
    }

    public FullMatrixLoading() {
        super(new FullMatrixLoading.FMLResult());

        fmlResult = (FullMatrixLoading.FMLResult) result;
    }

    @Override
    protected final void init() {
        fmlResult.matrices = new HashMap<>();
        fmlResult.minValues = new HashMap<>();
        fmlResult.maxValues = new HashMap<>();

        step = "Get csv of scooters moves";
        percentage = 0.0f;

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        Vector2f diff = new Vector2f(boundaries.z - boundaries.x, boundaries.w - boundaries.y);

        for (ETypeData key : ETypeData.values()){
            fmlResult.matrices.put(key, new int[NUMBER_CELLS[0] * NUMBER_CELLS[1]][NUMBER_CELLS[0] * NUMBER_CELLS[1]]);
            fmlResult.minValues.put(key, 0);
            fmlResult.maxValues.put(key, 0);
        }

        Vector2f startPosition, endPosition;

        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, "Noleggi_Parma_2022");

        if (csvFile != null) {
            step = "Bike path parsing : 0 / " + csvFile.size();
            percentage = 0.33f;
            float scooterLoadingPercentage = 0.01f;

            for (int i = 0; i < csvFile.size(); ++i) {

                startPosition = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 2)),
                        Float.parseFloat(csvFile.getData(i, 1))
                );

                endPosition  = new Vector2f(
                        Float.parseFloat(csvFile.getData(i, 5)),
                        Float.parseFloat(csvFile.getData(i, 4))
                );

                fillWithPositions(startPosition, endPosition, ETypeData.Scooter, boundaries, diff);

                if (i > scooterLoadingPercentage * csvFile.size()){
                    scooterLoadingPercentage += 0.01f;

                    step = "Bike path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                            + " / " + csvFile.size();
                    percentage = 0.33f + 0.33f * scooterLoadingPercentage;
                }
            }
        }

        csvFile = Resources.getInstance().getResource(CSVFile.class, "bikes-path");

        String temp;
        String[] positions;
        String[] position;
        if (csvFile != null) {
            step = "Scooter path parsing : 0 / " + csvFile.size();
            percentage = 0.66f;
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

                    fillWithPositions(startPosition, endPosition, ETypeData.Bike, boundaries, diff);
                }

                if (i > scooterLoadingPercentage * csvFile.size()){
                    scooterLoadingPercentage += 0.01f;

                    step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                            + " / " + csvFile.size();
                    percentage = 0.66f + 0.34f * scooterLoadingPercentage;
                }
            }
        }

        step = "Finished";
        percentage = 1;
    }

    public void fillWithPositions(Vector2f startPosition, Vector2f endPosition,
                                  ETypeData type, Vector4f boundaries, Vector2f diff){
        if (SceneConstants.inBoundaries(boundaries, startPosition)
                && (SceneConstants.inBoundaries(boundaries, endPosition))) {

            Vector2i startCellPosition = new Vector2i(
                    (int) ((startPosition.x - boundaries.x) / diff.x * FullMatrixLoading.NUMBER_CELLS[0]),
                    (int) ((diff.y - (startPosition.y - boundaries.y)) / diff.y * FullMatrixLoading.NUMBER_CELLS[1])
            );
            Vector2i endCellPosition = new Vector2i(
                    (int) ((endPosition.x - boundaries.x) / diff.x * FullMatrixLoading.NUMBER_CELLS[0]),
                    (int) ((diff.y - (endPosition.y - boundaries.y)) / diff.y * FullMatrixLoading.NUMBER_CELLS[1])
            );

            if (++fmlResult.matrices.get(type)[startCellPosition.y * NUMBER_CELLS[0] + startCellPosition.x]
                    [endCellPosition.y * NUMBER_CELLS[0] + endCellPosition.x] > fmlResult.maxValues.get(type))
                fmlResult.maxValues.put(type, fmlResult.matrices.get(type)
                        [startCellPosition.y * NUMBER_CELLS[0] + startCellPosition.x]
                        [endCellPosition.y * NUMBER_CELLS[0] + endCellPosition.x]);
        }
    }
}
