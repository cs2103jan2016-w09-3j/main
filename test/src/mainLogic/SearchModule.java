package mainLogic;

import java.util.ArrayList;

import entity.TaskEntity;

public class SearchModule {
    
    /**
     * Searches the hashtag, description and name of TaskEntity for a search
     * term and append it to search results
     * 
     * @param searchTerm - Sub string to locate within each items's description, name and hashtag 
     * @param listToSearch - ArrayList to carry out the search on
     * @param searchResults - ArrayList to place results into
     */
    public static void searchStringAddToResults (String searchTerm, ArrayList<TaskEntity> listToSearch, ArrayList<TaskEntity> searchResults) {
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        
        for (int i = 0; i < listToSearch.size(); i++) {
            if (listToSearch.get(i).getName() == null || listToSearch.get(i).getDescription() == null
                    || listToSearch.get(i).getHashtags() == null) {
            System.out.println("Unable to search current term");
                continue;
            }
            
            if ( listToSearch.get(i).getName().toLowerCase().contains(lowerCaseSearchTerm) ) {
                searchResults.add(listToSearch.get(i));
            } else if ( listToSearch.get(i).getDescription().toLowerCase().contains(lowerCaseSearchTerm) ) {
                searchResults.add(listToSearch.get(i));
            } else if ( listToSearch.get(i).getHashtags().toLowerCase().contains(lowerCaseSearchTerm) ) {
                searchResults.add(listToSearch.get(i));
            } 
        }
    }
}