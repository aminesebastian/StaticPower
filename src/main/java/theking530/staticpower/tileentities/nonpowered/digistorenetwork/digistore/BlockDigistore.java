package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistore extends BaseDigistoreBlock {

	public BlockDigistore(String name) {
		super(name);
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity != null && tileEntity instanceof TileEntityDigistore) {
			TileEntityDigistore digistore = (TileEntityDigistore) worldIn.getTileEntity(pos);
			if (player.isSneaking()) {
				if (!worldIn.isRemote) {
					NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityDigistore) digistore, pos);
					return ActionResultType.CONSUME;
				}
			} else {
				digistore.onBarrelRightClicked(player, handIn, hit);
				return ActionResultType.PASS;
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn) {
		TileEntityDigistore entity = (TileEntityDigistore) worldIn.getTileEntity(pos);
		if (entity != null) {
			entity.onBarrelLeftClicked(playerIn);
		}
	}

	@Override
	public void onStaticPowerBlockHarvested(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
		super.onStaticPowerBlockHarvested(world, player, pos, state, te, stack);
		if (!world.isRemote && world.getTileEntity(pos) instanceof TileEntityBase) {
			TileEntityDigistore barrel = (TileEntityDigistore) world.getTileEntity(pos);
			if (!barrel.wasWrenchedDoNotBreak) {
				int storedAmount = barrel.getStoredAmount();
				while (storedAmount > 0) {
					ItemStack droppedItem = barrel.getStoredItem().copy();
					droppedItem.setCount(Math.min(storedAmount, droppedItem.getMaxStackSize()));
					storedAmount -= droppedItem.getCount();
					// WorldUtilities.dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(),
					// droppedItem);
				}
				// for (int i = 0; i < barrel.slotsUpgrades.getSlots(); i++) {
				// if (!barrel.slotsUpgrades.getStackInSlot(i).isEmpty()) {
				// WorldUtilities.dropItem(worldIn, pos.getX(), pos.getY(), pos.getZ(),
				// barrel.slotsUpgrades.getStackInSlot(i));
				// }
				// }
			}
		}
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new TileEntityDigistore();
	}
}
