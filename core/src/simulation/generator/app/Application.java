package simulation.generator.app;

import org.griphyn.vdl.dax.ADAG;

import java.io.OutputStream;

/**
 * @author Shishir Bharathi
 * @author Gideon Juve <juve@usc.edu>
 */
public interface Application {
    public ADAG getDAX();
    public void generateWorkflow(String... args) throws Exception;
    public void printWorkflow(OutputStream os) throws Exception;
}
