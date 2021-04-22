public class Girl extends Person implements Sneezing, Knock, Mushrooms {
    private String appearance;
    private int height;
    private Position position;
    private String mood;
    // constructor for Girl
    public Girl(Location location, String name, Gender gender,
                String appearance, int height, Position position, String mood) {
        super(name, gender, location);
        this.appearance = appearance;
        this.height = height;
        this.position = position;
        this.mood = mood;
    }
    // getter for girl's height
    public int getHeight() {
        return height;
    }
    // method verify the gender for a girl, otherwise throws an exception
    public boolean isGenderCorrect() throws GenderException {
        if (getGender() == Gender.FEMALE) {
            return true;
        } else {
            throw new GenderException();
        }
    }

    public void conversationWithDoveAndWayToGlade(Girl girl) {
        //
        System.out.println(getName() + ": LEAVING!");
        girl.setLocation(Location.FOREST);
        // when leaving changing location to forest
        System.out.println(getName() + " in " + girl.getLocation().toString());
        // stats where a girl is
        System.out.println(getName() + " is trying to get away from the forest.");
        girl.setLocation(Location.GLADE);
        // got away from the forest and came to the glade
        System.out.println(getName() + " in " + girl.getLocation().toString());
        System.out.println(getName() + ": I need to become as I was.");
        // a girl decide to grow with magic mushrooms
        while (girl.height != 170) {
            // the process of growing
            if (girl.height > 170) {
                System.out.println(getName() + " takes some bites of THAT mushroom.");
                while(girl.height > 170)
                    girl.height = decreaseHeightMushroom(girl.height);
                // if upper 170 decreases the height.
                // method implemented by this class so and the realization is there. Near the end of code.
            } else if(girl.height < 170) {
                System.out.println(getName() + " takes some bites of ANOTHER mushroom.");
                while(girl.height < 170)
                    // if under 170 - increasing, realization as the previous in the end of code
                    girl.height = increaseHeightMushroom(girl.height);
            }
        }
        System.out.println(girl.getName() + "'s height is: " + girl.getHeight());
        // what height did we get, typical getter
    }

    public void noticedTheHouse(House house, Girl girl){
        // using a house with final parameters in it (height and location)
        System.out.println(getName() + ": Oh, I want to get in this house!");
        System.out.println(getHeightAtTheMoment(girl).height);
        // printed parameter of static variable from static inner class
        if(girl.height > house.getHeight(house)/7){
            // skipping a bit and just setting needed height for girl.
            System.out.println(getName() + ": Oh no, I'm too tall! \n " + getName() + " changes her height with mushrooms " +
                    "\n " + getName() + "'s height now: " + house.getHeight(house)/7);
            girl.height = house.getHeight(house)/7;
            // I decided that height of house divided on 7 is enough.
        }
        // if shorter than needed correcting her height.
        else{
            System.out.println(getName() + ": Oh no, I'm too short! \n " + getName() + " changes her height with mushrooms " +
                    "\n " + getName() + "'s height now: " + house.getHeight(house)/7);
            girl.height = house.getHeight(house)/7;
        }
    }
    public void nearTheHouse(Doorman doorman, Girl girl, Duchess duchess, Kid kid, LadyCook ladyCook){
        // used objects: Doorman, Girl, Duchess, Kid, LadyCook.
        System.out.println(girl.getName() + " is in front of the door.");
        girl.knock();
        // method knock represent knocking of a girl.
        // Located in Girl
        System.out.println(doorman.getName() + ": There is no point in knocking, " +
                "because we are behind the door and it's noisily inside");
        duchess.sneeze();
        kid.scream();
        kid.sneeze();
        ladyCook.crackAndRing();
        // methods represents the loud behind the door.
        // Located in Duchess, Kid, LadyCook.
        System.out.println(girl.getName() + ": What can I do to come in?");
        System.out.print(doorman.getName() + ": ");
        doorman.doingNothing();
        // methods uses anonymous realization of interface, represents action of doorman, when he ignored a girl.
        // Located in Doorman.
        System.out.println(girl.getName() + ": I'm sorry, what can i do to come in?");
        System.out.println(doorman.getName() + ": If you were on the other side of the door," +
                " I would open it for you..");
        System.out.print(doorman.getName() + ": ");
        doorman.doingNothing();
        // Same method as above.
        System.out.println("Door opened and some dish was threw in the doorman," +
                " but luckily it didn't make any harm to him");
        System.out.println(girl.getName() + ": HOW CAN I COME IN?");
        System.out.println(doorman.getName() + ": Who said that you should enter the house?");
        System.out.println("Because of doorman's answers girl became angry");
        girl.mood = "angry";
        // changing the mood of a girl as an answer on conversation
        System.out.println(girl.getName() + ": So what can I do!?");
        System.out.println(doorman.getName() + ": Anything you want.");
        System.out.println(girl.getName() + " opened the door by herself and entered the house");
        girl.setLocation(Location.HOUSE);
        // changing location for girl
        // Located in Creature.
        girl.position = Position.INSIDE;
        // changing position because she entered the house.
    }
    public void inHouse(Girl girl, Duchess duchess, Kid kid, Cat cat, LadyCook ladyCook){
        // used objects: Girl, Duchess, Kid, Car, LadyCook.
        System.out.println(girl.getName() + " entering the kitchen. " +
                "\n There is very dimly and noisy. She smell a lot of pepper " +
                "\n In the center of the room sit Duchess with kid on her laps");
        girl.sneeze();
        duchess.sneeze();
        kid.scream();
        kid.sneeze();
        ladyCook.crackAndRing();
        // methods represent loud that are made by girl, duchess, kid and lady cook.
        // located in classes: Girl, Duchess, Kid, LadyCook.
        System.out.println("Haven't been sneezing only the lady cook and a cat, that was laying near the bake.");
        cat.smile();
        // represent cat's action in house.
        // located in class Cat.
        ladyCook.makeSoup();
        // represent lady cook's action in house.
        // located in class LadyCook.
    }
    public static class Height{
        // static inner class, allows to contain height of girl at the moment as var Height.
        private int height;
        public Height(int height){
            this.height = height;
        }
    }
    public static Height getHeightAtTheMoment(Girl girl){
        // uses static inner class
        return new Height(girl.getHeight());
    }
    @Override
    // realization of method from Mushrooms interface
    public int increaseHeightMushroom(int height) {
        return height + 8;
    }

    @Override
    // realization of method from Mushrooms interface
    public int decreaseHeightMushroom(int height) {
        return height - 5;
    }

    @Override
    // realization of method from Sneezing interface
    public void sneeze() {
        System.out.println("Girl sneezed");
    }

    @Override
    // realization of method from Knock interface
    public void knock() {
        System.out.println("Girl knocked");
    }

    @Override
    // overriding toString from java.lang.Object class
    public String toString() {
        return "Girl: \n Name: " + getName() + "\n Gender: " + getGender() + "\n Height: " + getHeight();
    }
}