// @@author A0125415N
package parser;

import java.util.Scanner;

public class IdParser {
    private static final String ID_CONST = "ID";
    private String id;
    private String id2;
    private final String TWO_ID_REGEX = "[I][D][0-9a-zA-Z]+[-][I][D][0-9a-zA-Z]+";

    public IdParser() {
        id = null;
        id2 = null;
    }

    public String getId(String input) {
        if (hasIdKey(input)) {
            Scanner sc = new Scanner(input);
            while (sc.hasNext()) {
                String test = sc.next();
                if (hasIdKey(test)) {
                    if (test.matches(TWO_ID_REGEX)) {
                        setId1And2(test);
                    } else {
                        id = test.substring(2, test.length());
                    }
                }
            }
        }
        return id;
    }

    private void setId1And2(String test) {
        String[] temp = test.split("-");
        id = temp[0].substring(2, temp[0].length());
        id2 = temp[1].substring(2, temp[1].length());
    }

    public Pair<String, String> getLinkId(String input) {
        getId(input);
        return new Pair<String, String>(id, id2);
    }

    public String xmlId(String input) {
        getId(input);
        if (id2 != null) {
            input = input.replace(ID_CONST + id + "-" + ID_CONST + id2, "<" + XMLParser.ID_TAG + ">"
                    + ID_CONST + id + "-" + ID_CONST + id2 + "</" + XMLParser.ID_TAG + ">");
        } else if (id != null) {
            input = input.replace(ID_CONST + id,
                    "<" + XMLParser.ID_TAG + ">ID" + id + "</" + XMLParser.ID_TAG + ">");
        }

        return input;
    }

    private boolean hasIdKey(String input) {
        if (input.contains(ID_CONST)) {
            return true;
        } else {
            return false;
        }
    }
}
