public class House {
    // final vars that used for comparing with girl and knowing the location of house
    private final Location location = Location.GLADE;
    private final int height = 500;
    public House(){
    }
    // getter method to use the height of house
    public int getHeight(House house){
        return house.height;
    }
}
