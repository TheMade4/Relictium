package io.themade4.relictium.core.client.render.pipeline;

import io.themade4.relictium.core.client.model.light.LightMode;
import io.themade4.relictium.core.client.model.light.LightPipeline;
import io.themade4.relictium.core.client.model.light.LightPipelineProvider;
import io.themade4.relictium.core.client.model.light.data.QuadLightData;
import io.themade4.relictium.core.client.model.quad.ModelQuad;
import io.themade4.relictium.core.client.model.quad.ModelQuadView;
import io.themade4.relictium.core.client.model.quad.ModelQuadViewMutable;
import io.themade4.relictium.core.client.model.quad.blender.BiomeColorBlender;
import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFacing;
import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFlags;
import io.themade4.relictium.core.client.render.chunk.compile.buffers.ChunkModelBuffers;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;
import io.themade4.relictium.core.client.util.MathUtil;
import io.themade4.relictium.core.client.util.Norm3b;
import io.themade4.relictium.core.client.util.color.ColorABGR;
import io.themade4.relictium.core.client.world.biome.BlockColorsExtended;
import io.themade4.relictium.common.util.DirectionUtil;
import io.themade4.relictium.common.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import io.themade4.relictium.render.fluid.FluidSpriteCache;
import repack.joml.Vector3d;

public class FluidRenderer {

    private static final float EPSILON = 0.001f;

    private static final IBlockColor FLUID_COLOR_PROVIDER = (state, world, pos, tintIndex) -> WorldUtil.getFluid(state).getColor();

    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();

    private final ModelQuadViewMutable quad = new ModelQuad();

    private final LightPipelineProvider lighters;
    private final BiomeColorBlender biomeColorBlender;

    private final QuadLightData quadLightData = new QuadLightData();
    private final int[] quadColors = new int[4];

    private final FluidSpriteCache fluidSpriteCache = new FluidSpriteCache();

    private final BlockColors vanillaBlockColors;

    public FluidRenderer(Minecraft client, LightPipelineProvider lighters, BiomeColorBlender biomeColorBlender) {
        int normal = Norm3b.pack(0.0f, 1.0f, 0.0f);

        for (int i = 0; i < 4; i++) {
            this.quad.setNormal(i, normal);
        }

        this.lighters = lighters;
        this.biomeColorBlender = biomeColorBlender;
        this.vanillaBlockColors = client.getBlockColors();
    }

    private boolean isFluidOccluded(IBlockAccess world, int x, int y, int z, EnumFacing dir, Fluid fluid) {
        BlockPos pos = this.scratchPos.setPos(x, y, z);
        IBlockState blockState = world.getBlockState(pos);
        BlockPos adjPos = this.scratchPos.setPos(x + dir.getXOffset(), y + dir.getYOffset(), z + dir.getZOffset());
        Fluid adjFluid = WorldUtil.getFluid(world.getBlockState(adjPos));

        if (blockState.getMaterial().isOpaque()) {
            return fluid == adjFluid || blockState.isSideSolid(world,pos,dir);
            // fluidlogged or next to water, occlude sides that are solid or the same liquid
        }
        return fluid == adjFluid;
    }

    private boolean isSideExposed(IBlockAccess world, int x, int y, int z, EnumFacing dir) {
        BlockPos pos = this.scratchPos.setPos(x + dir.getXOffset(), y + dir.getYOffset(), z + dir.getZOffset());
        IBlockState blockState = world.getBlockState(pos);

        if (blockState.getMaterial().isOpaque()) {
            // TODO check if correct
            BlockFaceShape shape = blockState.getBlockFaceShape(world, pos, dir);

            // Hoist these checks to avoid allocating the shape below
            if (shape == BlockFaceShape.SOLID) {
                // The top face always be inset, so if the shape above is a full cube it can't possibly occlude
                return dir == EnumFacing.UP;
            } else if (shape == BlockFaceShape.UNDEFINED) {
                return true;
            }
        }

        return true;
    }

    public boolean render(IBlockAccess world, IBlockState fluidState, BlockPos pos, ChunkModelBuffers buffers) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        Fluid fluid = WorldUtil.toFluidBlock(fluidState.getBlock()).getFluid();

        boolean sfUp = this.isFluidOccluded(world, posX, posY, posZ, EnumFacing.UP, fluid);
        boolean sfDown = this.isFluidOccluded(world, posX, posY, posZ, EnumFacing.DOWN, fluid) ||
                !this.isSideExposed(world, posX, posY, posZ, EnumFacing.DOWN);
        boolean sfNorth = this.isFluidOccluded(world, posX, posY, posZ, EnumFacing.NORTH, fluid);
        boolean sfSouth = this.isFluidOccluded(world, posX, posY, posZ, EnumFacing.SOUTH, fluid);
        boolean sfWest = this.isFluidOccluded(world, posX, posY, posZ, EnumFacing.WEST, fluid);
        boolean sfEast = this.isFluidOccluded(world, posX, posY, posZ, EnumFacing.EAST, fluid);

