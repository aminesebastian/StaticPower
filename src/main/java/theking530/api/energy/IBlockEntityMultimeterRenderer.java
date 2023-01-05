package theking530.api.energy;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityMultimeterRenderer<T extends BlockEntity> {
	public void render(T blockEntity, Level level, LocalPlayer player, PoseStack stack, MultiBufferSource.BufferSource buffer);

	public void renderCameraFacingText(T blockEntity, Level level, LocalPlayer player, PoseStack stack, MultiBufferSource.BufferSource buffer);
}
