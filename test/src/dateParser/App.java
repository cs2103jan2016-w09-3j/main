package dateParser;
import java.util.Date;
import java.util.List;


import java.util.Map;
import java.util.Scanner;


import com.joestelmach.natty.*;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Parser parser = new Parser();
        parser.parse("today");
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            
            String temp = sc.nextLine();
            List <DateGroup> groups = parser.parse(temp);
            System.out.println("test");
            for(DateGroup group:groups) {
                List dates = group.getDates();
                int line = group.getLine();
                int column = group.getPosition();
                String matchingValue = group.getText();
                String syntaxTree = group.getSyntaxTree().toStringTree();
                boolean isRecurreing = group.isRecurring();
                Date recursUntil = group.getRecursUntil();
                System.out.println("test2");
                for (int i=0; i<dates.size(); i++){
                    System.out.println("test3");
                    System.out.println(dates.get(i).toString());
                }
              }
        }
        

        
    }
}
