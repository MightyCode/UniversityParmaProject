package MobilityViewer.mightylib.resources.texture;

import MobilityViewer.mightylib.resources.DataType;
import org.lwjgl.BufferUtils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL13.*;

public class Texture extends DataType implements IGLBindable {
    private int width;
    private int height;

    private int textureId;

    private int qualityType;

    private int textureType;

    public Texture(String name, String path) {
        super(name, path);

        textureId = -1;
        qualityType = TextureParameters.REALISTIC_PARAMETERS;
        textureType = GL_TEXTURE_2D;
    }

    public void setAspectTexture(int aspectTexture){
        this.qualityType = aspectTexture;
    }

    public void bind() {
        bind(0);
    }

    public void bind(int texturePos) {
        // Active the texture to right position
        glActiveTexture(GL_TEXTURE0 + texturePos);
        if (isCorrectlyLoaded()){
            glBindTexture(textureType, textureId);
        // If isn't correct loaded, bind error texture
        } else glBindTexture(textureType, 1);
    }

    public void createImage(BufferedImage img) {
        if (textureId != -1)
            unload();

        textureId = glGenTextures();

        try {
            int[] pixels = new int[img.getHeight() * img.getWidth()];

            img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);

            this.width = img.getWidth();
            this.height = img.getHeight();

            for (int a = 0; a < height; ++a) {
                for (int b = 0; b < width; ++b) {
                    int pixel = pixels[a * width + b];
                    byteBuffer.put((byte) ((pixel >> 16) & 0xFF));
                    byteBuffer.put((byte) ((pixel >> 8)  & 0xFF));
                    byteBuffer.put((byte) ((pixel        & 0xFF)));
                    byteBuffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }

            byteBuffer.flip();

            glBindTexture(this.textureType, textureId);
            glTexImage2D(this.textureType, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);

            TextureParameters.applyParameters(this);

            //System.out.println("Texture : " + textureId + " , loaded with path : " + path);

            correctlyLoaded = true;
        } catch (Exception e) {
            System.err.println("Fail to create texture " + path + " :");
            e.printStackTrace();
            glDeleteTextures(textureId);

            correctlyLoaded = false;
        }
    }

    public int getTexId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void setTextParam(int param, int value) {
        bind();
        if (isCorrectlyLoaded())
            glTexParameteri(this.textureType, param, value);
    }

    public int getQualityType(){
        return qualityType;
    }

    public void setTextureType(int textureType){
        this.textureType = textureType;
    }

    public int getTextureType(){
        return this.textureType;
    }

    @Override
    public void unload() {
        if (isCorrectlyLoaded()) {
            glDeleteTextures(textureId);
            correctlyLoaded = false;
        }
    }
}
