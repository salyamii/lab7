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
            if(pop.get((long)city.getValue().getPopulation()) == null){
                long key = city.getValue().getPopulation();
                pop.put(key, (long) 1);
            }
            else{
                Long popul = pop.get((long) city.getValue().getPopulation());
                pop.put((long) city.getValue().getPopulation(), popul + 1);
            }
        }
        if(pop.entrySet().isEmpty()){
            return "There are no cities in collection.";
        }
        for(Map.Entry<Long, Long> population : pop.entrySet()){
            result += "Cities with population: " + population.getKey() + " amount: " + population.getValue() + ";\n";
        }
        return result;
    }
}
