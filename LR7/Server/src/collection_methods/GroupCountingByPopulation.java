package collection_methods;

import server_base.*;
import server_base.CollectionAdministrator;
import data.City;

import java.util.HashMap;
import java.util.Map;

public class GroupCountingByPopulation extends SimpleMethod{
    public GroupCountingByPopulation(CollectionAdministrator administrator){
        super(administrator);
    }

    @Override
    public String run() {
        HashMap<Long, Long> pop = new HashMap<Long, Long>();
        String result = "";
        for(Map.Entry<Long, City> city : getAdministrator().getCities().entrySet()){
            if(pop.get(city.getKey()) == null){
                long key = city.getKey();
                pop.put(key, (long) 1);
            }
            else{
                pop.put(city.getKey(), pop.get(city.getKey() + 1));
            }
        }
        if(pop.entrySet().isEmpty()){
            return "There are no cities in collection.";
        }
        for(Map.Entry<Long, Long> population : pop.entrySet()){
            result += "City: " + population.getKey() + " has population: " + population.getValue() + ";\n";
        }
        return result;
    }
}
