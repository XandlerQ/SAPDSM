package resource;

import point.Point;
import point.Point2D;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceNode extends Resource {
    static AtomicInteger resIdGen = new AtomicInteger(0);
    private final int id;
    private Point2D coordinates;
    private double size = configuration.Resource.RESOURCENODESIZE;
    private Color cl = configuration.Resource.RESOURCECOLOR;

    //--------------------------------------
    //-----------  Constructors  -----------
    //---------------------------------------

    ResourceNode() {
        super();
        this.id = resIdGen.incrementAndGet();
        this.coordinates = null;
        this.size = 0;
    }

    ResourceNode(double maxRes, double fraction, double resRepSpeed, int repCtrPeak,
                 double x, double y) {
        super(maxRes, fraction, resRepSpeed, repCtrPeak);
        this.id = resIdGen.incrementAndGet();
        this.coordinates = new Point2D(x, y);
        this.size = configuration.Resource.RESOURCENODESIZE * super.resource / super.maxResource;
    }

    ResourceNode(double maxRes, double fraction, double resRepSpeed, int repCtrPeak,
                 Point2D coordinates) {
        this(maxRes, fraction, resRepSpeed, repCtrPeak, coordinates.getX(), coordinates.getY());
    }

    //---------------------------------------
    //---------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------

    int getId() {
        return this.id;
    }

    double getX() {
        return this.coordinates.getX();
    }

    double getY() {
        return this.coordinates.getY();
    }

    Point2D getCoordinates() {
        return this.coordinates;
    }

    double getSize() {
        return this.size;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    void setCoordinates(double x, double y) {
        this.coordinates.setXY(x, y);
    }

    void setColor(Color cl) {
        this.cl = cl;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    void recalculateSize() {
        this.size = configuration.Resource.RESOURCENODESIZE * this.resource / this.maxResource;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        ResourceNode arg = (ResourceNode) obj;
        return this.id == arg.getId();
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------


    void render(){
        recalculateSize();
        App.processingRef.noStroke();
        App.processingRef.fill(cl.getRGB(), 255);
        App.processingRef.circle(configuration.Render.ORIGINX + (float)this.coordinates.getX(), configuration.Render.ORIGINY + (float)this.coordinates.getY(), 2);
        App.processingRef.fill(cl.getRGB(), 150);
        App.processingRef.circle(configuration.Render.ORIGINX + (float)this.coordinates.getX(), configuration.Render.ORIGINY + (float)this.coordinates.getY(), (float)size);
    }

    //-----------------------------------
    //-----------------------------------
}