        if (sfUp && sfDown && sfEast && sfWest && sfNorth && sfSouth) {
            return false;
        }

        TextureAtlasSprite[] sprites = fluidSpriteCache.getSprites(fluid);
        // Treat any non-lava fluid as colored, fluids without color providers will just use -1 which is a fine default
        boolean hc = fluidState.getBlock() != Blocks.LAVA && fluidState.getBlock() != Blocks.FLOWING_LAVA;

        boolean rendered = false;

        float h1 = this.getCornerHeight(world, posX, posY, posZ, fluid);
        float h2 = this.getCornerHeight(world, posX, posY, posZ + 1, fluid);
        float h3 = this.getCornerHeight(world, posX + 1, posY, posZ + 1, fluid);
        float h4 = this.getCornerHeight(world, posX + 1, posY, posZ, fluid);

        float yOffset = sfDown ? 0.0F : EPSILON;

        final ModelQuadViewMutable quad = this.quad;

        LightMode lightMode = hc && Minecraft.isAmbientOcclusionEnabled() ? LightMode.SMOOTH : LightMode.FLAT;
        LightPipeline lighter = this.lighters.getLighter(lightMode);

        quad.setFlags(0);

        if (!sfUp && this.isSideExposed(world, posX, posY, posZ, EnumFacing.UP)) {
            h1 -= 0.001F;
            h2 -= 0.001F;
            h3 -= 0.001F;
            h4 -= 0.001F;

            Vector3d velocity = WorldUtil.getVelocity(world, pos, fluidState);

            TextureAtlasSprite sprite;
            ModelQuadFacing facing;
            float u1, u2, u3, u4;
            float v1, v2, v3, v4;

            if (velocity.x == 0.0D && velocity.z == 0.0D) {
                sprite = sprites[0];
                facing = ModelQuadFacing.UP;
                u1 = sprite.getInterpolatedU(0.0D);
                v1 = sprite.getInterpolatedV(0.0D);
                u2 = u1;
                v2 = sprite.getInterpolatedV(16.0D);
                u3 = sprite.getInterpolatedU(16.0D);
                v3 = v2;
                u4 = u3;
                v4 = v1;
            } else {
                sprite = sprites[1];
                facing = ModelQuadFacing.UNASSIGNED;
                float dir = (float) MathHelper.atan2(velocity.z, velocity.x) - (1.5707964f);
                float sin = MathHelper.sin(dir) * 0.25F;
                float cos = MathHelper.cos(dir) * 0.25F;
                u1 = sprite.getInterpolatedU(8.0F + (-cos - sin) * 16.0F);
                v1 = sprite.getInterpolatedV(8.0F + (-cos + sin) * 16.0F);
                u2 = sprite.getInterpolatedU(8.0F + (-cos + sin) * 16.0F);
                v2 = sprite.getInterpolatedV(8.0F + (cos + sin) * 16.0F);
                u3 = sprite.getInterpolatedU(8.0F + (cos + sin) * 16.0F);
                v3 = sprite.getInterpolatedV(8.0F + (cos - sin) * 16.0F);
                u4 = sprite.getInterpolatedU(8.0F + (cos - sin) * 16.0F);
                v4 = sprite.getInterpolatedV(8.0F + (-cos - sin) * 16.0F);
            }

            float uAvg = (u1 + u2 + u3 + u4) / 4.0F;
            float vAvg = (v1 + v2 + v3 + v4) / 4.0F;
            float s1 = (float) sprites[0].getIconWidth() / (sprites[0].getMaxU() - sprites[0].getMinU());
            float s2 = (float) sprites[0].getIconHeight() / (sprites[0].getMaxV() - sprites[0].getMinV());
            float s3 = 4.0F / Math.max(s2, s1);

            u1 = (float) MathUtil.lerp(s3, u1, uAvg);
            u2 = (float) MathUtil.lerp(s3, u2, uAvg);
            u3 = (float) MathUtil.lerp(s3, u3, uAvg);
            u4 = (float) MathUtil.lerp(s3, u4, uAvg);
            v1 = (float) MathUtil.lerp(s3, v1, vAvg);
            v2 = (float) MathUtil.lerp(s3, v2, vAvg);
            v3 = (float) MathUtil.lerp(s3, v3, vAvg);
            v4 = (float) MathUtil.lerp(s3, v4, vAvg);

            quad.setSprite(sprite);

            this.setVertex(quad, 0, 0.0f, h1, 0.0f, u1, v1);
            this.setVertex(quad, 1, 0.0f, h2, 1.0F, u2, v2);
            this.setVertex(quad, 2, 1.0F, h3, 1.0F, u3, v3);
            this.setVertex(quad, 3, 1.0F, h4, 0.0f, u4, v4);

            this.calculateQuadColors(quad, world, pos, lighter, EnumFacing.UP, 1.0F, hc);
            this.flushQuad(buffers, quad, facing, false);

            if (WorldUtil.method_15756(world, this.scratchPos.setPos(posX, posY + 1, posZ), fluid)) {
                this.setVertex(quad, 3, 0.0f, h1, 0.0f, u1, v1);
                this.setVertex(quad, 2, 0.0f, h2, 1.0F, u2, v2);
                this.setVertex(quad, 1, 1.0F, h3, 1.0F, u3, v3);
                this.setVertex(quad, 0, 1.0F, h4, 0.0f, u4, v4);

                this.flushQuad(buffers, quad, ModelQuadFacing.DOWN, true);
            }

            rendered = true;
        }

