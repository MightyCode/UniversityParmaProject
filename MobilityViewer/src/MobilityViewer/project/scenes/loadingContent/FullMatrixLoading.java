package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

public class FullMatrixLoading extends LoadingContent {

    public static final int NUMBER_CELLS_BY_COORD = 127;

    protected FullMatrixLoading.FMLResult fmlResult;
    public static class FMLResult extends Result {
        public int[][] matrix;
        public int maxValue;
    }

    public FullMatrixLoading() {
        super(new FullMatrixLoading.FMLResult());

        fmlResult = (FullMatrixLoading.FMLResult) result;
    }

    @Override
    protected final void init() {
        step = "Get csv of scooters moves";
        percentage = 0.0f;

        Vector4f boundaries = SceneConstants.BOUNDARIES;

        Vector2f diff = new Vector2f(boundaries.z - boundaries.x, boundaries.w - boundaries.y);

        Vector2i numbers_cells = new Vector2i(
                (int)(Math.ceil(diff.x * FullMatrixLoading.NUMBER_CELLS_BY_COORD)),
                (int)(Math.ceil(diff.y * FullMatrixLoading.NUMBER_CELLS_BY_COORD))
        );

        fmlResult.matrix = new int[numbers_cells.y * numbers_cells.x][numbers_cells.y * numbers_cells.x];
        CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, "Noleggi_Parma_2022");

        if (csvFile != null) {
            step = "Scooter path parsing : 0 / " + csvFile.size();
            percentage = 0.5f;
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
                            (int) ((startPosition.x - boundaries.x) * FullMatrixLoading.NUMBER_CELLS_BY_COORD),
                            (int) ((startPosition.y - boundaries.y) * FullMatrixLoading.NUMBER_CELLS_BY_COORD)
                    );
                    Vector2i endCellPosition = new Vector2i(
                            (int) ((endPosition.x - boundaries.x) * FullMatrixLoading.NUMBER_CELLS_BY_COORD),
                            (int) ((endPosition.y - boundaries.y) * FullMatrixLoading.NUMBER_CELLS_BY_COORD)
                    );

                    if (++fmlResult.matrix[startCellPosition.y * numbers_cells.x + startCellPosition.x]
                            [endCellPosition.y * numbers_cells.x + endCellPosition.x] > fmlResult.maxValue)
                        fmlResult.maxValue = fmlResult.matrix[startCellPosition.y * numbers_cells.x + startCellPosition.x]
                                [endCellPosition.y * numbers_cells.x + endCellPosition.x];
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
