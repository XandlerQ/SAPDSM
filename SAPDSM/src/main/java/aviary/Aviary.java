package aviary;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import builder.Builder;
import observer.Observer;
import point.Point2D;
import resource.*;
import area.*;
import agent.*;
import structure.*;

public class Aviary {
    Color rivalryCl = new Color(207, 0, 255);

    configuration.Resource.RESOURCETYPES resType;
    ResourceGroup resGroup;
    ResourceGrid resGrid;
    configuration.PropertyGrid.SHIFTINTERSECTIONMODES shiftIntersection;
    PropertyGrid2<Integer> propertyGrid2;
    ArrayList<Agent> agents;
    ArrayList<Pack> packs;
    int tk = 0;
    Observer observer;


    //--------------------------------------
    //-----------  Constructors  -----------
    //--------------------------------------

    public Aviary() {
        this.resType = configuration.Resource.RESTYPE;
        this.resGroup = null;
        this.resGrid = null;
        this.shiftIntersection = configuration.PropertyGrid.SHIFTINTERSECTION;
        this.propertyGrid2 = null;
        this.agents = null;
        this.packs = null;
        this.observer = null;
    }

    //--------------------------------------
    //--------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------


    public Color getRivalryColor() {
        return rivalryCl;
    }

    public ResourceGroup getResourceGroup() {
        return resGroup;
    }

    public PropertyGrid2<Integer> getPropertyGrid() {
        return propertyGrid2;
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public ArrayList<Pack> getPacks() {
        return packs;
    }

    //---------------------------------

    Pack getPack(Agent agent){

        if(agent.getConnectionCount() == 0) return null;

        for (Pack pck : packs) {
            if (pck.contains(agent)) {
                return pck;
            }
        }
        return null;
    }

    public int getPopulation() {
        return this.agents.size();
    }

    public double[][] getDataInAreas() {
        double areaData[][] = new double[4][2];

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 2; j++) {
                areaData[i][j] = 0.;
            }
        }

        for(Iterator<Agent> iterator = this.agents.iterator(); iterator.hasNext();) {
            Agent agent = iterator.next();
            areaData[0][this.propertyGrid2.getPropertyAreaIndex(agent.getCoordinates())] += 1;
            areaData[1][this.propertyGrid2.getPropertyAreaIndex(agent.getCoordinates())] += agent.getEnergy();
        }
        for(Iterator<Pack> iterator = this.packs.iterator(); iterator.hasNext();) {
            Pack pack = iterator.next();
            areaData[2][this.propertyGrid2.getPropertyAreaIndex(pack.getPackCenter())] += 1;
        }

