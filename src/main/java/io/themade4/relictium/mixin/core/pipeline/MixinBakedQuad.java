package io.themade4.relictium.mixin.core.pipeline;

import io.themade4.relictium.core.client.model.quad.ModelQuadView;
import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFlags;
import io.themade4.relictium.core.client.render.vertex.VertexFormatDescription;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BakedQuad.class, remap = false)
public class MixinBakedQuad implements ModelQuadView {

    @Shadow
    @Final
    protected TextureAtlasSprite sprite;

    @Shadow
    @Final
    protected int tintIndex;

    @Shadow public int[] getVertexData() {
        throw new AssertionError();
    }

    @Shadow @Final protected VertexFormat format;
    protected int cachedFlags;

    private VertexFormatDescription formatDescription;

    @Inject(method = "<init>([IILnet/minecraft/util/EnumFacing;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;ZLnet/minecraft/client/renderer/vertex/VertexFormat;)V", at = @At("RETURN"))
    private void init(int[] vertexData, int colorIndex, EnumFacing face, TextureAtlasSprite sprite, boolean shade, VertexFormat format, CallbackInfo ci) {
        this.formatDescription = VertexFormatDescription.get(format);
        if(!UnpackedBakedQuad.class.isAssignableFrom(this.getClass())) {
            this.cachedFlags = ModelQuadFlags.getQuadFlags((BakedQuad) (Object) this);
        }
    }

    private int vertexOffset(int idx) {
        return idx * this.format.getIntegerSize();
    }

    @Override
    public float getX(int idx) {
        int positionIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.POSITION);
        if (positionIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + positionIndex]);
    }

    @Override
    public float getY(int idx) {
        int positionIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.POSITION);
        if (positionIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + positionIndex + 1]);
    }

    @Override
    public float getZ(int idx) {
        int positionIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.POSITION);
        if (positionIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + positionIndex + 2]);
    }

    @Override
    public int getColor(int idx) {
        int colorIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.COLOR);
        if (colorIndex == -1) {
            return 0;
        }
        return this.getVertexData()[vertexOffset(idx) + colorIndex];
    }

    @Override
    public TextureAtlasSprite rubidium$getSprite() {
        return this.sprite;
    }

    @Override
    public float getTexU(int idx) {
        int textureIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.TEXTURE);
        if (textureIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + textureIndex]);
    }

    @Override
    public float getTexV(int idx) {
        int textureIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.TEXTURE);
        if (textureIndex == -1) {
            return 0;
        }
        return Float.intBitsToFloat(this.getVertexData()[vertexOffset(idx) + textureIndex + 1]);
    }

    @Override
    public int getFlags() {
        return this.cachedFlags;
    }

    @Override
    public int getNormal(int idx) {
        int normalIndex = this.formatDescription.getIndex(VertexFormatDescription.Element.NORMAL);
        if (normalIndex == -1) {
            return 0;
        }
        return this.getVertexData()[vertexOffset(idx) + normalIndex];
    }

    @Override
    public int getColorIndex() {
        return this.tintIndex;
    }
}
