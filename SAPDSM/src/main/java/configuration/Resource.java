package configuration;

import java.awt.*;

public class Resource {
    public enum RESOURCETYPES {
        DISCRETE, PLAIN
    }
    public static Color RESOURCECOLOR = new Color(255, 170, 0);
    public static double RESOURCENODESIZE = 20;
    public static int QUADX = 5;
    public static int QUADY = 5;

    public static int PLAINX = 80;
    public static int PLAINY = 80;

    public static RESOURCETYPES RESTYPE = RESOURCETYPES.PLAIN;

    public static double BASERES = 0.1;
    public static double RESREPSPEEDMULTIPLIER = 60.;
    public static double RESREPSPEED = BASERES / RESREPSPEEDMULTIPLIER;
    public static int RESPERQUAD = 4;
    public static int RESREPCTRPEAK = 0;
}
