package server.data;

import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Shkurenko Nikita
 * @version 1.0
 * Class for describing: Parameters of a city
 */
@XmlRootElement(name = "city")
@XmlAccessorType(XmlAccessType.NONE)
public class City {
    /** Field ID */
    @XmlElement
    private long id;
    /** Field name
     *  */
    @XmlElement
    private String name;
    /** Field coordinates */
    @XmlElement
    private Coordinates coordinates;
    /** Field creationDate */
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @XmlElement
    private java.time.LocalDateTime creationDate;
    /** Field area */
    @XmlElement
    private double area; //Значение поля должно быть больш
    /** Field population */
    @XmlElement
    private int population; //Значение поля должно быть больше 0
    /** Field metersAboveSeaLevel */
    @XmlElement
    private float metersAboveSeaLevel;
    /** Field establishmentDate */
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    @XmlElement
    private java.time.LocalDate establishmentDate;
    /** Field telephoneCode */
    @XmlElement
    private int telephoneCode; //Значение поля должно быть больше 0, Максимальное значение поля: 100000
    /** Field climate */
    @XmlElement
    private Climate climate; //Поле может быть null
    /** Field governor */
    @XmlElement
    private Human governor; //Поле может быть null

    /** Constructor for creating a City
     * @param id
     * @param name
     * @param coordinates
     * @param creationDate
     * @param area
     * @param population
     * @param metersAboveSeaLevel
     * @param establishmentDate
     * @param telephoneCode
     * @param climate
     * @param governor
     */

    public City(long id, String name, Coordinates coordinates, LocalDateTime creationDate, double area, int population,
                float metersAboveSeaLevel, LocalDate establishmentDate, int telephoneCode, Climate climate,
                Human governor){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.creationDate = creationDate;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.establishmentDate = establishmentDate;
        this.telephoneCode = telephoneCode;
        this.climate = climate;
        this.governor = governor;
    }
    /** Default constructor */
    public City() {}

    /** Method for printing City-class object into String representation */
    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "name: " + name + "\n" +
                "coordinates: " + coordinates + "\n" +
                "area: " + area + "\n" +
                "creationDate: " + creationDate.format(dateFormat) + "\n" +
                "population: " + population + "\n" +
                "metersAboveSeaLevel: " + metersAboveSeaLevel + "\n" +
                "establishmentDate: " + establishmentDate + "\n" +
                "telephoneCode: " + telephoneCode + "\n" +
                "climate: " + climate + "\n" +
                "governor: " + governor + "\n";

    }

    public long getId(){
        return this.id;
    }
    public void setId(long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Coordinates getCoordinates(){return coordinates;}
    public void setCoordinates(Coordinates coordinates){this.coordinates = coordinates;}

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public LocalDateTime getCreationDate(){return creationDate;}
    public void setCreationDate(LocalDateTime creationDate){this.creationDate = creationDate;}

    public double getArea(){return area;}
    public void setArea(double area){this.area = area;}

    public int getPopulation(){return  population;}
    public void setPopulation(int population){this.population = population;}

    public float getMetersAboveSeaLevel(){return metersAboveSeaLevel;}
    public void setMetersAboveSeaLevel(float metersAboveSeaLevel){this.metersAboveSeaLevel = metersAboveSeaLevel;}

    public LocalDate getEstablishmentDate(){return establishmentDate;}
    public void setEstablishmentDate(LocalDate establishmentDate){this.establishmentDate = establishmentDate;}

    public int getTelephoneCode(){return telephoneCode;}
    public void setTelephoneCode(int telephoneCode){this.telephoneCode = telephoneCode;}

    public Climate getClimate(){return climate;}
    public void setClimate(Climate climate){this.climate = climate;}

    public Human getGovernor(){return governor;}
    public void setGovernor(Human governor){this.governor = governor;}

}
