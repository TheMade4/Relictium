package io.themade4.relictium.compat.codechickenlib;

import codechicken.lib.render.block.BlockRenderingRegistry;
import io.themade4.relictium.core.client.model.quad.properties.ModelQuadFacing;
import io.themade4.relictium.core.client.render.chunk.compile.buffers.ChunkModelBuffers;
import io.themade4.relictium.core.client.render.chunk.format.ModelVertexSink;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.IntBuffer;

public class CCLCompat {
	private static final boolean CCL_LOADED = Loader.isModLoaded("codechickenlib");
    private static final ThreadLocal<BufferBuilder> CCL_BUILDERS = CCL_LOADED ? ThreadLocal.withInitial(() -> new BufferBuilder(200 * 1024)) : null;
    private static final VertexFormat FORMAT = DefaultVertexFormats.BLOCK;

    private static final MethodHandle CCL_RENDER;

    static {
        try {
            CCL_RENDER = CCL_LOADED ? MethodHandles.publicLookup().unreflect(ObfuscationReflectionHelper.findMethod(BlockRenderingRegistry.class, "renderBlock", void.class, IBlockAccess.class, BlockPos.class, IBlockState.class, BufferBuilder.class)) : null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean canHandle(EnumBlockRenderType type) {
        return CCL_LOADED && BlockRenderingRegistry.canHandle(type);
    }

    private static void pumpIntoBuffer(BufferBuilder builder, ModelVertexSink sink) {
        IntBuffer theVertexData = builder.getByteBuffer().asIntBuffer();

        int numQuads = theVertexData.limit() / FORMAT.getIntegerSize();

        sink.ensureCapacity(numQuads);

        // TODO maybe use format description instead of hardcoding offsets
        for(int i = 0; i < numQuads; i++) {
            int vOff = i * FORMAT.getIntegerSize();
            float x = Float.intBitsToFloat(theVertexData.get(vOff));
            float y = Float.intBitsToFloat(theVertexData.get(vOff + 1));
            float z = Float.intBitsToFloat(theVertexData.get(vOff + 2));
            int color = theVertexData.get(vOff + 3);
            float u = Float.intBitsToFloat(theVertexData.get(vOff + 4));
            float v = Float.intBitsToFloat(theVertexData.get(vOff + 5));
            int light = theVertexData.get(vOff + 6);
            sink.writeQuad(x, y, z, color, u, v, light);
        }

        sink.flush();
    }

    public static void renderBlock(IBlockAccess world, BlockPos pos, IBlockState state, ChunkModelBuffers buffers) {
        BufferBuilder builder = CCL_BUILDERS.get();

        builder.begin(GL11.GL_QUADS, FORMAT);

        builder.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());

        try {
            // flag is needed for the methodhandle to work
            //noinspection unused
            boolean flag = (boolean)CCL_RENDER.invokeExact(world, pos, state, builder);
        } catch(Throwable e) {
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            builder.finishDrawing();
        }

        pumpIntoBuffer(builder, buffers.getSink(ModelQuadFacing.UNASSIGNED));
    }
	
}
