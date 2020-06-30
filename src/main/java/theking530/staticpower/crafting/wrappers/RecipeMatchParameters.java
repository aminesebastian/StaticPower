package theking530.staticpower.crafting.wrappers;

import java.util.HashMap;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeMatchParameters {
	private ItemStack[] items;
	private FluidStack[] fluids;
	private int storedEnergy;
	private final HashMap<String, Object> extraProperties;

	public RecipeMatchParameters() {
		extraProperties = new HashMap<String, Object>();
	}

	public RecipeMatchParameters(ItemStack... items) {
		this();
		this.items = items;
	}

	public RecipeMatchParameters(FluidStack... fluids) {
		this();
		this.fluids = fluids;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public RecipeMatchParameters setItems(ItemStack... items) {
		this.items = items;
		return this;
	}

	public FluidStack[] getFluids() {
		return fluids;
	}

	public RecipeMatchParameters setFluids(FluidStack... fluids) {
		this.fluids = fluids;
		return this;
	}

	public int getStoredEnergy() {
		return storedEnergy;
	}

	public RecipeMatchParameters setStoredEnergy(int energy) {
		this.storedEnergy = energy;
		return this;
	}

	public RecipeMatchParameters addAdditionalProperty(String key, Object value) {
		extraProperties.put(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getExtraProperty(String key) {
		if (extraProperties.containsKey(key)) {
			return (Optional<T>) Optional.of(extraProperties.get(key));
		}
		return Optional.empty();
	}

}
