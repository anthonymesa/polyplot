package com.luridarc.polyplot.util.plot;

import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import net.minecraft.util.math.BlockPos;

public class PlotTriangle {

    public Point A;
    public Point B;
    public Point C;

    public PlotTriangle(DelaunayTriangle tri) {
        this.A = new Point((int) tri.points[0].getX(), (int) tri.points[0].getY());
        this.B = new Point((int) tri.points[1].getX(), (int) tri.points[1].getY());
        this.C = new Point((int) tri.points[2].getX(), (int) tri.points[2].getY());
    }

    public PlotTriangle(int ax, int ay, int bx, int by, int cx, int cy) {
        this.A = new Point(ax, ay);
        this.B = new Point(bx, by);
        this.C = new Point(cx, cy);
    }

    public Point getA() {
        return A;
    }

    public Point getB() {
        return B;
    }

    public Point getC() {
        return C;
    }

    static double area(int x1, int y1, int x2, int y2, int x3, int y3)
    {
        return Math.abs((x1*(y2-y3) + x2*(y3-y1)+ x3*(y1-y2))/2.0);
    }

    public boolean isHitBy(BlockPos pos)
    {  
        int x = (int) pos.getX();
        int y = (int) pos.getZ();

        double triangle_area = area (A.x, A.y, B.x, B.y, C.x, C.y);
        double minor_tri_area_1 = area (x, y, B.x, B.y, C.x, C.y);
        double minor_tri_area_2 = area (A.x, A.y, x, y, C.x, C.y);
        double minor_tri_area_3 = area (A.x, A.y, B.x, B.y, x, y);

        return (triangle_area == minor_tri_area_1 + minor_tri_area_2 + minor_tri_area_3);
    }

    public Bounds getBounds() {
        return new Bounds(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[(" + A.x + ", " + A.y + "), (" + B.x + ", " + B.y + "), (" + C.x + ", " + C.y + ")]");
        return sb.toString();
    }
}
