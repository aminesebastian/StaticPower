package theking530.staticpower.fluid;

import javax.annotation.Nullable;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import theking530.staticcore.utilities.SDColor;

public class FluidClientExtension implements IClientFluidTypeExtensions {
	private SDColor fogColor;
	private int tint;
	private ResourceLocation stillTexture;
	private ResourceLocation flowingTexture;

	public FluidClientExtension() {
		tint = 0xFFFFFFFF;
	}

	public void setFogColor(SDColor fogColor) {
		this.fogColor = fogColor;
	}

	public void setTint(SDColor tint) {
		this.tint = tint.encodeInInteger();
	}

	public void setStillTexture(ResourceLocation stillTexture) {
		this.stillTexture = stillTexture;
	}

	public void setFlowingTexture(ResourceLocation flowingTexture) {
		this.flowingTexture = flowingTexture;
	}

	@Override
	public ResourceLocation getFlowingTexture() {
		return flowingTexture;
	}

	@Nullable
	@Override
	public ResourceLocation getOverlayTexture() {
		return null;
	}

	@Override
	public ResourceLocation getRenderOverlayTexture(Minecraft minecraft) {
		return null;
	}

	@Override
	public ResourceLocation getStillTexture() {
		return stillTexture;
	}

	@Override
	public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return tint;
	}

	@Override
	public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
		return fogColor == null ? IClientFluidTypeExtensions.super.modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor)
				: fogColor.toVector3f();
	}

	@Override
	public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
		RenderSystem.setShaderFogStart(1f);
		RenderSystem.setShaderFogEnd(6f);
	}
}
