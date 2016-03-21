package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CommandHandler {
    
    private String commandFilePath;
    private File commandFile;
    private ArrayList<String> commandArrayList;

    public CommandHandler() {
        commandFilePath = "commandFile.txt";
        processCommandFile();
    }
    
    public ArrayList<String> getCommandArray() {
        return commandArrayList;
    }

    public void setCommandArray(ArrayList<String> commandArray) {
        this.commandArrayList = commandArray;
    }
    
    /**
     * Reads and stores data from existing file if any, creates a new file otherwise
     */
    private void processCommandFile() {
        commandFile = new File(commandFilePath);

        if (commandFile.exists()) {
            setCommandArray(readFromExistingCommandFile());
        } else {
            createNewCommandFile(commandFile);
        }
    }

    private void createNewCommandFile(File storedCommands) {
        try {
            storedCommands.createNewFile();
            System.out.println("Created new file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads data from an existing file and returns the appended string
     * @return ArrayList<String>
     */
    public ArrayList<String> readFromExistingCommandFile() {        
        BufferedReader buffer;
        ArrayList<String> readCommands = new ArrayList<String>();
        try {
            buffer = new BufferedReader(new FileReader(commandFilePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readCommands.add(currentLine);
            }
            buffer.close();
            System.out.println("Read from file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readCommands;
    }
    
    /**
     * Returns true if data is written to a file, false otherwise
     * Whether the data has been written depends on the last modified time of the file
     * @param data
     * @return boolean
     */
    public boolean writeToCommandFile(ArrayList<String> commands) {
        FileWriter fileWriter;
        long beforeModify = commandFile.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(commandFilePath); 
            for (int i = 0; i < commands.size(); i++) {
                fileWriter.write(commands.get(i) + '\n');
            }
            fileWriter.flush();
            fileWriter.close();
            afterModify = commandFile.lastModified();
        } catch (IOException e) {
            e.printStackTrace();  
        }
        return isModified(beforeModify, afterModify);
    }

    private boolean isModified(long timeBeforeModification, long timeAfterModification) {
        return timeAfterModification > timeBeforeModification;
    }
    
    private void saveUponExit(boolean isExit) {
        if (isExit == true) {
            writeToCommandFile(commandArrayList);
        }
    }
    
    private void saveUponTimeOut() {
        
    }
}
