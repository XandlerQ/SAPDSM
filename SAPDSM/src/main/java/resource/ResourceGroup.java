package resource;

import point.Point2D;
import area.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ResourceGroup {
    private double defX;  //Group dimension along X axis
    private double defY;  //Group dimension along Y axis
    private int grCtX;  //Amount of areas along X axis
    private int grCtY;  //Amount of areas along Y axis
    private double sideX;  //Area dimension along X axis
    private double sideY;  //Area dimension along Y axis

    private int density;  //Amount of resource nodes in area
    private ArrayList<ResourceNode> resNodes;  //Resource nodes
    private int resCount;

    private Color cl = configuration.Resource.RESOURCECOLOR;
    //--------------------------------------
    //-----------  Constructors  -----------
    //---------------------------------------

    public ResourceGroup() {
        this.defX = 0;
        this.defY = 0;
        this.grCtX = 0;
        this.grCtY = 0;
        this.sideX = 0;
        this.sideY = 0;

        this.density = 0;
        this.resNodes = null;
        this.resCount = 0;
    }

    public ResourceGroup(double defX, double defY, int grCtX, int grCtY, int density) {
        this.defX = defX;
        this.defY = defY;
        this.grCtX = grCtX;
        this.grCtY = grCtY;
        this.sideX = defX / grCtX;
        this.sideY = defY / grCtY;

        this.density = density;
        this.resCount = grCtX * grCtY * density;

        this.resNodes = new ArrayList<ResourceNode> (this.resCount);
        //ResourceNode(double maxRes, double fraction, double resRepSpeed, int repCtrPeak,
        //           Dot coordinates) {
    }

    //---------------------------------------
    //---------------------------------------

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

    public ArrayList<ResourceNode> getResNodes() {
        return resNodes;
    }

    public int getDensity() {
        return density;
    }

    public int getResCount() {
        return resCount;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------


    public void setNodesMaxRes(double maxRes){
        this.resNodes.forEach((node) -> {node.setMaxResource(maxRes);});
    }

    public void setNodesResRepSpeed(double resRepSpeed){
        this.resNodes.forEach((node) -> {node.setReplenishmentSpeed(resRepSpeed);});
    }

    public void setNodesRepCtrPeak(int repCtrPeak){
        this.resNodes.forEach((node) -> {node.setReplenishmentCooldown(repCtrPeak);});
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    public double[] getResourceInAreas(PropertyGrid4 propertyGrid4) {
        double[] resourceInAres = new double[4];

        for (int i = 0; i < 4; i++) {
            resourceInAres[i] = 0;
        }

        for (ResourceNode resourceNode : this.resNodes) {
            resourceInAres[propertyGrid4.getPropertyAreaIndex(resourceNode.getCoordinates())] += resourceNode.getResource();
        }

        return resourceInAres;
    }

    public double[] getResourceInAreas(PropertyGrid2 propertyGrid2) {
        double[] resourceInAres = new double[2];

        for (int i = 0; i < 2; i++) {
            resourceInAres[i] = 0;
        }

        for (ResourceNode resourceNode : this.resNodes) {
            resourceInAres[propertyGrid2.getPropertyAreaIndex(resourceNode.getCoordinates())] += resourceNode.getResource();
        }

        return resourceInAres;
    }

    public Point2D getRndCoordInArea(double origX, double origY, double sideX, double sideY) {
        Random r = new Random();
        Point2D coordinates = new Point2D();
        coordinates.setX(origX + sideX/20 + (18 * sideX / 20) * r.nextDouble());
        coordinates.setY(origY + sideY/20 + (18 * sideY / 20) * r.nextDouble());
        return coordinates;
    }

    public void fillResNodes(double maxRes, double fraction, double resRepSpeed, int repCtrPeak) {
        for(int i = 0; i < this.grCtX; i++){
            for(int j = 0; j < this.grCtY; j++){
                for (int k = 0; k < this.density; k++){
                    this.resNodes.add(
                            new ResourceNode(
                                    maxRes,
                                    fraction,
                                    resRepSpeed,
                                    repCtrPeak,
                                    getRndCoordInArea(i * sideX, j * sideY, sideX, sideY)
                            )
                    );
                }
            }
        }
    }

    public void replenishNodes(double deltaTime){
        this.resNodes.forEach((node) -> node.replenish(deltaTime));
    }

    public ArrayList<ResourceNode> getVisibleResNodes(double x, double y, double radius){

        if(x < 0) x = 0;
        if(y < 0) y = 0;

        if(x > this.defX) x = this.defX;
        if(y > this.defY) y = this.defY;

        int posQuadX = 0;
        posQuadX = (int)((x - (x % this.sideX)) / this.sideX);
        if(posQuadX >= this.grCtX) posQuadX = this.grCtX - 1;
        int posQuadY = 0;
        posQuadY = (int)((y - (y % this.sideY)) / this.sideY);
        if(posQuadY >= this.grCtY) posQuadY = this.grCtY - 1;

        int moreL = 0;
        int moreR = 0;
        int moreT = 0;
        int moreB = 0;

        double radiusOverlapL = posQuadX * this.sideX - (x - radius);
        double radiusOverlapR = (x + radius) - (posQuadX + 1) * this.sideX;
        double radiusOverlapT = posQuadY * this.sideY - (y - radius);
        double radiusOverlapB = (y + radius) - (posQuadY + 1) * this.sideY;

        int quadLeftL = posQuadX;
        int quadLeftR = this.grCtX - (posQuadX + 1);
        int quadLeftT = posQuadY;
        int quadLeftB = this.grCtY - (posQuadY + 1);

        if(radiusOverlapL > 0){
            moreL = (int)((radiusOverlapL - radiusOverlapL % this.sideX) / this.sideX) + 1;
            if(moreL > quadLeftL){
                moreL = quadLeftL;
            }
        }
        if(radiusOverlapR > 0){
            moreR = (int)((radiusOverlapR - radiusOverlapR % this.sideX) / this.sideX) + 1;
            if(moreR > quadLeftR){
                moreR = quadLeftR;
            }
        }
        if(radiusOverlapT > 0){
            moreT = (int)((radiusOverlapT - radiusOverlapT % this.sideY) / this.sideY) + 1;
            if(moreT > quadLeftT){
                moreT = quadLeftT;
            }
        }
        if(radiusOverlapB > 0){
            moreB = (int)((radiusOverlapB - radiusOverlapB % this.sideY) / this.sideY) + 1;
            if(moreB > quadLeftB){
                moreB = quadLeftB;
            }
        }

        ArrayList<ResourceNode> visibleResNodes = new ArrayList<>();

        for(int i = posQuadX - moreL; i <= posQuadX + moreR; i++){
            for(int j = posQuadY - moreT; j <= posQuadY + moreB; j++){
                for(int k = 0; k < this.density; k++){
                    visibleResNodes.add(this.resNodes.get(i * this.grCtY * this.density + j * this.density + k));
                }
            }
        }

        return visibleResNodes;
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    public void render()
    {
        this.resNodes.forEach(ResourceNode::render);
        app.App.processingRef.stroke(cl.getRGB(), 100);
        app.App.processingRef.strokeWeight(2);
        for(int i = 0; i < this.grCtX + 1; i++){
            app.App.processingRef.line(configuration.Render.ORIGINX + (float)(i * this.sideX), configuration.Render.ORIGINY,
                    configuration.Render.ORIGINX + (float)(i * this.sideX), configuration.Render.ORIGINY + (float)(this.defY));
        }
        for(int j = 0; j < this.grCtY + 1; j++){
            app.App.processingRef.line(configuration.Render.ORIGINX, configuration.Render.ORIGINY + (float)(j * this.sideY),
                    configuration.Render.ORIGINX + (float)(this.defX), configuration.Render.ORIGINY + (float)(j * this.sideY));
        }
    }

    //-----------------------------------
    //-----------------------------------





}
