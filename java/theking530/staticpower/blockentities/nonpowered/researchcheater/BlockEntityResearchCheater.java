package theking530.staticpower.blockentities.nonpowered.researchcheater;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.OutputServoComponent;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;

public class BlockEntityResearchCheater extends BlockEntityConfigurable implements MenuProvider {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityResearchCheater> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityResearchCheater(pos, state),
			ModBlocks.ResearchCheater);
	private static final float GENERATION_RATE = 1;
	public final InventoryComponent inventory;
	private float timer;

	public BlockEntityResearchCheater(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		enableFaceInteraction();
		registerComponent(inventory = new InventoryComponent("Inventory", 7, MachineSideMode.Output).setShiftClickEnabled(true));
		registerComponent(new OutputServoComponent("OutputServo", 2, inventory));
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

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.ALL_SIDES_OUTPUT;
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Output;
	}

	private void populateResearch() {
		inventory.setStackInSlot(0, new ItemStack(ModItems.ResearchTier1.get(), 64));
		inventory.setStackInSlot(1, new ItemStack(ModItems.ResearchTier2.get(), 64));
		inventory.setStackInSlot(2, new ItemStack(ModItems.ResearchTier3.get(), 64));
		inventory.setStackInSlot(3, new ItemStack(ModItems.ResearchTier4.get(), 64));
		inventory.setStackInSlot(4, new ItemStack(ModItems.ResearchTier5.get(), 64));
		inventory.setStackInSlot(5, new ItemStack(ModItems.ResearchTier6.get(), 64));
		inventory.setStackInSlot(6, new ItemStack(ModItems.ResearchTier7.get(), 64));
	}
}
