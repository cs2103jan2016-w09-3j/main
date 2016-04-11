// @@author A0125415N
package parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import entity.TaskEntity;
import parser.CommandParser.COMMAND;

public class InputParser {
    private String input;
    private DateParser dateParser;
    private CommandParser cmdParser;
    private InformationParser infoParser;
    private IdParser idParser;
    private GarbageCollectorParser gcParser;

    // private static Logger logger = Logger.getLogger("InputParser");

    /**
     * Intializes parser and creates individual command parsers
     * 
     * @param input String to be parsed
     */
    public InputParser(String input) {
        /**
         * try{
         * Handler fh = new FileHandler("inputParser.log");
         * logger.addHandler(fh);
         * logger.setLevel(Level.FINEST);
         * }catch(IOException e){
         * 
         * }
         * logger.log(Level.INFO,"InputParser init");
         * 
         **/
        input = XMLParser.removeAllTags(input);
        setInput(input);
        dateParser = new DateParser();
        cmdParser = new CommandParser();
        infoParser = new InformationParser();
        idParser = new IdParser();
        gcParser = new GarbageCollectorParser();
    }

    /**
     * changes input to XML form
     */
    public void addXML() {
        // DO NOT CHANGE ORDER!!
        // logger.log(Level.INFO,"Add xml to input");
        assert (input != null) : "Input is null";

        input = XMLParser.removeAllTags(input);

        COMMAND given = cmdParser.getCommand(input);
        // System.out.println("before"+input);
        addXMLDate();// problem with quotes lie here!
        // System.out.println("after date"+input);
        addXMLCmd();
        // System.out.println("after cmd"+input);
        if ((given != COMMAND.SEARCH) && (given != COMMAND.SAVETO) && (given != COMMAND.THEME)
                && (given != COMMAND.LOADFROM)) {
            addXMLID();
            // System.out.println("after ID"+input);
            addXMLTitleDesc();
            // System.out.println("after title"+input);
        }
        addXMLGarbage();
        // System.out.println("after"+input);

    }

    public void removeId() {
        input = XMLParser.removeAllTags(input);
        // System.out.println("removeId1 "+input);
        addXMLID();
        input = XMLParser.removeAllAttributes(input);
        // System.out.println("removeId2 "+input);

    }

    private void addXMLGarbage() {
        input = gcParser.xmlAllOthers(input);
    }

    private void addXMLID() {
        input = idParser.xmlID(input);
    }

    public String getSearchString() {
        input = XMLParser.removeAllTags(input);
        addXMLCmd();
        input = XMLParser.removeAllAttributes(input);
        return input.trim();
    }

    /**
     * adds xml to title
     */
    private void addXMLTitleDesc() {
        input = gcParser.xmlHash(input);
        input = infoParser.xmlTitleAndDesc(input);
    }

    /**
     * adds xml to date
     */
    private void addXMLDate() {
        // System.out.println("before xml"+ input);
        input = dateParser.xmlDate(input);
        // System.out.println("after xml2"+ input);
    }

    /**
     * adds xml to cmd(first word)
     */
    private void addXMLCmd() {
        input = cmdParser.xmlFirstWord(input);
    }

    /**
     * getter for input
     * 
     * @return input
     */
    public String getInput() {
        return input;
    }

    /**
     * setter for input
     * 
     * @param input
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * Parses the input and returns an enum COMMAND
     * 
     * @return respective COMMAND
     */
    public COMMAND getCommand() {
        COMMAND returnEnum = cmdParser.getCommand(input);
        return returnEnum;
    }

