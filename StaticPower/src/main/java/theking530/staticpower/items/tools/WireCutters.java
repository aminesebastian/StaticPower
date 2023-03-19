package theking530.staticpower.items.tools;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.power.wireconnector.BlockEntityWireConnector;
import theking530.staticpower.blockentities.power.wireconnector.BlockWireConnector;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.items.StaticPowerItem;

public class WireCutters extends StaticPowerItem {
	private final ResourceLocation tier;
	private final Supplier<Item> repairItem;

	public WireCutters(ResourceLocation tier, Supplier<Item> repairItem) {
		super(new Item.Properties().stacksTo(1).tab(ModCreativeTabs.TOOLS));
		this.tier = tier;
		this.repairItem = repairItem;
	}

	@Override
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!world.isClientSide()) {
			if (world.getBlockState(pos).getBlock() instanceof BlockWireConnector) {
				BlockEntityWireConnector connector = (BlockEntityWireConnector) world.getBlockEntity(pos);
				AbstractCableProviderComponent cableComp = connector.getComponent(AbstractCableProviderComponent.class);
				cableComp.breakAllSparseLinks();
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == repairItem.get()) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		ItemStack stackCopy = stack.copy();
		if (stackCopy.hurt(1, RandomSource.create(), null)) {
			stackCopy.shrink(1);
			stackCopy.setDamageValue(0);
		}
		return stackCopy;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return StaticPowerConfig.getTier(tier).toolConfiguration.wireCutterUses.get();
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(Component.literal("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(Component.literal("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}
}
