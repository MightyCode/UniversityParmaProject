package MobilityViewer.mightylib.resources.sound;

import MobilityViewer.mightylib.resources.DataType;
import MobilityViewer.mightylib.sounds.SoundLoadInfo;
import MobilityViewer.mightylib.sounds.SoundManager;

import static org.lwjgl.openal.AL10.*;

public class SoundData extends DataType {

    private final static String PATH = "resources/sounds/";

    private int bufferId;

    public SoundData(String dataName, String path) {
        super(dataName, PATH + path);

        bufferId = -1;
    }


    public void createSound(SoundLoadInfo info){
        if (bufferId != -1) {
            return;
        }

        this.bufferId = alGenBuffers();
        alBufferData(bufferId, info.Channel == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, info.Buffer, info.SampleRate);

        int error = alGetError();

        if (error == AL_NO_ERROR){
            correctlyLoaded = bufferId != -1;
        } else {
            SoundManager.DisplayError("al buffer data (\" + path + \")\"", error);
        }
    }


    public int getBufferId(){
        return bufferId;
    }

    @Override
    public void unload() {
        if (this.bufferId != -1) {
            alDeleteBuffers(this.bufferId);
            int error = alGetError();
            if (error != AL_NO_ERROR)
                SoundManager.DisplayError("al delete buffers (" + path + ")", error);
        }

        correctlyLoaded = bufferId != -1;
    }
}
