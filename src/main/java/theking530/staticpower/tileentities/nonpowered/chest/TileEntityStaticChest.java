package theking530.staticpower.tileentities.nonpowered.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderStaticChest;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;

public class TileEntityStaticChest extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStaticChest> BASIC_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStaticChest(type, StaticPowerTiers.BASIC, 36, pos, state), ModBlocks.BasicChest);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStaticChest> ADVANCED_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStaticChest(type, StaticPowerTiers.ADVANCED, 45, pos, state), ModBlocks.AdvancedChest);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStaticChest> STATIC_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStaticChest(type, StaticPowerTiers.STATIC, 54, pos, state), ModBlocks.StaticChest);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStaticChest> ENERGIZED_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStaticChest(type, StaticPowerTiers.ENERGIZED, 72, pos, state), ModBlocks.EnergizedChest);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityStaticChest> LUMUM_TYPE = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityStaticChest(type, StaticPowerTiers.LUMUM, 81, pos, state), ModBlocks.LumumChest);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			BASIC_TYPE.setTileEntitySpecialRenderer(TileEntityRenderStaticChest::new);
			ADVANCED_TYPE.setTileEntitySpecialRenderer(TileEntityRenderStaticChest::new);
			STATIC_TYPE.setTileEntitySpecialRenderer(TileEntityRenderStaticChest::new);
			ENERGIZED_TYPE.setTileEntitySpecialRenderer(TileEntityRenderStaticChest::new);
			LUMUM_TYPE.setTileEntitySpecialRenderer(TileEntityRenderStaticChest::new);
		}
	}
	
	public final ResourceLocation tier;
	public final InventoryComponent inventory;
	public float openAlpha;
	private int openCount;

	public TileEntityStaticChest(BlockEntityTypeAllocator<TileEntityStaticChest> type, ResourceLocation tier, int slotCount, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.tier = tier;
		registerComponent(inventory = new InventoryComponent("Inventory", slotCount, MachineSideMode.NA).setShiftClickEnabled(true));
	}

	public static Vector2D getInventorySize(TileEntityStaticChest chest) {
		int rows = chest.inventory.getSlots() / getSlotsPerRow(chest);
		return new Vector2D(176, 116 + (rows * 18));
	}

	public static int getSlotsPerRow(TileEntityStaticChest chest) {
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
