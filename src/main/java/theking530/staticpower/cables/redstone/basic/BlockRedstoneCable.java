package theking530.staticpower.cables.redstone.basic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.network.NetworkHooks;
import theking530.staticcore.cablenetwork.CableBoundsHoverResult;
import theking530.staticcore.cablenetwork.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.data.CableSideConnectionState.CableConnectionType;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.redstone.basic.gui.ContainerBasicRedstoneIO;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;

public class BlockRedstoneCable extends AbstractCableBlock {
	private static boolean canProvidePower;
	private final String color;

	public BlockRedstoneCable(String color) {
		super(new BasicRedstoneCableBoundsCache(color.contains("naked") ? 0.75D : 1.25D, new Vector3D(2.0f, 2.0f, 2.0f), new Vector3D(2.0f, 2.0f, 2.0f)),
				color.contains("naked") ? 1.0f : 1.5f);

		// String the color from the last section of the registry name.
		this.color = color;
		canProvidePower = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		if (color.equals("naked")) {
			ResourceLocation straightModel = StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_STRAIGHT;
			ResourceLocation extensionModel = StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_EXTENSION;
			ResourceLocation attachmentModel = StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT;
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		} else {
			ResourceLocation straightModel = StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[0];
			ResourceLocation extensionModel = StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[1];
			ResourceLocation attachmentModel = StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT;
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		}
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return canProvidePower;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		// If not null, and if we are hovering the default attachment, check to see if
		// we're connected to a tile entity on that side.
		// If so, then we have a UI, otherwise we do not.
		if (component != null) {
			CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
			if (hoverResult.type == CableBoundsHoverType.DEFAULT_ATTACHMENT) {
				Direction cableSide = hoverResult.direction;
				return component.getConnectionTypeOnSide(cableSide) == CableConnectionType.DESTINATION ? HasGuiType.ALWAYS : HasGuiType.NEVER;
			}
		} else {
			StaticPower.LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the redstone cable gui.", pos));
		}
		return HasGuiType.NEVER;
	}

	/**
	 * Server side only. This method givens the inheritor the opportunity to enter a
	 * GUI.By default, the standard GUI opening flow is followed calling openGui on
	 * {@link NetworkHooks}.
	 * 
	 * @param tileEntity
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hand
	 * @param hit
	 * @return
	 */
	@Override
	public void enterGuiScreen(BlockEntityBase tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
			RedstoneCableContainerProvider provider = new RedstoneCableContainerProvider(this, (BlockEntityRedstoneCable) tileEntity, hit.getDirection());
			NetworkGUI.openScreen((ServerPlayer) player, provider, buf -> {
				buf.writeBlockPos(pos);
				buf.writeInt(hoverResult.direction.ordinal());
			});
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (color) {
		case "black":
			return BlockEntityRedstoneCable.TYPE_BASIC_BLACK.create(pos, state);
		case "dark_blue":
			return BlockEntityRedstoneCable.TYPE_BASIC_DARK_BLUE.create(pos, state);
		case "dark_green":
			return BlockEntityRedstoneCable.TYPE_BASIC_DARK_GREEN.create(pos, state);
		case "dark_aqua":
			return BlockEntityRedstoneCable.TYPE_BASIC_DARK_AQUA.create(pos, state);
		case "dark_red":
			return BlockEntityRedstoneCable.TYPE_BASIC_DARK_RED.create(pos, state);
		case "dark_purple":
			return BlockEntityRedstoneCable.TYPE_BASIC_DARK_PURPLE.create(pos, state);
		case "gold":
			return BlockEntityRedstoneCable.TYPE_BASIC_GOLD.create(pos, state);
		case "gray":
			return BlockEntityRedstoneCable.TYPE_BASIC_GRAY.create(pos, state);
		case "dark_gray":
			return BlockEntityRedstoneCable.TYPE_BASIC_DARK_GRAY.create(pos, state);
		case "blue":
			return BlockEntityRedstoneCable.TYPE_BASIC_BLUE.create(pos, state);
		case "green":
			return BlockEntityRedstoneCable.TYPE_BASIC_GREEN.create(pos, state);
		case "aqua":
			return BlockEntityRedstoneCable.TYPE_BASIC_AQUA.create(pos, state);
		case "red":
			return BlockEntityRedstoneCable.TYPE_BASIC_RED.create(pos, state);
		case "light_purple":
			return BlockEntityRedstoneCable.TYPE_BASIC_LIGHT_PURPLE.create(pos, state);
		case "yellow":
			return BlockEntityRedstoneCable.TYPE_BASIC_YELLOW.create(pos, state);
		case "white":
			return BlockEntityRedstoneCable.TYPE_BASIC_WHITE.create(pos, state);
		default:
			return BlockEntityRedstoneCable.TYPE_BASIC_NAKED.create(pos, state);
		}
	}

	public class RedstoneCableContainerProvider implements MenuProvider {
		public final Direction side;
		public final Block owningBlock;
		public final BlockEntityRedstoneCable cable;

		public RedstoneCableContainerProvider(Block owningBlock, BlockEntityRedstoneCable cable, Direction side) {
			this.owningBlock = owningBlock;
			this.side = side;
			this.cable = cable;
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
			return new ContainerBasicRedstoneIO(windowId, inventory, cable, side);
		}

		@Override
		public Component getDisplayName() {
			return Component.translatable(owningBlock.getDescriptionId());
		}

	}
}
