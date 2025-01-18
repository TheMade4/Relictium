package io.themade4.relictium.core.client.render.chunk.shader;

import io.themade4.relictium.core.client.gl.shader.GlProgram;
import io.themade4.relictium.core.client.gl.device.RenderDevice;
import io.themade4.relictium.core.client.render.GameRendererContext;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

import java.util.function.Function;

/**
 * A forward-rendering shader program for chunks.
 */
public class ChunkProgram extends GlProgram {
    // Uniform variable binding indexes
    private final int uModelViewProjectionMatrix;
    private final int uModelScale;
    private final int uTextureScale;
    private final int uBlockTex;
    private final int uLightTex;

    // The fog shader component used by this program in order to setup the appropriate GL state
    private final ChunkShaderFogComponent fogShader;

    protected ChunkProgram(RenderDevice owner, ResourceLocation name, int handle, Function<ChunkProgram, ChunkShaderFogComponent> fogShaderFunction) {
        super(owner, name, handle);

        this.uModelViewProjectionMatrix = this.getUniformLocation("u_ModelViewProjectionMatrix");

        this.uBlockTex = this.getUniformLocation("u_BlockTex");
        this.uLightTex = this.getUniformLocation("u_LightTex");
        this.uModelScale = this.getUniformLocation("u_ModelScale");
        this.uTextureScale = this.getUniformLocation("u_TextureScale");

        this.fogShader = fogShaderFunction.apply(this);
    }

    public void setup(float modelScale, float textureScale) {
        GL20.glUniform1i(this.uBlockTex, 0);
        GL20.glUniform1i(this.uLightTex, 1);

        GL20.glUniform3f(this.uModelScale, modelScale, modelScale, modelScale);
        GL20.glUniform2f(this.uTextureScale, textureScale, textureScale);

        this.fogShader.setup();

        GL20.glUniformMatrix4(this.uModelViewProjectionMatrix, false, GameRendererContext.getModelViewProjectionMatrix());
    }
}
