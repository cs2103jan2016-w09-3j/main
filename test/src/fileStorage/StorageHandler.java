package fileStorage;

public class StorageHandler {
    public static void main (String args[]) {
        FileHandler fm = new FileHandler();
        System.out.println(fm.getFilePath());
        fm.writeToFile("Testing a file");
        System.out.println(fm.readFromExistingFile());
    }
}
