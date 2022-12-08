package com.luridarc.polyplot.util.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class Bounds {
    private int left;
    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    private int right;
    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    private int top;
    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    private int bottom;
    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public Bounds(int left, int right, int top, int bottom) 
    {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public Bounds(PlotTriangle _triangle)
    {
        List<Integer> points = new ArrayList<>(Arrays.asList(
            _triangle.A.x,
            _triangle.B.x,
            _triangle.C.x,
            _triangle.A.y,
            _triangle.B.y,
            _triangle.C.y
        ));

        if (points.get(0) > points.get(1)) {
            Collections.swap(points, 0, 1);
        }

        if (points.get(1) > points.get(2)) {
            Collections.swap(points, 1, 2);
        }

        if (points.get(0) > points.get(1)) {
            Collections.swap(points, 0, 1);
        }
        
        if (points.get(3) > points.get(4)) {
            Collections.swap(points, 3, 4);
        }

        if (points.get(4) > points.get(5)) {
            Collections.swap(points, 4, 5);
        }

        if (points.get(3) > points.get(4)) {
            Collections.swap(points, 3, 4);
        }

        setTop(points.get(5));
        setBottom(points.get(3));
        setLeft(points.get(0));
        setRight(points.get(2));
    }

    private PlotTriangle getTriangleA() {
        return new PlotTriangle(left, top, left, bottom, right, bottom);
    }
    
    private PlotTriangle getTriangleB() {
        return new PlotTriangle(left, top, right, top, right, bottom);
    }

    public boolean isHitBy(BlockPos pos) {
        if(getTriangleA().isHitBy(pos)) {
            return true;
        } else if (getTriangleB().isHitBy(pos)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean intersectsTopLeft(Bounds a, Bounds b) {
        return (a.getBottom() < b.getTop()) && 
               (a.getBottom() >= b.getBottom()) &&
               (a.getRight() > b.getLeft()) &&
               (a.getRight() <= b.getRight());
    }

    public static boolean intersectsTopRight(Bounds a, Bounds b) {
        return (a.getBottom() < b.getTop()) && 
               (a.getBottom() >= b.getBottom()) &&
               (a.getLeft() <= b.getRight()) &&
               (a.getLeft() > b.getLeft());
    }

    public static boolean intersectsBottomLeft(Bounds a, Bounds b) {
        return (a.getTop() >= b.getBottom()) && 
               (a.getTop() < b.getTop()) &&
               (a.getRight() > b.getLeft()) &&
               (a.getRight() <= b.getRight());
    }

    public static boolean intersectsBottomRight(Bounds a, Bounds b) {
        return (a.getTop() >= b.getBottom()) && 
               (a.getTop() < b.getTop()) &&
               (a.getLeft() <= b.getRight()) &&
               (a.getLeft() > b.getLeft());
    }

    public boolean intersects(Bounds bounds_a) {
        if(intersectsTopLeft(bounds_a, this) ||
           intersectsBottomLeft(bounds_a, this) ||
           intersectsTopRight(bounds_a, this) ||
           intersectsBottomRight(bounds_a, this)) {
            return true;
           } else {
            return false;
           }
    }

    public static boolean intersect(Bounds bounds_a, Bounds bounds_b) {
        if(intersectsTopLeft(bounds_a, bounds_b) ||
           intersectsBottomLeft(bounds_a, bounds_b) ||
           intersectsTopRight(bounds_a, bounds_b) ||
           intersectsBottomRight(bounds_a, bounds_b)) {
            return true;
           } else {
            return false;
           }
    }

    @Override
    public String toString() {
        return String.format("Bounds L: %d R: %d T: %d B: %d", left, right, top, bottom);
    }

    public void expand(Bounds some_bounds) {
        this.setLeft(getExpandedLeftBound(some_bounds));
        this.setRight(getExpandedRightBounds(some_bounds));
        this.setTop(getExpandedTopBound(some_bounds));
        this.setBottom(getExpandedBottomBound(some_bounds));
    }

    private int getExpandedLeftBound(Bounds some_bounds) {
        if(some_bounds.getLeft() < this.getLeft()) {
            return some_bounds.getLeft();
        } else {
            return this.getLeft();
        }
    }

    private int getExpandedRightBounds(Bounds some_bounds) {
        if(some_bounds.getRight() > this.getRight()) {
            return some_bounds.getRight();
        } else {
            return this.getRight();
        }
    }

    private int getExpandedTopBound(Bounds some_bounds) {
        if(some_bounds.getTop() > this.getTop()) {
            return some_bounds.getTop();
        } else {
            return this.getTop();
        }
    }

    private int getExpandedBottomBound(Bounds some_bounds) {
        if(some_bounds.getBottom() < this.getBottom()) {
            return some_bounds.getBottom();
        } else {
            return this.getBottom();
        }
    }
}
