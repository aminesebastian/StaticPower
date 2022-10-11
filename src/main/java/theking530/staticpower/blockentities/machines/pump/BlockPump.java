package theking530.staticpower.blockentities.machines.pump;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockPump extends StaticPowerBlockEntityBlock {

	public BlockPump(ResourceLocation tier) {
		super(tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL).noOcclusion());
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		// Create the decimal formatter.
		DecimalFormat decimalFormat = new DecimalFormat("#.#");

		// Format the pump rate and add it as a tooltip.
		String pumpRate = decimalFormat.format(tierObject.pumpRate.get() / 20.0f);
		tooltip.add(Component.translatable("gui.staticpower.pump_rate_format", ChatFormatting.AQUA.toString() + pumpRate));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//		if (tier == StaticPowerTiers.IRON) {
//			return BlockEntityPump.TYPE_IRON.create(pos, state);
//		} else 
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityPump.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityPump.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityPump.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityPump.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityPump.TYPE_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityPump.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}
}
