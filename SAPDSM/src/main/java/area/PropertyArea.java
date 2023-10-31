package area;

import agent.Agent;
import point.Point2D;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertyArea<Property> {
    public static enum PropertyType {
        VALENCE
    }

    static AtomicInteger areaIdGen = new AtomicInteger(0);
    private final int id;
    private double originX;
    private double originY;
    private double sideX;
    private double sideY;
    private PropertyType propertyType;
    private Property property;

    private Color color;

    //--------------------------------------
    //-----------  Constructors  -----------
    //--------------------------------------

    public PropertyArea() {
        this.id = areaIdGen.incrementAndGet();
        this.originX = 0;
        this.originY = 0;
        this.sideX = 0;
        this.sideY = 0;
        this.propertyType = PropertyType.VALENCE;
        this.color = null;
    }

    public PropertyArea(double originX, double originY, double sideX, double sideY) {
        this();
        this.originX = originX;
        this.originY = originY;
        this.sideX = sideX;
        this.sideY = sideY;
    }

    //--------------------------------------
    //--------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------


    public int getId() {
        return id;
    }

    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public double getSideX() {
        return sideX;
    }

    public double getSideY() {
        return sideY;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public Property getProperty() {
        return property;
    }

    public Color getColor() {
        return color;
    }

    public Point2D getAreaCenter() {
        return new Point2D(this.originX + this.sideX / 2, this.originY + this.sideY / 2);
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    public void setOriginX(double originX) {
        this.originX = originX;
    }

    public void setOriginY(double originY) {
        this.originY = originY;
    }

    public void setSideX(double sideX) {
        this.sideX = sideX;
    }

    public void setSideY(double sideY) {
        this.sideY = sideY;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    public boolean inArea(double x, double y) {
        return x >= this.originX && x <= this.originX + this.sideX &&
                y >= this.originY && y <= this.originY + this.sideY;
    }

    public boolean inArea(Point2D coordinates) {
        return this.inArea(coordinates.getX(), coordinates.getY());
    }

    public boolean inArea(Agent agent) {
        return inArea(agent.getCoordinates());
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        PropertyArea<Property> arg = (PropertyArea<Property>) obj;
        return this.id == arg.getId();
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    public void render() {
        app.processingRef.fill(color.getRGB(), 25);
        app.processingRef.stroke(color.getRGB(), 50);
        app.processingRef.strokeWeight(2);
        app.processingRef.rect((float)(configuration.Render.ORIGINX + this.originX), (float)(configuration.Render.ORIGINY + this.originY), (float)(this.sideX), (float)(this.sideY));
    }

    //-----------------------------------
    //-----------------------------------


}
