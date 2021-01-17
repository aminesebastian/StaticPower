package theking530.staticpower.tileentities.nonpowered.rustycauldron;

import java.util.List;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderRustyCauldron;
import theking530.staticpower.entities.RubberWoodBarkEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;

public class TileEntityRustyCauldron extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityRustyCauldron> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityRustyCauldron(), ModBlocks.RustyCauldron);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderRustyCauldron::new);
		}
	}

	public final FluidTankComponent internalTank;

	public TileEntityRustyCauldron() {
		super(TYPE);
		registerComponent(internalTank = new FluidTankComponent("InputFluidTank", 1000).setCanFill(true).setCapabilityExposedModes(MachineSideMode.Output).setAutoSyncPacketsEnabled(true));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!world.isRemote) {
			// Create the AABB to search within.
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() + 0.125, pos.getY() + 0.1875, pos.getZ() + 0.125, pos.getX() + 0.875, pos.getY() + 1.0, pos.getZ() + 0.875);
			handleRubberWoodBark(aabb);
			this.internalTank.fill(new FluidStack(Fluids.WATER, 10), FluidAction.EXECUTE);
		}
	}

	protected void handleRubberWoodBark(AxisAlignedBB bounds) {
		List<ItemEntity> items = getWorld().getEntitiesWithinAABB(ItemEntity.class, bounds);
		for (ItemEntity item : items) {
			// Since RubberWoodBarkEntity inherits from ItemEntity, we must check in order
			// to avoid a loop.
			if (item instanceof RubberWoodBarkEntity) {
				continue;
			}
			if (item.getItem().getItem() == ModItems.RubberWoodBark) {
				RubberWoodBarkEntity entity = new RubberWoodBarkEntity(this.getWorld(), item.getPosX(), item.getPosY(), item.getPosZ(), item.getItem().copy());
				entity.setMotion(item.getMotion());
				entity.setPickupDelay(80); // Set this value initially a little high!
				this.getWorld().addEntity(entity);

				item.remove();
			}
		}
	}
}
