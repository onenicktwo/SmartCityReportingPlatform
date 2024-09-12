package coms309.people;


/**
 * Provides the Definition/Structure for the cars
 *
 * @author Sam Hostetter
 */

public class Car {

    private String model;

    private String make;

    private int year;

    private String modelAndMake;

    private String color;

    public Car(){

        
    }

    public Car(String model, String make, int year, String modelAndMake, String color){
        this.model = model;
        this.make = make;
        this.year = year;
        this.modelAndMake = modelAndMake;
        this.color = color;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return this.make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMakeAndModel() {
        return this.modelAndMake;
    }
    public void setModelAndMake(String modelAndMake){
        this.modelAndMake = modelAndMake;
    }

    public String getColor() {
        return this.color;
    }
    public void setColor(String color){
        this.color = color;
    }

    @Override
    public String toString() {
        return model + " "
               + make + " "
               + year + " "
                + color + " "
                + modelAndMake;
    }
}
