/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * @author SimHigh
 *
 */
public class InputStreamConsumer extends Thread {

    private InputStream is;
    private IOException exp;
    private PrintStream out;

    public InputStreamConsumer(InputStream is, PrintStream out) {
        this.is = is;
        this.out = out;
    }

    @Override
    public void run() {
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
        } catch (IOException ex) {
            Log.err(this, ex);
            exp = ex;
        }
    }

    public IOException getException() {
        return exp;
    }
}
