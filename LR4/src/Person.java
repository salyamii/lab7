abstract public class Person extends Creature{
    private String name;
    private Gender gender;
    // constructor for Person
    public Person(String name, Gender gender, Location location){
        super(location);
        this.name = name;
        this.gender = gender;
    }
    // method that change the gender if needed
    public void replaceGender(){
        if(this.gender == Gender.FEMALE)
            this.gender = Gender.MALE;
        else
            this.gender = Gender.FEMALE;
    }
    // getter of name method
    public String getName(){
        return name;
    }
    // getter of gender method
    public Gender getGender(){
        return gender;
    }

    @Override
    // overriding toString() from java.lang.Objects to represent stats of object.
    public String toString() {
        return "Person: \n Name: " + getName() + "\n Gender: " + getGender();
    }
}
