package coms309.people;


/**
 * Provides the Definition/Structure for the cars
 *
 * @author Sam Hostetter
 */

public class Product {

    private String title;

    private double price;



    public Product(){


    }

    public Product(String title, double price){
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



    @Override
    public String toString() {
        return title + " "
                + price;
    }
}