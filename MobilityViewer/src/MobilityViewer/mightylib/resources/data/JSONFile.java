package MobilityViewer.mightylib.resources.data;

import MobilityViewer.mightylib.resources.DataType;
import org.json.JSONObject;

public class JSONFile extends DataType {
    private JSONObject object;
    public JSONFile(String dataName, String path) {
        super(dataName, path);
    }

    void init(JSONObject object){
        this.object = object;
    }

    public JSONObject getObject() {
        return object;
    }

    @Override
    public void unload() {

    }
}
