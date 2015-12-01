package ayushkumar.smartroomsop.interfaces;

/**
 * Created by Ayush Kumar on 22/08/15
 *
 * @author Ayush Kumar
 *
 * Interface to Audio functions
 * Create mode supports only recording & Open Mode supports only playing
 * Functions in this interface throw UnsupportedOperationException depending on the mode they are in.
 * The exception thrown has to be enforced by the programmer in the implementation of the corresponding mode
 */
public interface AudioRecordListener  {

    /**
     * Start the recording
     * @throws UnsupportedOperationException
     */
    void startRecording() throws UnsupportedOperationException;

    /**
     * Stop the recording
     * @throws UnsupportedOperationException
     */
    void stopRecording() throws UnsupportedOperationException;

    /**
     * Start the playing
     * @throws UnsupportedOperationException
     */
    void startPlaying() throws UnsupportedOperationException;

    /**
     * Stop the playing
     * @throws UnsupportedOperationException
     */
    void stopPlaying() throws UnsupportedOperationException;
}

