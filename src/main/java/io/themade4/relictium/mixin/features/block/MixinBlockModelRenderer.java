package io.themade4.relictium.mixin.features.block;

import io.themade4.relictium.core.client.model.quad.ModelQuadView;
import io.themade4.relictium.core.client.model.vertex.VanillaVertexTypes;
import io.themade4.relictium.core.client.model.vertex.VertexDrain;
import io.themade4.relictium.core.client.model.vertex.formats.quad.QuadVertexSink;
import io.themade4.relictium.core.client.render.texture.SpriteUtil;
import io.themade4.relictium.core.client.util.ModelQuadUtil;
import io.themade4.relictium.core.client.util.math.MatrixStack;
import io.themade4.relictium.core.client.util.rand.XoRoShiRoRandom;
import io.themade4.relictium.common.util.DirectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {
    private final XoRoShiRoRandom random = new XoRoShiRoRandom();

    /**
     * @reason Use optimized vertex writer intrinsics, avoid allocations
     * @author JellySquid
     */
    // TODO Light
    @Overwrite
    public boolean renderModel(IBlockAccess world, IBakedModel bakedModel, IBlockState blockState, BlockPos pos, BufferBuilder buffer, boolean checkSides, long rand) {
        boolean flag = false;
        QuadVertexSink drain = VertexDrain.of(buffer)
                .createSink(VanillaVertexTypes.QUADS);
        XoRoShiRoRandom random = this.random;
        MatrixStack matrixStack = new MatrixStack();

        random.setSeed(42L);
        for (EnumFacing direction : DirectionUtil.ALL_DIRECTIONS) {
            List<BakedQuad> quads = bakedModel.getQuads(blockState, direction, random.nextLong());

            if (!quads.isEmpty()) {
                renderQuad(matrixStack.peek(), drain, quads, 200, 0);
                flag = true;
            }
        }

        random.setSeed(42L);
        List<BakedQuad> quads = bakedModel.getQuads(blockState, null, random.nextLong());

        if (!quads.isEmpty()) {
            renderQuad(matrixStack.peek(), drain, quads, 200, 0);
            flag = true;
        }

        drain.flush();

        return flag;
    }

    private static void renderQuad(MatrixStack.Entry entry, QuadVertexSink drain, List<BakedQuad> list, int light, int overlay) {
        if (list.isEmpty()) {
            return;
        }

        drain.ensureCapacity(list.size() * 4);

        for (BakedQuad bakedQuad : list) {
            int color = bakedQuad.hasTintIndex() ? bakedQuad.getTintIndex() : 0xFFFFFFFF;

            ModelQuadView quad = ((ModelQuadView) bakedQuad);

            for (int i = 0; i < 4; i++) {
                drain.writeQuad(entry, quad.getX(i), quad.getY(i), quad.getZ(i), color, quad.getTexU(i), quad.getTexV(i),
                        light, overlay, ModelQuadUtil.getFacingNormal(bakedQuad.getFace(), quad.getNormal(i)));
            }

            SpriteUtil.markSpriteActive(quad.rubidium$getSprite());
        }
    }
}
