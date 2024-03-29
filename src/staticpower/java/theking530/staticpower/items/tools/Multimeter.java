package theking530.staticpower.items.tools;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticpower.cables.power.PowerNetworkModule;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.items.StaticPowerItem;

public class Multimeter extends StaticPowerItem {
	public static final String INITIAL_POSITION_TAG = "initial_sample_pos";

	public Multimeter() {
		super(new Item.Properties().stacksTo(1).setNoRepair().tab(ModCreativeTabs.TOOLS));
	}

	@Override
	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		// If on the server.
		if (!world.isClientSide()) {
			// If we shift right clicked, clear the pending location.
			if (player.isCrouching() && isPendingSecondLocation(item)) {
				clearPendingLocation(item);
				player.sendSystemMessage(Component.translatable("gui.staticpower.multimeter_cleared"));
				return InteractionResult.PASS;
			}

			// If we right clicked on a cable.
			if (CableNetworkAccessor.get(world).isTrackingCable(pos)) {
				// Get the cable.
				Cable cable = CableNetworkAccessor.get(world).getCable(pos);
				if (isPendingSecondLocation(item)) {
					BlockPos firstLocation = getFirstLocation(item);
					clearPendingLocation(item);

					if (!CableNetworkAccessor.get(world).isTrackingCable(pos)) {
						player.sendSystemMessage(Component.translatable("gui.staticpower.invalid_multimeter_second_location"));
						return InteractionResult.PASS;
					} else {
						Cable firstPositionCable = CableNetworkAccessor.get(world).getCable(firstLocation);
						if (firstPositionCable.getNetwork() != cable.getNetwork()) {
							player.sendSystemMessage(Component.translatable("gui.staticpower.invalid_multimeter_second_location"));
							return InteractionResult.PASS;
						}
					}

					// Get the multimeter output and log it to the chat. Then clear it.
					List<Component> messages = new ArrayList<Component>();
					PowerNetworkModule module = cable.getNetwork().<PowerNetworkModule>getModule(ModCableModules.Power.get());

					module.getMultimeterOutput(messages, firstLocation, pos);
					for (Component message : messages) {
						player.sendSystemMessage(message);
					}
					return InteractionResult.SUCCESS;
				} else {
					if (cable.supportsNetworkModule(ModCableModules.Power.get())) {
						setFirstSampleLocation(item, pos);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return InteractionResult.PASS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack item, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	public Component getName(ItemStack item) {
		return super.getName(item);
	}

	@Override
	public Rarity getRarity(ItemStack item) {
		return Rarity.RARE;
	}

	public void setFirstSampleLocation(ItemStack stack, BlockPos pos) {
		stack.getOrCreateTag().putLong(INITIAL_POSITION_TAG, pos.asLong());
	}

	public BlockPos getFirstLocation(ItemStack stack) {
		return BlockPos.of(stack.getTag().getLong(INITIAL_POSITION_TAG));
	}

	public boolean isPendingSecondLocation(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(INITIAL_POSITION_TAG);
	}

	public void clearPendingLocation(ItemStack stack) {
		stack.removeTagKey(INITIAL_POSITION_TAG);
	}
}
