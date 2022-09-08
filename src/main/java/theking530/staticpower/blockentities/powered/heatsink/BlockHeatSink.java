package theking530.staticpower.blockentities.powered.heatsink;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockHeatSink extends StaticPowerBlockEntityBlock {
	protected final ResourceLocation heatSinkTier;

	public BlockHeatSink(ResourceLocation heatSinkTier) {
		// NOTE: This tier is the tier that provides the machine properties of this
		// heatsink, not the heat properties.
		super(StaticPowerTiers.BASIC, Block.Properties.of(Material.METAL).strength(3.5f));
		this.heatSinkTier = heatSinkTier;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		tooltip.add(HeatTooltipUtilities.getHeatConductivityTooltip(tierObject.heatSinkConductivity.get()));
		tooltip.add(HeatTooltipUtilities.getOverheatingTooltip(tierObject.heatSinkOverheatTemperature.get()));
		tooltip.add(HeatTooltipUtilities.getMaximumHeatTooltip(tierObject.heatSinkMaximumTemperature.get()));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		super.getAdvancedTooltip(stack, worldIn, tooltip);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		tooltip.add(HeatTooltipUtilities.getHeatGenerationTooltip(tierObject.heatSinkElectricHeatGeneration.get()));
		tooltip.add(new TextComponent(ChatFormatting.GRAY + "Generation Usage: ")
				.append(StaticPowerEnergyTextUtilities.formatPowerRateToString(tierObject.heatSinkElectricHeatPowerUsage.get())).withStyle(ChatFormatting.RED));
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.COPPER) {
			return BlockEntityHeatSink.TYPE_COPPER.create(pos, state);
		} else if (tier == StaticPowerTiers.TIN) {
			return BlockEntityHeatSink.TYPE_TIN.create(pos, state);
		} else if (tier == StaticPowerTiers.SILVER) {
			return BlockEntityHeatSink.TYPE_SILVER.create(pos, state);
		} else if (tier == StaticPowerTiers.GOLD) {
			return BlockEntityHeatSink.TYPE_GOLD.create(pos, state);
		} else if (tier == StaticPowerTiers.ALUMINUM) {
			return BlockEntityHeatSink.TYPE_ALUMINUM.create(pos, state);
		}
		return null;
	}
}
