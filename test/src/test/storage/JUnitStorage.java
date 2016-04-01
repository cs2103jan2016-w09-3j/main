package test.storage;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fileStorage.StorageHandler;

public class JUnitStorage {
    
    private StorageHandler sh;

    @Before
    public void initialise() {
        sh = new StorageHandler();
    }
    
    @Test
    public void testReadAndWriteMain() {
        assertEquals(sh.writeToMainFile("HELLO"), true);
        assertEquals(sh.readFromExistingMainFile(1), "HELLO\n");
    }
    
    @Test
    public void testReadAndWriteCommand() {
        assertEquals(sh.writeToCommandFile("HELLO"), true);
        assertEquals(sh.writeToCommandFile("IT'S ME"), true); // Check if append
        sh.processFile();
        assertEquals(sh.getAllCommandsQueue().size(), 2);
    }
    
    @After
    public void cleanUp() {
        sh.clearCommandFileUponCommit();
    }

}
