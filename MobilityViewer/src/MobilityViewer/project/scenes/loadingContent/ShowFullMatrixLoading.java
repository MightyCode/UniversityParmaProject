package MobilityViewer.project.scenes.loadingContent;

import MobilityViewer.mightylib.resources.Resources;
import MobilityViewer.mightylib.resources.data.CSVFile;
import MobilityViewer.mightylib.resources.data.JSONFile;
import MobilityViewer.project.scenes.SceneConstants;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.json.JSONObject;

import java.util.HashMap;

public class FullMatrixLoading extends LoadingContent {

    public static final int[] NUMBER_CELLS = new int[]{ 12, 15 };

    protected FullMatrixLoading.FMLResult fmlResult;
    public static class FMLResult extends Result {
        public HashMap<String, int[][]> matrices;
        public HashMap<String, Integer> minValues;
        public HashMap<String, Integer> maxValues;
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

        JSONFile displayableResources =  Resources.getInstance().getResource(JSONFile.class, "displayableResources");
        String[] resourceCategories = displayableResources.getObject().keySet().toArray(new String[]{});

        for (String key : resourceCategories){
            if (key.equals("general"))
                continue;

            fmlResult.matrices.put(key, new int[NUMBER_CELLS[0] * NUMBER_CELLS[1]][NUMBER_CELLS[0] * NUMBER_CELLS[1]]);
            fmlResult.minValues.put(key, 0);
            fmlResult.maxValues.put(key, 0);
        }

        JSONObject fileObject = displayableResources.getObject();
        Vector2f startPosition = new Vector2f(), endPosition = new Vector2f();
        int numbResource = 0;

        step = "Prepare to parse files";
        percentage = 0.2f;

        for (String key : resourceCategories){
            if (key.equals("general"))
                continue;

            JSONObject resourceObject = fileObject.getJSONObject(key);

            CSVFile csvFile = Resources.getInstance().getResource(CSVFile.class, resourceObject.getString("file"));

            if (csvFile != null) {
                step = "Scooter path parsing : 0 / " + csvFile.size();
                percentage = 0.2f + (0.8f / fmlResult.matrices.size() * numbResource);

                float scooterLoadingPercentage = 0.01f;

                for (int i = 0; i < csvFile.size(); ++i) {
                    fillPosition(startPosition, endPosition, resourceObject, csvFile, i);

                    fillWithPositions(startPosition, endPosition, key, boundaries, diff);

                    if (i > scooterLoadingPercentage * csvFile.size()){
                        scooterLoadingPercentage += 0.01f;

                        step = "Scooter path parsing : " + (int)(scooterLoadingPercentage * csvFile.size())
                                + " / " + csvFile.size();
                        percentage = 0.2f + (0.8f / fmlResult.matrices.size() * numbResource)
                                + (0.8f / fmlResult.matrices.size()) * scooterLoadingPercentage;
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
        if (type.equalsIgnoreCase("start/end")){
            JSONObject info = object.getJSONObject("info");

            startPosition.x = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("startLongitude").getInt("col")));

            startPosition.y = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("startLatitude").getInt("col")));


            endPosition.x = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("endLongitude").getInt("col")));

            endPosition.y = Float.parseFloat(csvFile.getData(index,
                    info.getJSONObject("endLatitude").getInt("col")));

        } else if (type.equalsIgnoreCase("path")){
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
