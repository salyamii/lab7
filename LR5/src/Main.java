import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Collection manager started.");
        Commander commander;
        try{
            commander = new Commander(new CollectionManager(args[0]));
        }
        catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            System.out.println("Path to the file is absent. Insert it, please.");
            commander = new Commander(new CollectionManager(""));
        }
        commander.interactiveMode();
    }
}
