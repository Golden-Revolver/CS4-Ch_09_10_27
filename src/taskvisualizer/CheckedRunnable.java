package taskvisualizer;

/**
 *
 * @author Christian Brandon
 */
@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}