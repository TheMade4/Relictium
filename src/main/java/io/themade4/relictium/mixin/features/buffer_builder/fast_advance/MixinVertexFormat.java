package io.themade4.relictium.mixin.features.buffer_builder.fast_advance;

import io.themade4.relictium.core.client.buffer.ExtendedVertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Thanks to Maximum for this optimization, taken from Fireblanket.
 */
@Mixin(VertexFormat.class)
public class MixinVertexFormat implements ExtendedVertexFormat {
    @Shadow
    @Final
    private List<VertexFormatElement> elements;

    private ExtendedVertexFormat.Element[] embeddium$extendedElements;

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/vertex/VertexFormat;)V", at = @At("RETURN"))
    private void embeddium$createElementArray(VertexFormat format, CallbackInfo ci) {
        this.embeddium$extendedElements = new ExtendedVertexFormat.Element[this.elements.size()];

        if (this.elements.isEmpty())
            return; // prevent crash with mods that create empty VertexFormats

        VertexFormatElement currentElement = elements.get(0);
        int id = 0;
        for (VertexFormatElement element : this.elements) {
            if (element.getUsage() == VertexFormatElement.EnumUsage.PADDING) continue;

            int oldId = id;
            int byteLength = 0;

            do {
                if (++id >= this.embeddium$extendedElements.length)
                    id -= this.embeddium$extendedElements.length;
                byteLength += currentElement.getSize();
                currentElement = this.elements.get(id);
            } while (currentElement.getUsage() == VertexFormatElement.EnumUsage.PADDING);

            this.embeddium$extendedElements[oldId] = new ExtendedVertexFormat.Element(element, id - oldId, byteLength);
        }
    }

    @Override
    public ExtendedVertexFormat.Element[] embeddium$getExtendedElements() {
        return this.embeddium$extendedElements;
    }
}
