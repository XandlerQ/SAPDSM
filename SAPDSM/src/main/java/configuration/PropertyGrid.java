package configuration;

import java.awt.*;

public class PropertyGrid {
    public enum SHIFTINTERSECTIONMODES { ////////////////////////////////////////////////////////////////////
        STATIC, POPULATION, ENERGY, ENERGYDENSITY
    }
    public static SHIFTINTERSECTIONMODES SHIFTINTERSECTION = SHIFTINTERSECTIONMODES.ENERGY;
    public static Color[] PROPERTY_AREA_COLORS = {
            new Color(255, 134, 125),
            new Color(250, 236, 127),
            new Color(157, 245, 132),
            new Color(137, 250, 235)
    };

    public static Integer[] PROPERTY_AREA_VALUES = {
            6, 2, 2, 0
    };

    public static boolean LOCKEDAREAS = true; ///////////////////////////////////////////////////////////////////
    public static boolean PAYMENT = true;
    public static double PAYMENTRATIO = 0.33;
}
