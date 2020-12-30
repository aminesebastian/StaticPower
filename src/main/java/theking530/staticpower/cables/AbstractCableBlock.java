package theking530.staticpower.cables;

import java.util.ArrayList;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractCableBlock extends StaticPowerBlock implements ICustomModelSupplier {
	public static final Logger LOGGER = LogManager.getLogger(AbstractCableBlock.class);
	public final CableBoundsCache cableBoundsCache;

	public AbstractCableBlock(String name, CableBoundsCache cableBoundsGenerator) {
		super(name, Block.Properties.create(Material.IRON).hardnessAndResistance(1.5f).notSolid().harvestTool(ToolType.PICKAXE).setRequiresTool());
		cableBoundsCache = cableBoundsGenerator;
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return cableBoundsCache.getShape(state, worldIn, pos, context);
	}

	@Deprecated
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return cableBoundsCache.getShape(state, worldIn, pos, context);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the Attachment Gui.", pos));
			return ActionResultType.FAIL;
		}

		// Get the attachment side that is hovered (if any).
		CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
		if (!hoverResult.isEmpty()) {
			Direction hoveredDirection = cableBoundsCache.getHoveredAttachmentOrCover(pos, player).direction;

			if (hoveredDirection != null && component.hasAttachment(hoveredDirection)) {
				// Get the attachment on the hovered side.
				ItemStack attachment = component.getAttachment(hoveredDirection);

				// Get the attachment's item.
				AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();

				// If the item requests a GUI, open it.
				if (attachmentItem.hasGui(attachment)) {
					if (!world.isRemote) {
						NetworkGUI.openGui((ServerPlayerEntity) player, attachmentItem.getContainerProvider(attachment, component, hoveredDirection), buff -> {
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
	public ActionResultType sneakWrenchBlock(PlayerEntity player, SneakWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		// Drop the block.
		if (returnDrops && !world.isRemote) {
			BlockState state = world.getBlockState(pos);
			List<ItemStack> stacks = Block.getDrops(state, (ServerWorld) world, pos, world.getTileEntity(pos));
			for (ItemStack stack : stacks) {
				WorldUtilities.dropItem(world, pos, stack);
			}
			this.spawnAdditionalDrops(state, (ServerWorld) world, pos, wrench);
		}

		// Perform this on both the client and the server so the client updates any
		// render changes (any conected cables).
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
		world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), 1 | 2, 512);

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
			CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);

			// If non null, check for any attached cover or attachment.
			if (!hoverResult.isEmpty()) {
				Direction hoveredDirection = cableBoundsCache.getHoveredAttachmentOrCover(pos, player).direction;

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
		CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);

		if (!hoverResult.isEmpty()) {
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
			if (hoverResult.type == CableBoundsHoverType.ATTACHED_ATTACHMENT) {
				if (component.hasAttachment(hoverResult.direction)) {
					ItemStack attachment = component.getAttachment(hoverResult.direction);

					if (!attachment.isEmpty()) {
						// Make a clean version of attachment with clear inventory.
						ItemStack cleanAttachment = new ItemStack(attachment.getItem());
						return cleanAttachment.copy();
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

	@Deprecated
	@Override
	public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack) {
		// Get the cable provider if present.
		ComponentUtilities.getComponent(AbstractCableProviderComponent.class, worldIn.getTileEntity(pos)).ifPresent(component -> {
			// Allocate a container for the additional drops.
			List<ItemStack> additionalDrops = new ArrayList<ItemStack>();

			// Check the attachments on all sides, and if there is one, get the additional
			// drops.
			for (Direction dir : Direction.values()) {
				if (component.hasAttachment(dir)) {
					ItemStack attachment = component.getAttachment(dir);
					AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
					attachmentItem.getAdditionalDrops(attachment, component, additionalDrops);
				}
			}

			// Drop the additional drops.
			for (ItemStack drop : additionalDrops) {
				WorldUtilities.dropItem(worldIn, pos, drop);
			}
		});
	}

	protected boolean isDisabledOnSide(IWorld world, BlockPos pos, Direction direction) {
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(direction);
		}
		return true;
	}
}
