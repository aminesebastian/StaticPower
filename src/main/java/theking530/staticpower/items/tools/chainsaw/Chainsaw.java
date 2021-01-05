package theking530.staticpower.items.tools.chainsaw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.multipart.AbstractMultiPartSlot;
import theking530.api.multipart.MultiPartSlots;
import theking530.api.power.ItemStackStaticVoltCapability;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.ChainsawItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class Chainsaw extends AbstractMultiHarvestTool implements ICustomModelSupplier {
	private static final Set<Block> FULL_SPEED_BLOCKS = Sets.newHashSet(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);

	private static final Set<ToolType> TOOL_TYPES = new HashSet<ToolType>();
	private static final List<AbstractMultiPartSlot> PARTS = new ArrayList<AbstractMultiPartSlot>();
	private static final int MAX_RECURSION = 100;
	private Ingredient woodIngredient;
	public final ResourceLocation tier;

	public Chainsaw(String name, float attackDamageIn, float attackSpeedIn, ResourceLocation tier) {
		super(new Item.Properties().setNoRepair(), name, attackDamageIn, attackSpeedIn);
		this.tier = tier;
		TOOL_TYPES.add(ToolType.AXE);
		PARTS.add(MultiPartSlots.CHAINSAW_BLADE);
	}

	public long getCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get() * 2;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	@Override
	public List<AbstractMultiPartSlot> getSlots(ItemStack stack) {
		return PARTS;
	}

	@Override
	public ItemStack getPartInSlot(ItemStack stack, AbstractMultiPartSlot slot) {
		if (slot == MultiPartSlots.CHAINSAW_BLADE) {
			IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inventory != null) {
				return inventory.getStackInSlot(0);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isReadyToMine(ItemStack itemstack) {
		return isComplete(itemstack) && EnergyHandlerItemStackUtilities.getStoredPower(itemstack) > 0;
	}

	@Override
	protected float getEfficiency(ItemStack itemstack) {
		AtomicReference<Float> efficiency = new AtomicReference<Float>(5.0f);

		if (isSlotPopulated(itemstack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(itemstack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			efficiency.set(bladeItem.getMiningTier().getEfficiency() * 0.25f);
			blade.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
				if (attributable.hasAttribute(HasteAttributeDefenition.ID)) {
					HasteAttributeDefenition hasteDefenition = (HasteAttributeDefenition) attributable.getAttribute(HasteAttributeDefenition.ID);
					efficiency.set(efficiency.get() * (((hasteDefenition.getValue() * 10.0f) / 300.0f) + 1.0f));
				}
			});
		}
		return efficiency.get();
	}

	@Override
	public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
		if (isSlotPopulated(stack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(stack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			return bladeItem.getMiningTier().getHarvestLevel();
		}
		return super.getHarvestLevel(stack, tool, player, blockState);
	}

	/**
	 * When right clicked, open the UI.
	 */
	@Override
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (!world.isRemote && player.isSneaking()) {
			NetworkGUI.openGui((ServerPlayerEntity) player, new ChainsawContainerProvider(item), buff -> {
				buff.writeInt(player.inventory.currentItem);
			});
			return ActionResult.resultSuccess(item);
		}
		return ActionResult.resultPass(item);
	}

	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayerEntity player, TileEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// If the player is in creative, do nothing.
		if (isCreative) {
			return;
		}

		// Allocate a list of the items that would be dropped.
		List<ItemStack> droppableItems = Block.getDrops(state, player.getServerWorld(), pos, tileEntity, player, heldItem);

		// Get the drill bit attributes.
		IAttributable drillBitAttributes = getPartInSlot(heldItem, MultiPartSlots.CHAINSAW_BLADE).getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);

		// If we have attributes.
		if (drillBitAttributes != null) {
			// Check for the smelting attribute. If we do, handle it.
			if (drillBitAttributes.hasAttribute(SmeltingAttributeDefenition.ID)) {
				// Get the smelting attribute.
				SmeltingAttributeDefenition smeltingAttribute = (SmeltingAttributeDefenition) drillBitAttributes.getAttribute(SmeltingAttributeDefenition.ID);
				handleSmeltingAttribute(smeltingAttribute, droppableItems, state, block, pos, player, tileEntity, heldItem, experience, isCreative);
			}
		}

		// Drop all the droppable stacks.
		for (ItemStack stack : droppableItems) {
			WorldUtilities.dropItem(player.getServerWorld(), pos, stack);
		}

		// Drop the XP.
		if (experience > 0) {
			state.getBlock().dropXpOnBlockBreak((ServerWorld) player.getEntityWorld(), pos, experience);
		}

		// Spawn any additional drops.
		state.spawnAdditionalDrops((ServerWorld) player.getEntityWorld(), pos, heldItem);
	}

	protected boolean handleSmeltingAttribute(SmeltingAttributeDefenition smeltingAttribute, List<ItemStack> droppableItems, BlockState state, Block block, BlockPos pos,
			ServerPlayerEntity player, TileEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {

		// Allocate a flag to check if anything was smelted.
		boolean wasAnythingSmelted = false;

		// If the smelting attribute is enabled.
		if (smeltingAttribute.getValue()) {
			// Iterate through all the items that were going to be dropped.
			for (int i = droppableItems.size() - 1; i >= 0; i--) {
				// Get the droppable stack and get the furnace recipe for it if it exists.
				ItemStack droppableStack = droppableItems.get(i);
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(droppableStack);
				Optional<FurnaceRecipe> recipe = player.getServerWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(matchParameters.getItems()[0]),
						player.getServerWorld());

				// Replace the spot the droppable list with the smelting output if it exists.
				if (recipe.isPresent()) {
					droppableItems.set(i, recipe.get().getRecipeOutput());
					wasAnythingSmelted = true;
				}
			}
		}

		// Return the flag.
		return wasAnythingSmelted;
	}

	/**
	 * This method is only raised on the server.
	 */
	@Override
	protected void onAllBlocksMined(ItemStack stack, List<BlockPos> blocksMined, PlayerEntity player) {
		// Apply damage to the bit.
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			handler.getStackInSlot(0).damageItem(blocksMined.size(), player, (entity) -> {
				entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		});

		// Update the energy usage on client and server.
		if (!player.isCreative()) {
			EnergyHandlerItemStackUtilities.drainPower(stack, blocksMined.size(), false);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		long remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		long capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));

		if (isSlotPopulated(stack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(stack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			bladeItem.getTooltip(blade, worldIn, tooltip, isShowingAdvanced);
			tooltip.add(new TranslationTextComponent("gui.staticpower.mining_speed").appendString(" ").appendString(String.valueOf(getEfficiency(stack))));
			AttributeUtilities.addTooltipsForAttribute(blade, tooltip, isShowingAdvanced);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		if (isSlotPopulated(stack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(stack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			bladeItem.getAdvancedTooltip(blade, worldIn, tooltip);
		}
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, 5))
				.addCapability(new ItemStackStaticVoltCapability("default", stack, getCapacity(), getCapacity(), getCapacity()));
	}

	@Override
	protected boolean canHarvestBlockInternal(ItemStack stack, BlockState state) {
		// Check the tool.
		if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) {
			return true;
		}

		// If we are able to harvest the block full speed, no need to further check.
		if (FULL_SPEED_BLOCKS.contains(state.getBlock())) {
			return true;
		}

		// Then check by harvest level
		if (state.getHarvestTool() != null) {
			int i = this.getHarvestLevel(stack, state.getHarvestTool(), null, state);
			return i >= state.getHarvestLevel();
		}

		Material material = state.getMaterial();
		return material == Material.WOOD || material == Material.NETHER_WOOD;
	}

	@Override
	public boolean canHarvestAtFullSpeed(ItemStack stack, BlockState state) {
		// If we are able to harvest the block full speed, do so.
		if (FULL_SPEED_BLOCKS.contains(state.getBlock())) {
			return true;
		}

		// Check the tool.
		if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) {
			return true;
		}

		// Finally, check the materials.
		Material material = state.getMaterial();
		return material == Material.WOOD || material == Material.NETHER_WOOD;
	}

	@Override
	public Set<ToolType> getToolTypes(ItemStack stack) {
		return TOOL_TYPES;
	}

	@Override
	public int getWidth(ItemStack itemstack) {
		return 1;
	}

	@Override
	public int getHeight(ItemStack itemstack) {
		return 1;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public List<BlockPos> getMineableExtraBlocks(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// Capture the list of all the blocks to mine.
		List<BlockPos> minableBlocks = new ArrayList<BlockPos>();

		// If we can't mine, just return the single block.
		if (!isReadyToMine(itemstack)) {
			minableBlocks.add(pos);
			return minableBlocks;
		}

		// Perform the recursive analysis.
		recursiveTreeAnalyzer(itemstack, pos, player, minableBlocks, new HashSet<BlockPos>(), 0);

		// Return the list of mineable blocks.
		return minableBlocks;
	}

	@SuppressWarnings("deprecation")
	private void recursiveTreeAnalyzer(ItemStack itemstack, BlockPos pos, PlayerEntity player, List<BlockPos> positions, Set<BlockPos> visited, int currentDepth) {
		// If we maxed out on depth, stop.
		if (currentDepth > MAX_RECURSION) {
			return;
		}

		// If we have already visited this spot, stop.
		if (visited.contains(pos)) {
			return;
		}

		// Mark this spot as visited.
		visited.add(pos);

		// Get the state and block here, and check if they are air.
		BlockState state = player.getEntityWorld().getBlockState(pos);
		Block block = state.getBlock();
		if (!block.isAir(state, player.getEntityWorld(), pos)) {
			// Make an itemstack for the block.
			ItemStack blockStack = new ItemStack(Item.getItemFromBlock(block));

			// If not air, check to see if it is wood. If it is, and its harvestable,
			// harvest it.
			if (getLogTag().test(blockStack)) {
				if (this.canHarvestBlockInternal(itemstack, state)) {
					positions.add(pos);

					// Recurse.
					for (Direction dir : Direction.values()) {
						recursiveTreeAnalyzer(itemstack, pos.offset(dir), player, positions, visited, currentDepth + 1);
					}
				}
			}
		}
	}

	private Ingredient getLogTag() {
		if (woodIngredient == null) {
			woodIngredient = Ingredient.fromTag(ModTags.LOG);
		}
		return woodIngredient;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new ChainsawItemModel(existingModel);
	}

	public class ChainsawContainerProvider implements INamedContainerProvider {
		public ItemStack targetItemStack;

		public ChainsawContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
			return new ContainerChainsaw(windowId, inventory, targetItemStack);
		}

		@Override
		public ITextComponent getDisplayName() {
			return targetItemStack.getDisplayName();
		}
	}
}