    /**
     * generates a list of tasks from the input, with no time
     * 
     * @return ArrayList of tasks
     */
    public ArrayList<TaskEntity> getTask() {
        input = XMLParser.removeAllTags(input);
        if (input.trim().equalsIgnoreCase("edit")) {
            return new ArrayList<TaskEntity>();
        }
        ArrayList<TaskEntity> tasks = new ArrayList<TaskEntity>();
        if (input.trim().equals("")) {
            // logger.log(Level.WARNING, "Input is empty", new
            // IllegalArgumentException("input is empty"));
        } else {
            // System.out.println("TEST"+input);
            List<Date> dates = dateParser.parseToList(input);
            // System.out.println("TEST"+input);
            addXMLDate();
            addXMLCmd();
            input = gcParser.xmlHash(input);
            ArrayList<String> hashes = gcParser.getHashes();
            infoParser.setInformation(this.input);
            String name = infoParser.getTitle();
            String desc = infoParser.getDescription();
            if (dates.size() == 0) {
                TaskEntity toAdd = new TaskEntity(name, desc);
                toAdd.setFloating(true);
                for (int i = 0; i < hashes.size(); i++) {
                    toAdd.addHashtag(hashes.get(i));
                }
                tasks.add(toAdd);
            } else if (dates.size() == 2) {

                Calendar c = Calendar.getInstance();
                c.setTime(dates.get(0));
                c.clear(Calendar.SECOND);
                c.clear(Calendar.MILLISECOND);

                Calendar c2 = Calendar.getInstance();
                c2.setTime(dates.get(1));
                c2.clear(Calendar.SECOND);
                c2.clear(Calendar.MILLISECOND);
                /*
                 * System.out.println("START"+c.getTime());
                 * System.out.println(c2.getTime());
                 * 
                 * if(c2.before(c)){
                 * int hour = c2.get(Calendar.HOUR_OF_DAY);
                 * int min = c2.get(Calendar.MINUTE);
                 * c2.setTime(c.getTime());
                 * c2.set(Calendar.HOUR_OF_DAY, hour);
                 * c2.set(Calendar.MINUTE, min);
                 * c2.clear(Calendar.SECOND);
                 * c2.clear(Calendar.MILLISECOND);
                 * c2.add(Calendar.DATE, 1);
                 * }
                 * 
                 * //System.out.println("FULL DAY"+checkFullDay(c,c2));
                 * 
                 * System.out.println(c.getTime());
                 * System.out.println(c2.getTime());
                 */
                TaskEntity toAdd = new TaskEntity(name, c, c2, checkFullDay(c, c2), desc);
                for (int j = 0; j < hashes.size(); j++) {
                    toAdd.addHashtag(hashes.get(j));
                }
                tasks.add(toAdd);
            } else {
                for (int i = 0; i < dates.size(); i++) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(dates.get(i));
                    c.clear(Calendar.SECOND);
                    c.clear(Calendar.MILLISECOND);
                    TaskEntity toAdd = new TaskEntity(name, null, c, false, desc);
                    for (int j = 0; j < hashes.size(); j++) {
                        toAdd.addHashtag(hashes.get(j));
                    }
                    tasks.add(toAdd);
                }
            }
        }
        return tasks;
    }

    private boolean checkFullDay(Calendar c, Calendar c2) {
        boolean isFullDay = false;
        if (c.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
            // System.out.println("year");
            if (c.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
                // System.out.println("month");
                if (c.get(Calendar.DATE) == c2.get(Calendar.DATE)) {
                    // System.out.println("day");
                    if ((c.get(Calendar.HOUR_OF_DAY) == 0) && (c2.get(Calendar.HOUR_OF_DAY) == 23)) {
                        // System.out.println("hour");
                        if ((c.get(Calendar.MINUTE) == 0) && (c2.get(Calendar.MINUTE) == 59)) {
                            // System.out.println("min");
                            isFullDay = true;
                        }
                    }
                }
            }
        }
        return isFullDay;
    }

    public String getID() {
        return idParser.getID(XMLParser.removeAllTags(input));
    }

    public Pair<String, String> getLinkID() {
        return idParser.getLinkID(XMLParser.removeAllTags(input));
    }

    public static void main(String args[]) {
        while (true) {
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            // System.out.println(ParserCommons.getLastWord(input));
            InputParser parser = new InputParser(input);
            parser.addXML();
            parser.getTask();
            System.out.println(parser.getInput());
        }
    }
}
