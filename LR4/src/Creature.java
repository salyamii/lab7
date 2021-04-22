abstract public class Creature {
    private Location location;
    // constructor for Creature
    public Creature(Location location) {
        this.location = location;
    }
    // getter method to know the location of object
    public Location getLocation(){
        return location;
    }
    // setter method to change the location of object
    public void setLocation(Location location) {
        this.location = location;
    }
}
