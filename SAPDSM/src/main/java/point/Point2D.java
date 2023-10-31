package point;

public class Point2D extends Point {
    private double x;
    private double y;

    public Point2D() {
        this.x = 0;
        this.y = 0;
    }

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(Point2D point2D) {
        this.x = point2D.x;
        this.y = point2D.y;
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setXY(double x, double y) { setX(x); setY(y); }
    public void setXY(Point2D point2D) { setX(point2D.getX()); setY(point2D.getY()); }

    @Override
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    @Override
    public Point2D normalize() {
        double length = this.length();
        if (length == 0) return new Point2D();
        return new Point2D(this.x / length, this.y / length);
    }

    @Override
    public Point2D normalizeThis() {
        double length = this.length();
        if (length != 0) {
            this.x /= length;
            this.y /= length;
        }
        return this;
    }

    @Override
    public Point2D multiplyByValue(double value) {
        return new Point2D(this.x * value, this.y * value);
    }

    @Override
    public Point2D multiplyThisByValue(double value) {
        this.x *= value;
        this.y *= value;
        return this;
    }

    public Point2D add(Point2D point2D) {
        return new Point2D(this.x + point2D.x, this.y + point2D.y);
    }

    public Point2D addToThis(Point2D point2D) {
        this.x += point2D.x;
        this.y += point2D.y;
        return this;
    }

    public Point2D negate() {
        return new Point2D(-this.x, -this.y);
    }

    public Point2D negateThis() {
        this.x *= -1;
        this.y *= -1;
        return this;
    }

    public Point2D subtract(Point2D point2D) {
        return new Point2D(this.x - point2D.x, this.y - point2D.y);
    }

    public Point2D subtractFromThis(Point2D point2D) {
        this.x -= point2D.x;
        this.y -= point2D.y;
        return this;
    }

    public double distanceTo(Point2D point2D) {
        return this.subtract(point2D).length();
    }

    public static double distanceBetween(Point2D point2D1, Point2D point2D2) {
        return point2D1.distanceTo(point2D2);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        Point2D arg = (Point2D) obj;
        return (arg.getX() == this.x && arg.getY() == this.y);
    }
}
