package theking530.staticpower.cables.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.StaticPowerAdditionalModels.CableModelSet;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class BlockItemCable extends AbstractCableBlock {

	public BlockItemCable(ResourceLocation tier) {
		super(tier, new CableBoundsCache(2.0D, new Vector3D(3.0f, 3.0f, 3.0f)), 2.5f);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityItemCable.TYPE.create(pos, state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		CableModelSet model = StaticPowerAdditionalModels.ITEM_CABLES.get(tier);
		return new CableBakedModel(existingModel, model);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		double blocksPerTick = StaticPowerConfig.getTier(tier).cableItemConfiguration.itemCableMaxSpeed.get();

		tooltip.add(Component.translatable("gui.staticpower.max_transfer_rate"));
		tooltip.add(Component.literal("• ").append(Component.translatable("gui.staticpower.item_cable_transfer_rate",
				ChatFormatting.GREEN + GuiTextUtilities.formatUnitRateToString(blocksPerTick).getString(), Component.translatable("gui.staticpower.blocks").getString())));
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(Component.literal(""));
		tooltip.add(Component.translatable("gui.staticpower.item_cable_acceleration", ChatFormatting.BLUE
				+ GuiTextUtilities.formatUnitRateToString(StaticPowerConfig.getTier(tier).cableItemConfiguration.itemCableAcceleration.get() * 100).getString()));
		tooltip.add(Component.translatable("gui.staticpower.item_cable_friction",
				ChatFormatting.RED + GuiTextUtilities.formatUnitRateToString(StaticPowerConfig.getTier(tier).cableItemConfiguration.itemCableFriction.get() * 100).getString()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}
}
