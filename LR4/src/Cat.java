public class Cat extends Animal {
    private Position position;
    // constructor for Cat
    public Cat(Location location, String species, Position position){
        super(location, species);
        this.position = position;
    }
    // method represents smiling action of cat
    public void smile(){
        System.out.println("Cat is beaming");
    }
}
