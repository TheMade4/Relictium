package io.themade4.relictium.core.client.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class ChunkSectionPos extends Vec3i {
    private ChunkSectionPos(int i, int j, int k) {
        super(i, j, k);
    }

    public static ChunkSectionPos from(int x, int y, int z) {
        return new ChunkSectionPos(x, y, z);
    }

    public static ChunkSectionPos from(BlockPos pos) {
        return new ChunkSectionPos(getSectionCoord(pos.getX()), getSectionCoord(pos.getY()), getSectionCoord(pos.getZ()));
    }

    public static ChunkSectionPos from(ChunkPos chunkPos, int y) {
        return new ChunkSectionPos(chunkPos.x, y, chunkPos.z);
    }

    public static ChunkSectionPos from(Entity entity) {
        return new ChunkSectionPos(getSectionCoord(MathHelper.floor(entity.getPosition().getX())), getSectionCoord(MathHelper.floor(entity.getPosition().getY())), getSectionCoord(MathHelper.floor(entity.getPosition().getZ())));
    }

    public static ChunkSectionPos from(long packed) {
        return new ChunkSectionPos(unpackX(packed), unpackY(packed), unpackZ(packed));
    }

    public static long offset(long packed, EnumFacing direction) {
        return offset(packed, direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
    }

    public static long offset(long packed, int x, int y, int z) {
        return asLong(unpackX(packed) + x, unpackY(packed) + y, unpackZ(packed) + z);
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }

    public static int getLocalCoord(int coord) {
        return coord & 15;
    }

    public static short packLocal(BlockPos pos) {
        int i = getLocalCoord(pos.getX());
        int j = getLocalCoord(pos.getY());
        int k = getLocalCoord(pos.getZ());
        return (short) (i << 8 | k << 4 | j);
    }

    public static int unpackLocalX(short packedLocalPos) {
        return packedLocalPos >>> 8 & 15;
    }

    public static int unpackLocalY(short packedLocalPos) {
        return packedLocalPos & 15;
    }

    public static int unpackLocalZ(short packedLocalPos) {
        return packedLocalPos >>> 4 & 15;
    }

    public static int getBlockCoord(int sectionCoord) {
        return sectionCoord << 4;
    }

    public static int unpackX(long packed) {
        return (int) (packed >> 42);
    }

    public static int unpackY(long packed) {
        return (int) (packed << 44 >> 44);
    }

    public static int unpackZ(long packed) {
        return (int) (packed << 22 >> 42);
    }

    public static long fromBlockPos(long blockPos) {
        BlockPos pos = BlockPos.fromLong(blockPos);
        return asLong(getSectionCoord(pos.getX()), getSectionCoord(pos.getY()), getSectionCoord(pos.getZ()));
    }

    public static long withZeroY(long pos) {
        return pos & -1048576L;
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long) x & 4194303L) << 42;
        l |= ((long) y & 1048575L);
        l |= ((long) z & 4194303L) << 20;
        return l;
    }

    public int unpackBlockX(short packedLocalPos) {
        return this.getMinX() + unpackLocalX(packedLocalPos);
    }

    public int unpackBlockY(short packedLocalPos) {
        return this.getMinY() + unpackLocalY(packedLocalPos);
    }

    public int unpackBlockZ(short packedLocalPos) {
        return this.getMinZ() + unpackLocalZ(packedLocalPos);
    }

    public BlockPos unpackBlockPos(short packedLocalPos) {
        return new BlockPos(this.unpackBlockX(packedLocalPos), this.unpackBlockY(packedLocalPos), this.unpackBlockZ(packedLocalPos));
    }

    public int getSectionX() {
        return this.getX();
    }

    public int getSectionY() {
        return this.getY();
    }

    public int getSectionZ() {
        return this.getZ();
    }

    public int getMinX() {
        return this.getSectionX() << 4;
    }

    public int getMinY() {
        return this.getSectionY() << 4;
    }

    public int getMinZ() {
        return this.getSectionZ() << 4;
    }

    public int getMaxX() {
        return (this.getSectionX() << 4) + 15;
    }

    public int getMaxY() {
        return (this.getSectionY() << 4) + 15;
    }

    public int getMaxZ() {
        return (this.getSectionZ() << 4) + 15;
    }

    public BlockPos getMinPos() {
        return new BlockPos(getBlockCoord(this.getSectionX()), getBlockCoord(this.getSectionY()), getBlockCoord(this.getSectionZ()));
    }

    public BlockPos getCenterPos() {
        return this.getMinPos().add(8, 8, 8);
    }

    public ChunkPos toChunkPos() {
        return new ChunkPos(this.getSectionX(), this.getSectionZ());
    }

    public long asLong() {
        return asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
    }

    public Iterable<BlockPos> streamBlocks() {
        return BlockPos.getAllInBox(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }
}