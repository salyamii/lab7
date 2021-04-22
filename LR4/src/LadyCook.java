public class LadyCook extends Person{
    private Position position;
    // constructor for Lady Cook
    public LadyCook(String name, Gender gender, Location location, Position position){
        super(name, gender, location);
        this.position = position;
    }
    // method that uses local class that contains method we used to represent cracking and ringing.
    public void makeSoup(){
        class Soup{
            String action = "Lady Cook making soup";
            void makeAction(){
                System.out.println(action);
            }
        }
        Soup soup = new Soup();
        soup.makeAction();
    }
    public void crackAndRing(){
        System.out.println("The dishes are cracking and ringing");
    }
}
