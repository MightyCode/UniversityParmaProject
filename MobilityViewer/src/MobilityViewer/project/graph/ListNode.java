package MobilityViewer.project.graph;

import java.util.*;

public abstract class ListNode<T extends ListNode<?>>{
    private final long id;
    private final ArrayList<Long> ids;
    private final SortedMap<Long, T> nodes;

    public ListNode(long id){
        this.id = id;

        nodes = new TreeMap<>();
        ids = new ArrayList<>();
    }

    public long getId(){
        return id;
    }


    public void clear(){
        nodes.clear();
    }

    public int size(){
        return nodes.size();
    }

    public boolean add(T node){
        if (ids.add(node.getId())) {
            nodes.put(node.getId(), node);
            return true;
        }

        return false;
    }

    public void addAll(Collection<T> nodes){
        for (T node : nodes)
            add(node);
    }

    public T get(int i){
        return nodes.get(ids.get(i));
    }

    public boolean contains(T n){
        return nodes.containsKey(n.getId());
    }

    public boolean containsId(long id){
        return nodes.containsKey(id);
    }

    public int indexOf(long id){
        return ids.indexOf(id);
    }

    public int indexOf(T node){
        return ids.indexOf(node.getId());
    }

    public T getById(long id){ return nodes.get(id); }

    public Collection<T> getNodes(){
        return nodes.values();
    }

    public Collection<Long> getIds(){ return ids; }
}
