package theking530.staticpower.cables;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.common.wrench.RegularWrenchMode;
import theking530.common.wrench.SneakWrenchMode;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractCableBlock extends StaticPowerBlock implements ICustomModelSupplier {
	public static final Logger LOGGER = LogManager.getLogger(AbstractCableBlock.class);
	public final CableBoundsCache CableBounds;

	public AbstractCableBlock(String name, CableBoundsCache cableBoundsGenerator) {
		super(name, Block.Properties.create(Material.IRON).notSolid());
		CableBounds = cableBoundsGenerator;
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return CableBounds.getShape(state, worldIn, pos, context);
	}

	@Deprecated
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return CableBounds.getShape(state, worldIn, pos, context);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Deprecated
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		// Call the super.
		List<ItemStack> superDrops = super.getDrops(state, builder);

		// Drop the covers and attachments.
		BlockPos pos = builder.get(LootParameters.POSITION);
		if (pos != null) {
			AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(builder.getWorld(), builder.get(LootParameters.POSITION));
			for (Direction dir : Direction.values()) {
				if (cable.hasAttachment(dir)) {
					superDrops.add(cable.removeAttachment(dir));
				}
				if (cable.hasCover(dir)) {
					superDrops.add(cable.removeCover(dir));
				}
			}
		}

		return superDrops;
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			ServerCable cable = CableNetworkManager.get(world).getCable(pos);
			player.sendMessage(new StringTextComponent("NetworkID: " + cable.getNetwork().getId() + "  " + " with: " + cable.getNetwork().getGraph().getCables().size() + " cables."));
		}

		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the Attachment Gui.", pos));
			return ActionResultType.FAIL;
		}

		// Get the attachment side that is hovered (if any).
		CableBoundsHoverResult hoverResult = CableBounds.getHoveredAttachmentOrCover(pos, player);

		if (!hoverResult.isEmpty() && hoverResult.type == CableBoundsHoverType.ATTACHED_ATTACHMENT) {
			Direction hoveredDirection = CableBounds.getHoveredAttachmentOrCover(pos, player).direction;

			if (hoveredDirection != null && component.hasAttachment(hoveredDirection)) {
				// Get the attachment on the hovered side.
				ItemStack attachment = component.getAttachment(hoveredDirection);

				// Get the attachment's item.
				AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();

				// If the item requests a GUI, open it.
				if (attachmentItem.hasGui(attachment)) {
					if (!world.isRemote) {
						NetworkHooks.openGui((ServerPlayerEntity) player, attachmentItem.getContainerProvider(attachment, component, hoveredDirection), buff -> {
							buff.writeInt(hoveredDirection.ordinal());
							buff.writeBlockPos(pos);
						});
					}
					return ActionResultType.CONSUME;
				}
			}
		}
		// IF we didn't return earlier, continue the execution.
		return ActionResultType.PASS;
	}

	@Override
	public void onStaticPowerNeighborChanged(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onStaticPowerNeighborChanged(state, world, pos, neighbor);
		if (!world.isRemote()) {
			ServerCable cable = CableNetworkManager.get((ServerWorld) world).getCable(pos);
			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerWorld) world, pos);
			}
		}
	}

	@Override
	public ActionResultType sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		// Perform this on both the client and the server so the client updates any
		// render changes (any conected cables).
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
		return ActionResultType.SUCCESS;
	}

	@Override
	public ActionResultType wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		// Only perform on the server.
		if (!world.isRemote) {
			// Get the cable component and make sure its valid.
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
			if (component == null) {
				return ActionResultType.FAIL;
			}

			// Check for the hover result.
			CableBoundsHoverResult hoverResult = CableBounds.getHoveredAttachmentOrCover(pos, player);

			// If non null, check for any attached cover or attachment.
			if (!hoverResult.isEmpty()) {
				Direction hoveredDirection = CableBounds.getHoveredAttachmentOrCover(pos, player).direction;

				// Remove the attachment on that side if there is one.
				if (hoverResult.type == CableBoundsHoverType.ATTACHED_ATTACHMENT) {
					ItemStack output = component.removeAttachment(hoveredDirection);
					if (!output.isEmpty()) {
						WorldUtilities.dropItem(world, pos, output, 1);
						return ActionResultType.SUCCESS;
					}
				} else if (hoverResult.type == CableBoundsHoverType.ATTACHED_COVER) {
					// Now also remove the cover if there is one.
					ItemStack output = component.removeCover(hoveredDirection);
					if (!output.isEmpty()) {
						WorldUtilities.dropItem(world, pos, output, 1);
						return ActionResultType.SUCCESS;
					}
				}
			}

			// If we didnt return earlier, we probably hit the cable itseelf, lets see if we
			// can disable or enabled part of it.
			Direction hitSide = !hoverResult.isEmpty() ? hoverResult.direction : facing;
			component.setSideDisabledState(hitSide, !component.isSideDisabled(hitSide));

			// Update the cable opposite from the side we just toggled if a cable exists
			// there.
			AbstractCableProviderComponent oppositeComponent = CableUtilities.getCableWrapperComponent(world, pos.offset(hitSide));
			if (oppositeComponent != null) {
				oppositeComponent.setSideDisabledState(hitSide.getOpposite(), component.isSideDisabled(hitSide));
			}

			// Refresh the cable.
			CableNetworkManager.get(world).refreshCable(CableNetworkManager.get(world).getCable(pos));

			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		// Get the attachment side we're hovering.
		CableBoundsHoverResult hoverResult = CableBounds.getHoveredAttachmentOrCover(pos, player);

		if (!hoverResult.isEmpty()) {
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
			if (hoverResult.type == CableBoundsHoverType.ATTACHED_ATTACHMENT) {
				if (component.hasAttachment(hoverResult.direction)) {
					ItemStack attachment = component.getAttachment(hoverResult.direction);
					if (!attachment.isEmpty()) {
						return attachment.copy();
					}
				}
			} else if (hoverResult.type == CableBoundsHoverType.ATTACHED_COVER) {
				if (component.hasCover(hoverResult.direction)) {
					ItemStack cover = component.getCover(hoverResult.direction);
					if (!cover.isEmpty()) {
						return cover.copy();
					}
				}
			}
		}

		return super.getPickBlock(state, target, world, pos, player);
	}

	protected boolean isDisabledOnSide(IWorld world, BlockPos pos, Direction direction) {
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(direction);
		}
		return true;
	}
}
