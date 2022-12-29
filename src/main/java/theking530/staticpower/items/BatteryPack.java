package theking530.staticpower.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.BatteryPackItemModel;

public class BatteryPack extends StaticPowerEnergyStoringItem implements ICustomModelSupplier {
	private static final String ACTIVATED_TAG = "activated";
	public final ResourceLocation tier;

	public BatteryPack(ResourceLocation tier) {
		this.tier = tier;
	}

	public boolean isActivated(ItemStack stack) {
		// If this stack has not been initialized, do so.
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}

		// Update the tag if it does not contain the activated tag.
		if (!stack.getTag().contains(ACTIVATED_TAG)) {
			stack.getTag().putBoolean(ACTIVATED_TAG, false);
		}

		// Read the activated tag.
		return stack.getTag().getBoolean(ACTIVATED_TAG);
	}

	public void toggleActivated(ItemStack stack) {
		// Make sure we call isActivated first to ensure we are initialized.
		if (!isActivated(stack)) {
			stack.getTag().putBoolean(ACTIVATED_TAG, true);
		} else {
			stack.getTag().putBoolean(ACTIVATED_TAG, false);
		}
	}

	/**
	 * When shift right clicked, toggle activation.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (player.isShiftKeyDown()) {
			toggleActivated(item);
			return InteractionResultHolder.success(item);
		}
		return InteractionResultHolder.pass(item);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isActivated(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		// If we're in a player's inventory.
		if (isActivated(stack) && entityIn instanceof Player && !worldIn.isClientSide()) {
			// Get the power capability.
			IStaticPowerStorage powerStorage = stack.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).orElse(null);
			if (powerStorage == null) {
				return;
			}

			// If power is stored, attempt to charge items.
			if (powerStorage.getStoredPower() > 0) {
				// Get the player.
				Player player = (Player) entityIn;

				// Get all the chargeable items.
				List<IStaticPowerStorage> items = new ArrayList<IStaticPowerStorage>();

				// Iterate through the inventory.
				for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
					// Get the stack in the slot. Skip if its empty.
					ItemStack inventoryStack = player.getInventory().getItem(i);
					if (inventoryStack.isEmpty() || inventoryStack == stack || inventoryStack.getItem() instanceof BatteryPack) {
						continue;
					}

					if (EnergyHandlerItemStackUtilities.isEnergyContainer(inventoryStack)) {
						items.add(EnergyHandlerItemStackUtilities.getEnergyContainer(inventoryStack).orElse(null));
					}
				}

				// How much power should we distribute?
				if (items.size() > 0) {
					double perItemDistribute = powerStorage.getStoredPower() / items.size();
					perItemDistribute = SDMath.clamp(perItemDistribute, 1, powerStorage.getCapacity() / 100);
					perItemDistribute = Math.min(perItemDistribute, powerStorage.getMaximumPowerOutput());

					for (IStaticPowerStorage otherItem : items) {
						double charged = otherItem.addPower(new PowerStack(perItemDistribute, powerStorage.getOutputVoltage()), false);
						powerStorage.drainPower(charged, false);

						// Break out if we used all the power.
						if (powerStorage.getStoredPower() <= 0) {
							break;
						}
					}
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(Component.translatable(isActivated(stack) ? "gui.staticpower.active" : "gui.staticpower.inactive"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new BatteryPackItemModel(existingModel);
	}

	@Override
	public double getCapacity() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryCapacity.get() * 3;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	@Override
	public double getMaximumOutputPower() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}

	@Override
	public boolean canOutputExternalPower() {
		return true;
	}
}
