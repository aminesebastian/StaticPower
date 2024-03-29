package theking530.staticpower.cables.attachments.drain;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.utilities.StaticPowerRarities;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.cables.fluid.FluidCableComponent;
import theking530.staticpower.cables.fluid.FluidNetworkModule;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.cables.ModCableModules;

public class DrainAttachment extends AbstractCableAttachment {
	public static final String DRAINER_TIMER_TAG = "drain_timer";
	private static final Vector3D DRAIN_BOUNDS = new Vector3D(2.5f, 2.5f, 5f);
	private final ResourceLocation model;
	private final ResourceLocation tierType;

	public DrainAttachment(ResourceLocation tierType, ResourceLocation model) {
		super(ModCreativeTabs.CABLES);
		this.model = model;
		this.tierType = tierType;
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		super.onAddedToCable(attachment, side, cable);
		getAttachmentTag(attachment).putInt("redstone_mode", RedstoneMode.Low.ordinal());
		getAttachmentTag(attachment).putInt(DRAINER_TIMER_TAG, 0);
	}

	@Override
	public Vector3D getBounds() {
		return DRAIN_BOUNDS;
	}

	@SuppressWarnings("resource")
	@Override
	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Check redstone signal.
		if (cable.getLevel().isClientSide || !cable.doesAttachmentPassRedstoneTest(attachment)) {
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
		if (!getAttachmentTag(attachment).contains(DRAINER_TIMER_TAG)) {
			getAttachmentTag(attachment).putInt(DRAINER_TIMER_TAG, 0);
		}

		// Get the current timer and the extraction rate.
		int currentTimer = getAttachmentTag(attachment).getInt(DRAINER_TIMER_TAG);

		// Increment the current timer.
		currentTimer += 1;
		if (currentTimer >= StaticCoreConfig.getTier(tierType).cableAttachmentConfiguration.cableExtractorRate.get()) {
			getAttachmentTag(attachment).putInt(DRAINER_TIMER_TAG, 0);
			return true;
		} else {
			getAttachmentTag(attachment).putInt(DRAINER_TIMER_TAG, currentTimer);
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

		FluidNetworkModule module = fluidCable.<FluidNetworkModule>getNetworkModule(ModCableModules.Fluid.get()).orElse(null);
		if (module == null) {
			return false;
		}

		FluidStack drained = module.supply(cable.getPos(), 1000, FluidAction.SIMULATE);
		if (drained.getAmount() < 1000) {
			return false;
		}

		if (!WorldUtilities.tryPlaceFluid(fluid, null, cable.getLevel(), cable.getPos().relative(side), null)) {
			return false;
		}

		module.supply(cable.getPos(), 1000, FluidAction.EXECUTE);
		return false;
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		return model;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return StaticPowerRarities.getRarityForTier(this.tierType);
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(Component.translatable("gui.staticpower.drain_tooltip").withStyle(ChatFormatting.DARK_AQUA));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}
}
