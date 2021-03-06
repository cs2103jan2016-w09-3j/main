// @@author A0125415N
package parser;

public class CommandParser {

    public enum COMMAND {
        ADD, EDIT, DELETE, EXIT, MAIN, HIDE, SHOW, FLOAT, SEARCH, JUMP, LINK, DONE, SAVETO, LOADFROM, UNDO, THEME, INVALID;
    };

    public CommandParser() {

    }

    /**
     * takes in a string and retuns the related command
     * 
     * @param input
     * @return COMMAND enum
     */
    public COMMAND getCommand(String input) {
        String inputCmd = ParserCommons.getFirstWord(input);
        COMMAND returnVal = COMMAND.INVALID;
        if (inputCmd.equalsIgnoreCase("add")) {
            returnVal = COMMAND.ADD;
        } else if (inputCmd.equalsIgnoreCase("edit")) {
            returnVal = COMMAND.EDIT;
        } else if (inputCmd.equalsIgnoreCase("delete")) {
            returnVal = COMMAND.DELETE;
        } else if (inputCmd.equalsIgnoreCase("exit")) {
            returnVal = COMMAND.EXIT;
        } else if (inputCmd.equalsIgnoreCase("main")) {
            returnVal = COMMAND.MAIN;
        } else if (inputCmd.equalsIgnoreCase("hide")) {
            returnVal = COMMAND.HIDE;
        } else if (inputCmd.equalsIgnoreCase("show")) {
            returnVal = COMMAND.SHOW;
        } else if (inputCmd.equalsIgnoreCase("float")) {
            returnVal = COMMAND.FLOAT;
        } else if (inputCmd.equalsIgnoreCase("search")) {
            returnVal = COMMAND.SEARCH;
        } else if (inputCmd.equalsIgnoreCase("jump")) {
            returnVal = COMMAND.JUMP;
        } else if (inputCmd.equalsIgnoreCase("link")) {
            returnVal = COMMAND.LINK;
        } else if (inputCmd.equalsIgnoreCase("done")) {
            returnVal = COMMAND.DONE;
        } else if (inputCmd.equalsIgnoreCase("saveto")) {
            returnVal = COMMAND.SAVETO;
        } else if (inputCmd.equalsIgnoreCase("undo")) {
            returnVal = COMMAND.UNDO;
        } else if (inputCmd.equalsIgnoreCase("theme")) {
            returnVal = COMMAND.THEME;
        } else if (inputCmd.equalsIgnoreCase("loadfrom")) {
            returnVal = COMMAND.LOADFROM;
        }
        return returnVal;
    }

    /**
     * takes the first word and adds XML to it
     * 
     * @param input
     * @return xml of input
     */
    public String xmlFirstWord(String input) {
        String inputFirstWord = ParserCommons.getFirstWord(input);
        String returnStrVal = input;
        if (!inputFirstWord.trim().equals("")) {
            returnStrVal = input.substring(inputFirstWord.length());
            returnStrVal = "<" + XMLParser.CMD_TAG + ">" + inputFirstWord + "</" + XMLParser.CMD_TAG + ">"
                    + returnStrVal;
        }
        return returnStrVal;
    }
}
