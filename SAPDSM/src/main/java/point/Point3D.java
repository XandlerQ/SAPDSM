package point;

public class Point3D extends Point {
    private double x;
    private double y;
    private double z;

    public Point3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(Point3D point3D) {
        this.x = point3D.x;
        this.y = point3D.y;
        this.z = point3D.z;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setZ(double z) {
        this.z = z;
    }
    public void setXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setXYZ(Point3D point3D) {
        this.x = point3D.x;
        this.y = point3D.y;
        this.z = point3D.z;
    }

    @Override
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    @Override
    public Point3D normalize() {
        double length = this.length();
        if (length == 0) return new Point3D();
        return new Point3D(this.x / length, this.y / length, this.z / length);
    }

    @Override
    public Point3D normalizeThis() {
        double length = this.length();
        if (length != 0) {
            this.x /= length;
            this.y /= length;
            this.z /= length;
        }
        return this;
    }

    @Override
    public Point3D multiplyByValue(double value) {
        return new Point3D(this.x * value, this.y * value, this.z * value);
    }

    @Override
    public Point3D multiplyThisByValue(double value) {
        this.x *= value;
        this.y *= value;
        this.z *= value;
        return this;
    }

    public Point3D add(Point3D point3D) {
        return new Point3D(this.x + point3D.x, this.y + point3D.y, this.z + point3D.z);
    }

    public Point3D addToThis(Point3D point3D) {
        this.x += point3D.x;
        this.y += point3D.y;
        this.z += point3D.z;
        return this;
    }

    public Point3D negate() {
        return new Point3D(-this.x, -this.y, -this.z);
    }

    public Point3D negateThis() {
        this.x *= -1;
        this.y *= -1;
        this.z *= -1;
        return this;
    }

    public Point3D subtract(Point3D point3D) {
        return new Point3D(this.x - point3D.x, this.y - point3D.y, this.z - point3D.z);
    }

    public Point3D subtractFromThis(Point3D point3D) {
        this.x -= point3D.x;
        this.y -= point3D.y;
        this.z -= point3D.z;
        return this;
    }

    public double distanceTo(Point3D point3D) {
        return this.subtract(point3D).length();
    }

    public static double distanceBetween(Point3D point3D1, Point3D point3D2) {
        return point3D1.distanceTo(point3D2);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        Point3D arg = (Point3D) obj;
        return (arg.getX() == this.x && arg.getY() == this.y && arg.getZ() == this.z);
    }
}
