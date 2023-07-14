package MobilityViewer.project;

import MobilityViewer.project.graph.Node;

import java.util.Arrays;
import java.util.SortedMap;

public abstract class ProjectUtil {
    public static Node findClosest(SortedMap<Long, Node> nodes, Node position){
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

    public static Node[] findNClosest(SortedMap<Long, Node> nodes, Node position, int N) {
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
