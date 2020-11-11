package theking530.staticpower.items.tools.chainsaw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.power.ItemStackStaticVoltCapability;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.ChainsawItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class Chainsaw extends AbstractMultiHarvestTool implements ICustomModelSupplier {
	private static final Set<Block> FULL_SPEED_BLOCKS = Sets.newHashSet(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);

	private static final Set<ToolType> TOOL_TYPES = new HashSet<ToolType>();
	private static final int MAX_RECURSION = 100;
	private Ingredient woodIngredient;
	public final ResourceLocation tier;

	public Chainsaw(String name, float attackDamageIn, float attackSpeedIn, ResourceLocation tier) {
		super(new Item.Properties().setNoRepair(), name, attackDamageIn, attackSpeedIn);
		this.tier = tier;
		TOOL_TYPES.add(ToolType.AXE);
	}

	public int getCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get() * 2;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	public boolean hasChainsawBlade(ItemStack stack) {
		// Get the inventory.
		IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

		// If null, return false.
		if (inventory == null) {
			return false;
		}

		// Check to make sure we have a blade, and its not broken.
		return !inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getDamage() < inventory.getStackInSlot(0).getMaxDamage();
	}

	public ItemStack getChainsawBlade(ItemStack stack) {
		IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

		// If null, return false.
		if (inventory == null) {
			return ItemStack.EMPTY;
		}

		// Return the bit.
		return inventory.getStackInSlot(0);
	}

	@Override
	public boolean isReadyToMine(ItemStack itemstack) {
		return hasChainsawBlade(itemstack) && EnergyHandlerItemStackUtilities.getStoredPower(itemstack) > 0;
	}

	@Override
	protected float getEfficiency(ItemStack itemstack) {
		AtomicReference<Float> efficiency = new AtomicReference<Float>(1.0f);

		if (hasChainsawBlade(itemstack)) {
			ItemStack blade = getChainsawBlade(itemstack);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			efficiency.set(bladeItem.getMiningTier().getEfficiency());
			blade.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
				if (attributable.hasAttribute(HasteAttributeDefenition.ID)) {
					HasteAttributeDefenition hasteDefenition = (HasteAttributeDefenition) attributable.getAttribute(HasteAttributeDefenition.ID);
					efficiency.set(efficiency.get() * ((hasteDefenition.getValue() / 300.0f) + 1.0f));
				}
			});
		}
		return efficiency.get();
	}

	@Override
	public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
		if (hasChainsawBlade(stack)) {
			ItemStack blade = getChainsawBlade(stack);
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
		// Allocate a list of the items that would be dropped.
		List<ItemStack> droppableItems = Block.getDrops(state, player.getServerWorld(), pos, tileEntity, player, heldItem);

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
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return hasChainsawBlade(stack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		// Get the inventory.
		IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		if (inventory == null) {
			return 0.0f;
		}

		// Get the power ratio.
		return inventory.getStackInSlot(0).getItem().getDurabilityForDisplay(inventory.getStackInSlot(0));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		int remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		int capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));

		if (hasChainsawBlade(stack)) {
			ItemStack blade = this.getChainsawBlade(stack);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			bladeItem.getTooltip(blade, worldIn, tooltip, isShowingAdvanced);
			if (isShowingAdvanced) {
				bladeItem.getAdvancedTooltip(blade, worldIn, tooltip);
			}
			AttributeUtilities.addTooltipsForAttribute(blade, tooltip, isShowingAdvanced);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.mining_speed").appendString(" ").appendString(String.valueOf(this.getEfficiency(stack))));
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