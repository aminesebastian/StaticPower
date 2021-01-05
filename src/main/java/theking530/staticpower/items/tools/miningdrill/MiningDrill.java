package theking530.staticpower.items.tools.miningdrill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
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
import theking530.api.attributes.defenitions.FortuneAttributeDefenition;
import theking530.api.attributes.defenitions.GrindingAttributeDefenition;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.SilkTouchAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.multipart.AbstractMultiPartSlot;
import theking530.api.multipart.MultiPartSlots;
import theking530.api.power.ItemStackStaticVoltCapability;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.MiningDrillItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class MiningDrill extends AbstractMultiHarvestTool implements ICustomModelSupplier {
	private static final Set<Block> FULL_SPEED_BLOCKS = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK,
			Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK,
			Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE,
			Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.CRYING_OBSIDIAN, Blocks.OBSIDIAN, Blocks.GRANITE,
			Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB,
			Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB,
			Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE,
			Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB,
			Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB,
			Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.PISTON,
			Blocks.STICKY_PISTON, Blocks.PISTON_HEAD, Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM,
			Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL);

	private static final Set<ToolType> TOOL_TYPES = new HashSet<ToolType>();
	private static final List<AbstractMultiPartSlot> PARTS = new ArrayList<AbstractMultiPartSlot>();
	public final ResourceLocation tier;

	public MiningDrill(String name, float attackDamageIn, float attackSpeedIn, ResourceLocation tier) {
		super(new Item.Properties().setNoRepair(), name, attackDamageIn, attackSpeedIn);
		this.tier = tier;
		TOOL_TYPES.add(ToolType.PICKAXE);
		TOOL_TYPES.add(ToolType.SHOVEL);
		PARTS.add(MultiPartSlots.DRILL_BIT);
	}

	public long getPowerCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get() * 2;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	@Override
	public ItemStack getPartInSlot(ItemStack stack, AbstractMultiPartSlot slot) {
		if (slot == MultiPartSlots.DRILL_BIT) {
			IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inventory != null) {
				return inventory.getStackInSlot(0);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public List<AbstractMultiPartSlot> getSlots(ItemStack stack) {
		return PARTS;
	}

	@Override
	public boolean isReadyToMine(ItemStack itemstack) {
		return isComplete(itemstack) && EnergyHandlerItemStackUtilities.getStoredPower(itemstack) > 0;
	}

	@Override
	protected float getEfficiency(ItemStack itemstack) {
		AtomicReference<Float> efficiency = new AtomicReference<Float>(1.0f);

		if (isSlotPopulated(itemstack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBitStack = getPartInSlot(itemstack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBit = (DrillBit) drillBitStack.getItem();
			efficiency.set(drillBit.getMiningTier().getEfficiency() * 0.1f);
			drillBitStack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
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
		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBitStack = getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBit = (DrillBit) drillBitStack.getItem();
			return drillBit.getMiningTier().getHarvestLevel();
		}
		return super.getHarvestLevel(stack, tool, player, blockState);
	}

	/**
	 * When right clicked, open the drill UI.
	 */
	@Override
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (!world.isRemote && player.isSneaking()) {
			NetworkGUI.openGui((ServerPlayerEntity) player, new MiningDrillContainerProvider(item), buff -> {
				buff.writeInt(player.inventory.currentItem);
			});
			return ActionResult.resultSuccess(item);
		}
		return ActionResult.resultPass(item);
	}

	@Override
	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayerEntity player, TileEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// If the player is in creative, do nothing.
		if (isCreative) {
			return;
		}

		// Get the drill bit attributes.
		IAttributable drillBitAttributes = getPartInSlot(heldItem, MultiPartSlots.DRILL_BIT).getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);

		// Allocate a list of the items that would be dropped.
		List<ItemStack> droppableItems = Block.getDrops(state, player.getServerWorld(), pos, tileEntity, player, heldItem);

		// If we have attributes.
		if (drillBitAttributes != null) {
			// Check for the grinder attribute. If we do, we add the grindable items to the
			// list if grindable.
			if (drillBitAttributes.hasAttribute(GrindingAttributeDefenition.ID)) {
				// Get the grinding attribute and check if its enabled.
				GrindingAttributeDefenition grindingAttribute = (GrindingAttributeDefenition) drillBitAttributes.getAttribute(GrindingAttributeDefenition.ID);
				handleGrindingAttribute(grindingAttribute, droppableItems, state, block, pos, player, tileEntity, heldItem, experience, isCreative);
			}

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

	protected boolean handleGrindingAttribute(GrindingAttributeDefenition grindingAttribute, List<ItemStack> droppableItems, BlockState state, Block block, BlockPos pos,
			ServerPlayerEntity player, TileEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {

		// Allocate a flag to check if anything was ground.
		boolean wasAnythingGround = false;

		// Capture the ground items.
		List<ItemStack> groundItems = new ArrayList<ItemStack>();

		// Get the grinding attribute and check if its enabled.
		if (grindingAttribute.getValue()) {
			// Iterate through all the items that were going to be dropped.
			for (int i = droppableItems.size() - 1; i >= 0; i--) {
				// Get the droppable stack and get the grinding recipe for it if it exists.
				ItemStack droppableStack = droppableItems.get(i);
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(droppableStack);
				Optional<GrinderRecipe> recipe = StaticPowerRecipeRegistry.getRecipe(GrinderRecipe.RECIPE_TYPE, matchParameters);

				// If the recipe is present, create the ground droppables.
				if (recipe.isPresent()) {
					boolean didGrindingSucceed = false;
					for (ProbabilityItemStackOutput output : recipe.get().getOutputItems()) {
						if (SDMath.diceRoll(output.getOutputChance())) {
							groundItems.add(output.getItem());
							didGrindingSucceed = true;
						}
					}

					// If the grinding of this particular item succeeded, remove it from the
					// droppable list.
					if (didGrindingSucceed) {
						droppableItems.remove(i);
					}
					wasAnythingGround = true;
				}
			}
		}

		// Add the ground items to the output.
		droppableItems.addAll(groundItems);

		return wasAnythingGround;
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

	@Override
	protected void onStartingBlockMining(ItemStack stack, List<BlockPos> blocksMined, PlayerEntity player) {
		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack bit = getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			bit.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
				if (attributable.hasAttribute(FortuneAttributeDefenition.ID)) {
					FortuneAttributeDefenition fortune = (FortuneAttributeDefenition) attributable.getAttribute(FortuneAttributeDefenition.ID);
					int fLevel = fortune.getFortuneLevelWithChance();
					stack.addEnchantment(Enchantments.FORTUNE, fLevel);
				}
				if (attributable.hasAttribute(SilkTouchAttributeDefenition.ID)) {
					SilkTouchAttributeDefenition silkTouch = (SilkTouchAttributeDefenition) attributable.getAttribute(SilkTouchAttributeDefenition.ID);
					if (silkTouch.getValue()) {
						stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
					}
				}
			});
		}
	}

	/**
	 * This method is only raised on the server.
	 */
	@Override
	protected void onAllBlocksMined(ItemStack stack, List<BlockPos> blocksMined, PlayerEntity player) {
		// Apply damage to the drill bit.
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			handler.getStackInSlot(0).damageItem(blocksMined.size(), player, (entity) -> {
				entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
			});
		});

		// Update the energy usage on client and server.
		if (!player.isCreative()) {
			EnergyHandlerItemStackUtilities.drainPower(stack, blocksMined.size(), false);
		}

		// Remove the enchantments.
		removeEnchantment(stack, Enchantments.FORTUNE);
		removeEnchantment(stack, Enchantments.SILK_TOUCH);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		long remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		long capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));

		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBit = this.getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBitItem = (DrillBit) drillBit.getItem();
			drillBitItem.getTooltip(drillBit, worldIn, tooltip, isShowingAdvanced);
			tooltip.add(new TranslationTextComponent("gui.staticpower.mining_speed").appendString(" ").append(GuiTextUtilities.formatUnitRateToString(this.getEfficiency(stack))));
			AttributeUtilities.addTooltipsForAttribute(drillBit, tooltip, isShowingAdvanced);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBit = this.getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBitItem = (DrillBit) drillBit.getItem();
			drillBitItem.getAdvancedTooltip(drillBit, worldIn, tooltip);
		}
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, 5))
				.addCapability(new ItemStackStaticVoltCapability("default", stack, getPowerCapacity(), getPowerCapacity(), getPowerCapacity()));
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
		return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL || material == Material.CLAY || material == Material.SAND || material == Material.EARTH;
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
		return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL || material == Material.CLAY || material == Material.SAND || material == Material.EARTH;
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
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new MiningDrillItemModel(existingModel);
	}

	public class MiningDrillContainerProvider implements INamedContainerProvider {
		public ItemStack targetItemStack;

		public MiningDrillContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
			return new ContainerMiningDrill(windowId, inventory, targetItemStack);
		}

		@Override
		public ITextComponent getDisplayName() {
			return targetItemStack.getDisplayName();
		}
	}
}