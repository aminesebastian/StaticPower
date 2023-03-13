package theking530.api.energy;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;

public interface IBlockEntityMultimeterRenderer<T extends BlockEntity> {
	public void render(T blockEntity, boolean isFocused, Level level, LocalPlayer player, PoseStack stack, MultiBufferSource.BufferSource buffer, Stage renderStage);
}
