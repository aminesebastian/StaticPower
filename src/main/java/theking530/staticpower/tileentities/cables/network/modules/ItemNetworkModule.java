package theking530.staticpower.tileentities.cables.network.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.utilities.InventoryUtilities;

public class ItemNetworkModule extends AbstractCableNetworkModule {

	public ItemNetworkModule() {
		super(CableNetworkModuleTypes.ITEM_NETWORK_ATTACHMENT);
	}

	@Override
	public void tick(World world) {

	}

	public Path getPathForItem(ItemStack item, BlockPos fromPosition, BlockPos sourcePosition) {
		List<Path> potentialPaths = new LinkedList<Path>();
		
		// Iterate through all the destinations in the graph.
		for (TileEntity dest : Network.getGraph().getDestinations()) {
			// Skip trying to go to the same position the item came from or is at.
			if(dest.getPos().equals(sourcePosition)) {
				continue;
			}
			
			// Allocate an atomic bool to capture if a path is valid.
			AtomicBoolean isValid = new AtomicBoolean(false);

			// Iterate through all the paths to the proposed tile entity.
			for (Path path : Network.getPathCache().getPaths(fromPosition, dest.getPos())) {
				// If we're able to insert into that inventory, set the atomic boolean.
				dest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, path.getDestinationDirection()).ifPresent(inv -> {
					isValid.set(InventoryUtilities.canInsertItemIntoInventory(inv, item));
				});
				// If the atomic boolean is valid, then we have a valid path and we return it.
				if (isValid.get()) {
					potentialPaths.add(path);
				}
			}
		}
		
		Path shortestPath = null;
		int shortestPathLength = Integer.MAX_VALUE;
		for(Path path : potentialPaths) {
			if(path.getLength() < shortestPathLength) {
				shortestPathLength = path.getLength();
				shortestPath = path;
			}
		}
		
		
		return shortestPath;
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {

	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return null;
	}

}
