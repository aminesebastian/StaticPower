package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;

public class Magnet extends StaticPowerEnergyStoringItem {
	private static final String ACTIVATED_TAG = "activated";
	private final ResourceLocation tier;

	public Magnet(String name, ResourceLocation tier) {
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

	protected void pullItems(ItemStack stack, World world, PlayerEntity player) {
		// Do nothing on the client.
		if (world.isRemote) {
			return;
		}

		int radius = getRadius(stack);

		// Create the AABB to search within.
		AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - radius, player.getPosY() - radius, player.getPosZ() - radius, player.getPosX() + radius, player.getPosY() + radius,
				player.getPosZ() + radius);

		// Search for all the item entities.
		List<ItemEntity> droppedItems = world.getEntitiesWithinAABB(ItemEntity.class, aabb, (ItemEntity item) -> true);

		// Iterate and try to pull each one.
		for (ItemEntity entity : droppedItems) {
			double x = (player.getPosX() + 0.5D - entity.getPosX());
			double y = (player.getPosY() + 0.5D - entity.getPosY());
			double z = (player.getPosZ() + 0.5D - entity.getPosZ());

			// Get the distance away.
			double distance = Math.sqrt(x * x + y * y + z * z);

			// If the entity is right next to us, suck it in. Otherwise, pull it towards us.
			if (distance < 1.1f) {
				if (player.inventory.addItemStackToInventory(entity.getItem())) {
					if (entity.getItem().isEmpty()) {
						entity.remove();
						world.addParticle(ParticleTypes.PORTAL, player.getPosX() + 0.5, player.getPosY() + 0.5, player.getPosZ() + 0.5, 0.0D, 0.0D, 0.0D);
						world.playSound(null, player.getPosition(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.5F);
					}
				}
			} else {
				double var11 = 1.0 - distance / 15.0;
				if (var11 > 0.0D) {
					var11 *= var11;
					entity.addVelocity(x / distance * var11 * 0.06, y / distance * var11 * 0.15, z / distance * var11 * 0.06);
					Vector3d entityPos = entity.getPositionVec();
					world.addParticle(ParticleTypes.PORTAL, entityPos.x, entityPos.y - 0.5, entityPos.z, 0.0D, 0.0D, 0.0D);
				}
			}
		}

		// Use power as needed.
		if (droppedItems.size() > 0) {
			stack.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
				powerStorage.drainPower(1, false);
			});
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new TranslationTextComponent(isActivated(stack) ? "gui.staticpower.active" : "gui.staticpower.inactive")
				.mergeStyle(isActivated(stack) ? TextFormatting.GREEN : TextFormatting.RED));
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.radius"))
				.appendString(" " + TextFormatting.GREEN.toString() + String.valueOf(getRadius(stack))));

		tooltip.add(new StringTextComponent(""));

		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	public int getRadius(ItemStack stack) {
		return StaticPowerConfig.getTier(tier).magnetRadius.get();
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		// Check the power.
		if (isActivated(stack)) {
			stack.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
				if (powerStorage.getStoredPower() <= 0) {
					toggleActivated(stack);
				}
			});
		}

		// Pull Items.
		if (entityIn instanceof PlayerEntity && isActivated(stack)) {
			pullItems(stack, worldIn, (PlayerEntity) entityIn);
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return isActivated(stack);
	}

	@Override
	public long getCapacity() {
		return StaticPowerConfig.getTier(tier).magnetPowerCapacity.get();
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
}
