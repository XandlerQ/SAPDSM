package resource;

public class Resource {
    protected double resource;  //Resource currently stored
    protected double maxResource;  //Max resource amount
    protected double replenishmentSpeed;  //Resource replenishment speed
    protected double replenishmentTimer;  //Replenishment counter
    protected double replenishmentCooldown;  //Replenishment counter peak

    //--------------------------------------
    //-----------  Constructors  -----------
    //---------------------------------------

    public Resource() {  //Default constructor
        this.resource = 0;  //Empty by default
        this.maxResource = 0;  //Zero capacity
        this.replenishmentSpeed = 0;  //No replenishment speed
        this.replenishmentTimer = 0;  //Standard replenishment counter value
        this.replenishmentCooldown = 0;  //No replenishment counter peak
    }

    public Resource(double maxResource, double fraction, double replenishmentSpeed, double replenishmentCooldown) {
        //maxResource - Resource capacity;
        //fraction - initial resource amount coefficient, assumed to be in range [0; 1];
        //replenishmentSpeed - resource replenishment speed per tick;
        //replenishmentCooldown - peak value for replenishment counter, determines frequency of resource replenishment.
        this.resource = maxResource * fraction;
        this.maxResource = maxResource;
        normalizeResource();  //Normalizes initial this.res value
        this.replenishmentSpeed = replenishmentSpeed;
        this.replenishmentTimer = 0;
        this.replenishmentCooldown = replenishmentCooldown;
        normalizeReplenishmentCooldown(); //Normalizes initial this.replenishmentCooldown value
    }

    //---------------------------------------
    //---------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------

    double getResource() { return this.resource; }

    public double getMaxResource() {
        return maxResource;
    }

    boolean empty() { return this.resource <= 0; }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    void setMaxResource(double maxResource) { this.maxResource = maxResource; }

    void setReplenishmentCooldown(double replenishmentCooldown) {
        this.replenishmentCooldown = replenishmentCooldown;
        normalizeReplenishmentCooldown();
    }

    void setReplenishmentSpeed(double replenishmentSpeed) { this.replenishmentSpeed = replenishmentSpeed; }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    void normalizeResource() {  //Normalizes current this.resource value by projecting it to [0; maxRes]
        if (this.resource > this.maxResource) {
            this.resource = this.maxResource;
        }
        if (this.resource < 0) {
            this.resource = 0;
        }
    }

    void normalizeReplenishmentCooldown() { //Normalizes replenishment counter by assuring it is not lower then 0
        if(this.replenishmentCooldown < 0) {
            this.replenishmentCooldown = 0;
        }
    }


    double lowerRes(double amount){  //Lowers current this.resource by amount
        if (amount > this.resource){  //If amount is greater than currently stored resource amount
            double taken = this.resource;  //Make new variable representing maximum possible resource withdraw
            this.resource = 0;  //Set current resource stored to 0
            this.replenishmentTimer = this.replenishmentCooldown;  //As soon as resource is withdrawn, delay replenishment by this.repCtrPeak frames
            return taken;  //Return maximum possible resource withdraw
        }
        else {  //If amount is less than currently stored resource amount
            this.resource -= amount;  //Lower resource currently stored by amount
            this.replenishmentTimer = this.replenishmentCooldown;  //As soon as resource is withdrawn, delay replenishment by this.repCtrPeak frames
            return amount;  //Return withdrawn amount
        }
    }

    void replenish(double deltaTime){  //Handles replenishment timer and replenishes stored resource
        if(this.replenishmentTimer > 0){  //If the replenishment timer is not 0
            this.replenishmentTimer -= deltaTime;  //Lower the replenishment timer
        }
        else{  //If the replenishment timer is 0
            if(this.resource < this.maxResource)  //If resource currently stored is not at maximum
                this.resource += this.replenishmentSpeed * deltaTime;  //Replenish resource currently stored
            if(this.resource > this.maxResource)  //If resource currently stored is greater then maximum
                this.resource = this.maxResource;  //Set resource currently stored as maximum
        }
    }

    //---------------------------------
    //---------------------------------
}
