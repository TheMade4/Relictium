package io.themade4.relictium.common.util;

import net.minecraft.util.EnumFacing;

/**
 * Contains a number of cached arrays to avoid allocations since calling Enum#values() requires the backing array to
 * be cloned every time.
 */
public class DirectionUtil {
    public static final EnumFacing[] ALL_DIRECTIONS = EnumFacing.values();
    public static final int DIRECTION_COUNT = ALL_DIRECTIONS.length;

    // Provides the same order as enumerating Direction and checking the axis of each value
    public static final EnumFacing[] HORIZONTAL_DIRECTIONS = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
}
