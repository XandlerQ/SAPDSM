package builder;

import agent.Agent;
import resource.ResourceGroup;
import resource.ResourceGrid;

import java.util.ArrayList;
import java.util.Random;

public class Builder {
    public static ArrayList<Agent> buildAgentArray() {
        ArrayList<Agent> agents = new ArrayList<>(configuration.Aviary.INITAGENTAMOUNT1 + configuration.Aviary.INITAGENTAMOUNT2);

        Random r = new Random();
        if (!configuration.Aviary.REGIONSPECIFIC) {
            for (int i = 0; i < configuration.Aviary.INITAGENTAMOUNT1; i++) {
                agents.add(buildAgent(0, 0, 0, configuration.Render.DEFX, configuration.Render.DEFY));
            }

            for (int i = 0; i < configuration.Aviary.INITAGENTAMOUNT2; i++) {
                agents.add(buildAgent(1, 0, 0, configuration.Render.DEFX, configuration.Render.DEFY));
            }
        }
        else {
            int agentsPerRegion1 = configuration.Aviary.INITAGENTAMOUNT1 / 4;
            int agentsPerRegion2 = configuration.Aviary.INITAGENTAMOUNT2 / 4;
            for (int i = 0; i < agentsPerRegion1; i++) {
                agents.add(buildAgent(0, 0, 0, configuration.Render.DEFX / 2., configuration.Render.DEFY / 2.));
            }
            for (int i = 0; i < agentsPerRegion1; i++) {
                agents.add(buildAgent(0, 0, configuration.Render.DEFY / 2., configuration.Render.DEFX / 2., configuration.Render.DEFY / 2.));
            }
            for (int i = 0; i < agentsPerRegion1; i++) {
                agents.add(buildAgent(0, configuration.Render.DEFX / 2., 0, configuration.Render.DEFX / 2., configuration.Render.DEFY / 2.));
            }
            for (int i = 0; i < agentsPerRegion1; i++) {
                agents.add(buildAgent(0, configuration.Render.DEFX / 2., configuration.Render.DEFY / 2., configuration.Render.DEFX / 2., configuration.Render.DEFY / 2.));
            }
        }

        return agents;
    }

    public static Agent buildAgent(int species, double originX, double originY, double sideX, double sideY) {
        Random r = new Random();

        Agent ag = new Agent();
        ag.setSpecies(species);
        if (species == 1) {
            if (configuration.Aviary.SYSSPAWN) ag.setCoordinates(originX + sideX / 20 + (9 * sideX / 20) * r.nextDouble(),
                    originY + sideY / 20 + (9 * sideY / 10) * r.nextFloat()
            );
            else ag.setCoordinates(originX + sideX / 20 + (9 * sideX / 10) * r.nextDouble(),
                    originY + sideY / 20 + (9 * sideY / 10) * r.nextFloat()
            );
        } else {
            if (configuration.Aviary.SYSSPAWN) ag.setCoordinates(originX + 19 * sideX / 20 - (9 * sideX / 20) * r.nextDouble(),
                    originY + sideY / 20 + (9 * sideY / 10) * r.nextFloat()
            );
            else ag.setCoordinates(originX + sideX / 20 + (9 * sideX / 10) * r.nextDouble(),
                    originY + sideY / 20 + (9 * sideY / 10) * r.nextFloat()
            );
        }

        ag.setDirection(2 * Math.PI * r.nextDouble());
        if (species == 0) ag.setBaseSpeed(configuration.Agent.BASESPEED1);
        else ag.setBaseSpeed(configuration.Agent.BASESPEED2);

        ag.setAge(2 * configuration.Agent.BASEMAXAGE * r.nextDouble() / 3);
        ag.setMaxAge(4 * configuration.Agent.BASEMAXAGE / 5 + 2 * configuration.Agent.BASEMAXAGE * r.nextDouble() / 5);
        ag.setAgeIncr(configuration.Agent.AGEPERSTEP);

        ag.setMaxEnergy(configuration.Agent.MAXENERGY);
        ag.setEnergy(configuration.Agent.SUFFENERGY + configuration.Agent.SUFFENERGY / 1.2);
        ag.setSuffEnergy(configuration.Agent.SUFFENERGY);

        if (species == 0) ag.setEnergyDecr(configuration.Agent.NRGPERSTEP1);
        else ag.setEnergyDecr(configuration.Agent.NRGPERSTEP2);

        if (species == 0) ag.setValence(configuration.Agent.VALENCE1);
        else ag.setValence(configuration.Agent.VALENCE2);

        ag.setActCtrPeak(configuration.Agent.ACTCTRPEAK);

        return ag;
    }

    public static ResourceGroup buildResourceGroup() {
        ResourceGroup resGroup = new ResourceGroup(configuration.Render.DEFX, configuration.Render.DEFY, configuration.Resource.QUADX, configuration.Resource.QUADY, configuration.Resource.RESPERQUAD);
        resGroup.fillResNodes(configuration.Resource.BASERES, 0.5, configuration.Resource.RESREPSPEED, configuration.Resource.RESREPCTRPEAK);

        return resGroup;
    }

    public static ResourceGrid buildResourceGrid() {
        ResourceGrid resGrid = new ResourceGrid(configuration.Render.DEFX, configuration.Render.DEFY, configuration.Resource.PLAINX, configuration.Resource.PLAINY);
        resGrid.fillResources(configuration.Resource.BASERES, 0.5, configuration.Resource.RESREPSPEED, configuration.Resource.RESREPCTRPEAK);

        return resGrid;
    }
}
