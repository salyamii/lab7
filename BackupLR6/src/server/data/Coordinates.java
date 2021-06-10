package server.data;

import javax.xml.bind.annotation.*;

/**
 * @author Shkurenko Nikita
 * @version 1.0
 * Class for describing field: Coordinates of element
 */
@XmlRootElement(name = "coordinates")
@XmlAccessorType(XmlAccessType.NONE)
public class Coordinates {
    @XmlElement
    private float x; //Значение поля должно быть больше -944
    @XmlElement
    private int y;

    /**
     * Constructor for making a Coordinates field
     * @param x - coordinate x
     * @param y - coordinate y
     */
    public Coordinates (float x, int y) {
        this.x = x;
        this.y = y;
    }
    /* Default constructor */
    public Coordinates() {}

    /* Method for printing */
    @Override
    public String toString() {
        return "Coordinates: x = " + x + " ; y = " + y;
    }

    public float getX(){
        return x;
    }
    public void setX(float x){
        this.x = x;
    }

    public int getY(){
        return y;
    }
    public void setY(int y){
        this.y = y;
    }
}

