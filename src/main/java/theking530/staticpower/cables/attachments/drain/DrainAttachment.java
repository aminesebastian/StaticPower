package theking530.staticpower.cables.attachments.drain;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.fluid.FluidCableComponent;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;
import theking530.staticpower.utilities.WorldUtilities;

public class DrainAttachment extends AbstractCableAttachment {
	public static final String DRAINER_TIMER_TAG = "drain_timer";
	private static final Vector3D DRAIN_BOUNDS = new Vector3D(2.5f, 2.5f, 5f);
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public DrainAttachment(String name, ResourceLocation tierType, ResourceLocation model) {
		super(name);
		this.model = model;
		this.tierType = tierType;
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
		attachment.getTag().putInt("redstone_mode", RedstoneMode.Low.ordinal());
		attachment.getTag().putInt(DRAINER_TIMER_TAG, 0);
	}

	public void onRemovedFromCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		attachment.getTag().remove(DRAINER_TIMER_TAG);
	}

	@Override
	public Vector3D getBounds() {
		return DRAIN_BOUNDS;
	}

	@SuppressWarnings("resource")
	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Check redstone signal.
		if (cable.getWorld().isClientSide || !cable.doesAttachmentPassRedstoneTest(attachment)) {
			return;
		}

		if (incrementDrainTimer(attachment)) {
			attemptPlaceFluid(attachment, side, cable);
		}
	}

	protected boolean incrementDrainTimer(ItemStack attachment) {

		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundTag());
		}
		if (!attachment.getTag().contains(DRAINER_TIMER_TAG)) {
			attachment.getTag().putInt(DRAINER_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = attachment.getTag().getInt(DRAINER_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticPowerConfig.getTier(tierType).cableExtractorRate.get()) {
			attachment.getTag().putInt(DRAINER_TIMER_TAG, 0);
			return true;
		} else {
			attachment.getTag().putInt(DRAINER_TIMER_TAG, currentTimer);
			return false;
		}
	}

	protected boolean attemptPlaceFluid(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Get the fluid cable and the fluid in the cable. Return early if there is no
		// fluid.
		FluidCableComponent fluidCable = (FluidCableComponent) cable;
		FluidStack fluid = fluidCable.getFluidInTank(0);
		if (fluid.isEmpty()) {
			return false;
		}
		return WorldUtilities.tryPlaceFluid(fluid, null, cable.getWorld(), cable.getPos().relative(side), null);
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslatableComponent("gui.staticpower.drain_tooltip").withStyle(ChatFormatting.DARK_AQUA));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}
}