        if (configuration.Resource.RESTYPE == configuration.Resource.RESOURCETYPES.PLAIN && this.resGrid != null) {
            double[] resourceInAreas = this.resGrid.getResourceInAreas(this.propertyGrid2);
            for (int i = 0; i < 2; i++) {
                areaData[3][i] = resourceInAreas[i];
            }
        }
        else if (configuration.Resource.RESTYPE == configuration.Resource.RESOURCETYPES.DISCRETE && this.resGroup != null) {
            double[] resourceInAreas = this.resGroup.getResourceInAreas(this.propertyGrid2);
            for (int i = 0; i < 2; i++) {
                areaData[3][i] = resourceInAreas[i];
            }
        }
        return areaData;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    public void setRivalryCl(Color rivalryCl) {
        this.rivalryCl = rivalryCl;
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    void removeAgentFromPacks(Agent agent){
        //int at = 0;
        for (Iterator<Pack> iterator = packs.iterator(); iterator.hasNext();){
            Pack pack = iterator.next();
            if(pack.contains(agent)){
                //println("PACK IDX", at, "CONTAINED THIS AGENT, REMOVING AGENT FROM THIS PACK");
                pack.removeAgent(agent);
                if(pack.empty()){
                    //println("PACK TURNED OUT TO BE EMPTY, REMOVING THE WHOLE PACK");
                    iterator.remove();
                }
                break;
            }

        }
        //println("maybe removed a pack, current pack count:", packs.size());
    }

    //------Resource calculations------

    void agResourceLocking(Agent agent){

        ResourceNode lockedRes = agent.getLockedResourceNode();

        if(lockedRes != null){
            if(lockedRes.empty() || agent.getDistanceTo(lockedRes.getCoordinates()) > configuration.Agent.VISUALDIST){
                agent.setLockedResourceNode(null);
                agent.setStationary(false);
            }
        }
        else{
            if(agent.getConnectionCount() == 0 && agent.wellFedLone()){
                return;
            }
            ArrayList<ResourceNode> resources = resGroup.getVisibleResNodes(agent.getX(), agent.getY(), configuration.Agent.VISUALDIST);
            double minDist = agent.getDistanceTo(resources.get(0).getCoordinates()) + 1;
            int minDistIdx = -1;
            int idx = 0;
            for (ResourceNode res : resources) {
                if (!res.empty() && this.propertyGrid2.getPropertyAreaIndex(agent.getCoordinates()) == this.propertyGrid2.getPropertyAreaIndex(res.getCoordinates())) {
                    double currDist = agent.getDistanceTo(res.getCoordinates());
                    if (minDist > currDist) {
                        minDist = currDist;
                        minDistIdx = idx;
                    }
                }
                idx++;
            }
            if(minDistIdx != -1){
                if(minDist <= configuration.Agent.VISUALDIST){
                    ResourceNode foundRes = resources.get(minDistIdx);
                    agent.setLockedResourceNode(foundRes);
                    return;
                }
            }
            agent.setLockedResourceNode(null);
            agent.setStationary(false);
        }
    }

    void agResCollection(Agent agent){
        if (this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) {
            ResourceNode lockedRes = agent.getLockedResourceNode();
            if (lockedRes == null) {
                agent.setStationary(false);
                return;
            }
            double dist = agent.getDistanceTo(lockedRes.getCoordinates());
            if (dist <= lockedRes.getSize() + 4) {
                if (agent.getConnectionCount() == 0) {
                    double hunger = agent.getHunger();
                    if (hunger == 0) {
                        agent.setLockedResourceNode(null);
                        agent.setStationary(false);
                    } else {
                        agent.collect(lockedRes.lowerRes(Math.min(hunger, configuration.Aviary.TICKDELTATIME * configuration.Agent.RESOURCECOLLECTIONSPEED)));
                        agent.setStationary(true);
                    }
                } else {
                    Pack pack = getPack(agent);
                    if (pack != null) {
                        double packHunger = getPack(agent).getMedHunger();
                        agent.collect(lockedRes.lowerRes(Math.min(packHunger, configuration.Aviary.TICKDELTATIME * configuration.Agent.RESOURCECOLLECTIONSPEED)));
                        agent.setStationary(true);
                    }
                }
            } else {
                agent.setStationary(false);
            }
        }
        else {
            if (agent.getConnectionCount() == 0) {
                double hunger = agent.getHunger();
                agent.collect(this.resGrid.resourceWithdraw(agent.getCoordinates(), Math.min(hunger, configuration.Aviary.TICKDELTATIME * configuration.Agent.RESOURCECOLLECTIONSPEED)));
            }
            else {
                packResCollection(agent);
            }
        }
    }

    void packResCollection(Agent agent) {
        Pack pack = getPack(agent);
        if(pack == null) return;
        double packHunger = pack.getMedHunger();
        int conCount = agent.getConnectionCount();
        int extent = (int)(((double)conCount / configuration.PropertyGrid.PROPERTY_AREA_VALUES[0]) * configuration.Agent.GRADIENTREFINEMENT);
        double resourceWithdrawn = this.resGrid.resourceWithdraw(agent.getCoordinates(), this.propertyGrid2.getIntersection(), Math.min(packHunger, configuration.Aviary.TICKDELTATIME * configuration.Agent.RESOURCECOLLECTIONSPEED), extent);
        agent.collect(resourceWithdrawn);
    }

    //---Pack direction calculations---

    double getPackDirFar(Agent agent){

        Pack argPack = getPack(agent);

        if(argPack != null){
            ArrayList<Agent> conAg = argPack.getConnected(agent);
            ArrayList<Double> dirs = new ArrayList<>(conAg.size());
            for(Iterator<Agent> iter = conAg.iterator(); iter.hasNext();) {
                Agent ag = iter.next();
                if(agent.getDistanceTo(ag.getX(), ag.getY()) > configuration.Agent.PACKDIST)
                    dirs.add(agent.dirToFace(ag.getX(), ag.getY()));
            }
            double resDir = angle.Angle.directionAddition(dirs);
            return resDir;
        }
        else
            return -1;
    }

    double getPackDirClose(Agent agent){

        Pack argPack = getPack(agent);

        if(argPack != null){
            ArrayList<Agent> conAg = argPack.getConnected(agent);
            ArrayList<Double> dirs = new ArrayList<>(conAg.size());
            for (Iterator<Agent> iter = conAg.iterator(); iter.hasNext();) {
                Agent ag = iter.next();
                if(agent.getDistanceTo(ag.getX(), ag.getY()) < configuration.Agent.COMDIST)
                    dirs.add(agent.dirToFace(ag.getX(), ag.getY()));
            }
            double resDir = angle.Angle.directionAddition(dirs);
            if(resDir == -1){
                return -1;
            }
            resDir += Math.PI;
            resDir = angle.Angle.normalizeDirection(resDir);
            return resDir;
        }
        else
            return -1;
    }

    Pack getSameSpeciesClosestUncomPack(Agent agent){
        Pack packTooClose = null;
        double minDist = configuration.Aviary.DEFX;
        Pack argPack = getPack(agent);
        if(argPack == null)
            return null;

        for (Iterator<Pack> iter = packs.iterator(); iter.hasNext();){
            Pack pack = iter.next();
            if(!(argPack == pack) && pack.getPackSpecies() == agent.getSpecies()){
                Point2D packCoordinates = pack.getPackCenter();
                double distance = agent.getDistanceTo(packCoordinates);
                if (minDist > distance && distance < configuration.Agent.PACKCOMDIST){
                    minDist = distance;
                    packTooClose = pack;
                }
            }
        }
        return packTooClose;
    }

    //-----Direction calculations-----

    double foodDirectionDecision(Agent agent){
        if (this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) {
            if (agent.getLockedResourceNode() != null) {
                return agent.dirToFace(agent.getLockedResourceNode().getCoordinates());
            }
            else {
                return -1;
            }
        }
        else if (this.resType == configuration.Resource.RESOURCETYPES.PLAIN){
            double direction;
            if (configuration.PropertyGrid.LOCKEDAREAS) {
                direction = this.resGrid.getGradientDirectionIntersection(agent, this.propertyGrid2.getIntersection());
            }
            else {
                direction = this.resGrid.getGradientDirection(agent);
            }
            if (direction != -1) {
                return angle.Angle.directionAddition(agent.getDirection(), direction);
            }
            else return -1;
        }

        return -1;
    }

    void directionDecision(Agent agent){    //Direction decision for a single agent, for lone agents only food decisioning, for pack agents depending on locked bolean variable value either only food, or only pack

        double packDirClose = getPackDirClose(agent);
        double packDirFar = getPackDirFar(agent);

        if(agent.getConnectionCount() == 0){
            if (this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) agResourceLocking(agent);
            double foodDir = foodDirectionDecision(agent);
            if(foodDir != -1){
                agent.setDirection(foodDir);
            }
        }
        else{
            if (this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) {
                if (packDirFar != -1) {
                    agent.setDirection(packDirFar);
                    return;
                }
                agResourceLocking(agent);
                double foodDir = foodDirectionDecision(agent);
                if (foodDir != -1) {
                    agent.setDirection(foodDir);
                    return;
                }
                if (packDirClose != -1) {
                    agent.setDirection(packDirClose);
                    return;
                }

                Pack packToClose = getSameSpeciesClosestUncomPack(agent);

                if (packToClose != null) {
                    agent.setDirection(agent.dirToFace(packToClose.getPackCenter()) + Math.PI);
                }
            }
            else {
                if (packDirFar != -1) {
                    agent.setDirection(packDirFar);
                    return;
                }
                if (packDirClose != -1) {
                    agent.setDirection(packDirClose);
                    return;
                }
                double foodDir = foodDirectionDecision(agent);
                if (foodDir != -1) {
                    agent.setDirection(foodDir);
                    return;
                }


                Pack packToClose = getSameSpeciesClosestUncomPack(agent);

                if (packToClose != null) {
                    agent.setDirection(agent.dirToFace(packToClose.getPackCenter()) + Math.PI);
                }
            }
        }
    }

    //-------------Screams-------------

    void scream(Agent agent){
        if(agent.getConnectionCount() == 0 && agent.wellFed() && agent.getLockedResourceNode() == null && agent.readyToAct()){
            loneAgentConnectionListen(agent);
        }
        if(agent.getConnectionCount() == 0 && agent.getLockedResourceNode() != null && configuration.Agent.LONERESSCREAM && this.resType == configuration.Resource.RESOURCETYPES.DISCRETE){
            loneAgentResScream(agent);
        }
        if(agent.getConnectionCount() != 0){
            packAgentResListen(agent);
        }
    }

    void loneAgentConnectionListen(Agent agent){

        if(agent.getValence() == 0) return;

        for(Iterator<Agent> iter = agents.iterator(); iter.hasNext();){
            Agent ag = iter.next();
            if(ag != agent && ag.getSpecies() == agent.getSpecies()){    //Different agent, same species
                double dist = agent.getDistanceTo(ag.getX(), ag.getY());
                if(dist <= configuration.Agent.CONNECTDIST
                        && (this.propertyGrid2.getPropertyArea(agent.getCoordinates())
                        == this.propertyGrid2.getPropertyArea(ag.getCoordinates()))
                ){
                    Pack agPack = getPack(ag);
                    if(agPack != null){
                        if(agPack.addAgent(agent)){
                            break;
                        }
                    }
                    else{
                        Pack newPack = new Pack();
                        newPack.addAgent(agent);
                        if(newPack.addAgent(ag)){
                            packs.add(newPack);
                            break;
                        }
                    }
                }
                else if(dist < configuration.Agent.SCRHEARDIST){
                    agent.face(ag.getCoordinates());
                }
            }
        }
    }

    void loneAgentResScream(Agent agent){
        for(Iterator<Agent> iter = agents.iterator(); iter.hasNext();){
            Agent ag = iter.next();
            double distance = agent.getDistanceTo(ag.getX(), ag.getY());
            if(ag.getLockedResourceNode() == null
                    && ag.getConnectionCount() == 0
                    && ag.getSpecies() == agent.getSpecies()
                    && distance < configuration.Agent.SCRHEARDIST){
                if(ag.getValence() == 0){
                    if(!ag.wellFedLone()){
                        ag.face(agent.getCoordinates());
                    }
                }
                else{
                    ag.face(agent.getCoordinates());
                }
            }
        }
    }

    void packAgentResListen(Agent agent){
        Pack argPack = getPack(agent);
        if(argPack == null)
            return;
        ArrayList<Agent> connAg = argPack.getConnected(agent);
        if (configuration.Resource.RESTYPE == configuration.Resource.RESOURCETYPES.DISCRETE) {
            connAg.forEach((ag) -> {
                if (ag.getLockedResourceNode() != null) {
                    if (agent.getLockedResourceNode() == null) {
                        if (agent.getLastHeardAge() < ag.getAge()) {
                            agent.face(ag.getCoordinates());
                            agent.setLastHeardAge(ag.getAge());
                        }
                    } else {
                        if (agent.getLockedResourceNode() != ag.getLockedResourceNode()) {
                            if (agent.getAge() < ag.getAge()) {
                                if (agent.getLastHeardAge() < ag.getAge()) {
                                    agent.setLastHeardAge(ag.getAge());
                                    agent.setLockedResourceNode(null);
                                    agent.setStationary(false);
                                    agent.face(ag.getCoordinates());
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    //-------------Rivalry-------------

    void fight(Agent agent1, Agent agent2){

        double coef1 = agent1.getConnectionCount() + 1;
        double coef2 = agent2.getConnectionCount() + 1;

        ArrayList<Agent> connected1 = null;
        ArrayList<Agent> connected2 = null;

        if(coef1 != 1){
            connected1 = getPack(agent1).getConnected(agent1);
        }

        if(coef2 != 1){
            connected2 = getPack(agent2).getConnected(agent2);
        }

        agent1.addToEnergy(-configuration.Aviary.TICKDELTATIME * configuration.Agent.FIGHTENERGYDRAINSPEED / coef1);
        agent2.addToEnergy(-configuration.Aviary.TICKDELTATIME * configuration.Agent.FIGHTENERGYDRAINSPEED / coef2);

        if(connected1 != null){
            connected1.forEach((ag) -> {
                ag.addToEnergy(-configuration.Aviary.TICKDELTATIME * configuration.Agent.FIGHTENERGYDRAINSPEED / coef1);
                app.App.processingRef.stroke(rivalryCl.getRGB(),100);
                app.App.processingRef.strokeWeight(2);
                app.App.processingRef.circle((float)ag.getX(), (float)ag.getY(), 5);
            });
        }
        if(connected2 != null){
            connected2.forEach((ag) -> {
                ag.addToEnergy(-configuration.Aviary.TICKDELTATIME * configuration.Agent.FIGHTENERGYDRAINSPEED / coef2);
                app.App.processingRef.stroke(rivalryCl.getRGB(),100);
                app.App.processingRef.strokeWeight(2);
                app.App.processingRef.circle((float)ag.getX(), (float)ag.getY(), 5);
            });
        }

        app.App.processingRef.stroke(rivalryCl.getRGB(),100);
        app.App.processingRef.strokeWeight(2);
        app.App.processingRef.line((float)(configuration.Render.ORIGINX + agent1.getX()), (float)(configuration.Render.ORIGINY + agent1.getY()), (float)(configuration.Render.ORIGINX + agent2.getX()), (float)(configuration.Render.ORIGINY + agent2.getY()));
    }

    void fights(){
        for (Agent ag1 : agents) {
            for (Agent ag2 : agents) {
                if (ag1.getSpecies() != ag2.getSpecies() && ag1.getDistanceTo(ag2.getX(), ag2.getY()) <= configuration.Agent.FIGHTDIST) {
                    fight(ag1, ag2);
                }
            }
        }
    }

    //----------Reproduction----------

    void reproduction(Agent argAg){
        Random r = new Random();
        boolean rep = false;
        double tech = r.nextDouble();

        if(argAg.getSpecies() == 0){
            if(tech <= configuration.Aviary.TICKDELTATIME * configuration.Agent.REPRODUCTPROB1)
                rep = true;
        }
        else{
            if(tech <= configuration.Aviary.TICKDELTATIME * configuration.Agent.REPRODUCTPROB2)
                rep = true;
        }

        if(rep){

            Agent child = Builder.buildAgent(argAg.getSpecies(), 0, 0, configuration.Aviary.DEFX, configuration.Aviary.DEFY);
            child.setCoordinates(new Point2D(argAg.getCoordinates()));
            child.setAge(0);
            child.updateSpeed();

            argAg.addToEnergy(-configuration.Agent.REPRODUCTCOST);
            agents.add(child);
            if(argAg.topCon() || argAg.getValence() == 0) return;
            Pack parentPack = getPack(argAg);
            if(parentPack != null){
                parentPack.addAgent(child);
            }
            else{
                Pack newPack = new Pack();
                newPack.addAgent(argAg);
                if(newPack.addAgent(child)){
                    packs.add(newPack);
                }
            }
        }
    }

    //------Property calculations------

    void updateProperty(Agent agent) {
        int propertyAreaIndex = this.propertyGrid2.getPropertyAreaIndex(agent.getCoordinates());
        PropertyArea propertyArea = this.propertyGrid2.getPropertyArea(propertyAreaIndex);
        if (agent.getPropertyArea() != propertyArea) {
            int areaValence = this.propertyGrid2.getProperty(agent.getCoordinates());
            removeAgentFromPacks(agent);
            agent.setValence(areaValence);
            agent.setPropertyArea(propertyArea);
            if(configuration.PropertyGrid.PAYMENT) {
                int newAreaPopulation = 0;
                ArrayList<Agent> newAreaAgents = new ArrayList<>();
                for (Iterator<Agent> iterator = this.agents.iterator(); iterator.hasNext(); ) {
                    Agent ag = iterator.next();
                    if (ag.getPropertyArea() == propertyArea) {
                        newAreaPopulation++;
                        newAreaAgents.add(ag);
                    }
                }
                if (newAreaPopulation <= 0) return;
                double payment = configuration.PropertyGrid.PAYMENTRATIO * agent.getEnergy();
                for (Iterator<Agent> iterator = newAreaAgents.iterator(); iterator.hasNext(); ) {
                    Agent ag = iterator.next();
                    ag.addToEnergy(payment / newAreaPopulation);
                }
                agent.addToEnergy(-payment);
            }
        }
    }

    void shiftIntersection() {
        double areaData[][] = new double[3][2];

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 2; j++) {
                areaData[i][j] = 0.;
            }
        }

        for(Iterator<Agent> iterator = this.agents.iterator(); iterator.hasNext();) {
            Agent agent = iterator.next();
            areaData[0][this.propertyGrid2.getPropertyAreaIndex(agent.getCoordinates())] += 1;
            areaData[1][this.propertyGrid2.getPropertyAreaIndex(agent.getCoordinates())] += agent.getEnergy();
        }

        for(int j = 0; j < 2; j++) {
            if(areaData[0][j] != 0) areaData[2][j] = areaData[1][j] / areaData[0][j];
            else areaData[2][j] = 0;
        }

        double intersectionX = (double)configuration.Aviary.DEFX / 2;
        double intersectionY = (double)configuration.Aviary.DEFY / 2;

        switch (this.shiftIntersection) {
            case POPULATION -> {
                //(double)App.DEFX / 10 + 0.8 *
                if (areaData[0][0] + areaData[0][1] != 0) {
                    intersectionX = (double)configuration.Aviary.DEFX * (areaData[0][0]) / (areaData[0][0] + areaData[0][1]);
                }
            }
            case ENERGY -> {
                if (areaData[1][0] + areaData[1][1] != 0) {
                    intersectionX = (double)configuration.Aviary.DEFX * (areaData[1][0]) / (areaData[1][0] + areaData[1][1]);
                }
            }
            case ENERGYDENSITY -> {
                if (areaData[2][0] + areaData[2][1] != 0) {
                    intersectionX = (double)configuration.Aviary.DEFX * (areaData[2][0]) / (areaData[2][0] + areaData[2][1]);
                }
            }
        }

        double speed = 0.03;

        double intersection = this.propertyGrid2.getIntersection() + (intersectionX - this.propertyGrid2.getIntersection()) * speed;

        this.propertyGrid2.setIntersection(intersection);
    }

    //---------------Main---------------

    public void initialize() {

        if(this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) this.resGroup = Builder.buildResourceGroup();
        if(this.resType == configuration.Resource.RESOURCETYPES.PLAIN) this.resGrid = Builder.buildResourceGrid();

        this.propertyGrid2 = new PropertyGrid2<>(configuration.Aviary.DEFX, configuration.Aviary.DEFY);
        this.propertyGrid2.fillPropertyAreas(configuration.PropertyGrid.PROPERTY_AREA_VALUES, configuration.PropertyGrid.PROPERTY_AREA_COLORS);

        this.agents = Builder.buildAgentArray();

        this.packs = new ArrayList<>();

        this.observer = new Observer(this);
        this.observer.fillTimeGraphs();
        if (configuration.Observer.REPORTTOFILE) {
            this.observer.setDataReportFileName(this.observer.formTimeStampDataFileName());
            this.observer.setParameterReportFileName(this.observer.formTimeStampParametersFileName());

            ArrayList<String> reportDataHeaders = new ArrayList<>();
            reportDataHeaders.add("Population");

            reportDataHeaders.add("Population area 0");
            reportDataHeaders.add("Population area 1");
            reportDataHeaders.add("Population area 2");
            reportDataHeaders.add("Population area 3");

            reportDataHeaders.add("Energy density area 0");
            reportDataHeaders.add("Energy density area 1");
            reportDataHeaders.add("Energy density area 2");
            reportDataHeaders.add("Energy density area 3");

            reportDataHeaders.add("Pack count area 0");
            reportDataHeaders.add("Pack count area 1");
            reportDataHeaders.add("Pack count area 2");

            reportDataHeaders.add("Resource area 0");
            reportDataHeaders.add("Resource area 1");
            reportDataHeaders.add("Resource area 2");
            reportDataHeaders.add("Resource area 3");

            this.observer.setReportDataHeaders(reportDataHeaders);
            this.observer.writeDataHeaders();

            this.observer.parameterReport();
        }
    }

    void preProcedure() {
        if (this.resType == configuration.Resource.RESOURCETYPES.DISCRETE && this.resGroup != null) resGroup.replenishNodes(configuration.Aviary.TICKDELTATIME);
        if (this.resType == configuration.Resource.RESOURCETYPES.PLAIN && this.resGrid != null) resGrid.replenish(configuration.Aviary.TICKDELTATIME);
        if (this.shiftIntersection != configuration.PropertyGrid.SHIFTINTERSECTIONMODES.STATIC) shiftIntersection();
    }

    void mainProcedure() {
        ArrayList<Agent> reproductList = new ArrayList<>();

        for (Iterator<Agent> iter = agents.iterator(); iter.hasNext();){
            Agent ag = iter.next();

            if(ag.dead()){
                removeAgentFromPacks(ag);
                iter.remove();
                continue;
            }

            if(!ag.wellFed() && ag.getConnectionCount() != 0){
                removeAgentFromPacks(ag);
            }

            directionDecision(ag);
            agResCollection(ag);
            scream(ag);

            updateProperty(ag);
            ag.step(configuration.Aviary.TICKDELTATIME);



            if(ag.getEnergy() > configuration.Agent.REPRODUCTCOST + configuration.Agent.SUFFENERGY && ag.getAge() >= configuration.Agent.REPRODUCTLOW && ag.getAge() <= configuration.Agent.REPRODUCTHIGH){
                reproductList.add(ag);
            }

            if(ag.getConnectionCount() == 0){
                ag.eatCollected();
            }

        }

        reproductList.forEach(this::reproduction);
    }

    void postProcedure() {
        packs.forEach((pack) -> {
            pack.collectedResDistribution();
            pack.energyDepletion(configuration.Aviary.TICKDELTATIME);
        });

        if (configuration.Agent.FIGHTS) fights();

        agents.forEach((ag) -> {
            ag.resetCollectedRes();
            ag.resetLastHeardAge();
            ag.resetSeenRes();
        });
        this.observer.addGraphData();
        if (configuration.Observer.REPORTTOFILE) this.observer.reportRow();

        this.tk++;
    }

    boolean endPredicate() {
        double intersection = this.propertyGrid2.getIntersection();
        double x = intersection;
        return this.observer.getReportRowCtr() >= 600
                || (x <= 0.00001)
                || (x >= configuration.Aviary.DEFX - 0.00001)
                || (getPopulation() <= 0);
    }

    void deinitialize() {
        if (configuration.Observer.REPORTTOFILE) this.observer.finalReport();
    }

    void tick(){
        preProcedure();
        mainProcedure();
        postProcedure();
    }

    public boolean run(){                                                       //Main method                                                                           //Perform animation tick
        render();
        tick();
        boolean end = endPredicate();
        if (end) deinitialize();
        return end;
    }

    //---------------------------------
    //---------------------------------


    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    void renderRes(){                                                                   //Renders resources
        if(this.resGroup != null) this.resGroup.render();
        if(this.resGrid != null) this.resGrid.render();
    }

    void renderPropertyGrid() { this.propertyGrid2.render(); }

    void renderPacks(){
        packs.forEach(Pack::render);
    }

    void renderAgent(){                                                                 //Renders agents
        agents.forEach(Agent::render);
    }

    void renderObserver() {
       this.observer.render();
    }

    void render(){                                                    //Renders aviary
        app.App.processingRef.background(0);
        renderPropertyGrid();
        if(configuration.Render.RENDER) {
            renderRes();
            renderPacks();
            renderAgent();
        }
        renderObserver();
    }

    //-----------------------------------
    //-----------------------------------


}
