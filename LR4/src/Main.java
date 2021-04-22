public class Main {
    public static void main(String[] args) {
        Girl alice = new Girl(Location.NEST, "Alice", Gender.FEMALE,
                "beauty", 20, Position.OUTSIDE, "okay");
        // making a new object of Girl, it will be alice. Because of model alice is a girl. So we check it out.
        try{
            alice.isGenderCorrect();
            // method in Person that checking if gender for a girl is correct.
        }
        catch (GenderException e){
            alice.replaceGender();
            // if not correct throws my own exception that we catch and changing gender
        }
        Dove golubka = new Dove(Location.NEST, "Bird", 10);
        // making a bird, giving it some eggs. Obviously it can't be lower than 0. Checking it.
        try{
            System.out.println("Dove has: " + golubka.getAmountOfEggs() + " eggs");
            // method getAmountOfEggs is in Dove, print amount of eggs.
        }
        catch(AmountOfEggsException e)
        {
            golubka.zeroAmountOfEggs();
            // if throws an exception changing it to zero.
            System.out.println("Dove hasn't any eggs");
        }
        golubka.dudgeon(alice);
        // method that represent complain of Dove if girl near.
        // Located in class Dove.
        alice.conversationWithDoveAndWayToGlade(alice);
        // method represent conversation and way from the forest.
        // Located in class Girl
        Lackey lackey = new Lackey(Location.FOREST, "Karas", Gender.MALE, "fish", true);
        // object Lackey, set location, name, gender, appearance, livery
        Lackey.Message message = lackey.new Message("Queen is invited for a qroquet");
        // made a inner class to make an object (invitational) that is linked to lackey.
        Doorman doorman = new Doorman(Location.HOUSE, "Golovastik",
                Gender.MALE, "fish", Position.INSIDE, true);
        // object Doorman, set location, name, gender, appearance, position, livery
        House house = new House();
        // made an object to use parameters that are final in class House.
        alice.noticedTheHouse(house, alice);
        // method represent noticing and the actions after it. Located in class Girl.
        lackey.delivery(message, doorman, alice, lackey);
        // method represent conversation between doorman and lackey, delivery information, effect of conversation on girl
        // located in class Lackey
        Duchess duchess = new Duchess("Gercoginya", Gender.FEMALE, Location.HOUSE, Position.INSIDE);
        // object Duchess, set name, gender, location, position.
        Cat cat = new Cat(Location.HOUSE, "big cat", Position.INSIDE);
        // object Cat, set location, species, position.
        Kid kid = new Kid("Richard", Gender.MALE, Location.HOUSE, Position.INSIDE);
        // object Kid, set name, gender, location, position.
        LadyCook ladyCook = new LadyCook("Cooker", Gender.FEMALE, Location.HOUSE, Position.INSIDE);
        // object LadyCook, set name, gender, location, position.
        alice.nearTheHouse(doorman, alice, duchess, kid, ladyCook);
        // method represent actions of doorman, girl in front of the door. Duchess, kid and lady cook making noises behind the door.
        // located in class Girl.
        alice.inHouse(alice, duchess, kid, cat, ladyCook);
        // method represent actions of girl, duchess, kid, cat and lady cook in the house
        // located in class Girl
    }
}
