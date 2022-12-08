package com.luridarc.polyplot.util.plot;

import java.util.HashSet;
import java.util.Set;

import org.poly2tri.geometry.polygon.PolygonPoint;

import com.luridarc.polyplot.util.save_data.builder_config.PillarConfigSaveData;
import com.luridarc.polyplot.util.save_data.builder_config.WallConfigSaveData;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Builder {

    public static void build(Plot _plot)
    {
        switch(_plot.getWand_type()){
            case EDGE:
                buildWalls(_plot);
                break;
            case NON:
                break;
            case POINT:
                buildPillars(_plot);
                break;
            default:
                break;
        }
    }

    private static void buildWalls(Plot _plot) {
        Set<Long> pos_set = new HashSet<Long>();

        int dimension_id = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(_plot.getOwnerUUID()).dimension;

        for(int i = 0; i < _plot.getPoints().size(); i++) {
            if(i == _plot.getPoints().size() - 1) {
                buildWall(
                    dimension_id,
                    pos_set,
                    (PolygonPoint) _plot.getPoints().toArray()[i], 
                    (PolygonPoint) _plot.getPoints().toArray()[0]
                );
            } else {
                buildWall(
                    dimension_id,
                    pos_set,
                    (PolygonPoint) _plot.getPoints().toArray()[i], 
                    (PolygonPoint) _plot.getPoints().toArray()[i + 1]
                );
            }
        }

        buildPillars(_plot);
    }

    private static boolean posAreEqual(BlockPos point1, BlockPos point2)
    {
        if(point1 == null || point2 == null) {
            return false;
        } else {
            return (point1.getX() == point2.getX()) && (point1.getY() == point2.getY()) && (point1.getZ() == point2.getZ());
        }
    }

    private static void buildWall(int dimension_id, Set<Long> pos_set, PolygonPoint a, PolygonPoint b) 
    {
        double distance = distance(a, b);
        for(int i = 0; i < distance; i++) 
        {
            BlockPos lerped_pos = lerpPos(a, b, i, distance);
            if(posAreEqual(lerped_pos, new BlockPos(a.getX(), 0, a.getY())) || posAreEqual(lerped_pos, new BlockPos(b.getX(), 0, b.getY()))) {
                continue;
            }
            BlockPos current_block = getTopTerrainBlock(lerped_pos);

            if(!pos_set.contains(new BlockPos(current_block.getX(), 0, current_block.getZ()).toLong())){
                generateWallPillar(dimension_id, current_block);
                pos_set.add(new BlockPos(current_block.getX(), 0, current_block.getZ()).toLong());
            }
        }
    }

    private static BlockPos lerpPos(PolygonPoint a, PolygonPoint b, int i, double distance) {
        int lerped_x = (int) Math.round(a.getX() + (b.getX() - a.getX()) * (i/distance));
        int lerped_y = (int) Math.round(a.getY() + (b.getY() - a.getY()) * (i/distance));
        return new BlockPos(lerped_x, 0, lerped_y);
    }

    private static double distance(PolygonPoint a, PolygonPoint b)
    {
        return Math.sqrt(Math.pow((a.getX() - b.getX()), 2) + Math.pow((a.getY() - b.getY()), 2));
    }

    private static void generatePillar(int dimension_id, BlockPos _pos)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().worlds[dimension_id].setBlockState(_pos, PillarConfigSaveData.getBlockStates(0).bottom);
        FMLCommonHandler.instance().getMinecraftServerInstance().worlds[dimension_id].setBlockState(_pos.add(0, 1, 0), PillarConfigSaveData.getBlockStates(0).middle);
        FMLCommonHandler.instance().getMinecraftServerInstance().worlds[dimension_id].setBlockState(_pos.add(0, 2, 0), PillarConfigSaveData.getBlockStates(0).top);
    }

    private static void generateWallPillar(int dimension_id, BlockPos _pos)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().worlds[dimension_id].setBlockState(_pos, WallConfigSaveData.getBlockStates(0).bottom);
        FMLCommonHandler.instance().getMinecraftServerInstance().worlds[dimension_id].setBlockState(_pos.add(0, 1, 0), WallConfigSaveData.getBlockStates(0).middle);
        FMLCommonHandler.instance().getMinecraftServerInstance().worlds[dimension_id].setBlockState(_pos.add(0, 2, 0),  WallConfigSaveData.getBlockStates(0).top);
    }

    private static void buildPillars(Plot _plot) {
        int dimension_id = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(_plot.getOwnerUUID()).dimension;

        for(PolygonPoint each : _plot.getPoints()) 
        {
            BlockPos point_pos = polygonPointToWorldTopBlockPos(each);
            BlockPos pillar_base = getTopTerrainBlock(point_pos);

            generatePillar(dimension_id, pillar_base);
        }
    }

    private static BlockPos polygonPointToWorldTopBlockPos(PolygonPoint _point) 
    {
        return new BlockPos(_point.getX(), 0, _point.getY());
    }

    private static BlockPos getTopTerrainBlock(BlockPos _pos) {

        for(int i = 255; i > 0; i--) {
            if(!FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getBlockState(_pos.add(0, i, 0)).getBlock().equals(Blocks.AIR)) {
                return _pos.add(0, i + 1, 0);
            }
        }

        return _pos;
    }
}
