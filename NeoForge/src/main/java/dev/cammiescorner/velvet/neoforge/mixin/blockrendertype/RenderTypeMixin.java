package dev.cammiescorner.velvet.neoforge.mixin.blockrendertype;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.cammiescorner.velvet.mixin.VelvetMixinPlugin;
import dev.cammiescorner.velvet.neoforge.impl.NeoRenderType;
import dev.cammiescorner.velvet.impl.RenderTypeUtil;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.WeakHashMap;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin extends RenderStateShard implements NeoRenderType {

	@Unique
	private static WeakHashMap<RenderType, Object> velvet$CACHED_LAYERS;

	@Shadow
	public int chunkLayerId;

	private RenderTypeMixin(String name, Runnable setupState, Runnable clearState) {
		super(name, setupState, clearState);
		throw new UnsupportedOperationException();
	}

	@WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;chunkBufferLayers()Ljava/util/List;"))
	private static List<RenderType> getChunkBuffers(Operation<List<RenderType>> original) {
		RenderTypeUtil.freezeRegistryHack = false;
		List<RenderType> buffers = original.call();
		RenderTypeUtil.freezeRegistryHack = true;
		return buffers;
	}

	@Override
	public void velvet$initBlockLayerId() {
		if (this.chunkLayerId == -1) {
			// make sure chunk buffer layers always get their static ID
			RenderTypeUtil.freezeRegistryHack = false;
			//noinspection ResultOfMethodCallIgnored
			RenderType.chunkBufferLayers();
			RenderTypeUtil.freezeRegistryHack = true;

			if (velvet$CACHED_LAYERS == null) {
				velvet$CACHED_LAYERS = new WeakHashMap<>();
			}

			this.chunkLayerId = velvet$CACHED_LAYERS.keySet().stream().mapToInt(RenderType::getChunkLayerId).max().orElse(0) + 1;
		}
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void construct(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState, CallbackInfo ci) {
		/*
		Prevents the game from crashing due to incompatibility with Iris.
		When Iris is installed, rendering a block outline causes an immediate crash.
		Should the mixin fail to be disabled by the plugin, this is used as a fallback to prevent the crash.
		 */
		if (!VelvetMixinPlugin.isAllowRenderLayerMixins()) return;

		if (velvet$CACHED_LAYERS == null) {
			velvet$CACHED_LAYERS = new WeakHashMap<>();
		}

		velvet$CACHED_LAYERS.put((RenderType) (Object) this, null);
	}
}
