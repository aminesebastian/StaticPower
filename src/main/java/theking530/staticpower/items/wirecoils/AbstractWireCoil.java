package theking530.staticpower.items.wirecoils;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.gui.text.TooltipUtilities;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.power.wireconnector.BlockWireConnector;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.items.StaticPowerItem;

public class AbstractWireCoil extends StaticPowerItem {
	protected static final String INITIAL_LOCATOIN_TAG_NAME = "initial_connecting_point";
	protected final SDColor wireColor;
	protected final boolean isInsulated;
	protected final float wireThickness;
	protected final Supplier<CableNetworkModuleType> cableModuleType;
	protected final ResourceLocation tier;

	public AbstractWireCoil(SDColor wireColor, float wireThickness, boolean isInsulated, ResourceLocation tier, Supplier<CableNetworkModuleType> cableModuleType) {
		this.wireColor = wireColor;
		this.isInsulated = isInsulated;
		this.wireThickness = wireThickness;
		this.cableModuleType = cableModuleType;
		this.tier = tier;
	}

	public SDColor getColor() {
		return wireColor;
	}

	public float getWireThickness() {
		return wireThickness;
	}

	public int getMaximumDistance(ItemStack wireStack) {
		if (isInsulated) {
			return StaticPowerConfig.getTier(tier).cablePowerConfiguration.insulatedWireCoilMaxDistance.get();
		} else {
			return StaticPowerConfig.getTier(tier).cablePowerConfiguration.wireCoilMaxDistance.get();
		}
	}

	public boolean canApplyToTerminal(ItemStack coil, AbstractCableProviderComponent component) {
		return component.getSupportedNetworkModuleTypes().contains(cableModuleType.get());
	}

	@Override
	public Component getName(ItemStack item) {
		Component superCall = super.getName(item);
		if (isPendingSecondLocation(item)) {
			BlockPos initialLocation = getFirstLocation(item);
			return Component.translatable(superCall.getString()).append(": ")
					.append(Component.translatable("gui.staticpower.wire_coil_connecting", initialLocation.toShortString()));
		} else {
			return superCall;
		}
	}

	@Override
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!world.isClientSide()) {
			if (player.isCrouching()) {
				clearPendingLocation(item);
				return InteractionResult.SUCCESS;
			} else {
				if (isPendingSecondLocation(item)) {
					tryCompleteConnection(world, player, item, pos);
					return InteractionResult.SUCCESS;
				} else {
					if (trySetFirstSampleLocation(world, player, item, pos)) {
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		return InteractionResult.PASS;
	}

	protected CompoundTag getLinkDataTag(Level world, BlockPos pos, Player player, ItemStack item) {
		CompoundTag output = new CompoundTag();

		ItemStack wire = item.copy();
		wire.setCount(1);

		// Make sure we clear this first before we upload the item to connector data
		// tag.
		clearPendingLocation(wire);

		output.put("wire", wire.serializeNBT());
		return output;
	}

	public boolean trySetFirstSampleLocation(Level world, Player player, ItemStack stack, BlockPos pos) {
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			return false;
		}

		stack.getOrCreateTag().putLong(INITIAL_LOCATOIN_TAG_NAME, pos.asLong());
		MutableComponent message = Component.literal(String.format("First connection position set to %1$s .", pos.toShortString()));
		player.sendSystemMessage(message);
		world.playSound(null, pos, SoundEvents.LEASH_KNOT_PLACE, SoundSource.PLAYERS, 0.75f, 1.0f);
		return true;
	}

	public boolean tryCompleteConnection(Level world, Player player, ItemStack item, BlockPos pos) {
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			return false;
		}

		BlockPos initialLocation = getFirstLocation(item);
		if (!(world.getBlockState(initialLocation).getBlock() instanceof BlockWireConnector)) {
			clearPendingLocation(item);
			MutableComponent message = Component.literal(String.format("Wire connector no longer exists at initial position %1$s .", initialLocation.toShortString()));
			player.sendSystemMessage(message);
			return false;
		}

		// Make sure the connection is not too far.
		double distance = Math.sqrt(initialLocation.distSqr(pos));
		if (distance > getMaximumDistance(item)) {
			MutableComponent message = Component.literal(String.format("This wire cannot complete a connection to a terminal %1$d blocks away!", distance));
			player.sendSystemMessage(message);
			return false;
		}

		// Check to make sure this wire coil will work on this component type.
		if (!canApplyToTerminal(item, component)) {
			MutableComponent message = Component.literal(String.format("This wire is not useable on a terminal of this type!"));
			player.sendSystemMessage(message);
			return false;
		}

		if (component.addSparseConnection(initialLocation, getLinkDataTag(world, pos, player, item)) != null) {
			if (!player.isCreative()) {
				item.shrink(1);
			}

			MutableComponent message = Component
					.literal(String.format("Linked wire connector at location %1$s to position %2$s.", pos.toShortString(), initialLocation.toShortString()));
			player.sendSystemMessage(message);
			clearPendingLocation(item);
			world.playSound(null, pos, SoundEvents.LEASH_KNOT_BREAK, SoundSource.PLAYERS, 0.75f, 1.0f);
			return true;
		}

		return false;
	}

	public BlockPos getFirstLocation(ItemStack stack) {
		return BlockPos.of(stack.getTag().getLong(INITIAL_LOCATOIN_TAG_NAME));
	}

	public boolean isPendingSecondLocation(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(INITIAL_LOCATOIN_TAG_NAME);
	}

	public void clearPendingLocation(ItemStack stack) {
		stack.removeTagKey(INITIAL_LOCATOIN_TAG_NAME);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.power_wire_max_distance", ChatFormatting.LIGHT_PURPLE, String.valueOf(getMaximumDistance(stack)));
	}
}
