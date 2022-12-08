package com.luridarc.polyplot.util.plot;

import java.util.ArrayList;
import java.util.List;

import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class Plot extends ProtoPlot
{
    private Bounds bounds;
    private Polygon polygon;

    public Plot(ProtoPlot proto)
    {
        super(proto.getWand_type(), proto.getPlotName(), proto.getOwnerUUID(), proto.getPoints());

        this.polygon = new Polygon(new ArrayList<PolygonPoint>(getPoints()));
        Poly2Tri.triangulate(this.polygon);
        this.bounds = generateBounds(getTriangles());
    }

    public Bounds getBounds() {
        return bounds;
    }

    public boolean isHitBy(BlockPos pos) 
    {
        for(PlotTriangle each : this.getTriangles()) 
        {
            if(each.isHitBy(pos)) {
                return true;
            }
        }

        return false;
    }

    public void toBuffer(ByteBuf buf) throws Exception
    {
        super.toBuffer(buf);
    }

    public static Plot fromBuffer(ByteBuf buf) throws Exception
    {
        return new Plot(ProtoPlot.fromBuffer(buf));
    }

    private Bounds generateBounds(List<PlotTriangle> list) 
    {
        Bounds new_bounds = list.get(0).getBounds();

        for(PlotTriangle i_triangle : list) 
        {
            new_bounds.expand(i_triangle.getBounds());
        }

        return new_bounds;
    }

    private List<PlotTriangle> getTriangles() 
    {
        ArrayList<PlotTriangle> triangles = new ArrayList<PlotTriangle>();

        for(DelaunayTriangle each : polygon.getTriangles()) 
        {
            triangles.add(new PlotTriangle(each));
        }

        return triangles;
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nPlot name: %s", getPlotName()));
        sb.append(String.format("\nPlot Owner UUID: %s", getOwnerUUID().toString()));
        sb.append(String.format("\nBounds: left=%d right=%d top=%d bottom=%d", 
            this.bounds.getLeft(), 
            this.bounds.getRight(), 
            this.bounds.getTop(), 
            this.bounds.getBottom()
        ));
        sb.append("\nListing Plot Triangles: ");
        for(PlotTriangle each : getTriangles()) {
            sb.append("\n" + each.toString());
        }
        sb.append("\nListing polygon edge characteristics: ");
        for(DelaunayTriangle each : polygon.getTriangles()) {
            sb.append("\n\n Triangle info:");
            sb.append("\nTriangulation Point 1: " + each.points[0].toString());
            sb.append("\nTriangulation Point 2: " + each.points[1].toString());
            sb.append("\nTriangulation Point 3: " + each.points[2].toString());
            sb.append("\nis interior: " + each.isInterior());
            sb.append("\nconstrained edges: " + each.cEdge[0] + " " + each.cEdge[1] + " " + each.cEdge[2]);
            sb.append("\ndelauney edges: " + each.dEdge[0] + " " + each.dEdge[1] + " " + each.dEdge[2]);
        }
        return sb.toString();
    }

    public void writeToDynamicBuffer(DynamicByteBuffer dbuf) {
        try {
            super.writeToDynamicBuffer(dbuf);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
