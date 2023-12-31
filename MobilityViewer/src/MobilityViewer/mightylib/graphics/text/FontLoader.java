package MobilityViewer.mightylib.graphics.text;

import MobilityViewer.mightylib.resources.DataType;
import MobilityViewer.mightylib.resources.FileMethods;
import MobilityViewer.mightylib.resources.ResourceLoader;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class FontLoader extends ResourceLoader {
    @Override
    public Class<?> getType() {
        return FontFace.class;
    }

    @Override
    public String getResourceNameType() {
        return "FontFace";
    }

    @Override
    public void create(Map<String, DataType> data){
        JSONObject obj = new JSONObject(FileMethods.readFileAsString("resources/textures/fonts/fonts.json"));
        obj = obj.getJSONObject("fonts");

        create(data, obj);
    }

    private void create(Map<String, DataType> data, JSONObject node){
        Iterator<String> arrayNodes = node.keys();

        if(!arrayNodes.hasNext())
            return;

        do{
            String currentNode = arrayNodes.next();
            JSONObject values = node.getJSONObject(currentNode);

            if (values.length() == 2)
                data.put(currentNode, new FontFace(currentNode, values.getString("texture"), values.getString("info")));

        } while(arrayNodes.hasNext());
    }


    @Override
    public void load(DataType dataType) {
        if (!(dataType instanceof FontFace))
            return;

        FontFace fontFace = (FontFace) dataType;

        fontFace.getFontFile().load();

        fontFace.setCorrectlyLoaded();
    }

    @Override
    public void createAndLoad(Map<String, DataType> data, String resourceName, String resourcePath) {
        // Todo
    }
}
