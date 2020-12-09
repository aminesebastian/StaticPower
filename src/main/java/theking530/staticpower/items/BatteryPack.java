package theking530.staticpower.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.PowerEnergyInterface;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.BatteryPackItemModel;

public class BatteryPack extends StaticPowerEnergyStoringItem implements ICustomModelSupplier {
	private static final String ACTIVATED_TAG = "activated";
	public final ResourceLocation tier;

	public BatteryPack(String name, ResourceLocation tier) {
		super(name, 0);
		this.tier = tier;
	}

	public boolean isActivated(ItemStack stack) {
		// If this stack has not been initialized, do so.
		if (!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
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
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (player.isSneaking()) {
			toggleActivated(item);
			return ActionResult.resultSuccess(item);
		}
		return ActionResult.resultPass(item);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return isActivated(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		// If we're in a player's inventory.
		if (isActivated(stack) && entityIn instanceof PlayerEntity) {
			// Get the power capability.
			stack.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
				// If power is stored, attempt to charge items.
				if (powerStorage.getStoredPower() > 0) {
					// Get the player.
					PlayerEntity player = (PlayerEntity) entityIn;

					// Get all the chargeable items.
					List<PowerEnergyInterface> items = new ArrayList<PowerEnergyInterface>();

					// Iterate through the inventory.
					for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
						// Get the stack in the slot. Skip if its empty.
						ItemStack inventoryStack = player.inventory.getStackInSlot(i);
						if (inventoryStack.isEmpty() || inventoryStack == stack || inventoryStack.getItem() instanceof BatteryPack) {
							continue;
						}

						// Capture the energy interface as needed.
						PowerEnergyInterface energyInterface = PowerEnergyInterface.getFromItemStack(inventoryStack);
						if (energyInterface != null) {
							if (energyInterface.getEnergyStored() < energyInterface.getMaxEnergyStored()) {
								items.add(energyInterface);
							}
						}
					}

					// How much power should we distribute?
					if (items.size() > 0) {
						int perItemDistribute = powerStorage.getStoredPower() / items.size();
						perItemDistribute = SDMath.clamp(perItemDistribute, 1, powerStorage.getCapacity() / 100);

						for (PowerEnergyInterface powerInterface : items) {
							int charged = powerInterface.receivePower(perItemDistribute, true);
							int drained = powerStorage.drainPower(charged, false);
							powerInterface.receivePower(drained, false);

							// Break out if we used all the power.
							if (powerStorage.getStoredPower() <= 0) {
								break;
							}
						}
					}
				}
			});
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new TranslationTextComponent(isActivated(stack) ? "gui.staticpower.active" : "gui.staticpower.inactive"));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new BatteryPackItemModel(existingModel);
	}

	@Override
	public int getCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get() * 3;
	}
}
