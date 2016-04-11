/**
 * @author Angie A0126357A
 * @@author Angie A0126357A
 */

package test.storage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import entity.AllTaskLists;
import entity.TaskEntity;
import logic.TaskUtils;
import storage.JsonConverter;
import storage.StorageHandler;

public class JUnitStorage {
    
    private ArrayList<TaskEntity> mainTasks;
    private ArrayList<TaskEntity> floatingTasks;
    private AllTaskLists allLists;
    private StorageHandler sh;
    
    private static final int READ_FROM_CONFIG_FILE = 1;
    private static final int READ_FROM_MAIN_FILE = 2;
    private static final int WRITE_TO_MAIN_FILE = 2;

    @Before
    public void init() {
        mainTasks = new ArrayList<TaskEntity>();
        floatingTasks = new ArrayList<TaskEntity>();
        sh = new StorageHandler();
    }
    
    @Test
    public void testStorage_addDummyTasks() {
        mainTasks.add(new TaskEntity("mainTaskOne", null, TaskUtils.createDate(9, 4, 2016), true));
        floatingTasks.add(new TaskEntity("floatingTaskOne"));
        
        assertEquals(mainTasks.size(), 1);
        assertEquals(floatingTasks.size(), 1);
        
        allLists = new AllTaskLists(mainTasks, floatingTasks);
    }
    
    @Test
    public void testStorage_convertJavaToJsonAndBack_conversionSuccessful() {
        JsonConverter jc = new JsonConverter();
        
        testStorage_addDummyTasks();
        String allListsJson = jc.javaToJson(allLists);
        AllTaskLists convertedFromJson = jc.jsonToJava(allListsJson);
        
        assertEquals(allLists.equals(convertedFromJson), convertedFromJson.equals(allLists));
    }
    
    @Test
    public void testStorage_convertJsonToJava_conversionUnsuccessful_nullExpected() {
        JsonConverter jc = new JsonConverter();
        AllTaskLists invalidConversion = jc.jsonToJava("Non-Json String");
        
        assertEquals(invalidConversion, null);
    }
    
    @Test
    public void testStorage_initFiles_successful() {
        File configFile = new File("configFile.txt");
        File mainTasksFile = new File("mainTasksFile.txt");
        File backUpTasksFile = new File("backUpTasksFile.txt");
        File commandsFile = new File("commandsFile.txt");
        
        configFile.exists();
        mainTasksFile.exists();
        backUpTasksFile.exists();
        commandsFile.exists();
    }
    
    @Test
    public void testStorage_readConfigFile_defaultFilePathAndThemeExpected() {
        String setting = sh.readFromExistingFile(READ_FROM_CONFIG_FILE);
        String[] settingSplit = setting.split("\n");
        String mainFilePathConfig = settingSplit[0];
        String defaultThemeConfig = settingSplit[1];
        
        File mainFile = new File("mainTasksFile.txt");
        String mainFilePathGet = mainFile.getAbsolutePath();
        
        assertEquals(mainFilePathConfig, mainFilePathGet);
        assertEquals(defaultThemeConfig, "default");
    }
    
    @Test
    public void testStorage_readAndWriteMainFile_successful() {
        String toBeWritten = "Write a random string.";
        boolean isWritten = sh.identifyWriteTo(toBeWritten, WRITE_TO_MAIN_FILE);
        assertEquals(isWritten, true);
        
        String readFromFile = sh.readFromExistingFile(READ_FROM_MAIN_FILE);
        assertEquals(toBeWritten, readFromFile);
    }
    
    @Test
    public void testStorage_readAndWriteCommandFile_successfullyAppended() {        
        String commandOne = "Add commandOne";
        boolean isWritten = sh.writeToCommandFile(commandOne);
        assertEquals(isWritten, true);
        
        String commandTwo = "Add commandTwo";
        isWritten = sh.writeToCommandFile(commandTwo);
        assertEquals(isWritten, true);
        
        Queue<String> readFromFile = sh.readFromExistingCommandFile();
        assertEquals(readFromFile.toString(), "[" + commandOne + ", " + commandTwo + "]");
    }
    
    @Test
    public void testStorage_saveToNewDirectory_successful() {
        sh.changeDirectory("newMainTasksFile.txt");
        File newFile = new File("newMainTasksFile.txt");
        
        assertEquals(sh.getMainFilePath(), newFile.getAbsolutePath());
    }
    
    @Test 
    public void testStorage_loadFromExistingFile_fileExists_successful() {
        testStorage_saveToNewDirectory_successful();
        
        sh.loadFromExistingFile("mainTasksFile.txt");
        File newFile = new File("mainTasksFile.txt");
        
        assertEquals(newFile.exists(), true);
        assertEquals(sh.getMainFilePath(), newFile.getAbsolutePath());
    }
    
    @Test
    public void testStorage_loadFromExistingFile_fileDoesNotExist_unsuccessful() {
        boolean isLoaded = sh.loadFromExistingFile("invalidFile.txt");
        File newFile = new File("invalidFile.txt");
        
        assertEquals(newFile.exists(), false);
        assertEquals(isLoaded, false);
    }
    
    @After
    public void cleanUp() {
        File configFile = new File("configFile.txt");
        File mainTasksFile = new File("mainTasksFile.txt");
        File backUpTasksFile = new File("backUpTasksFile.txt");
        File commandsFile = new File("commandsFile.txt");
        File newMainTasksFile = new File("newMainTasksFile.txt");
        
        configFile.delete();
        mainTasksFile.delete();
        backUpTasksFile.delete();
        commandsFile.delete();
        newMainTasksFile.delete();
    }
}
