package agent;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import point.Point2D;
import resource.ResourceNode;
import area.PropertyArea;

public class Agent {

    static AtomicInteger agentIdGen = new AtomicInteger(0);
    private final int id;
    private int species;

    private Point2D coordinates;
    private double direction;
    private double baseSpeed;
    private double speed;

    private double age;
    private double maxAge;
    private double agingSpeed;

    private double energy;
    private double maxEnergy;
    private double sufficientEnergy;
    private double energyDrainSpeed;

    private ResourceNode lockedResourceNode;
    private double collectedResource;
    private double seenResource;

    private int valence;
    private PropertyArea propertyArea;
    private int connectionCount;
    private double lastHeardAge;

    private int actionTimer;
    private int actionCooldown;

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
        this.agingSpeed = 0;

        this.energy = 1;
        this.maxEnergy = 1;
        this.sufficientEnergy = 1;
        this.energyDrainSpeed = 0;

        this.lockedResourceNode = null;
        this.collectedResource = 0;
        this.seenResource = 0;

        this.valence = 0;
        this.propertyArea = null;
        this.connectionCount = 0;
        this.lastHeardAge = -1;


        this.actionTimer = 0;
        this.actionCooldown = 0;

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
    public  double getAgingSpeed() { return this.agingSpeed; }

    public  double getEnergy() { return this.energy; }
    public double getMaxEnergy() { return this.maxEnergy; }
    public double getSufficientEnergy() { return this.sufficientEnergy; }
    public double getEnergyDrainSpeed() { return this.energyDrainSpeed; }

    public double getHunger() { return this.maxEnergy - this.energy; }
    public boolean wellFed() { return this.energy >= this.sufficientEnergy; }
    public boolean wellFedLone() { return this.energy >= this.maxEnergy * 0.8; }

    public ResourceNode getLockedResourceNode() { return this.lockedResourceNode; }
    public double getCollectedResource() { return this.collectedResource; }

    public double getSeenResource() {
        return seenResource;
    }

    public int getValence() { return this.valence; }

    public PropertyArea getPropertyArea() {
        return propertyArea;
    }

    public int getConnectionCount() { return this.connectionCount; }
    public double getLastHeardAge() { return this.lastHeardAge; }
    public boolean topCon() { return this.connectionCount >= this.valence; }

    public int getActCounter() { return this.actionTimer; }
    public int getActCounterPeak() { return this.actionCooldown; }

    public boolean readyToAct() { return this.actionTimer <= 0; }

    public boolean stationary() { return this.stationary; }

    public boolean dead() { return this.energy <= 0 || this.age > this.maxAge; }



    //---------------------------------

    public double getDistanceTo(double x, double y) { return Point2D.distanceBetween(this.coordinates, new Point2D(x, y)); }
    public double getDistanceTo(Point2D dot) { return getDistanceTo(dot.getX(), dot.getY()); }

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
    public void setAgingSpeed(double agingSpeed) { this.agingSpeed = agingSpeed; }

    public void setEnergy(double energy) {
        this.energy = energy;
        normalizeEnergy();
    }

    public void setMaxEnergy(double maxEnergy) { this.maxEnergy = maxEnergy; }
    public void setSufficientEnergy(double sufficientEnergy) { this.sufficientEnergy = sufficientEnergy; }
    public void setEnergyDrainSpeed(double energyDrainSpeed) { this.energyDrainSpeed = energyDrainSpeed; }

    public void setLockedResourceNode(ResourceNode resNode) { this.lockedResourceNode = resNode; }
    public void setCollectedResource(double collectedResource) { this.collectedResource = collectedResource; }

    public void setSeenResource(double seenResource) {
        this.seenResource = seenResource;
    }

    public void collect(double res) { this.collectedResource += res; }
    public void resetCollectedRes() { this.collectedResource = 0; }
    public void resetSeenRes() { this.seenResource = 0; }

    public void setValence(int valence) { this.valence = valence; }

    public void setPropertyArea(PropertyArea propertyArea) {
        this.propertyArea = propertyArea;
    }

    public void setLastHeardAge(double age) { this.lastHeardAge = age; }
    public void resetLastHeardAge() { this.lastHeardAge = -1; }
    public void resetConCount() { this.connectionCount = 0; }


    public void setActionCooldown(int actionCooldown) { this.actionCooldown = actionCooldown; }
    public void getReadyToAct() { this.actionTimer = 0; }
    public void resetActionTimer() { this.actionTimer = this.actionCooldown; }

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

    public void addToEnergy(double energy) {
        this.energy += energy;
        normalizeEnergy();
    }

    public void eat(double resource) {
        this.energy += resource;
        normalizeEnergy();
    }

