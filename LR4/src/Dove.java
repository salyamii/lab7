public class Dove extends Animal{
    private int amountOfEggs;
    // constructor for Dove
    public Dove(Location location, String species, int amountOfEggs){
        super(location, species);
        // taken from superclass Animal (species) and Creature (location)
        this.amountOfEggs = amountOfEggs;
    }
    public void zeroAmountOfEggs(){
        // changing the amount of eggs for "catch"
        amountOfEggs = 0;
    }
    public int getAmountOfEggs() throws AmountOfEggsException{
        // return amount of eggs to decide is it ok or not
        if(amountOfEggs > 0){
            return amountOfEggs;
        }
        else{
            throw new AmountOfEggsException(); // if not normal, throw an exception
        }
    }
    public void dudgeon(Girl girl){ // method that represent the complaining at the beginning. Checks the place of a girl
                                    // so if she isn't around the nest no reason to worry.
        if(girl.getLocation() == Location.NEST){
            System.out.println("Dove: Go away! Don't touch my eggs!");
        }
        else{
            System.out.println("Dove sleeps at the eggs");
        }
    }
}
