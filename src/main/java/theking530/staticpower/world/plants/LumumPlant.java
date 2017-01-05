package theking530.staticpower.world.plants;

import net.minecraft.item.Item;

public class LumumPlant extends BaseCrop {

	public LumumPlant(String name) {
		super(name);
	}

	@Override
    public Item getSeeds() {
    	return ModPlants.LumumSeeds;
    }
	@Override
    public Item getCrops() {
    	return ModPlants.LumumCrop;
    }
}