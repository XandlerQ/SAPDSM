package angle;

import point.Point2D;

import java.util.ArrayList;
import java.util.Random;

public class Angle {
    public static double directionAddition(double direction1, double direction2){
        double resDir = -1;
        double x1, y1, x2, y2;
        x1 = Math.cos(direction1);
        y1 = Math.sin(direction1);
        x2 = Math.cos(direction2);
        y2 = Math.sin(direction2);

        double xres, yres;

        xres = x1 + x2;
        yres = y1 + y2;

        double module = Math.sqrt((xres * xres) + (yres * yres));

        if (module == 0){
            Random r = new Random();
            resDir = (2 * Math.PI * r.nextDouble());
        }
        else{
            xres /= module;
            yres /= module;
            resDir = Math.acos(xres);
            if(yres < 0)
                resDir = 2 * Math.PI - resDir;
        }
        return resDir;
    }

    public static double directionAddition(ArrayList<Double> directions){
        double resDir = -1;

        if(directions.size() == 0)
            return resDir;

        if(directions.size() == 1){
            resDir = directions.get(0);
            return resDir;
        }

        ArrayList<Double> xi = new ArrayList<>(directions.size());
        ArrayList<Double> yi = new ArrayList<>(directions.size());

        directions.forEach((dir) -> {
            xi.add(Math.cos(dir));
            yi.add(Math.sin(dir));
        });

        double xres = 0, yres = 0;

        for (int i = 0; i < xi.size(); i++){
            xres += xi.get(i);
            yres += yi.get(i);
        }

        double module = Math.sqrt((xres * xres) + (yres * yres));

        if (module == 0){
            Random r = new Random();
            resDir = (2 * Math.PI * r.nextDouble());
        }
        else{
            xres /= module;
            yres /= module;
            resDir = Math.acos(xres);
            if(yres < 0)
                resDir = 2 * Math.PI - resDir;
        }
        return resDir;
    }

    public static double normalizeDirection(double direction){
        double dir = direction;
        while(dir < 0){
            dir += 2 * Math.PI;
        }
        while(dir >= 2 * Math.PI){
            dir -= 2 * Math.PI;
        }
        return dir;
    }

    public static double directionFromTo(Point2D from, Point2D to) {
        double distance = Point2D.distanceBetween(from, to);

        if(distance == 0) {
            return -1;
        }

        double direction = Math.acos((to.getX() - from.getX()) / distance);
        if(to.getY() > from.getY()) return direction;
        else return 2 * Math.PI - direction;
    }
}