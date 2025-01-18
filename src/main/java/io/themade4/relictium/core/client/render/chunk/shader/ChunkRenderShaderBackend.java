package io.themade4.relictium.core.client.render.chunk.shader;

import io.themade4.relictium.core.client.gl.attribute.GlVertexFormat;
import io.themade4.relictium.core.client.gl.shader.GlProgram;
import io.themade4.relictium.core.client.gl.shader.GlShader;
import io.themade4.relictium.core.client.gl.device.RenderDevice;
import io.themade4.relictium.core.client.gl.shader.ShaderLoader;
import io.themade4.relictium.core.client.gl.shader.ShaderType;
import io.themade4.relictium.core.client.gl.compat.FogHelper;
import io.themade4.relictium.core.client.model.vertex.type.ChunkVertexType;
import io.themade4.relictium.core.client.render.chunk.ChunkGraphicsState;
import io.themade4.relictium.core.client.render.chunk.ChunkRenderBackend;
import io.themade4.relictium.core.client.render.chunk.format.ChunkMeshAttribute;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;

public abstract class ChunkRenderShaderBackend<T extends ChunkGraphicsState>
        implements ChunkRenderBackend<T> {
    private final EnumMap<ChunkFogMode, ChunkProgram> programs = new EnumMap<>(ChunkFogMode.class);

    protected final ChunkVertexType vertexType;
    protected final GlVertexFormat<ChunkMeshAttribute> vertexFormat;

    protected ChunkProgram activeProgram;

    public ChunkRenderShaderBackend(ChunkVertexType vertexType) {
        this.vertexType = vertexType;
        this.vertexFormat = vertexType.getCustomVertexFormat();
    }

    private ChunkProgram createShader(RenderDevice device, ChunkFogMode fogMode, GlVertexFormat<ChunkMeshAttribute> vertexFormat) {
        GlShader vertShader = ShaderLoader.loadShader(device, ShaderType.VERTEX,
                new ResourceLocation("relictium", "chunk_gl20.v.glsl"), fogMode.getDefines());

        GlShader fragShader = ShaderLoader.loadShader(device, ShaderType.FRAGMENT,
                new ResourceLocation("relictium", "chunk_gl20.f.glsl"), fogMode.getDefines());

        try {
            return GlProgram.builder(new ResourceLocation("relictium", "chunk_shader"))
                    .attachShader(vertShader)
                    .attachShader(fragShader)
                    .bindAttribute("a_Pos", ChunkShaderBindingPoints.POSITION)
                    .bindAttribute("a_Color", ChunkShaderBindingPoints.COLOR)
                    .bindAttribute("a_TexCoord", ChunkShaderBindingPoints.TEX_COORD)
                    .bindAttribute("a_LightCoord", ChunkShaderBindingPoints.LIGHT_COORD)
                    .bindAttribute("d_ModelOffset", ChunkShaderBindingPoints.MODEL_OFFSET)
                    .build((program, name) -> new ChunkProgram(device, program, name, fogMode.getFactory()));
        } finally {
            vertShader.delete();
            fragShader.delete();
        }
    }

    @Override
    public final void createShaders(RenderDevice device) {
        this.programs.put(ChunkFogMode.NONE, this.createShader(device, ChunkFogMode.NONE, this.vertexFormat));
        this.programs.put(ChunkFogMode.LINEAR, this.createShader(device, ChunkFogMode.LINEAR, this.vertexFormat));
        this.programs.put(ChunkFogMode.EXP2, this.createShader(device, ChunkFogMode.EXP2, this.vertexFormat));
    }

    @Override
    public void begin() {
        this.activeProgram = this.programs.get(FogHelper.getFogMode());
        this.activeProgram.bind();
        this.activeProgram.setup(this.vertexType.getModelScale(), this.vertexType.getTextureScale());
    }

    @Override
    public void end() {
        this.activeProgram.unbind();
        this.activeProgram = null;
    }

    @Override
    public void delete() {
        for (ChunkProgram shader : this.programs.values()) {
            shader.delete();
        }
    }

    @Override
    public ChunkVertexType getVertexType() {
        return this.vertexType;
    }
}
