package theking530.staticpower.blockentities.nonpowered.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderStaticChest;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityStaticChest extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityStaticChest> BASIC_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityStaticChest(type, 36, pos, state), ModBlocks.BasicChest);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityStaticChest> ADVANCED_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityStaticChest(type, 45, pos, state), ModBlocks.AdvancedChest);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityStaticChest> STATIC_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityStaticChest(type, 54, pos, state), ModBlocks.StaticChest);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityStaticChest> ENERGIZED_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityStaticChest(type, 72, pos, state), ModBlocks.EnergizedChest);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityStaticChest> LUMUM_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new BlockEntityStaticChest(type, 81, pos, state), ModBlocks.LumumChest);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			BASIC_TYPE.setTileEntitySpecialRenderer(BlockEntityRenderStaticChest::new);
			ADVANCED_TYPE.setTileEntitySpecialRenderer(BlockEntityRenderStaticChest::new);
			STATIC_TYPE.setTileEntitySpecialRenderer(BlockEntityRenderStaticChest::new);
			ENERGIZED_TYPE.setTileEntitySpecialRenderer(BlockEntityRenderStaticChest::new);
			LUMUM_TYPE.setTileEntitySpecialRenderer(BlockEntityRenderStaticChest::new);
		}
	}

	public final InventoryComponent inventory;
	public float openAlpha;
	private int openCount;

	public BlockEntityStaticChest(BlockEntityTypeAllocator<BlockEntityStaticChest> type, int slotCount, BlockPos pos, BlockState state) {
		super(type, pos, state);
		registerComponent(inventory = new InventoryComponent("Inventory", slotCount, MachineSideMode.NA).setShiftClickEnabled(true));
	}

	@Override
	public void process() {
		// This is run on the game thread, so @ 20hz.
		if (isOpen()) {
			openAlpha = Math.min(openAlpha + 0.1f, 1);
		} else {
			openAlpha = Math.max(openAlpha - 0.1f, 0);
		}
	}

	public static Vector2D getInventorySize(BlockEntityStaticChest chest) {
		int rows = chest.inventory.getSlots() / getSlotsPerRow(chest);
		return new Vector2D(176, 114 + (rows * 18));
	}

	public static int getSlotsPerRow(BlockEntityStaticChest chest) {
		return 9;
	}

	public boolean isOpen() {
		return openCount > 0;
	}

	public void addOpenCount() {
		if (openCount == 0) {
			getLevel().playSound(null, getBlockPos(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5f, 1.05f + getLevel().getRandom().nextFloat() * 0.1f);
			getLevel().playSound(null, getBlockPos(), SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5f, 1.0f);
		}
		openCount++;
	}

	public void removeOpenCount() {
		openCount = Math.max(0, openCount - 1);
		if (openCount == 0) {
			getLevel().playSound(null, getBlockPos(), SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1.0f, 1.15f);
		}
	}

	public int getSlotCount() {
		return inventory.getSlots();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerStaticChest(windowId, inventory, this);
	}
}
