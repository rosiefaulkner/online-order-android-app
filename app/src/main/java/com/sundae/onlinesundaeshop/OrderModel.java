package com.sundae.onlinesundaeshop;
import androidx.appcompat.app.AppCompatActivity;

public class OrderModel extends AppCompatActivity {
    private String size;
    private String flavor;
    private String fudge;
    private String createdAt;
    private String price;
    private int id;


    /**
     * Getters for the order history model
     */
    public String getSize() { return size; }

    public void setSize(String size)
    {
        this.size = size;
    }

    public String getFlavor()
    {
        return flavor;
    }

    public void setFlavor(String flavor)
    {
        this.flavor = flavor;
    }

    public String getFudge() { return fudge; }

    public void setFudge(String fudge)
    {
        this.fudge = fudge;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }

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