/**
 * @author qy
 * @@author :a0125493a
 *
 *          File used by TaskEntityManager to search for terms in its list
 */
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
        
        String[] searchTerms = lowerCaseSearchTerm.split(" ");
        
        for (int i = 0; i < listToSearch.size(); i++) {
            
            if (listToSearch.get(i).getName() == null || listToSearch.get(i).getDescription() == null
                    || listToSearch.get(i).getHashtags() == null) {
                System.out.println("ERROR in searchModule: Certain search fields null");
                //Skip searching this faulty task
                continue;
            }
            
            boolean searchTermFound = false;

            searchTermFound = searchAllTerms(listToSearch, searchTerms, i);

            if (searchTermFound) {
                searchResults.add(listToSearch.get(i));
            }
        }
    }

    private static boolean searchAllTerms(ArrayList<TaskEntity> listToSearch, String[] searchTerms, int i) {
        boolean searchTermFound = false;
        
        for (int j = 0; j < searchTerms.length; j++) {
            searchTermFound = false;
            
            if (listToSearch.get(i).getName().toLowerCase().contains(searchTerms[j])
                    || listToSearch.get(i).getDescription().toLowerCase().contains(searchTerms[j])) {
                searchTermFound = true;
            } else if (listToSearch.get(i).getHashtags().toLowerCase().contains(searchTerms[j])) {
                if (checkHashMatch(searchTerms[j], listToSearch.get(i))) {
                    searchTermFound = true;
                } else {
                    // Hashtag not found. Conclude as not added by breaking the
                    // loop, effctively leaving searchTermFound as false
                    break;
                }
            } else {
                // One of the words/terms not found. Conclude as not added
                // by breaking the loop, effectively leaving searchTermFound as false
                break;
            }
        }
        
        return searchTermFound;
    }
    
    /**
     * If the term searched is a hashtag, check if it matches exactly
     * 
     * @param searchTerm - Search term passed in
     * @param currentItemSearched - Item that is being searched for the hashtag
     * @return true if there is an exact match in the list of hashtags in the
     *         currentItemSearched
     *         false otherwise
     */
    public static boolean checkHashMatch(String searchTerm, TaskEntity currentItemSearched) {
        //Length set as 2 as minimum hash is a # with one character. No empty hashtags allowed
        if (searchTerm.length() >= 2) {
            //Checks if current search term is a hashtag
            if(searchTerm.charAt(0) == '#') {
                String hashTerm = searchTerm.substring(1);
                String[] itemHashes = currentItemSearched.getHashtags().split("#");

                for (int i = 0; i < itemHashes.length; i++) {
                    if (itemHashes[i].equalsIgnoreCase(hashTerm)) {
                        return true;
                    }
                }
                // If none of the hashes match
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
}

