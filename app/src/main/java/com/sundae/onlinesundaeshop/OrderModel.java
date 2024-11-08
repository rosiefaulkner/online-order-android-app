package com.sundae.onlinesundaeshop;
import androidx.appcompat.app.AppCompatActivity;

public class OrderModel extends AppCompatActivity {
    private final String size;
    private String flavor;
    private final String fudge;
    private final String createdAt;
    private final String price;
    private final int id;


    /**
     * Getters for the order history model
     */
    public String getSize() { return size; }

    public String getFlavor()
    {
        return flavor;
    }

    public void setFlavor(String flavor)
    {
        this.flavor = flavor;
    }

    public String getFudge() { return fudge; }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public Integer getId() { return id; }

    public String getPrice() { return price; }

    /**
     * Constructor for the order history model
     *
     * @param id the id of the order
     * @param size the size of the order
     * @param flavor the flavor of the order
     * @param fudge the fudge of the order
     * @param createdAt the date and time of the order
     */
    public OrderModel(int id, String size, String flavor, String fudge, String createdAt, String price)
    {
        this.id = id;
        this.size = size;
        this.flavor = flavor;
        this.fudge = fudge;
        this.createdAt = createdAt;
        this.price = price;
    }
}