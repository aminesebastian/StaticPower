package theking530.staticcore.crafting;

import java.util.HashMap;
import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class RecipeMatchParameters implements Container {

	private ItemStack[] items;
	private FluidStack[] fluids;
	private BlockState[] blocks;
	private CompoundTag customParameters;
	private boolean verifyBlocks;
	private boolean verifyItems;
	private boolean verifyItemCounts;
	private boolean verifyFluids;
	private boolean verifyFluidAmounts;

	private String teamId;
	private boolean verifyResearchRestrictions;

	private final HashMap<String, Object> extraProperties;

	public RecipeMatchParameters(String teamId) {
		if (teamId == null || teamId.isBlank()) {
			this.teamId = null;
			verifyResearchRestrictions = false;
		} else {
			this.teamId = teamId;
			verifyResearchRestrictions = true;
		}

		customParameters = new CompoundTag();
		extraProperties = new HashMap<String, Object>();
		verifyItemCounts = true;
		verifyFluids = true;
		verifyFluidAmounts = true;
		verifyItems = true;
		verifyBlocks = true;
	}

	public RecipeMatchParameters(ItemStack... items) {
		this("");
		this.items = items;
	}

	public RecipeMatchParameters(String teamId, ItemStack... items) {
		this(teamId);
		this.items = items;
	}

	public RecipeMatchParameters(BlockState... blocks) {
		this("");
		this.blocks = blocks;
	}

	public RecipeMatchParameters(String teamId, BlockState... blocks) {
		this(teamId);
		this.blocks = blocks;
	}

	public RecipeMatchParameters(FluidStack... fluids) {
		this("");
		this.fluids = fluids;
	}

	public RecipeMatchParameters(String teamId, FluidStack... fluids) {
		this(teamId);
		this.fluids = fluids;
	}

	public CompoundTag getCustomParameterContainer() {
		return this.customParameters;
	}

	public boolean hasCustomParameters() {
		return this.customParameters.size() > 0;
	}

	public boolean hasCustomParameter(String key) {
		return this.customParameters.contains(key);
	}

	public RecipeMatchParameters setIntParameter(String key, int value) {
		this.customParameters.putInt(key, value);
		return this;
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

	public boolean shouldVerifyResearchRestrictions() {
		return verifyResearchRestrictions;
	}

	public String getTeamId() {
		return teamId;
	}

	public RecipeMatchParameters ignoreResearchRestrictions() {
		verifyResearchRestrictions = false;
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

	@Override
	public void clearContent() {

	}

	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int p_18941_) {
		return items[p_18941_];
	}

	@Override
	public ItemStack removeItem(int p_18942_, int p_18943_) {
		return null;
	}

	@Override
	public ItemStack removeItemNoUpdate(int p_18951_) {
		return null;
	}

	@Override
	public void setItem(int p_18944_, ItemStack p_18945_) {
		items[p_18944_] = p_18945_;
	}

	@Override
	public void setChanged() {

	}

	@Override
	public boolean stillValid(Player p_18946_) {
		return false;
	}

}