        if (!sfDown) {
            TextureAtlasSprite sprite = sprites[0];

            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();
            quad.setSprite(sprite);

            this.setVertex(quad, 0, 0.0f, yOffset, 1.0F, minU, maxV);
            this.setVertex(quad, 1, 0.0f, yOffset, 0.0f, minU, minV);
            this.setVertex(quad, 2, 1.0F, yOffset, 0.0f, maxU, minV);
            this.setVertex(quad, 3, 1.0F, yOffset, 1.0F, maxU, maxV);

            this.calculateQuadColors(quad, world, pos, lighter, EnumFacing.DOWN, 1.0F, hc);
            this.flushQuad(buffers, quad, ModelQuadFacing.DOWN, false);

            rendered = true;
        }

        quad.setFlags(ModelQuadFlags.IS_ALIGNED);

        for (EnumFacing dir : DirectionUtil.HORIZONTAL_DIRECTIONS) {
            float c1;
            float c2;
            float x1;
            float z1;
            float x2;
            float z2;

            switch (dir) {
                case NORTH:
                    if (sfNorth) {
                        continue;
                    }

                    c1 = h1;
                    c2 = h4;
                    x1 = 0.0f;
                    x2 = 1.0F;
                    z1 = 0.001f;
                    z2 = z1;
                    break;
                case SOUTH:
                    if (sfSouth) {
                        continue;
                    }

                    c1 = h3;
                    c2 = h2;
                    x1 = 1.0F;
                    x2 = 0.0f;
                    z1 = 0.999f;
                    z2 = z1;
                    break;
                case WEST:
                    if (sfWest) {
                        continue;
                    }

                    c1 = h2;
                    c2 = h1;
                    x1 = 0.001f;
                    x2 = x1;
                    z1 = 1.0F;
                    z2 = 0.0f;
                    break;
                case EAST:
                    if (sfEast) {
                        continue;
                    }

                    c1 = h4;
                    c2 = h3;
                    x1 = 0.999f;
                    x2 = x1;
                    z1 = 0.0f;
                    z2 = 1.0F;
                    break;
                default:
                    continue;
            }

            if (this.isSideExposed(world, posX, posY, posZ, dir)) {
                int adjX = posX + dir.getXOffset();
                int adjY = posY + dir.getYOffset();
                int adjZ = posZ + dir.getZOffset();

                TextureAtlasSprite sprite = sprites[1];
                TextureAtlasSprite oSprite = sprites[2];

                if (oSprite != null) {
                    BlockPos adjPos = this.scratchPos.setPos(adjX, adjY, adjZ);
                    IBlockState adjBlock = world.getBlockState(adjPos);

                    if (adjBlock.getBlockFaceShape(world, adjPos, dir.getOpposite()) == net.minecraft.block.state.BlockFaceShape.SOLID) {
                        sprite = oSprite;
                    }
                }

                float u1 = sprite.getInterpolatedU(0.0D);
                float u2 = sprite.getInterpolatedU(8.0D);
                float v1 = sprite.getInterpolatedV((1.0F - c1) * 16.0F * 0.5F);
                float v2 = sprite.getInterpolatedV((1.0F - c2) * 16.0F * 0.5F);
                float v3 = sprite.getInterpolatedV(8.0D);

                quad.setSprite(sprite);

                this.setVertex(quad, 0, x2, c2, z2, u2, v2);
                this.setVertex(quad, 1, x2, yOffset, z2, u2, v3);
                this.setVertex(quad, 2, x1, yOffset, z1, u1, v3);
                this.setVertex(quad, 3, x1, c1, z1, u1, v1);

                float br = dir.getAxis() == EnumFacing.Axis.Z ? 0.8F : 0.6F;

                ModelQuadFacing facing = ModelQuadFacing.fromDirection(dir);

                this.calculateQuadColors(quad, world, pos, lighter, dir, br, hc);
                this.flushQuad(buffers, quad, facing, false);

                if (sprite != oSprite) {
                    this.setVertex(quad, 0, x1, c1, z1, u1, v1);
                    this.setVertex(quad, 1, x1, yOffset, z1, u1, v3);
                    this.setVertex(quad, 2, x2, yOffset, z2, u2, v3);
                    this.setVertex(quad, 3, x2, c2, z2, u2, v2);

                    this.flushQuad(buffers, quad, facing.getOpposite(), true);
                }

                rendered = true;
            }
        }

