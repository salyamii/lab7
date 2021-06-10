package server.data;
import server.data.City;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Shkurenko Nikita
 * @version 1.0
 * Class for describing a collection in a marshalling-ready view
 */

@XmlRootElement(name = "cities")
@XmlAccessorType(XmlAccessType.NONE)
public class Cities {
    /** Field cities - list for keeping collection */
    @XmlElement(name = "city")
    private List<City> cities = null;

    /** Method for getting a cities list
     * @return List<City> cities
     */
    public List<City> getCities(){
        return cities;
    }

    public void setCities(ArrayList<City> cities){
        this.cities = cities;
    }
}
