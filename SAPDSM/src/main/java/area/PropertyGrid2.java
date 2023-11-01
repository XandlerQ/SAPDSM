package area;

import point.Point2D;

import java.awt.*;
import java.util.ArrayList;

public class PropertyGrid2<Property> {

    private double defX;
    private double defY;
    private int grCtX;
    private int grCtY;

    private double sideX;
    private double sideY;
    private double intersection;
    ArrayList<PropertyArea<Property>> propertyAreas;

    //--------------------------------------
    //-----------  Constructors  -----------
    //--------------------------------------


    public PropertyGrid2() {
        this.defX = 0;
        this.defY = 0;
        this.grCtX = 0;
        this.grCtY = 0;
        this.sideX = 0;
        this.sideY = 0;
        this.intersection = 0;
        this.propertyAreas = null;
    }

    public PropertyGrid2(double defX, double defY) {
        this.defX = defX;
        this.defY = defY;
        this.grCtX = 2;
        this.grCtY = 1;
        this.sideX = defX / this.grCtX;
        this.sideY = defY / this.grCtY;
        this.intersection = sideX;
        this.propertyAreas = new ArrayList<>(grCtX * grCtY);
    }

    //--------------------------------------
    //--------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------

    public double getDefX() {
        return defX;
    }

    public double getDefY() {
        return defY;
    }

    public int getGrCtX() {
        return grCtX;
    }

    public int getGrCtY() {
        return grCtY;
    }

    public double getSideX() {
        return sideX;
    }

    public double getSideY() {
        return sideY;
    }

    public double getIntersection() {
        return intersection;
    }

    public ArrayList<PropertyArea<Property>> getPropertyAreas() {
        return propertyAreas;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    public void setDefX(double defX) {
        this.defX = defX;
        recalculateSideX();
    }

    public void setDefY(double defY) {
        this.defY = defY;
        recalculateSideY();
    }

    public void setGrCtX(int grCtX) {
        this.grCtX = grCtX;
        recalculateSideX();
    }

    public void setGrCtY(int grCtY) {
        this.grCtY = grCtY;
        recalculateSideY();
    }

    //---------------------------------

    private void recalculateSideX() {
        this.sideX = this.defX / this.grCtX;
    }

    private void recalculateSideY() {
        this.sideY = this.defY / this.grCtY;
    }

    public void setIntersection(double intersection) {
        this.intersection = intersection;
        this.propertyAreas.get(0).setSideX(intersection);
        this.propertyAreas.get(1).setOriginX(intersection);
        this.propertyAreas.get(1).setSideX(this.defX - intersection);
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    public void fillPropertyAreas(Property[] propertyAreasValues, Color[] propertyAreasColors) {
        for (int i = 0; i < this.grCtX; i++) {
            for (int j = 0; j < this.grCtY; j++) {
                PropertyArea<Property> propertyArea = new PropertyArea<>(i * this.sideX, j * this.sideY, this.sideX, this.sideY);
                propertyArea.setProperty(propertyAreasValues[i * this.grCtY + j]);
                propertyArea.setColor(propertyAreasColors[i * this.grCtY + j]);
                this.propertyAreas.add(propertyArea);
            }
        }
    }

    public PropertyArea<Property> getPropertyArea(int propertyAreaIndex) {
        return this.propertyAreas.get(propertyAreaIndex);
    }

    public int getPropertyAreaIndex(double x, double y) {
        if(x < 0) x = 0;
        if(y < 0) y = 0;

        if(x > this.defX) x = this.defX;
        if(y > this.defY) y = this.defY;

        if(x <= this.intersection) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public int getPropertyAreaIndex(Point2D coordinates) {
        return getPropertyAreaIndex(coordinates.getX(), coordinates.getY());
    }

    public PropertyArea<Property> getPropertyArea(double x, double y) {
        return this.propertyAreas.get(getPropertyAreaIndex(x, y));
    }

    public PropertyArea<Property> getPropertyArea(Point2D coordinates) {
        return this.propertyAreas.get(getPropertyAreaIndex(coordinates));
    }

    public Property getProperty(double x, double y) {
        return getPropertyArea(x, y).getProperty();
    }

    public Property getProperty(Point2D coordinates) {
        return getProperty(coordinates.getX(), coordinates.getY());
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    public void render() {
        this.propertyAreas.forEach(PropertyArea::render);
    }

    //-----------------------------------
    //-----------------------------------
}
