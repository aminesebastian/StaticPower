package theking530.staticpower.world.plants;

import net.minecraft.item.Item;

public class EnergizedPlant extends BaseCrop {

	public EnergizedPlant(String name) {
		super(name);
	}

	@Override
    public Item getSeeds() {
    	return ModPlants.EnergizedSeeds;
    }
	@Override
    public Item getCrops() {
    	return ModPlants.EnergizedCrop;
    }
}