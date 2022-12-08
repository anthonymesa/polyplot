package com.luridarc.polyplot.util.plot;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.poly2tri.geometry.polygon.PolygonPoint;

import com.luridarc.polyplot.objects.items.ItemWand.WandType;
import com.luridarc.polyplot.util.exceptions.MalformedPlotException;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ProtoPlot {

    private WandType wand_type;
    private String plot_name;
    private UUID owner_id;
    private LinkedHashSet<PolygonPoint> points;

    public String getPlotName() {
        return plot_name;
    }
    
    public void setPlotName(String plot_name) {
        this.plot_name = plot_name;
    }

    public UUID getOwnerUUID() {
        return owner_id;
    }

    public void setOwnerUUID(UUID id) {
        this.owner_id = id;
    }

    public LinkedHashSet<PolygonPoint> getPoints() {
        return points;
    }

    public void setPoints(LinkedHashSet<PolygonPoint> points) {
        this.points = points;
    }

    public WandType getWand_type() {
        return wand_type;
    }

    public void setWand_type(WandType wand_type) {
        this.wand_type = wand_type;
    }

    public ProtoPlot() {
        this.wand_type = WandType.NON;
        this.plot_name = "";
        this.owner_id = null;
        this.points = new LinkedHashSet<PolygonPoint>();
    }

    public ProtoPlot(WandType type, String new_plot_name, UUID new_uuid, LinkedHashSet<PolygonPoint> new_plot_points) {
        this.wand_type = type;
        this.plot_name = new_plot_name;
        this.owner_id = new_uuid;
        this.points = new_plot_points;
    }

    public boolean isClosedBy(BlockPos pos) 
    {
        PolygonPoint pos_as_point = new PolygonPoint(pos.getX(), pos.getZ());
        return pointsAreEqual(pos_as_point, (PolygonPoint) this.points.toArray()[0]);
    }

    protected boolean pointsAreEqual(PolygonPoint point1, PolygonPoint point2)
    {
        return (point1.getX() == point2.getX()) && (point1.getY() == point2.getY());
    }

    public void appendPoint(BlockPos pos)
    {
        this.points.add(new PolygonPoint(pos.getX(), pos.getZ()));
    }

    public static ProtoPlot fromBuffer(ByteBuf buf) throws Exception
    {
        LinkedHashSet<PolygonPoint> new_plot_points = new LinkedHashSet<PolygonPoint>();

        WandType type = WandType.values()[buf.readInt()];

        String new_plot_name = ByteBufUtils.readUTF8String(buf);
        UUID new_owner_id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        int list_size = buf.readInt();

        for(int i = 0; i < list_size; i++) 
        {
            new_plot_points.add(new PolygonPoint(
                buf.readInt(), 
                buf.readInt()
            ));
        }

        return new ProtoPlot(type, new_plot_name, new_owner_id, new_plot_points);
    }

    public void toBuffer(ByteBuf buf) throws Exception
    {
        buf.writeInt(this.wand_type.ordinal());
        ByteBufUtils.writeUTF8String(buf, this.plot_name);
        ByteBufUtils.writeUTF8String(buf, this.owner_id.toString());
        buf.writeInt(this.points.size());

        for(PolygonPoint each : this.points) {
            buf.writeInt((int) each.getX());
            buf.writeInt((int) each.getY());
        }
    }

    public ProtoPlot(DynamicByteBuffer dbuf) throws MalformedPlotException {
        this();

        try {
            setWandTypeFromBuffer(dbuf);
        } catch (Exception e) {
            throw new MalformedPlotException("Couldn't get wand type from buffer due to " + e);
        }

        try {
            setPlotNameFromBuffer(dbuf);
        } catch (Exception e) {
            throw new MalformedPlotException("couldn't get name from buffer due to " + e);
        }

        try {
            setOwnerUUIDFromBuffer(dbuf);
        } catch (Exception e) {
            throw new MalformedPlotException("Couldn't get uuid from buffer due to " + e);
        }

        try {
            setPointsFromBuffer(dbuf);
        } catch (Exception e) {
            throw new MalformedPlotException("Couldn't get plots from buffer due to " + e);
        }
    }

    private void setWandTypeFromBuffer(DynamicByteBuffer dbuf) throws Exception {
        this.wand_type = WandType.values()[dbuf.getInt()];
    }

    private void setPlotNameFromBuffer(DynamicByteBuffer dbuf) throws Exception {
        StringBuilder sb1 = new StringBuilder();

        int plot_name_length = dbuf.getInt();

        for(int i = 0; i < plot_name_length; i++) {
            char current_char = (char) dbuf.get();
            sb1.append(current_char);
        }
        this.plot_name = sb1.toString();
    }

    private void setOwnerUUIDFromBuffer(DynamicByteBuffer dbuf) throws Exception {
        StringBuilder sb2 = new StringBuilder();
        int owner_id_length = dbuf.getInt();
        for(int i = 0; i < owner_id_length; i++) {
            sb2.append((char) dbuf.get());
        }
        this.owner_id = UUID.fromString(sb2.toString());
    }

    private void setPointsFromBuffer(DynamicByteBuffer dbuf) throws Exception {
        this.points = new LinkedHashSet<PolygonPoint>();
        int points_size = dbuf.getInt();
        for(int i = 0; i < points_size; i++) {
            PolygonPoint temp_point = new PolygonPoint(dbuf.getInt(), dbuf.getInt());
            this.points.add(temp_point);
        }
    }

    public void writeToDynamicBuffer(DynamicByteBuffer dbuf) {
        dbuf.putInt(wand_type.ordinal());

        dbuf.putInt(plot_name.length());
        dbuf.put(plot_name.getBytes(StandardCharsets.UTF_8));

        dbuf.putInt(owner_id.toString().length());
        dbuf.put(owner_id.toString().getBytes(StandardCharsets.UTF_8));

        dbuf.putInt(points.size());
        for(PolygonPoint each : this.points) {
            dbuf.putInt((int) each.getX());
            dbuf.putInt((int) each.getY());
        }
    }
}


