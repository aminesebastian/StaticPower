package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;

public class Magnet extends StaticPowerEnergyStoringItem {
	private static final String ACTIVATED_TAG = "activated";
	private final ResourceLocation tier;

	public Magnet(ResourceLocation tier) {
		super(new Item.Properties().tab(ModCreativeTabs.TOOLS));
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

	protected void pullItems(ItemStack stack, Level world, Player player) {
		// Do nothing on the client.
		if (world.isClientSide) {
			return;
		}

		int radius = getRadius(stack);

		// Create the AABB to search within.
		AABB aabb = new AABB(player.getX() - radius, player.getY() - radius, player.getZ() - radius, player.getX() + radius, player.getY() + radius, player.getZ() + radius);

		// Search for all the item entities.
		List<ItemEntity> droppedItems = world.getEntitiesOfClass(ItemEntity.class, aabb, (ItemEntity item) -> true);

		// Iterate and try to pull each one.
		for (ItemEntity entity : droppedItems) {
			double x = (player.getX() + 0.5D - entity.getX());
			double y = (player.getY() + 0.5D - entity.getY());
			double z = (player.getZ() + 0.5D - entity.getZ());

			// Get the distance away.
			double distance = Math.sqrt(x * x + y * y + z * z);

			// Pull any item entities that are outside our capture radius closer to us.
			if (distance > 1.1f) {
				double var11 = 1.0 - distance / 15.0;
				if (var11 > 0.0D) {
					var11 *= var11;
					entity.push(x / distance * var11 * 0.06, y / distance * var11 * 0.15, z / distance * var11 * 0.06);
					Vec3 entityPos = entity.position();
					world.addParticle(ParticleTypes.PORTAL, entityPos.x, entityPos.y - 0.5, entityPos.z, 0.0D, 0.0D, 0.0D);
				}
			}
		}

		// Use power as needed.
		if (droppedItems.size() > 0) {
			EnergyHandlerItemStackUtilities.drainPower(stack, 1, false);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(Component.translatable(isActivated(stack) ? "gui.staticcore.active" : "gui.staticcore.inactive")
				.withStyle(isActivated(stack) ? ChatFormatting.GREEN : ChatFormatting.RED));
		tooltip.add(
				Component.literal("� ").append(Component.translatable("gui.staticcore.radius")).append(" " + ChatFormatting.GREEN.toString() + String.valueOf(getRadius(stack))));

		tooltip.add(Component.literal(""));

		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	public int getRadius(ItemStack stack) {
		return StaticCoreConfig.getTier(tier).toolConfiguration.magnetRadius.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
		Player player = (Player) entityIn;
		if (player == null) {
			return;
		}

		// Toggle the state if the toggle magnet button was just pressed.
		if (!worldIn.isClientSide() && ModKeyBindings.TOGGLE_MAGNET.wasJustPressed()) {
			worldIn.playSound(null, entityIn.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.2f, !isActivated(stack) ? 1.0f : 0.5f);
			toggleActivated(stack);
		}

		// Check the power and toggle back off if we don't have any.
		if (isActivated(stack)) {
			if (EnergyHandlerItemStackUtilities.getStoredPower(stack) <= 0) {
				toggleActivated(stack);
			}
		}

		// Pull Items.
		if (entityIn instanceof Player && isActivated(stack)) {
			pullItems(stack, worldIn, (Player) entityIn);
		}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isActivated(stack);
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
	public double getCapacity() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryCapacity.get() * 2;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	@Override
	public double getMaximumOutputPower() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}
}
