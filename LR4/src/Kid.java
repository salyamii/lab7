public class Kid extends Person implements Sneezing{
    private Position position;
    // constructor for Kid
    public Kid(String name, Gender gender, Location location, Position position){
        super(name, gender, location);
        this.position = position;
    }
    // method represents scream of Kid
    public void scream(){
        System.out.println("Kid is screaming");
    }

    @Override
    // realization of method, from Sneezing interface. Represents sneezin of Kid
    public void sneeze() {
        System.out.println("Kid stopped screaming, sneezed and continued screaming");
    }
}
