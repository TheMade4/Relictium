package io.themade4.relictium.core.client.render.chunk;

import io.themade4.relictium.common.util.DirectionUtil;
import net.minecraft.util.EnumFacing;

public class ChunkRenderColumn<T extends ChunkGraphicsState> {
    @SuppressWarnings("unchecked")
    private final ChunkRenderContainer<T>[] renders = new ChunkRenderContainer[16];

    @SuppressWarnings("unchecked")
    private final ChunkRenderColumn<T>[] adjacent = new ChunkRenderColumn[6];

    private final int x, z;

    public ChunkRenderColumn(int x, int z) {
        this.x = x;
        this.z = z;

        this.setAdjacentColumn(EnumFacing.UP, this);
        this.setAdjacentColumn(EnumFacing.DOWN, this);
    }

    public void setAdjacentColumn(EnumFacing dir, ChunkRenderColumn<T> column) {
        this.adjacent[dir.ordinal()] = column;
    }

    public ChunkRenderColumn<T> getAdjacentColumn(EnumFacing dir) {
        return this.adjacent[dir.ordinal()];
    }

    public void setRender(int y, ChunkRenderContainer<T> render) {
        this.renders[y] = render;
    }

    public ChunkRenderContainer<T> getRender(int y) {
        if (y < 0 || y >= this.renders.length) {
            return null;
        }
        return this.renders[y];
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public boolean areNeighborsPresent() {
        for (EnumFacing dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            ChunkRenderColumn<T> adj = this.adjacent[dir.ordinal()];

            if (adj == null) {
                return false;
            }

            EnumFacing corner;

            // Access the adjacent corner chunk from the neighbor in this direction
            if (dir == EnumFacing.NORTH) {
                corner = EnumFacing.EAST;
            } else if (dir == EnumFacing.SOUTH) {
                corner = EnumFacing.WEST;
            } else if (dir == EnumFacing.WEST) {
                corner = EnumFacing.NORTH;
            } else if (dir == EnumFacing.EAST) {
                corner = EnumFacing.SOUTH;
            } else {
                continue;
            }

            // If no neighbor has been attached, the chunk is not present
            if (adj.getAdjacentColumn(corner) == null) {
                return false;
            }
        }

        return true;
    }
}
