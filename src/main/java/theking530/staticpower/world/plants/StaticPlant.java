package theking530.staticpower.world.plants;

import net.minecraft.item.Item;

public class StaticPlant extends BaseCrop {

	public StaticPlant(String name) {
		super(name);
	}

	@Override
    public Item getSeeds() {
    	return ModPlants.StaticSeeds;
    }
	@Override
    public Item getCrops() {
    	return ModPlants.StaticCrop;
    }
}
