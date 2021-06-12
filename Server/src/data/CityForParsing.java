package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CityForParsing")
@XmlAccessorType(XmlAccessType.NONE)
public class CityForParsing {
    /** Field ID */
    @XmlElement
    private long id;
    /** Field name */
    @XmlElement
    private String name;
    /** Field coordinates */
    @XmlElement
    private Coordinates coordinates;
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
    @XmlElement(name = "establishmentDate")
    private  String establishmentDate;
    /** Field telephoneCode */
    @XmlElement
    private int telephoneCode; //Значение поля должно быть больше 0, Максимальное значение поля: 100000
    /** Field climate */
    @XmlElement
    private Climate climate; //Поле может быть null
    /** Field governor */
    @XmlElement
    private HumanForParsing governor; //Поле может быть null

    //private String creationDate;

    /** Constructor for creating a City
     * @param id
     * @param name
     * @param coordinates
     * @param area
     * @param population
     * @param metersAboveSeaLevel
     * @param establishmentDate
     * @param telephoneCode
     * @param climate
     * @param governor
     */

    public CityForParsing(long id, String name, Coordinates coordinates, double area, int population,
                          float metersAboveSeaLevel, String establishmentDate, int telephoneCode, Climate climate,
                          HumanForParsing governor){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.establishmentDate = establishmentDate;
        this.telephoneCode = telephoneCode;
        this.climate = climate;
        this.governor = governor;
    }
    public CityForParsing(long id, String name, Coordinates coordinates, String creationDate, double area, int population,
                          float metersAboveSeaLevel, String establishmentDate, int telephoneCode, Climate climate,
                          HumanForParsing governor){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        //this.creationDate = creationDate;
        this.area = area;
        this.population = population;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.establishmentDate = establishmentDate;
        this.telephoneCode = telephoneCode;
        this.climate = climate;
        this.governor = governor;
    }
    public CityForParsing(){};

    //public String getCreationDate(){ return creationDate;}

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

    public double getArea(){return area;}
    public void setArea(double area){this.area = area;}

    public int getPopulation(){return  population;}
    public void setPopulation(int population){this.population = population;}

    public float getMetersAboveSeaLevel(){return metersAboveSeaLevel;}
    public void setMetersAboveSeaLevel(float metersAboveSeaLevel){this.metersAboveSeaLevel = metersAboveSeaLevel;}

    public String getEstablishmentDate(){return establishmentDate;}
    public void setEstablishmentDate(String establishmentDate){this.establishmentDate = establishmentDate;}

    public int getTelephoneCode(){return telephoneCode;}
    public void setTelephoneCode(int telephoneCode){this.telephoneCode = telephoneCode;}

    public Climate getClimate(){return climate;}
    public void setClimate(Climate climate){this.climate = climate;}

    public HumanForParsing getGovernor(){return governor;}
    public void setGovernor(HumanForParsing governor){this.governor = governor;}
}
