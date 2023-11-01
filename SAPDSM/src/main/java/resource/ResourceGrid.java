package resource;

import point.Point2D;
import agent.*;
import area.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ResourceGrid {
    private double defX;
    private double defY;
    private int grCtX;
    private int grCtY;
    private ArrayList<Resource> resources;
    private int resCount;

    private Color cl = configuration.Resource.RESOURCECOLOR;

    //--------------------------------------
    //-----------  Constructors  -----------
    //---------------------------------------

    public ResourceGrid() {
        this.defX = 0;
        this.defY = 0;
        this.grCtX = 0;
        this.grCtY = 0;
        this.resources = null;
    }

    public ResourceGrid(double defX, double defY, int grCtX, int grCtY) {
        this.defX = defX;
        this.defY = defY;
        this.grCtX = grCtX;
        this.grCtY = grCtY;
        this.resCount = grCtX * grCtY;
        this.resources = new ArrayList<>(this.resCount);
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

    public int getResCount() {
        return resCount;
    }
    public Resource getResourceAtIndex(int index) {
        if (index < 0 || index >= this.resources.size()) return null;
        return this.resources.get(index);
    }

    public Resource getResourceAtCell(int i, int j) {
        if (i < 0 || i >= this.grCtX ||
                j < 0 || j >= this.grCtY) {
            return null;
        }
        return this.resources.get(i * this.grCtY + j);
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Setters  -----------
    //---------------------------------

    public void setMaxRes(double maxRes){
        this.resources.forEach((res) -> {res.setMaxResource(maxRes);});
    }

    public void setResRepSpeed(double resRepSpeed){
        this.resources.forEach((res) -> {res.setReplenishmentSpeed(resRepSpeed);});
    }

    public void setRepCtrPeak(int repCtrPeak){
        this.resources.forEach((res) -> {res.setReplenishmentCooldown(repCtrPeak);});
    }

    //---------------------------------
    //---------------------------------

    //---------------------------------
    //-----------  Methods  -----------
    //---------------------------------

    public void fillResources(double maxRes, double fraction, double resRepSpeed, int repCtrPeak) {
        for(int i = 0; i < this.grCtX; i++) {
            for (int j = 0; j < this.grCtY; j++) {
                this.resources.add(
                        new Resource(
                                maxRes,
                                fraction,
                                resRepSpeed,
                                repCtrPeak
                        )
                );
            }
        }
    }

    public void replenish(double deltaTime){
        this.resources.forEach((res) -> {res.replenish(deltaTime);});
    }

    public double getGradientDirectionIntersection(Agent agent, Point2D intersection) {
        double gradientDirection;

        double sideX = this.defX / this.grCtX;
        double sideY = this.defY / this.grCtY;

        double x = agent.getX();
        double y = agent.getY();

        int iDot = (int)(x / sideX);
        int jDot = (int)(y / sideY);

        int iIntersection = (int)(intersection.getX() / sideX);
        int jIntersection = (int)(intersection.getY() / sideY);

        int areaIndex;

        if (agent.getX() <= intersection.getX()) {
            if (agent.getY() <= intersection.getY()) areaIndex = 0;
            else areaIndex = 1;
        }
        else {
            if (agent.getY() <= intersection.getY()) areaIndex = 2;
            else areaIndex = 3;
        }

        int iSpan = configuration.Agent.GRADIENTREFINEMENT;
        int jSpan = configuration.Agent.GRADIENTREFINEMENT;

        int iOrigin = 0,
                jOrigin = 0,
                iTarget = 0,
                jTarget = 0;

        switch (areaIndex) {
            case 0 -> {
                iOrigin = Math.max(0, iDot - iSpan);
                jOrigin = Math.max(0, jDot - jSpan);

                iTarget = Math.min(iDot + iSpan, iIntersection - 1);
                jTarget = Math.min(jDot + jSpan, jIntersection - 1);
            }
            case 1 -> {
                iOrigin = Math.max(0, iDot - iSpan);
                jOrigin = Math.max(jIntersection + 1, jDot - jSpan);

                iTarget = Math.min(iDot + iSpan, iIntersection - 1);
                jTarget = Math.min(jDot + jSpan, this.grCtY - 1);
            }
            case 2 -> {
                iOrigin = Math.max(iIntersection + 1, iDot - iSpan);
                jOrigin = Math.max(0, jDot - jSpan);

                iTarget = Math.min(iDot + iSpan, this.grCtX - 1);
                jTarget = Math.min(jDot + jSpan, jIntersection - 1);
            }
            case 3 -> {
                iOrigin = Math.max(iIntersection + 1, iDot - iSpan);
                jOrigin = Math.max(jIntersection + 1, jDot - jSpan);

                iTarget = Math.min(iDot + iSpan, this.grCtX - 1);
                jTarget = Math.min(jDot + jSpan, this.grCtY - 1);
            }
        }


        double resLeft = 0,
                resRight = 0,
                resTop = 0,
                resBottom = 0;

        for (int i = iOrigin; i < iDot; i++) {
            for (int j = jOrigin; j <= jTarget; j++) {
                resLeft += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        for (int i = iDot + 1; i <= iTarget; i++) {
            for (int j = jOrigin; j <= jTarget; j++) {
                resRight += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        for (int i = iOrigin; i <= iTarget; i++) {
            for (int j = jOrigin; j < jDot; j++) {
                resTop += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        for (int i = iOrigin; i <= iTarget; i++) {
            for (int j = jDot + 1; j <= jTarget; j++) {
                resBottom += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        agent.setSeenResource(resLeft + resRight + resTop + resBottom);

        double shiftX = resRight - resLeft;
        double shiftY = resBottom - resTop;

        if (Math.abs(shiftX) <= 2 * configuration.Resource.BASERES) shiftX = 0;
        if (Math.abs(shiftY) <= 2 * configuration.Resource.BASERES) shiftY = 0;

        if (shiftX == 0 && shiftY == 0) return -1;

        Point2D gradientDot = new Point2D(agent.getX() + shiftX, agent.getY() + shiftY);

        gradientDirection = angle.Angle.directionFromTo(agent.getCoordinates(), gradientDot);

        return gradientDirection;
    }

    public double getGradientDirection(Agent agent) {
        double gradientDirection;

        double sideX = this.defX / this.grCtX;
        double sideY = this.defY / this.grCtY;

        double x = agent.getX();
        double y = agent.getY();

        int iDot = (int)(x / sideX);
        int jDot = (int)(y / sideY);

        int iSpan = configuration.Agent.GRADIENTREFINEMENT;
        int jSpan = configuration.Agent.GRADIENTREFINEMENT;

        int iOrigin = Math.max(0, iDot - iSpan);
        int jOrigin = Math.max(0, jDot - jSpan);

        int iTarget = Math.min(this.grCtX - 1, iDot + iSpan);
        int jTarget = Math.min(this.grCtY - 1, jDot + jSpan);

        double resLeft = 0,
                resRight = 0,
                resTop = 0,
                resBottom = 0;

        for (int i = iOrigin; i < iDot; i++) {
            for (int j = jOrigin; j <= jTarget; j++) {
                resLeft += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        for (int i = iDot + 1; i <= iTarget; i++) {
            for (int j = jOrigin; j <= jTarget; j++) {
                resRight += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        for (int i = iOrigin; i <= iTarget; i++) {
            for (int j = jOrigin; j < jDot; j++) {
                resTop += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        for (int i = iOrigin; i <= iTarget; i++) {
            for (int j = jDot + 1; j <= jTarget; j++) {
                resBottom += getResourceAtCell(i, j).getResource()
                        / ((double)Math.abs(i - iDot) / iSpan + (double)Math.abs(j - jDot) / jSpan + 1);
            }
        }

        agent.setSeenResource(resLeft + resRight + resTop + resBottom);

        double shiftX = resRight - resLeft;
        double shiftY = resBottom - resTop;

        if (Math.abs(shiftX) <= 2 * configuration.Resource.BASERES) shiftX = 0;
        if (Math.abs(shiftY) <= 2 * configuration.Resource.BASERES) shiftY = 0;

        if (shiftX == 0 && shiftY == 0) return -1;

        Point2D gradientDot = new Point2D(agent.getX() + shiftX, agent.getY() + shiftY);

        gradientDirection = angle.Angle.directionFromTo(agent.getCoordinates(), gradientDot);

        return gradientDirection;
    }

    public double[] getResourceInAreas(PropertyGrid propertyGrid) {
        double[] resourceInAres = new double[4];

        for (int i = 0; i < 4; i++) {
            resourceInAres[i] = 0;
        }

        double sideX = this.defX / this.grCtX;
        double sideY = this.defY / this.grCtY;
        int index = 0;
        for (Iterator<Resource> iterator = this.resources.iterator(); iterator.hasNext();) {
            Resource resource = iterator.next();

            int j = index % this.grCtY;
            int i = (index - j) / this.grCtY;

            double originX = i * sideX + configuration.Render.ORIGINX;
            double originY = j * sideY + configuration.Render.ORIGINY;

            double centerX = originX + sideX / 2;
            double centerY = originY + sideY / 2;

            resourceInAres[propertyGrid.getPropertyAreaIndex(centerX, centerY)] += resource.getResource();

            index++;
        }

        return resourceInAres;
    }

    public double resourceWithdraw(Point2D dot, double amount) {
        double sideX = this.defX / this.grCtX;
        double sideY = this.defY / this.grCtY;

        double x = dot.getX();
        double y = dot.getY();

        int iDot = (int)((x - x % sideX) / sideX);
        int jDot = (int)((y - y % sideY) / sideY);

        Resource resource = getResourceAtCell(iDot, jDot);
        return resource.lowerRes(amount);
    }

    public double resourceWithdraw(Point2D dot, Point2D intersection, double amount, int extent) {

        if (extent == 0) {
            return resourceWithdraw(dot, amount);
        }

        double sideX = this.defX / this.grCtX;
        double sideY = this.defY / this.grCtY;

        double x = dot.getX();
        double y = dot.getY();

        int iDot = (int)((x - x % sideX) / sideX);
        int jDot = (int)((y - y % sideY) / sideY);

        int iIntersection = (int)(intersection.getX() / sideX);
        int jIntersection = (int)(intersection.getY() / sideY);

        int areaIndex;

        if (dot.getX() <= intersection.getX()) {
            if (dot.getY() <= intersection.getY()) areaIndex = 0;
            else areaIndex = 1;
        }
        else {
            if (dot.getY() <= intersection.getY()) areaIndex = 2;
            else areaIndex = 3;
        }

        int iOrigin = 0,
                jOrigin = 0,
                iTarget = 0,
                jTarget = 0;

        switch (areaIndex) {
            case 0 -> {
                iOrigin = Math.max(0, iDot - extent);
                jOrigin = Math.max(0, jDot - extent);

                iTarget = Math.min(iDot + extent, iIntersection - 1);
                jTarget = Math.min(jDot + extent, jIntersection - 1);
            }
            case 1 -> {
                iOrigin = Math.max(0, iDot - extent);
                jOrigin = Math.max(jIntersection + 1, jDot - extent);

                iTarget = Math.min(iDot + extent, iIntersection - 1);
                jTarget = Math.min(jDot + extent, this.grCtY - 1);
            }
            case 2 -> {
                iOrigin = Math.max(iIntersection + 1, iDot - extent);
                jOrigin = Math.max(0, jDot - extent);

                iTarget = Math.min(iDot + extent, this.grCtX - 1);
                jTarget = Math.min(jDot + extent, jIntersection - 1);
            }
            case 3 -> {
                iOrigin = Math.max(iIntersection + 1, iDot - extent);
                jOrigin = Math.max(jIntersection + 1, jDot - extent);

                iTarget = Math.min(iDot + extent, this.grCtX - 1);
                jTarget = Math.min(jDot + extent, this.grCtY - 1);
            }
        }

        double resourceWithdrawn = 0;
        int areaSideX = iTarget - iOrigin + 1;
        int areaSideY = jTarget - jOrigin + 1;
        int divider = areaSideX * areaSideY;

        for (int i = iOrigin; i <= iTarget; i++) {
            for (int j = jOrigin; j <= jTarget; j++) {
                resourceWithdrawn += getResourceAtCell(i, j).lowerRes(amount / divider);
            }
        }

        return resourceWithdrawn;
    }

    //---------------------------------
    //---------------------------------

    //-----------------------------------
    //-----------  Renderers  -----------
    //-----------------------------------

    public void render() {
        double sideX = this.defX / this.grCtX;
        double sideY = this.defY / this.grCtY;
        int index = 0;
        for (Iterator<Resource> iterator = this.resources.iterator(); iterator.hasNext();) {
            Resource res = iterator.next();
            float alpha = (float)(res.getResource() / res.getMaxResource());
            app.App.processingRef.stroke(cl.getRGB(), 0);
            app.App.processingRef.fill(cl.getRGB(), 255 * alpha / 4);

            int j = index % this.grCtY;
            int i = (index - j) / this.grCtY;

            double originX = i * sideX + configuration.Render.ORIGINX;
            double originY = j * sideY + configuration.Render.ORIGINY;

            app.App.processingRef.rect((float)originX, (float)originY, (float)sideX, (float)sideY);
            index++;
        }
    }

    //-----------------------------------
    //-----------------------------------
}
