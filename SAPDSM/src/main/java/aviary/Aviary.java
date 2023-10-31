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
    PropertyGrid<Integer> propertyGrid;
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
        this.propertyGrid = null;
        this.agents = null;
        this.packs = null;
        this.observer = null;
    }

    //--------------------------------------
    //--------------------------------------

    //---------------------------------
    //-----------  Getters  -----------
    //---------------------------------


    public Color getRivalryCl() {
        return rivalryCl;
    }

    public ResourceGroup getResGroup() {
        return resGroup;
    }

    public PropertyGrid<Integer> getPropertyGrid() {
        return propertyGrid;
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public ArrayList<Pack> getPacks() {
        return packs;
    }

    //---------------------------------

    Pack getPack(Agent agent){

        if(agent.getConCount() == 0) return null;

        for (Pack pck : packs) {
            if (pck.contains(agent)) {
                return pck;
            }
        }
        return null;
    }

    int getPopulation() {
        return this.agents.size();
    }

    double[][] getDataInAreas() {
        double areaData[][] = new double[4][4];

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {
                areaData[i][j] = 0.;
            }
        }

        for(Iterator<Agent> iterator = this.agents.iterator(); iterator.hasNext();) {
            Agent agent = iterator.next();
            areaData[0][this.propertyGrid.getPropertyAreaIndex(agent.getCoordinates())] += 1;
            areaData[1][this.propertyGrid.getPropertyAreaIndex(agent.getCoordinates())] += agent.getEnergy();
        }
        for(Iterator<Pack> iterator = this.packs.iterator(); iterator.hasNext();) {
            Pack pack = iterator.next();
            areaData[2][this.propertyGrid.getPropertyAreaIndex(pack.getPackCenter())] += 1;
        }

        if (configuration.Resource.RESTYPE == configuration.Resource.RESOURCETYPES.PLAIN && this.resGrid != null) {
            double[] resourceInAreas = this.resGrid.getResourceInAreas(this.propertyGrid);
            for (int i = 0; i < 4; i++) {
                areaData[3][i] = resourceInAreas[i];
            }
        }
        else if (configuration.Resource.RESTYPE == configuration.Resource.RESOURCETYPES.DISCRETE && this.resGroup != null) {
            double[] resourceInAreas = this.resGroup.getResourceInAreas(this.propertyGrid);
            for (int i = 0; i < 4; i++) {
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
        for (Iterator<Pack> iter = packs.iterator(); iter.hasNext();){
            Pack pack = iter.next();
            if(pack.contains(agent)){
                //println("PACK IDX", at, "CONTAINED THIS AGENT, REMOVING AGENT FROM THIS PACK");
                pack.removeAgent(agent);
                if(pack.empty()){
                    //println("PACK TURNED OUT TO BE EMPTY, REMOVING THE WHOLE PACK");
                    iter.remove();
                }
                break;
            }

        }
        //println("maybe removed a pack, current pack count:", packs.size());
    }

    //------Resource calculations------

    void agResourceLocking(Agent agent){

        ResourceNode lockedRes = agent.getLockedRes();

        if(lockedRes != null){
            if(lockedRes.empty() || agent.getDistTo(lockedRes.getCoordinates()) > configuration.Agent.VISUALDIST){
                agent.setLockedRes(null);
                agent.setStationary(false);
            }
        }
        else{
            if(agent.getConCount() == 0 && agent.wellFedLone()){
                return;
            }
            ArrayList<ResourceNode> resources = resGroup.getVisibleResNodes(agent.getX(), agent.getY(), configuration.Agent.VISUALDIST);
            double minDist = agent.getDistTo(resources.get(0).getCoordinates()) + 1;
            int minDistIdx = -1;
            int idx = 0;
            for(Iterator<ResourceNode> iter = resources.iterator(); iter.hasNext();){
                ResourceNode res = iter.next();
                if(!res.empty() && this.propertyGrid.getPropertyAreaIndex(agent.getCoordinates()) == this.propertyGrid.getPropertyAreaIndex(res.getCoordinates())) {
                    double currDist = agent.getDistTo(res.getCoordinates());
                    if(minDist > currDist){
                        minDist = currDist;
                        minDistIdx = idx;
                    }
                }
                idx++;
            }
            if(minDistIdx != -1){
                if(minDist <= configuration.Agent.VISUALDIST){
                    ResourceNode foundRes = resources.get(minDistIdx);
                    agent.setLockedRes(foundRes);
                    return;
                }
            }
            agent.setLockedRes(null);
            agent.setStationary(false);
        }
    }

    void agResCollection(Agent agent){
        if (this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) {
            ResourceNode lockedRes = agent.getLockedRes();
            if (lockedRes == null) {
                agent.setStationary(false);
                return;
            }
            double dist = agent.getDistTo(lockedRes.getCoordinates());
            if (dist <= lockedRes.getSize() + 4) {
                if (agent.getConCount() == 0) {
                    double hunger = agent.getHunger();
                    if (hunger == 0) {
                        agent.setLockedRes(null);
                        agent.setStationary(false);
                    } else {
                        agent.collect(lockedRes.lowerRes(Math.min(hunger, configuration.Agent.RESECOLLECTEDPERSTEP)));
                        agent.setStationary(true);
                    }
                } else {
                    Pack pack = getPack(agent);
                    if (pack != null) {
                        double packHunger = getPack(agent).getMedHunger();
                        agent.collect(lockedRes.lowerRes(Math.min(packHunger, configuration.Agent.RESECOLLECTEDPERSTEP)));
                        agent.setStationary(true);
                    }
                }
            } else {
                agent.setStationary(false);
            }
        }
        else {
            if (agent.getConCount() == 0) {
                double hunger = agent.getHunger();
                agent.collect(this.resGrid.resourceWithdraw(agent.getCoordinates(), Math.min(hunger, configuration.Agent.RESECOLLECTEDPERSTEP)));
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
        int conCount = agent.getConCount();
        int extent = (int)(((double)conCount / configuration.PropertyGrid.PROPERTY_AREA_VALUES[0]) * configuration.Agent.GRADIENTREFINEMENT);
        double resourceWithdrawn = this.resGrid.resourceWithdraw(agent.getCoordinates(), this.propertyGrid.getIntersection(), Math.min(packHunger, configuration.Agent.RESECOLLECTEDPERSTEP), extent);
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
                if(agent.getDistTo(ag.getX(), ag.getY()) > configuration.Agent.PACKDIST)
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
                if(agent.getDistTo(ag.getX(), ag.getY()) < configuration.Agent.COMDIST)
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
        double minDist = configuration.Render.DEFX;
        Pack argPack = getPack(agent);
        if(argPack == null)
            return null;

        for (Iterator<Pack> iter = packs.iterator(); iter.hasNext();){
            Pack pack = iter.next();
            if(!(argPack == pack) && pack.getPackSpecies() == agent.getSpecies()){
                Point2D packCoordinates = pack.getPackCenter();
                double distance = agent.getDistTo(packCoordinates);
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
            if (agent.getLockedRes() != null) {
                return agent.dirToFace(agent.getLockedRes().getCoordinates());
            }
            else {
                return -1;
            }
        }
        else if (this.resType == configuration.Resource.RESOURCETYPES.PLAIN){
            double direction;
            if (configuration.PropertyGrid.LOCKEDAREAS) {
                direction = this.resGrid.getGradientDirectionIntersection(agent, this.propertyGrid.getIntersection());
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

        if(agent.getConCount() == 0){
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
        if(agent.getConCount() == 0 && agent.wellFed() && agent.getLockedRes() == null && agent.readyToAct()){
            loneAgentConnectionListen(agent);
        }
        if(agent.getConCount() == 0 && agent.getLockedRes() != null && configuration.Agent.LONERESSCREAM && this.resType == configuration.Resource.RESOURCETYPES.DISCRETE){
            loneAgentResScream(agent);
        }
        if(agent.getConCount() != 0){
            packAgentResListen(agent);
        }
    }

    void loneAgentConnectionListen(Agent agent){

        if(agent.getValence() == 0) return;

        for(Iterator<Agent> iter = agents.iterator(); iter.hasNext();){
            Agent ag = iter.next();
            if(ag != agent && ag.getSpecies() == agent.getSpecies()){    //Different agent, same species
                double dist = agent.getDistTo(ag.getX(), ag.getY());
                if(dist <= configuration.Agent.CONNECTDIST
                        && (this.propertyGrid.getPropertyArea(agent.getCoordinates())
                        == this.propertyGrid.getPropertyArea(ag.getCoordinates()))
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
            double distance = agent.getDistTo(ag.getX(), ag.getY());
            if(ag.getLockedRes() == null
                    && ag.getConCount() == 0
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
                if (ag.getLockedRes() != null) {
                    if (agent.getLockedRes() == null) {
                        if (agent.getLastHeardAge() < ag.getAge()) {
                            agent.face(ag.getCoordinates());
                            agent.setLastHeardAge(ag.getAge());
                        }
                    } else {
                        if (agent.getLockedRes() != ag.getLockedRes()) {
                            if (agent.getAge() < ag.getAge()) {
                                if (agent.getLastHeardAge() < ag.getAge()) {
                                    agent.setLastHeardAge(ag.getAge());
                                    agent.setLockedRes(null);
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

        double coef1 = agent1.getConCount() + 1;
        double coef2 = agent2.getConCount() + 1;

        ArrayList<Agent> connected1 = null;
        ArrayList<Agent> connected2 = null;

        if(coef1 != 1){
            connected1 = getPack(agent1).getConnected(agent1);
        }

        if(coef2 != 1){
            connected2 = getPack(agent2).getConnected(agent2);
        }

        agent1.addToEnergy(-configuration.Agent.NRGPERFIGHT / coef1);
        agent2.addToEnergy(-configuration.Agent.NRGPERFIGHT / coef2);

        if(connected1 != null){
            connected1.forEach((ag) -> {
                ag.addToEnergy(-configuration.Agent.NRGPERFIGHT / coef1);
                app.App.processingRef.stroke(rivalryCl.getRGB(),100);
                app.App.processingRef.strokeWeight(2);
                app.App.processingRef.circle((float)ag.getX(), (float)ag.getY(), 5);
            });
        }
        if(connected2 != null){
            connected2.forEach((ag) -> {
                ag.addToEnergy(-configuration.Agent.NRGPERFIGHT / coef2);
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
        for(Iterator<Agent> iter1 = agents.iterator(); iter1.hasNext();){
            Agent ag1 = iter1.next();
            for(Iterator<Agent> iter2 = agents.iterator(); iter2.hasNext();){
                Agent ag2 = iter2.next();
                if(ag1.getSpecies() != ag2.getSpecies() && ag1.getDistTo(ag2.getX(), ag2.getY()) <= configuration.Agent.FIGHTDIST){
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
            if(tech <= configuration.Agent.REPRODUCTPROB1)
                rep = true;
        }
        else{
            if(tech <= configuration.Agent.REPRODUCTPROB2)
                rep = true;
        }

        if(rep){

            Agent child = Builder.buildAgent(argAg.getSpecies(), 0, 0, configuration.Render.DEFX, configuration.Render.DEFY);
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
        int propertyAreaIndex = this.propertyGrid.getPropertyAreaIndex(agent.getCoordinates());
        PropertyArea propertyArea = this.propertyGrid.getPropertyArea(propertyAreaIndex);
        if (agent.getPropertyArea() != propertyArea) {
            int areaValence = this.propertyGrid.getProperty(agent.getCoordinates());
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
        double areaData[][] = new double[3][4];

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {
                areaData[i][j] = 0.;
            }
        }

        for(Iterator<Agent> iterator = this.agents.iterator(); iterator.hasNext();) {
            Agent agent = iterator.next();
            areaData[0][this.propertyGrid.getPropertyAreaIndex(agent.getCoordinates())] += 1;
            areaData[1][this.propertyGrid.getPropertyAreaIndex(agent.getCoordinates())] += agent.getEnergy();
        }

        for(int j = 0; j < 4; j++) {
            if(areaData[0][j] != 0) areaData[2][j] = areaData[1][j] / areaData[0][j];
            else areaData[2][j] = 0;
        }

        double intersectionX = (double)configuration.Render.DEFX / 2;
        double intersectionY = (double)configuration.Render.DEFY / 2;

        switch (this.shiftIntersection) {
            case POPULATION -> {
                //(double)App.DEFX / 10 + 0.8 *
                if (areaData[0][0] + areaData[0][1] + areaData[0][2] + areaData[0][3] != 0) {
                    intersectionX = (double)configuration.Render.DEFX * (areaData[0][0] + areaData[0][1]) / (areaData[0][0] + areaData[0][1] + areaData[0][2] + areaData[0][3]);
                    intersectionY = (double)configuration.Render.DEFY * (areaData[0][0] + areaData[0][2]) / (areaData[0][0] + areaData[0][1] + areaData[0][2] + areaData[0][3]);
                }
            }
            case ENERGY -> {
                if (areaData[1][0] + areaData[1][1] + areaData[1][2] + areaData[1][3] != 0) {
                    intersectionX = (double)configuration.Render.DEFX * (areaData[1][0] + areaData[1][1]) / (areaData[1][0] + areaData[1][1] + areaData[1][2] + areaData[1][3]);
                    intersectionY = (double)configuration.Render.DEFY * (areaData[1][0] + areaData[1][2]) / (areaData[1][0] + areaData[1][1] + areaData[1][2] + areaData[1][3]);
                }
            }
            case ENERGYDENSITY -> {
                if (areaData[2][0] + areaData[2][1] + areaData[2][2] + areaData[2][3] != 0) {
                    intersectionX = (double)configuration.Render.DEFX * (areaData[2][0] + areaData[2][1]) / (areaData[2][0] + areaData[2][1] + areaData[2][2] + areaData[2][3]);
                    intersectionY = (double)configuration.Render.DEFY * (areaData[2][0] + areaData[2][2]) / (areaData[2][0] + areaData[2][1] + areaData[2][2] + areaData[2][3]);
                }
            }
        }

        Point2D intersection = new Point2D();

        double speed = 0.03;

        intersection.setX(this.propertyGrid.getIntersection().getX() + (intersectionX - this.propertyGrid.getIntersection().getX()) * speed);
        intersection.setY(this.propertyGrid.getIntersection().getY() + (intersectionY - this.propertyGrid.getIntersection().getY()) * speed);

        this.propertyGrid.setIntersection(intersection);
    }

    //---------------Main---------------

    public void initialize() {

        if(this.resType == configuration.Resource.RESOURCETYPES.DISCRETE) this.resGroup = Builder.buildResourceGroup();
        if(this.resType == configuration.Resource.RESOURCETYPES.PLAIN) this.resGrid = Builder.buildResourceGrid();

        this.propertyGrid = new PropertyGrid<>(configuration.Render.DEFX, configuration.Render.DEFY);
        this.propertyGrid.fillPropertyAreas(configuration.PropertyGrid.PROPERTY_AREA_VALUES, configuration.PropertyGrid.PROPERTY_AREA_COLORS);

        this.agents = Builder.buildAgentArray();

        this.packs = new ArrayList<>();

//        this.observer = new Observer(this);
//        this.observer.fillTimeGraphs();
//        if (App.REPORTTOFILE) {
//            this.observer.setDataReportFileName(this.observer.formTimeStampDataFileName());
//            this.observer.setParameterReportFileName(this.observer.formTimeStampParametersFileName());
//
//            ArrayList<String> reportDataHeaders = new ArrayList<>();
//            reportDataHeaders.add("Population");
//
//            reportDataHeaders.add("Population area 0");
//            reportDataHeaders.add("Population area 1");
//            reportDataHeaders.add("Population area 2");
//            reportDataHeaders.add("Population area 3");
//
//            reportDataHeaders.add("Energy density area 0");
//            reportDataHeaders.add("Energy density area 1");
//            reportDataHeaders.add("Energy density area 2");
//            reportDataHeaders.add("Energy density area 3");
//
//            reportDataHeaders.add("Pack count area 0");
//            reportDataHeaders.add("Pack count area 1");
//            reportDataHeaders.add("Pack count area 2");
//
//            reportDataHeaders.add("Resource area 0");
//            reportDataHeaders.add("Resource area 1");
//            reportDataHeaders.add("Resource area 2");
//            reportDataHeaders.add("Resource area 3");
//
//            this.observer.setReportDataHeaders(reportDataHeaders);
//            this.observer.writeDataHeaders();
//
//            this.observer.parameterReport();
//        }
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

            if(!ag.wellFed() && ag.getConCount() != 0){
                removeAgentFromPacks(ag);
            }

            directionDecision(ag);
            agResCollection(ag);
            scream(ag);

            updateProperty(ag);
            ag.step();



            if(ag.getEnergy() > configuration.Agent.REPRODUCTCOST + configuration.Agent.SUFFENERGY && ag.getAge() >= configuration.Agent.REPRODUCTLOW && ag.getAge() <= configuration.Agent.REPRODUCTHIGH){
                reproductList.add(ag);
            }

            if(ag.getConCount() == 0){
                ag.eatCollected();
            }

        }

        reproductList.forEach(this::reproduction);
    }

    void postProcedure() {
        packs.forEach((pack) -> {
            pack.collectedResDistribution();
            pack.energyDepletion();
        });

        if (configuration.Agent.FIGHTS) fights();

        agents.forEach((ag) -> {
            ag.resetCollectedRes();
            ag.resetLastHeardAge();
            ag.resetSeenRes();
        });
//        this.observer.addGraphData();
//        if (App.REPORTTOFILE) this.observer.reportRow();

        this.tk++;
    }

    boolean endPredicate() {
        Point2D intersection = this.propertyGrid.getIntersection();
        double x = intersection.getX();
        double y = intersection.getY();
//        return this.observer.getReportRowCtr() >= 600
//                || (x <= 0.00001 && y <= 0.00001)
//                || (x <= 0.00001 && y >= App.DEFY - 0.00001)
//                || (x >= App.DEFX - 0.00001 && y <= 0.00001)
//                || (x >= App.DEFX - 0.00001 && y >= App.DEFY - 0.00001)
//                || (getPopulation() <= 0);
        return false;
    }

    void deinitialize() {
//        if (configuration.Observer.REPORTTOFILE) this.observer.finalReport();
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

    void renderPropertyGrid() { this.propertyGrid.render(); }

    void renderPacks(){
        packs.forEach(Pack::render);
    }

    void renderAgent(){                                                                 //Renders agents
        agents.forEach(Agent::render);
    }

    void renderObserver() {
//        this.observer.render();
    }

    void render(){                                                    //Renders aviary
        app.App.processingRef.background(0);
        renderPropertyGrid();
        if(configuration.Render.RENDER) {
            renderRes();
            renderPacks();
            renderAgent();
        }
        //renderObserver();
    }

    //-----------------------------------
    //-----------------------------------


}
