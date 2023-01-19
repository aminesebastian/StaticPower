package theking530.staticpower.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticpower.StaticPower;

public class CableBlockModelProvider extends BlockStateProvider {

	public CableBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
	}

	protected void buildForBlock(Block block) {
	}
}
