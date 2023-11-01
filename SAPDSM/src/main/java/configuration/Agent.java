package configuration;

import java.awt.*;

public class Agent {

    public static Color SPECIESCOLORS[] = {
            new Color(255, 0,0),
            new Color(45, 255, 0),
            new Color(252, 223, 3),
            new Color(3, 252, 240)
    };

    public static double WALLTHICKNESS = 1;
    public static double BASESPEED1 = 1.6;
    public static double BASESPEED2 = 1.6;
    public static double SPEEDAGECOEFF = 1;

    public static double BASEMAXAGE = 3600;
    public static double AGEPERSTEP = 1;

    public static double SUFFENERGY = 25;
    public static double MAXENERGY = 50;
    public static double NRGPERSTEP1 = 0.05;//BASESPEED1/8;
    public static double NRGPERSTEP2 = 0.05;//BASESPEED2/8;

    public static int VALENCE1 = 3;
    public static int VALENCE2 = 4;

    public static double RESOURCECOLLECTIONSPEED = 1.0;

    //  Reproduction settings
    public static double REPRODUCTLOW = 1440;
    public static double REPRODUCTHIGH = 3000;
    public static double REPRODUCTPROB1 = 0.0015;
    public static double REPRODUCTPROB2 = 0.0015;
    public static double REPRODUCTCOST = 5.0;

    //  Fight settings
    public static boolean FIGHTS = false;
    public static double FIGHTENERGYDRAINSPEED = 1.0;

    //  Pack energy depletion settings
    public static double CONNECTIONENERGYDEPLETIONSPEED = 0.5 * NRGPERSTEP1 / PropertyGrid.PROPERTY_AREA_VALUES[0];

    //  Action counter
    public static int ACTCTRPEAK = 30;

    // Distances settings
    public static double SCRHEARDIST = 200;
    public static double PACKDIST = 80;
    public static double CONNECTDIST = 40;
    public static double COMDIST = 20;
    public static double PACKCOMDIST = 80;
    public static double FIGHTDIST = 40;
    public static double VISUALDIST = 65;
    public static int GRADIENTREFINEMENT = 4;

    //Behavioural settings
    public static boolean LONERESSCREAM = false;
}