        return rendered;
    }

    private void calculateQuadColors(ModelQuadView quad, IBlockAccess world, BlockPos pos, LightPipeline lighter, EnumFacing dir, float brightness, boolean colorized) {
        QuadLightData light = this.quadLightData;
        lighter.calculate(quad, pos, light, null, dir, false);

        int[] biomeColors = null;

        if (colorized) {
            IBlockState state = world.getBlockState(pos);
            IBlockColor colorProvider = ((BlockColorsExtended)this.vanillaBlockColors).getColorProvider(state);
            boolean containsColoredQuad = false;
            if(colorProvider != null) {
                biomeColors = this.biomeColorBlender.getColors(colorProvider, world, state, pos, quad);
                for(int color : biomeColors) {
                    if(color != 0xFFFFFF) {
                        containsColoredQuad = true;
                        break;
                    }
                }
            }
            if(!containsColoredQuad) {
                biomeColors = this.biomeColorBlender.getColors(FLUID_COLOR_PROVIDER, world, state, pos, quad);
            }
        }

        for (int i = 0; i < 4; i++) {
            this.quadColors[i] = ColorABGR.mul(biomeColors != null ? biomeColors[i] : 0xFFFFFFFF, light.br[i] * brightness);
        }
    }

    private void flushQuad(ChunkModelBuffers buffers, ModelQuadView quad, ModelQuadFacing facing, boolean flip) {
        int vertexIdx, lightOrder;

        if (flip) {
            vertexIdx = 3;
            lightOrder = -1;
        } else {
            vertexIdx = 0;
            lightOrder = 1;
        }

        ModelVertexSink sink = buffers.getSink(facing);
        sink.ensureCapacity(4);

        for (int i = 0; i < 4; i++) {
            float x = quad.getX(i);
            float y = quad.getY(i);
            float z = quad.getZ(i);

            int color = this.quadColors[vertexIdx];

            float u = quad.getTexU(i);
            float v = quad.getTexV(i);

            int light = this.quadLightData.lm[vertexIdx];

            sink.writeQuad(x, y, z, color, u, v, light);

            vertexIdx += lightOrder;
        }

        TextureAtlasSprite sprite = quad.rubidium$getSprite();

        if (sprite != null) {
            buffers.getRenderData().addSprite(sprite);
        }

        sink.flush();
    }

    private void setVertex(ModelQuadViewMutable quad, int i, float x, float y, float z, float u, float v) {
        quad.setX(i, x);
        quad.setY(i, y);
        quad.setZ(i, z);
        quad.setTexU(i, u);
        quad.setTexV(i, v);
    }

    private float getCornerHeight(IBlockAccess world, int x, int y, int z, Fluid fluid) {
        int samples = 0;
        float totalHeight = 0.0F;

        for (int i = 0; i < 4; ++i) {
            int x2 = x - (i & 1);
            int z2 = z - (i >> 1 & 1);

            Block block = world.getBlockState(this.scratchPos.setPos(x2, y + 1, z2)).getBlock();
            if (WorldUtil.getFluidOfBlock(block) == fluid) {
                return 1.0F;
            }

            BlockPos pos = this.scratchPos.setPos(x2, y, z2);

            IBlockState blockState = world.getBlockState(pos);
            Fluid fluid2 = WorldUtil.getFluidOfBlock(blockState.getBlock());

            if (fluid == fluid2) {
                float height = WorldUtil.getFluidHeight(fluid2, blockState.getValue(BlockLiquid.LEVEL));

                if (height >= 0.8F) {
                    totalHeight += height * 10.0F;
                    samples += 10;
                } else {
                    totalHeight += height;
                    ++samples;
                }
            } else if (!blockState.getMaterial().isSolid()) {
                ++samples;
            }
        }

        return totalHeight / (float) samples;
    }
}
