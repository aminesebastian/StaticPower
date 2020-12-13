package theking530.staticpower.data.crafting;

import java.util.HashMap;
import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeMatchParameters {
	private ItemStack[] items;
	private FluidStack[] fluids;
	private BlockState[] blocks;
	private boolean verifyBlocks;
	private boolean verifyItems;
	private boolean verifyItemCounts;
	private boolean verifyFluids;
	private boolean verifyFluidAmounts;

	private int storedEnergy;
	private final HashMap<String, Object> extraProperties;

	public RecipeMatchParameters() {
		extraProperties = new HashMap<String, Object>();
		verifyItemCounts = true;
		verifyFluids = true;
		verifyFluidAmounts = true;
		verifyItems = true;
		verifyBlocks = true;
	}

	public RecipeMatchParameters(ItemStack... items) {
		this();
		this.items = items;
	}

	public RecipeMatchParameters(BlockState... blocks) {
		this();
		this.blocks = blocks;
	}

	public RecipeMatchParameters(FluidStack... fluids) {
		this();
		this.fluids = fluids;
	}

	public boolean shouldVerifyItems() {
		return verifyItems;
	}

	public RecipeMatchParameters ignoreItems() {
		verifyItems = false;
		return this;
	}

	public boolean shouldVerifyItemCounts() {
		return verifyItemCounts;
	}

	public RecipeMatchParameters ignoreItemCounts() {
		verifyItemCounts = false;
		return this;
	}

	public boolean shouldVerifyFluids() {
		return verifyFluids;
	}

	public RecipeMatchParameters ignoreFluids() {
		this.verifyFluids = false;
		return this;
	}

	public boolean shouldVerifyFluidAmounts() {
		return verifyFluidAmounts;
	}

	public RecipeMatchParameters ignoreFluidAmounts() {
		this.verifyFluidAmounts = false;
		return this;
	}

	public ItemStack[] getItems() {
		return items;
	}

	public boolean hasItems() {
		return items != null && items.length > 0;
	}

	public RecipeMatchParameters setItems(ItemStack... items) {
		this.items = items;
		return this;
	}

	public boolean shouldVerifyBlocks() {
		return verifyBlocks;
	}

	public RecipeMatchParameters ignoreBlocks() {
		verifyBlocks = false;
		return this;
	}

	public boolean hasBlocks() {
		return blocks != null && blocks.length > 0;
	}

	public RecipeMatchParameters setBlocks(BlockState... blocks) {
		this.blocks = blocks;
		return this;
	}

	public BlockState[] getBlocks() {
		return blocks;
	}

	public FluidStack[] getFluids() {
		return fluids;
	}

	public boolean hasFluids() {
		return fluids != null && fluids.length > 0;
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
