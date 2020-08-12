package theking530.staticpower.tileentities.nonpowered.heatsink;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;

public class TileEntityHeatSink extends TileEntityBase implements INamedContainerProvider {
	public final HeatStorageComponent heatStorage;

	public TileEntityHeatSink() {
		super(ModTileEntityTypes.HEAT_SINK);
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorage", 1000.0f, 10.0f, 2.0f));
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.VacuumChest.getTranslationKey());
	}
}
