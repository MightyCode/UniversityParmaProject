package MobilityViewer.project.display;

import MobilityViewer.mightylib.graphics.renderer.Renderer;
import MobilityViewer.mightylib.graphics.renderer.Shape;
import MobilityViewer.mightylib.scene.Camera2D;
import MobilityViewer.mightylib.util.math.Color4f;
import MobilityViewer.mightylib.util.math.ColorList;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class MatrixRenderer {
    private static final int EBO_SHIFT = 6;
    private static final int VBO_POSITION_SHIFT = 4 * 2;
    private static final int VBO_COLOR_SHIFT = 4 * 4;
    private final Renderer renderer;

    private final int numberCell;
    private final int vboPositionIndex;
    private final int vboColorIndex;
    private Color4f lowValue;
    private Color4f highValue;

    public MatrixRenderer(Camera2D camera, int numberCell) {
        this.numberCell = numberCell;

        renderer = new Renderer("multiColoredShape2D", true);
        renderer.switchToColorMode(ColorList.Red());
        renderer.getShape().setEboStorage(Shape.STATIC_STORE);
        renderer.getShape().setEbo(new int[0]);
        renderer.setReferenceCamera(camera);

        vboPositionIndex = renderer.getShape().addVboFloat(new float[0], 2, Shape.STATIC_STORE);
        vboColorIndex = renderer.getShape().addVboFloat(new float[0], 4, Shape.STATIC_STORE);

        lowValue = new Color4f(1, 1, 1, 1);
        highValue = new Color4f(0, 0, 0, 1);
    }


    public void setLowValue(Color4f newValue){
        lowValue = newValue;
    }

    public void setHighValue(Color4f newValue){
        highValue = newValue;
    }

    public void updateNodes(int[][] matrix, float maxValue, Vector4f displayBoundaries){
        float [] position = new float[numberCell * VBO_POSITION_SHIFT];
        float [] colors = new float[numberCell * VBO_COLOR_SHIFT];
        int [] ebo = new int[numberCell * EBO_SHIFT];
        int [] eboValues = new int[]{ 0, 1, 2, 0, 2, 3 };

        Vector2f cellSize = new Vector2f(
                (displayBoundaries.z - displayBoundaries.x) / matrix[0].length,
                (displayBoundaries.w - displayBoundaries.y) / matrix.length);

        for (int y = 0; y < matrix.length; ++y){
            for (int x = 0; x < matrix[y].length; ++x){
                Color4f color = new Color4f(
                        lowValue.getR() + (highValue.getR() - lowValue.getR()) * (matrix[y][x] /  maxValue),
                        lowValue.getG() + (highValue.getG() - lowValue.getG()) * (matrix[y][x] /  maxValue),
                        lowValue.getB() + (highValue.getB() - lowValue.getB()) * (matrix[y][x] /  maxValue),
                        lowValue.getA() + (highValue.getA() - lowValue.getA()) * (matrix[y][x] /  maxValue)
                );

                Vector2f leftUpPosition = new Vector2f(
                        displayBoundaries.x + (displayBoundaries.z - displayBoundaries.x) * (x * 1.0f / matrix[y].length),
                        displayBoundaries.y + (displayBoundaries.w - displayBoundaries.y) * (y * 1.0f / matrix.length)
                                );

                Vector4f fourPositions = new Vector4f(
                         leftUpPosition.x, leftUpPosition.y,
                        leftUpPosition.x + cellSize.x, leftUpPosition.y + cellSize.y
                );

                int i = y * matrix[y].length + x;

                position[i * VBO_POSITION_SHIFT + 0] = fourPositions.x;
                position[i * VBO_POSITION_SHIFT + 1] = fourPositions.w;

                position[i * VBO_POSITION_SHIFT + 2] = fourPositions.x;
                position[i * VBO_POSITION_SHIFT + 3] = fourPositions.y;

                position[i * VBO_POSITION_SHIFT + 4] = fourPositions.z;
                position[i * VBO_POSITION_SHIFT + 5] = fourPositions.y;

                position[i * VBO_POSITION_SHIFT + 6] = fourPositions.z;
                position[i * VBO_POSITION_SHIFT + 7] = fourPositions.w;

                for (int l = 0; l < 16; ++l){
                    colors[i * VBO_COLOR_SHIFT + l] = color.get(l % 4);
                }

                for (int j = 0; j < EBO_SHIFT; ++j)
                    ebo[EBO_SHIFT * i + j] = eboValues[j] + 4 * i;
            }
        }

        renderer.getShape().setEbo(ebo);
        renderer.getShape().updateVbo(position, vboPositionIndex);
        renderer.getShape().updateVbo(colors, vboColorIndex);
    }

    public void display() {
        renderer.display();
    }

    public void unload() {
        renderer.unload();
    }
}
