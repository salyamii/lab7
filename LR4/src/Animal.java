abstract public class Animal extends Creature{
    String species;
    // constructor for Animal
    public Animal(Location location, String species) {
        super(location);
        this.species = species;
    }
}
