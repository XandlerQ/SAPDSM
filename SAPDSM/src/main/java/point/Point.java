package point;

public abstract class Point {
    public abstract double length();
    public abstract Point normalize();
    public abstract Point normalizeThis();
    public abstract Point multiplyByValue(double value);
    public abstract Point multiplyThisByValue(double value);
}
