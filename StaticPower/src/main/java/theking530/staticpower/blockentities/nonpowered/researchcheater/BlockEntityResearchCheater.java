package theking530.staticpower.blockentities.nonpowered.researchcheater;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesOutput;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.init.StaticCoreItems;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityResearchCheater extends BlockEntityBase implements MenuProvider {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityResearchCheater> TYPE = new BlockEntityTypeAllocator<>("research_cheater", (type, pos, state) -> new BlockEntityResearchCheater(pos, state),
			ModBlocks.ResearchCheater);
	private static final float GENERATION_RATE = 1;

	public final InventoryComponent inventory;
	public final SideConfigurationComponent ioSideConfiguration;
	private float timer;

	public BlockEntityResearchCheater(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		registerComponent(inventory = new InventoryComponent("Inventory", 7, MachineSideMode.Output).setShiftClickEnabled(true));
		registerComponent(new OutputServoComponent("OutputServo", 2, inventory));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", AllSidesOutput.INSTANCE));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!level.isClientSide) {
			timer++;
			if (timer >= GENERATION_RATE) {
				populateResearch();
				timer = 0;
			}
		}
	}

	private void populateResearch() {
		inventory.setStackInSlot(0, new ItemStack(StaticCoreItems.ResearchTier1.get(), 64));
		inventory.setStackInSlot(1, new ItemStack(StaticCoreItems.ResearchTier2.get(), 64));
		inventory.setStackInSlot(2, new ItemStack(StaticCoreItems.ResearchTier3.get(), 64));
		inventory.setStackInSlot(3, new ItemStack(StaticCoreItems.ResearchTier4.get(), 64));
		inventory.setStackInSlot(4, new ItemStack(StaticCoreItems.ResearchTier5.get(), 64));
		inventory.setStackInSlot(5, new ItemStack(StaticCoreItems.ResearchTier6.get(), 64));
		inventory.setStackInSlot(6, new ItemStack(StaticCoreItems.ResearchTier7.get(), 64));
	}
}
