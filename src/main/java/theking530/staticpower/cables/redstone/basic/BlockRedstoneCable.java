package theking530.staticpower.cables.redstone.basic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableBoundsHoverResult;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.cables.redstone.basic.gui.ContainerBasicRedstoneIO;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;
import theking530.staticpower.tileentities.TileEntityBase;

public class BlockRedstoneCable extends AbstractCableBlock {
	private static boolean canProvidePower;
	private final String color;

	public BlockRedstoneCable(String name) {
		super(name, new BasicRedstoneCableBoundsCache(name.contains("naked") ? 0.75D : 1.25D, new Vector3D(2.0f, 2.0f, 2.0f), new Vector3D(2.0f, 2.0f, 2.0f)));

		// String the color from the last section of the registry name.
		this.color = name.substring(name.indexOf("_", name.indexOf("_", name.indexOf("_") + 1) + 1) + 1);
		canProvidePower = true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		if (color.equals("naked")) {
			IBakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_STRAIGHT);
			IBakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_EXTENSION);
			IBakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT);
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		} else {
			IBakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[0]);
			IBakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[1]);
			IBakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT);
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		}
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return canProvidePower;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		// If not null, and if we are hovering the default attachment, check to see if
		// we're connected to a tile entity on that side.
		// If so, then we have a UI, otherwise we do not.
		if (component != null) {
			if (hit.hitInfo instanceof CableBoundsHoverResult) {
				CableBoundsHoverResult hoverResult = (CableBoundsHoverResult) hit.hitInfo;
				if (hoverResult.type == CableBoundsHoverType.DEFAULT_ATTACHMENT) {
					Direction cableSide = hoverResult.direction;
					return component.getConnectionState(cableSide) == CableConnectionState.TILE_ENTITY ? HasGuiType.ALWAYS : HasGuiType.NEVER;
				}
			}
		} else {
			LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the redstone cable gui.", pos));
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
	public void enterGuiScreen(TileEntityBase tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			if (hit.hitInfo instanceof CableBoundsHoverResult) {
				CableBoundsHoverResult hoverResult = (CableBoundsHoverResult) hit.hitInfo;
				RedstoneCableContainerProvider provider = new RedstoneCableContainerProvider(this, (TileEntityRedstoneCable) tileEntity, hit.getFace());
				NetworkGUI.openGui((ServerPlayerEntity) player, provider, buf -> {
					buf.writeBlockPos(pos);
					buf.writeInt(hoverResult.direction.ordinal());
				});
			}
		}
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		switch (color) {
		case "black":
			return TileEntityRedstoneCable.TYPE_BASIC_BLACK.create();
		case "dark_blue":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_BLUE.create();
		case "dark_green":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_GREEN.create();
		case "dark_aqua":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_AQUA.create();
		case "dark_red":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_RED.create();
		case "dark_purple":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_PURPLE.create();
		case "gold":
			return TileEntityRedstoneCable.TYPE_BASIC_GOLD.create();
		case "gray":
			return TileEntityRedstoneCable.TYPE_BASIC_GRAY.create();
		case "dark_gray":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_GRAY.create();
		case "blue":
			return TileEntityRedstoneCable.TYPE_BASIC_BLUE.create();
		case "green":
			return TileEntityRedstoneCable.TYPE_BASIC_GREEN.create();
		case "aqua":
			return TileEntityRedstoneCable.TYPE_BASIC_AQUA.create();
		case "red":
			return TileEntityRedstoneCable.TYPE_BASIC_RED.create();
		case "light_purple":
			return TileEntityRedstoneCable.TYPE_BASIC_LIGHT_PURPLE.create();
		case "yellow":
			return TileEntityRedstoneCable.TYPE_BASIC_YELLOW.create();
		case "white":
			return TileEntityRedstoneCable.TYPE_BASIC_WHITE.create();
		default:
			return TileEntityRedstoneCable.TYPE_BASIC_NAKED.create();
		}
	}

	public class RedstoneCableContainerProvider implements INamedContainerProvider {
		public final Direction side;
		public final Block owningBlock;
		public final TileEntityRedstoneCable cable;

		public RedstoneCableContainerProvider(Block owningBlock, TileEntityRedstoneCable cable, Direction side) {
			this.owningBlock = owningBlock;
			this.side = side;
			this.cable = cable;
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
			return new ContainerBasicRedstoneIO(windowId, inventory, cable, side);
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent(owningBlock.getTranslationKey());
		}

	}
}
