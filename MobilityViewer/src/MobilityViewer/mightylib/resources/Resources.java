package MobilityViewer.mightylib.resources;

import MobilityViewer.mightylib.graphics.text.FontLoader;
import MobilityViewer.mightylib.resources.animation.AnimationDataLoader;
import MobilityViewer.mightylib.resources.data.JsonLoader;
import MobilityViewer.mightylib.resources.sound.SoundLoader;
import MobilityViewer.mightylib.resources.texture.IconLoader;
import MobilityViewer.mightylib.resources.texture.TextureLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Resources {
    private static Resources singletonInstance = null;
    public static Resources getInstance(){
        if (singletonInstance == null){
            singletonInstance = new Resources();
        }

        return singletonInstance;
    }

    public final List<ResourceLoader> Loaders;

    private final HashMap<Class<?>, HashMap<String, DataType>> resources;

    private boolean initialized;
    private boolean firstLoad;

    private Resources(){
        resources = new HashMap<>();
        Loaders = new ArrayList<>();

        Loaders.add(new IconLoader());
        Loaders.add(new TextureLoader());
        Loaders.add(new AnimationDataLoader());
        Loaders.add(new FontLoader());
        Loaders.add(new SoundLoader());
        Loaders.add(new JsonLoader());

        initialized = false;
        firstLoad = false;
    }


    public void init(){
        if (initialized)
            return;

        for (ResourceLoader loader : Loaders){
            HashMap<String, DataType> map = new HashMap<>();
            loader.create(map);
            resources.put(loader.getType(), map);
        }

        initialized = true;
    }

    public void createAndInit(Class<?> resourceType, String resourceName, String resourcePath){
        for (ResourceLoader resourceLoader : singletonInstance.Loaders) {
            if (resourceType == resourceLoader.getType()) {
                resourceLoader.createAndLoad(resources.get(resourceType), resourceName, resourcePath);
                break;
            }
        }
    }

    public static Class<?> getClassFromName(String name){
        for (ResourceLoader resourceLoader : singletonInstance.Loaders){
            if (resourceLoader.getResourceNameType().equals(name))
                return resourceLoader.getType();
        }

        return Object.class;
    }


    public <T> T getResource(Class<T> type, String name){
        return type.cast(resources.get(type).get(name));
    }

   /*
   public <T extends DataType> T getResource(Class<T> type, String resourceName) {
        HashMap<String, DataType> typedResources = resources.get(type);
        if (typedResources != null && typedResources.containsKey(resourceName)) {
            System.out.println("Found resource: " + resourceName);
            return type.cast(typedResources.get(resourceName));
        }

        System.err.println("Resource not found: " + resourceName);
        return null;
    }
    */






    public boolean isExistingResource(Class<?> type, String name){
        if (!resources.containsKey(type))
            return false;

        return resources.get(type).containsKey(name);
    }


    public int load(){
        if (firstLoad)
            return -1;

        System.out.println("--Load Resources");
        int incorrectlyLoad = 0;

        for (ResourceLoader loader : Loaders){
            incorrectlyLoad += load(loader.getType());
        }

        firstLoad = true;
        return incorrectlyLoad;
    }


    private int load(Class<?> typeOfResource){
        int incorrectlyLoad = 0;

        for (DataType dataType : resources.get(typeOfResource).values()){
            if (dataType.isCorrectlyLoaded())
                continue;

            dataType.load(Objects.requireNonNull(getLoader(typeOfResource)));

            if (!dataType.isCorrectlyLoaded())
                ++incorrectlyLoad;
        }

        return incorrectlyLoad;
    }

    private ResourceLoader getLoader (Class<?> typeOfResource){
        for (ResourceLoader loader : Loaders){
            if (typeOfResource.equals(loader.getType()))
                return loader;
        }

        return null;
    }


    public int reload(){
        int incorrectlyReload = 0;

        for (Class<?> c : resources.keySet()){
            incorrectlyReload += reload(c);
        }

        return incorrectlyReload;
    }


    public int reload(Class<?> typeOfResource){
        int incorrectlyReload = 0;

        for (DataType dataType : resources.get(typeOfResource).values()){
            dataType.reload(Objects.requireNonNull(getLoader(typeOfResource)));

            if (!dataType.isCorrectlyLoaded())
                ++incorrectlyReload;
        }

        return incorrectlyReload;
    }


    public int unload(){
        System.out.println("--Unload Resources");
        int incorrectlyUnload = 0;

        for (Class<?> c : resources.keySet()){
            incorrectlyUnload += unload(c);
        }

        return incorrectlyUnload;
    }


    public int unload(Class<?> typeOfResource){
        int incorrectlyUnload = 0;

        for (DataType dataType : resources.get(typeOfResource).values()){
            if (!dataType.isCorrectlyLoaded())
                continue;

            dataType.unload();
            if (!dataType.isCorrectlyLoaded())
                ++incorrectlyUnload;
        }

        return incorrectlyUnload;
    }
}
