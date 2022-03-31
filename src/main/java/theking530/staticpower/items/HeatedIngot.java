package theking530.staticpower.items;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.HeatedItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class HeatedIngot extends StaticPowerItem implements ICustomModelSupplier {
	public static final int HEAT_LASTING_TIME = 200;
	public static final String HEATED_TAG = "heated_time";
	private final Supplier<Item> cooledVariantSupplier;

	public HeatedIngot(String name, Supplier<Item> cooledVariantSupplier) {
		super(name);
		this.cooledVariantSupplier = cooledVariantSupplier;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		// Add the heated time onto the stack.
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains(HEATED_TAG)) {
			if (nbt != null && nbt.contains(HEATED_TAG)) {
				tag.putInt(HEATED_TAG, nbt.getInt(HEATED_TAG));
			} else {
				tag.putInt(HEATED_TAG, HEAT_LASTING_TIME);
			}
		}
		return super.initCapabilities(stack, nbt);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, level, entity, slot, selected);

		// Only cool down if in a player's inventory (just for fun/balance reasons).
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (stack.hasTag()) {
				// Get the current remaining heat time.
				int remaining = stack.getTag().getInt(HEATED_TAG);

				// If it's ready to cool, perform the cooling.
				if (remaining <= 1) {
					ItemStack cooledStack = new ItemStack(cooledVariantSupplier.get(), stack.getCount());
					player.getInventory().setItem(slot, cooledStack);
					level.playSound(player, player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 1.0f);
				} else {
					stack.getTag().putInt(HEATED_TAG, remaining - 1);
				}
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarColor(ItemStack stack) {
		double hue = (0.0f / 360.0f) + (getRemainingHeatPercent(stack)) * (40.0f / 360.0f);
		return Mth.hsvToRgb((float) hue, 1.0F, 1.0F);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return (int) (getRemainingHeatPercent(stack) * 13);
	}

	public float getRemainingHeatPercent(ItemStack stack) {
		return getRemainingHeat(stack) / (float) HEAT_LASTING_TIME;
	}

	public int getRemainingHeat(ItemStack stack) {
		return stack.getTag() == null ? 0 : stack.getTag().getInt(HEATED_TAG);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TranslatableComponent("gui.staticpower.remaining_heat_time").append(" ").append(GuiTextUtilities.formatNumberAsStringOneDecimal(getRemainingHeat(stack) / 20.0f).withStyle(ChatFormatting.GOLD))
				.append(new TranslatableComponent("gui.staticpower.seconds.short").withStyle(ChatFormatting.GOLD)));
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new HeatedItemModel(existingModel);
	}
}
