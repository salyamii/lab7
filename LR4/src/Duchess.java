public class Duchess extends Person implements Sneezing{
    private Position position;
    // constructor for Duchess
    public Duchess(String name, Gender gender, Location location, Position position){
        super(name, gender, location);
        this.position = position;
    }
    // realization of method from Sneeze interface
    public void sneeze(){
        System.out.println("Duchess sneezed");
    }
}
