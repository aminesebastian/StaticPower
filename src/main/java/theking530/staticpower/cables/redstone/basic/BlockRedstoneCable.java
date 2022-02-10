package theking530.staticpower.cables.redstone.basic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.network.NetworkHooks;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.redstone.basic.gui.ContainerBasicRedstoneIO;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.TileEntityBase;

public class BlockRedstoneCable extends AbstractCableBlock {
	private static boolean canProvidePower;
	private final String color;

	public BlockRedstoneCable(String name) {
		super(name, new BasicRedstoneCableBoundsCache(name.contains("naked") ? 0.75D : 1.25D, new Vector3D(2.0f, 2.0f, 2.0f), new Vector3D(2.0f, 2.0f, 2.0f)),
				name.contains("naked") ? 1.0f : 1.5f);

		// String the color from the last section of the registry name.
		this.color = name.substring(name.indexOf("_", name.indexOf("_", name.indexOf("_") + 1) + 1) + 1);
		canProvidePower = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelBakeEvent event) {
		if (color.equals("naked")) {
			BakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_STRAIGHT);
			BakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_EXTENSION);
			BakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT);
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		} else {
			BakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[0]);
			BakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[1]);
			BakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT);
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
//TO-DO:		if (component != null) {
//			if (hit.hitInfo instanceof CableBoundsHoverResult) {
//				CableBoundsHoverResult hoverResult = (CableBoundsHoverResult) hit.hitInfo;
//				if (hoverResult.type == CableBoundsHoverType.DEFAULT_ATTACHMENT) {
//					Direction cableSide = hoverResult.direction;
//					return component.getConnectionState(cableSide) == CableConnectionState.TILE_ENTITY ? HasGuiType.ALWAYS : HasGuiType.NEVER;
//				}
//			}
//		} else {
//			LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the redstone cable gui.", pos));
//		}
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
	public void enterGuiScreen(TileEntityBase tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//TO-DO:		if (!world.isClientSide) {
//			if (hit.hitInfo instanceof CableBoundsHoverResult) {
//				CableBoundsHoverResult hoverResult = (CableBoundsHoverResult) hit.hitInfo;
//				RedstoneCableContainerProvider provider = new RedstoneCableContainerProvider(this, (TileEntityRedstoneCable) tileEntity, hit.getDirection());
//				NetworkGUI.openGui((ServerPlayer) player, provider, buf -> {
//					buf.writeBlockPos(pos);
//					buf.writeInt(hoverResult.direction.ordinal());
//				});
//			}
//		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (color) {
		case "black":
			return TileEntityRedstoneCable.TYPE_BASIC_BLACK.create(pos, state);
		case "dark_blue":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_BLUE.create(pos, state);
		case "dark_green":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_GREEN.create(pos, state);
		case "dark_aqua":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_AQUA.create(pos, state);
		case "dark_red":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_RED.create(pos, state);
		case "dark_purple":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_PURPLE.create(pos, state);
		case "gold":
			return TileEntityRedstoneCable.TYPE_BASIC_GOLD.create(pos, state);
		case "gray":
			return TileEntityRedstoneCable.TYPE_BASIC_GRAY.create(pos, state);
		case "dark_gray":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_GRAY.create(pos, state);
		case "blue":
			return TileEntityRedstoneCable.TYPE_BASIC_BLUE.create(pos, state);
		case "green":
			return TileEntityRedstoneCable.TYPE_BASIC_GREEN.create(pos, state);
		case "aqua":
			return TileEntityRedstoneCable.TYPE_BASIC_AQUA.create(pos, state);
		case "red":
			return TileEntityRedstoneCable.TYPE_BASIC_RED.create(pos, state);
		case "light_purple":
			return TileEntityRedstoneCable.TYPE_BASIC_LIGHT_PURPLE.create(pos, state);
		case "yellow":
			return TileEntityRedstoneCable.TYPE_BASIC_YELLOW.create(pos, state);
		case "white":
			return TileEntityRedstoneCable.TYPE_BASIC_WHITE.create(pos, state);
		default:
			return TileEntityRedstoneCable.TYPE_BASIC_NAKED.create(pos, state);
		}
	}

	public class RedstoneCableContainerProvider implements MenuProvider {
		public final Direction side;
		public final Block owningBlock;
		public final TileEntityRedstoneCable cable;

		public RedstoneCableContainerProvider(Block owningBlock, TileEntityRedstoneCable cable, Direction side) {
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
			return new TranslatableComponent(owningBlock.getDescriptionId());
		}

	}
}
