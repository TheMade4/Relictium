package io.themade4.relictium.core.client.model.vertex;

import io.themade4.relictium.core.client.model.vertex.formats.glyph.GlyphVertexSink;
import io.themade4.relictium.core.client.model.vertex.formats.glyph.GlyphVertexType;
import io.themade4.relictium.core.client.model.vertex.formats.line.LineVertexSink;
import io.themade4.relictium.core.client.model.vertex.formats.line.LineVertexType;
import io.themade4.relictium.core.client.model.vertex.formats.particle.ParticleVertexSink;
import io.themade4.relictium.core.client.model.vertex.formats.particle.ParticleVertexType;
import io.themade4.relictium.core.client.model.vertex.formats.quad.QuadVertexSink;
import io.themade4.relictium.core.client.model.vertex.formats.quad.QuadVertexType;
import io.themade4.relictium.core.client.model.vertex.formats.screen_quad.BasicScreenQuadVertexSink;
import io.themade4.relictium.core.client.model.vertex.formats.screen_quad.BasicScreenQuadVertexType;
import io.themade4.relictium.core.client.model.vertex.type.VanillaVertexType;

public class VanillaVertexTypes {
    public static final VanillaVertexType<QuadVertexSink> QUADS = new QuadVertexType();
    public static final VanillaVertexType<LineVertexSink> LINES = new LineVertexType();
    public static final VanillaVertexType<GlyphVertexSink> GLYPHS = new GlyphVertexType();
    public static final VanillaVertexType<ParticleVertexSink> PARTICLES = new ParticleVertexType();
    public static final VanillaVertexType<BasicScreenQuadVertexSink> BASIC_SCREEN_QUADS = new BasicScreenQuadVertexType();
}
