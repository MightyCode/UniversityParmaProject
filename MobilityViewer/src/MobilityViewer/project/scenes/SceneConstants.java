package MobilityViewer.project.scenes;

import MobilityViewer.mightylib.util.DataFolder;
import MobilityViewer.project.graph.Node;
import MobilityViewer.project.graph.Road;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;

public abstract class SceneConstants {
    private static final String PARMA_SAVE_POINT = "parma.txt";
    public static final Vector4f BOUNDARIES = new Vector4f( 10.260f ,44.7595f,10.385f, 44.8389f);
    //public static final Vector4f BOUNDARIES = new Vector4f( 10.1f ,44.5f,10.5f, 45f);

    public static void parseNode(JSONObject jsonObject, SortedMap<Long, Node> nodes, Vector4f boundaries){
        JSONArray elements = jsonObject.getJSONArray("elements");

        for (int i = 0; i < elements.length(); ++i){
            JSONObject element = elements.getJSONObject(i);

            if (element.getString("type").equals("node")
                    && inBoundaries(boundaries, new Vector2f(element.getFloat("lon"), element.getFloat("lat")))) {
                nodes.put(element.getLong("id"),
                        new Node(element.getLong("id"),
                                element.getFloat("lon"),
                                element.getFloat("lat"))
                );
            }
        }
    }

    public static void parseRoad(JSONObject jsonObject, SortedMap<Long, Road> roads,
                                 SortedMap<Long, Node> nodes, Vector4f boundaries){
        JSONArray elements = jsonObject.getJSONArray("elements");

        for (int i = 0; i < elements.length(); ++i){
            JSONObject element = elements.getJSONObject(i);

            if (element.getString("type").equals("way")){
                JSONArray array = element.getJSONArray("nodes");
                Road road = new Road(
                        element.getInt("id")
                );

                Node previous = null, next = null;
                for (int j = 0; j < array.length(); ++j) {
                    if (!nodes.containsKey(array.getLong(j))) {
                        previous = next;
                        continue;
                    }

                    next = nodes.get(array.getLong(j));
                    road.add(next);

                    if (previous != null){
                        previous.add(next);
                        next.add(previous);
                    }

                    previous = next;
                }

                roads.put(element.getLong("id"), road);
            }
        }
    }

    public static boolean inBoundaries(Vector4f boundaries, Vector2f point){
        return boundaries.x <= point.x && point.x <= boundaries.z
                && boundaries.y <= point.y && point.y <= boundaries.w;
    }

    public static String requestData(){
        return requestData(false);
    }

    public static String requestData(boolean includeAllTypeOfRoad){
        if (DataFolder.fileExists(PARMA_SAVE_POINT)){
            String data = DataFolder.getFileContent(PARMA_SAVE_POINT);

            System.out.println("Parma data exists locally, loading ...");

            if (data != null)
                return data;
        } else {
            System.out.println("Parma data doesn't exists, requesting ...");
        }

        String overpass_query = "";

        if (includeAllTypeOfRoad) {
            overpass_query = "[out:json];\n" +
                    "area[name=\"Parma\"]->.a;\n" +
                    "(\n" +
                    "way(area.a)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|bicycle|service|living_street|pedestrian|footway|cycleway|bus_guideway)$\"];\n" +
                    "node(w)->.nodes;\n" +
                    "way(bn.nodes)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|bicycle|service|living_street|pedestrian|footway|cycleway|bus_guideway)$\"];\n" +
                    ");\n" +
                    "out body;\n" +
                    ">;\n" +
                    "out skel qt;";
        } else {
            overpass_query = "[out:json];\n" +
                    "area[name=\"Parma\"]->.a;\n" +
                    "(\n" +
                    "way(area.a)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|bicycle)$\"];\n" +
                    "node(w)->.nodes;\n" +
                    "way(bn.nodes)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|bicycle)$\"];\n" +
                    ");\n" +
                    "out body;\n" +
                    ">;\n" +
                    "out skel qt;";
        }

        // Send the query to the Overpass API endpoint
        String encodedQuery = URLEncoder.encode(overpass_query, StandardCharsets.UTF_8);
        String urlString = "https://overpass-api.de/api/interpreter?data=" + encodedQuery;
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        String data = "null";

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                // Parse the response data
                data = httpResponse.body();
                // Process the data further as needed

                //System.out.println("Data received: " + data);
            } else {
                System.out.println("Error when fetching data of Parma streets: " + httpResponse.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Parma data save after requesting ... ");
        System.out.println(DataFolder.saveFile(data, PARMA_SAVE_POINT));

        return data;
    }
}