    public void eatCollected() {
        eat(this.collectedResource);
        resetCollectedRes();
    }

    //---------------------------------

    public boolean addConnection() {
        if(topCon()) return false;
        this.connectionCount++;
        return true;
    }

    public boolean removeConnection() {
        if(this.connectionCount == 0) return false;
        this.connectionCount--;
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

    public void step(double deltaTime) {

        // Update agent age
        this.age += this.agingSpeed * deltaTime;

        // Recalculate agent speed depending on their age
        updateSpeed();

        // Drain energy according to current agent speed
        this.energy -= this.energyDrainSpeed * deltaTime * ((this.speed * this.speed) / (this.baseSpeed * this.baseSpeed));

        // Update action timer. If action timer is zero or lower than readyToAct() method returns true.
        // Following code ensures that action timer is xero or lower for 1 program tick,
        // Same goes for readyToAct() method returning true for a single program tick
        if(this.actionTimer > 0) this.actionTimer -= deltaTime;
        else resetActionTimer();

        // If agent is set as stationary, then no need to calculate new position
        if(stationary) return;

        Random r = new Random();

        // Randomize direction of movement
        this.direction += -0.16 + (0.32) * r.nextDouble();
        normalizeDirection();

        // Calculate new coordinates using speed and direction values (in accordance with deltaTime)
        double newX = this.coordinates.getX() + this.speed * deltaTime * Math.cos(this.direction);
        double newY = this.coordinates.getY() + this.speed * deltaTime * Math.sin(this.direction);

        // If PropertyGrid is used and LOCKEDAREAS configuration variable is set to true
        if(configuration.PropertyGrid.LOCKEDAREAS && this.propertyArea != null) {

            // Get property area border coordinates
            double prAreaOriginX = this.propertyArea.getOriginX();
            double prAreaOriginY = this.propertyArea.getOriginY();
            double prAreaSideX = this.propertyArea.getSideX();
            double prAreaSideY = this.propertyArea.getSideY();

            // If new calculated coordinates are NOT within PropertyArea
            if (newX > prAreaOriginX + prAreaSideX - configuration.Agent.WALLTHICKNESS ||
                    newX < prAreaOriginX + configuration.Agent.WALLTHICKNESS ||
                    newY > prAreaOriginY + prAreaSideY - configuration.Agent.WALLTHICKNESS ||
                    newY < prAreaOriginY + configuration.Agent.WALLTHICKNESS) {
                // Set direction to the centre of current property area
                direction = angle.Angle.directionFromTo(this.coordinates, this.propertyArea.getAreaCenter());
                normalizeDirection();
                // Calculate corresponding new coordinates
                newX = this.coordinates.getX() + this.speed * deltaTime * Math.cos(this.direction);
                newY = this.coordinates.getY() + this.speed * deltaTime * Math.sin(this.direction);
            }
            if (newX > configuration.Aviary.DEFX - configuration.Agent.WALLTHICKNESS ||
                    newX < configuration.Agent.WALLTHICKNESS ||
                    newY > configuration.Aviary.DEFY - configuration.Agent.WALLTHICKNESS ||
                    newY < configuration.Agent.WALLTHICKNESS) {
                System.out.println("NowhereToGo");
                return;
            }
        }
        else { // If PropertyGrid is not used
            // If new calculated coordinates are NOT within Aviary borders
            if (newX > configuration.Aviary.DEFX - configuration.Agent.WALLTHICKNESS ||
                    newX < configuration.Agent.WALLTHICKNESS ||
                    newY > configuration.Aviary.DEFY - configuration.Agent.WALLTHICKNESS ||
                    newY < configuration.Agent.WALLTHICKNESS) {
                // Set direction to the centre of Aviary
                direction = angle.Angle.directionFromTo(this.coordinates, new Point2D(configuration.Aviary.DEFX / 2., configuration.Aviary.DEFY / 2.));
                normalizeDirection();
                // Calculate corresponding new coordinates
                newX = this.coordinates.getX() + this.speed * deltaTime * Math.cos(this.direction);
                newY = this.coordinates.getY() + this.speed * deltaTime * Math.sin(this.direction);
            }
        }
        // Set new coordinates
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
        app.App.processingRef.stroke(configuration.Agent.SPECIESCOLORS[this.species].getRGB());
        app.App.processingRef.strokeWeight(1);
        if (this.energy >= this.sufficientEnergy) app.App.processingRef.fill((new Color(255, 170, 0)).getRGB(), 150);
        else app.App.processingRef.fill(0);

        app.App.processingRef.circle((float)(configuration.Render.ORIGINX + this.coordinates.getX()), (float)(configuration.Render.ORIGINY + this.coordinates.getY()), 4);

    }

    //-----------------------------------
    //-----------------------------------
}
