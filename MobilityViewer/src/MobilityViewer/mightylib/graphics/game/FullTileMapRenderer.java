package MobilityViewer.mightylib.graphics.game;

import MobilityViewer.mightylib.resources.map.TileMap;
public class FullTileMapRenderer {
    private final TileMapRenderer forTileMapRenderer;
    private final TileMapRenderer backTileMapRenderer;


    public FullTileMapRenderer(String shader, boolean frequentlyUpdate) {
        forTileMapRenderer = new TileMapRenderer(shader, frequentlyUpdate, true);
        backTileMapRenderer = new TileMapRenderer(shader, frequentlyUpdate, false);
    }

    public void setTileMap(TileMap tilemap){
        forTileMapRenderer.setTilemap(tilemap);
        backTileMapRenderer.setTilemap(tilemap);
    }

    public void update(){
        forTileMapRenderer.update();
        backTileMapRenderer.update();
    }

    public void drawForLayers(){
        forTileMapRenderer.display();
    }

    public void drawBackLayers(){
        backTileMapRenderer.display();
    }

    public void unload(){
        forTileMapRenderer.unload();
        backTileMapRenderer.unload();
    }
}
