package ayushkumar.smartroomsop.interfaces;

/**
 * Created by ayush on 22/08/15
 */
public interface AudioRecordListener  {

    void startRecording() throws UnsupportedOperationException;

    void stopRecording() throws UnsupportedOperationException;

    void startPlaying() throws UnsupportedOperationException;

    void stopPlaying() throws UnsupportedOperationException;
}

