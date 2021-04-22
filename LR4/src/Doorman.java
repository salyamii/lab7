public class Doorman extends Person{
    private String appearance;
    private Position position;
    private boolean livery;
    private String takenInformation;
    // constructor for Doorman
    public Doorman(Location location, String name, Gender gender, String appearance, Position position, boolean livery){
        super(name, gender, location);
        this.appearance = appearance;
        this.position = position;
        this.livery = livery;
    }
    // setter for Doorman's Position
    public void setPosition(Position position) {
        this.position = position;
    }
    // setter for information, taken from Lackey.
    public void setTakenInformation(String takenInformation) {
        this.takenInformation = takenInformation;
    }
    // method, that uses anonymous class
    public void doingNothing(){
        // creating object of Stupidity interface that shows action of doorman
        Stupidity repeatingPhrase = new Stupidity() {
            public void lookingAtTheSky() {
                System.out.println("How many days do I need to spend there? One, two... or more?");
            }
        };
        // calling method created with anonymous class
        repeatingPhrase.lookingAtTheSky();
    }
}
