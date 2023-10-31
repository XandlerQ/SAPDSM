package agent;

import point.Point2D;
import resource.ResourceNode;
import area.PropertyArea;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent {
    static Color speciesColors[] = {
            new Color(255, 0,0),
            new Color(45, 255, 0),
            new Color(252, 223, 3),
            new Color(3, 252, 240)
    };


    static AtomicInteger agentIdGen = new AtomicInteger(0);
    private final int id;
    private int species;

    private Point2D coordinates;
    private double direction;
    private double baseSpeed;
    private double speed;

    private double age;
    private double maxAge;
    private double ageIncr;

    private double energy;
    private double maxEnergy;
    private double suffEnergy;
    private double energyDecr;

    private ResourceNode lockedRes;
    private double collectedRes;
    private double seenRes;

    private int valence;
    private PropertyArea propertyArea;
    private int conCount;
    private double lastHeardAge;

    private int actCtr;
    private int actCtrPeak;

    private boolean stationary;


    //--------------------------------------
    //-----------  Constructors  -----------
    //--------------------------------------

    public Agent() {
        this.id = agentIdGen.incrementAndGet();
        this.species = 0;

        this.coordinates = new Point2D();
        this.direction = 0;
        this.baseSpeed = 1;
        this.speed = 0;

        this.age = 0;
        this.maxAge = 1;
        this.ageIncr = 0;

        this.energy = 1;
        this.maxEnergy = 1;
        this.suffEnergy = 1;
        this.energyDecr = 0;

        this.lockedRes = null;
        this.collectedRes = 0;
        this.seenRes = 0;

        this.valence = 0;
        this.propertyArea = null;
        this.conCount = 0;
        this.lastHeardAge = -1;


        this.actCtr = 0;
        this.actCtrPeak = 0;

        this.stationary = false;
    }

    //---------------------------------------
    //---------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------



    public int getId() { return this.id; }
    public int getSpecies() { return this.species; }

    public double getX() { return this.coordinates.getX(); }
    public double getY() { return this.coordinates.getY(); }
    public Point2D getCoordinates() { return this.coordinates; }
    public double getDirection() { return this.direction; }
    public double getBaseSpeed() { return this.baseSpeed; }
    public double getSpeed() { return this.speed; }

    public double getAge() { return this.age; }
    public double getMaxAge() { return this.maxAge; }
    public  double getAgeIncr() { return this.ageIncr; }

    public  double getEnergy() { return this.energy; }
    public double getMaxEnergy() { return this.maxEnergy; }
    public double getSuffEnergy() { return this.suffEnergy; }
    public double getEnergyDecr() { return this.energyDecr; }

    public double getHunger() { return this.maxEnergy - this.energy; }
    public boolean wellFed() { return this.energy >= this.suffEnergy; }
    public boolean wellFedLone() { return this.energy >= this.maxEnergy * 0.8; }

    public ResourceNode getLockedRes() { return this.lockedRes; }
    public double getCollectedRes() { return this.collectedRes; }

    public double getSeenRes() {
        return seenRes;
    }

    public int getValence() { return this.valence; }

    public PropertyArea getPropertyArea() {
        return propertyArea;
    }

    public int getConCount() { return this.conCount; }
    public double getLastHeardAge() { return this.lastHeardAge; }
    public boolean topCon() { return this.conCount >= this.valence; }

    public int getActCounter() { return this.actCtr; }
    public int getActCounterPeak() { return this.actCtrPeak; }

    public boolean readyToAct() { return this.actCtr == 0; }

    public boolean stationary() { return this.stationary; }

    public boolean dead() { return this.energy <= 0 || this.age > this.maxAge; }



    //---------------------------------

    public double getDistTo(double x, double y) { return Point2D.distanceBetween(this.coordinates, new Point2D(x, y)); }
    public double getDistTo(Point2D dot) { return getDistTo(dot.getX(), dot.getY()); }

    public double getEnergyOver() { return this.energy - this.maxEnergy; }


    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    public void setSpecies(int species) { this.species = species; }

    public void setCoordinates(Point2D coordinates) { this.coordinates = coordinates; }
    public void setCoordinates(double x, double y) { this.coordinates.setXY(x, y); }
    public void setDirection(double direction) {
        this.direction = direction;
        normalizeDirection();
    }

    public void setBaseSpeed(double baseSpeed) { this.baseSpeed = baseSpeed; }

    public void setAge(double age) { this.age = age; }
    public void setMaxAge(double maxAge) { this.maxAge = maxAge; }
    public void setAgeIncr(double ageIncr) { this.ageIncr = ageIncr; }

    public void setEnergy(double energy) {
        this.energy = energy;
        normalizeEnergy();
    }

    public void setMaxEnergy(double maxEnergy) { this.maxEnergy = maxEnergy; }
    public void setSuffEnergy(double suffEnergy) { this.suffEnergy = suffEnergy; }
    public void setEnergyDecr(double energyDecr) { this.energyDecr = energyDecr; }

    public void setLockedRes(ResourceNode resNode) { this.lockedRes = resNode; }
    public void setCollectedRes(double collectedRes) { this.collectedRes = collectedRes; }

    public void setSeenRes(double seenRes) {
        this.seenRes = seenRes;
    }

    public void collect(double res) { this.collectedRes += res; }
    public void resetCollectedRes() { this.collectedRes = 0; }
    public void resetSeenRes() { this.seenRes = 0; }

    public void setValence(int valence) { this.valence = valence; }

    public void setPropertyArea(PropertyArea propertyArea) {
        this.propertyArea = propertyArea;
    }

    public void setLastHeardAge(double age) { this.lastHeardAge = age; }
    public void resetLastHeardAge() { this.lastHeardAge = -1; }
    public void resetConCount() { this.conCount = 0; }


    public void setActCtrPeak(int actCtrPeak) { this.actCtrPeak = actCtrPeak; }
    public void getReadyToAct() { this.actCtr = 0; }
    public void resetActCtr() { this.actCtr = this.actCtrPeak; }

    public void setStationary(boolean stationary) { this.stationary = stationary; }
    public void lock() { this.stationary = true; }
    public void unlock() { this.stationary = false; }
    public void turnAround() {
        this.direction += Math.PI;
        normalizeDirection();
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    private void normalizeEnergy() {
        if(this.energy > maxEnergy) {
            this.energy = maxEnergy;
        }
        if(this.energy < 0) {
            this.energy = 0;
        }
    }

    public void addToEnergy(double nrg) {
        this.energy += nrg;
        normalizeEnergy();
    }

    public void eat(double res) {
        this.energy += res;
        normalizeEnergy();
    }

    public void eatCollected() {
        eat(this.collectedRes);
        resetCollectedRes();
    }

    //---------------------------------

    public boolean addCon() {
        if(topCon()) return false;
        this.conCount++;
        return true;
    }

    public boolean removeCon() {
        if(this.conCount == 0) return false;
        this.conCount--;
        return true;
    }

    //---------------------------------

    public void updateSpeed() {
        this.speed = this.baseSpeed
                - configuration.Agent.SPEEDAGECOEFF * (this.baseSpeed / 4)
                * (4 * (this.age - this.maxAge/2) * (this.age - this.maxAge/2) / (this.maxAge * this.maxAge));
    }

    //---------------------------------

    public void normalizeDirection() {
        while(this.direction < 0) this.direction += 2 * Math.PI;
        while(this.direction >= 2 * Math.PI) this.direction -= 2 * Math.PI;
    }

    public double dirToFace(double x, double y) {
        double distance = Point2D.distanceBetween(this.coordinates, new Point2D(x, y));

        if(distance == 0) {
            Random r = new Random();
            return 2 * Math.PI * r.nextDouble();
        }

        double direction = Math.acos((x - this.coordinates.getX()) / distance);
        if(y > this.coordinates.getY()) return direction;
        else return 2 * Math.PI - direction;
    }

    public double dirToFace(Point2D dot) { return dirToFace(dot.getX(), dot.getY()); }

    public void face(double x, double y) { this.direction = dirToFace(x, y); }
    public void face(Point2D dot) { this.direction = dirToFace(dot); }
    public void adjustDirectionTo(Point2D dot) { this.direction = angle.Angle.directionAddition(this.direction, dirToFace(dot)); }

    //---------------------------------

    public void step() {

        this.age += this.ageIncr;
        updateSpeed();

        this.energy -= this.energyDecr * ((this.speed * this.speed) / (this.baseSpeed * this.baseSpeed));

        if(this.actCtr > 0) this.actCtr -= 1;
        else resetActCtr();

        if(stationary) return;

        Random r = new Random();

        this.direction += -0.16 + (0.32) * r.nextDouble();
        normalizeDirection();

        double newX = this.coordinates.getX() + this.speed * Math.cos(this.direction);
        double newY = this.coordinates.getY() + this.speed * Math.sin(this.direction);

        if(configuration.PropertyGrid.LOCKEDAREAS && this.propertyArea != null) {

            double prAreaOriginX = this.propertyArea.getOriginX();
            double prAreaOriginY = this.propertyArea.getOriginY();
            double prAreaSideX = this.propertyArea.getSideX();
            double prAreaSideY = this.propertyArea.getSideY();

            if (newX > prAreaOriginX + prAreaSideX - configuration.Agent.WALLTHICKNESS ||
                    newX < prAreaOriginX + configuration.Agent.WALLTHICKNESS ||
                    newY > prAreaOriginY + prAreaSideY - configuration.Agent.WALLTHICKNESS ||
                    newY < prAreaOriginY + configuration.Agent.WALLTHICKNESS) {
                direction = angle.Angle.directionFromTo(this.coordinates, this.propertyArea.getAreaCenter());
                normalizeDirection();
                newX = this.coordinates.getX() + this.speed * Math.cos(this.direction);
                newY = this.coordinates.getY() + this.speed * Math.sin(this.direction);
            }
            if (newX > configuration.Render.DEFX - configuration.Agent.WALLTHICKNESS ||
                    newX < configuration.Agent.WALLTHICKNESS ||
                    newY > configuration.Render.DEFY - configuration.Agent.WALLTHICKNESS ||
                    newY < configuration.Agent.WALLTHICKNESS) {
                return;
            }
        }
        else {
            if (newX > configuration.Render.DEFX - configuration.Agent.WALLTHICKNESS ||
                    newX < configuration.Agent.WALLTHICKNESS ||
                    newY > configuration.Render.DEFY - configuration.Agent.WALLTHICKNESS ||
                    newY < configuration.Agent.WALLTHICKNESS) {
                direction = angle.Angle.directionFromTo(this.coordinates, new Point2D(configuration.Render.DEFX / 2., configuration.Render.DEFY / 2.));
                normalizeDirection();
                newX = this.coordinates.getX() + this.speed * Math.cos(this.direction);
                newY = this.coordinates.getY() + this.speed * Math.sin(this.direction);
            }
        }

        this.setCoordinates(newX, newY);
    }

    //---------------------------------

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        Agent arg = (Agent) obj;
        return this.id == arg.id;
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    public void render()
    {
        app.App.processingRef.stroke(speciesColors[this.species].getRGB());
        app.App.processingRef.strokeWeight(1);
        if (this.energy >= this.suffEnergy) app.App.processingRef.fill((new Color(255, 170, 0)).getRGB(), 150);
        else app.App.processingRef.fill(0);

        app.App.processingRef.circle((float)(configuration.Render.ORIGINX + this.coordinates.getX()), (float)(configuration.Render.ORIGINY + this.coordinates.getY()), 4);

    }

    //-----------------------------------
    //-----------------------------------
}
