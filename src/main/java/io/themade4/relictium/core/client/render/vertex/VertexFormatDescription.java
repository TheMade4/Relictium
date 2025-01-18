package io.themade4.relictium.core.client.render.vertex;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * Inspired by modern Sodium's VertexFormatDescription.
 */
public class VertexFormatDescription {
    public enum Element {
        POSITION(DefaultVertexFormats.POSITION_3F),
        COLOR(DefaultVertexFormats.COLOR_4UB),
        TEXTURE(DefaultVertexFormats.TEX_2F),
        NORMAL(DefaultVertexFormats.NORMAL_3B);

        final VertexFormatElement underlyingElement;
        static final Map<VertexFormatElement, Element> VANILLA_TO_COMMON = new Object2ObjectOpenHashMap<>();
        Element(VertexFormatElement baseElement) {
            this.underlyingElement = baseElement;
        }

        static {
            for(Element e : Element.values()) {
                VANILLA_TO_COMMON.put(e.underlyingElement, e);
            }
        }
    }

    private static final Element[] COMMON_ELEMENTS = Element.values();

    private final int[] elementOffsets;
    private final VertexFormat format;

    private static final Map<VertexFormat, VertexFormatDescription> REGISTRY = new Reference2ReferenceOpenHashMap<>();
    private static final StampedLock LOCK = new StampedLock();

    VertexFormatDescription(VertexFormat format) {
        this.elementOffsets = new int[COMMON_ELEMENTS.length];
        Arrays.fill(this.elementOffsets, -1);
        this.format = format;
        for(int i = 0; i < format.getElementCount(); i++) {
            Element commonElement = Element.VANILLA_TO_COMMON.get(format.getElement(i));
            if(commonElement != null) {
                elementOffsets[commonElement.ordinal()] = format.getOffset(i) / 4;
            }
        }
    }

    public static VertexFormatDescription get(VertexFormat format) {
        long stamp = LOCK.readLock();
        VertexFormatDescription desc;
        try {
            desc = REGISTRY.get(format);
        } finally {
            LOCK.unlockRead(stamp);
        }

        if (desc != null) {
            return desc;
        }

        desc = new VertexFormatDescription(format);

        stamp = LOCK.writeLock();

        try {
            REGISTRY.put(format, desc);
        } finally {
            LOCK.unlockWrite(stamp);
        }

        return desc;
    }

    public int getIndex(VertexFormatDescription.Element element) {
        return this.elementOffsets[element.ordinal()];
    }
}
