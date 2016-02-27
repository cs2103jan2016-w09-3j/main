package fileStorage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;

public class FileManager {
    
    private RandomAccessFile file;
    private String fileName;
    private BufferedReader buffer;
    
    public FileManager() {
        fileName = "taskList.txt";
    }
    
    private void createNewFile() {
        try {
            file = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
    
    private void readFromExistingFile() {
        try {
            buffer = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
    
}
