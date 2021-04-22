public class Lackey  extends Person implements Knock{
    private final String appearance;
    private final  boolean livery;
    // constructor for Lackey
    public Lackey(Location location, String name, Gender gender, String appearance, boolean livery){
        super(name, gender, location);
        this.appearance = appearance;
        this.livery = livery;
    }
    // isLivery shows user if it is a usual Lackey, or livery.
    public String isLivery(){
        if(livery){
            return "It's a livery lackey";
        }
        else
            return "It's a usual lackey";
    }
    // realization of knock from Knock interface.
    @Override
    public void knock() {
        System.out.println("Lackey rapidly knocked at the door");
    }
    // inner class Message that provides us a inner variable that connected to an outer class Lackey.
    public class Message{
        private String message;
        Message(String message){
            this.message = message;
        }
        // getter method for our message.
        public String getMessage() {
            return message;
        }
    }

    // used inner class variable, that contains message from queen, object doorman, girl and lackey with we are making actions
    public void delivery(Lackey.Message lackeyMessage, Doorman doorman, Girl girl, Lackey lackey){
        System.out.println("Lackey ran out of the forest and came to the door");
        setLocation(Location.GLADE);
        // setter needed to change the location as in the model. Located in class Creature.
        knock();
        System.out.println("Doorman opened and the door and came out.");
        doorman.setPosition(Position.OUTSIDE);
        // method setPosition change position of object. Located in class Doorman.
        doorman.setLocation(Location.GLADE);
        // method setPosition change location of object. Located in class Doorman.
        System.out.println("Lackey tell the information from the message to Doorman");
        doorman.setTakenInformation(lackeyMessage.getMessage());
        // using the information from a lackey's message, so doorman knows it and has own var for it.
        // var and method located in class Doorman.
        System.out.println(doorman.getName() + ": message accepted \n Both made strange funny regards.");
        System.out.println(girl.getName() + " see it and starting laughing so she decided to run into the forest.");
        girl.setLocation(Location.FOREST);
        // changing location for a girl, method located in Creature.
        System.out.println("After a few minutes " + girl.getName() +
                " came back to the glade, but there wad only doorman. \n Seems that lackey ran into the forest.");
        lackey.setLocation(Location.FOREST);
        // changing location for a lackey, method located in Creature
        girl.setLocation(Location.GLADE);
        // changing location for a girl, method located in Creature.
        System.out.println(girl.getName() + " moved closer to the house.");
    }
}
