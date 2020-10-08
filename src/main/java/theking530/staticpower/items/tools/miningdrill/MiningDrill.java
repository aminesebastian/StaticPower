package theking530.staticpower.items.tools.miningdrill;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.MiningDrillItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.items.EnergyHandlerItemStack;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.items.utilities.ItemInventoryHandler;

public class MiningDrill extends AbstractMultiHarvestTool implements ICustomModelSupplier {
	private static final Set<Block> EFFECTIVE_ON = ImmutableSet.of(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL,
			Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK,
			Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE,
			Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB,
			Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB,
			Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB,
			Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB,
			Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX,
			Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD, Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND,
			Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER,
			Blocks.SOUL_SOIL);

	public MiningDrill(String name, float attackDamageIn, float attackSpeedIn) {
		super(new Item.Properties().setNoRepair().addToolType(ToolType.PICKAXE, ItemTier.DIAMOND.getHarvestLevel()).addToolType(ToolType.SHOVEL, ItemTier.DIAMOND.getHarvestLevel()), name, attackDamageIn, attackSpeedIn);
	}

	@Override
	public boolean canMine(ItemStack itemstack) {
		return hasDrillBit(itemstack) && EnergyHandlerItemStackUtilities.getEnergyStored(itemstack) > 0;
	}

	/**
	 * When right clicked, open the drill UI.
	 */
	@Override
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		if (!world.isRemote && player.isSneaking()) {
			NetworkGUI.openGui((ServerPlayerEntity) player, new MiningDrillContainerProvider(item), buff -> {
				buff.writeInt(player.inventory.getSlotFor(item));
			});
			return ActionResult.resultSuccess(item);
		}
		return ActionResult.resultPass(item);
	}

	@Override
	protected void onBlocksMined(ItemStack stack, List<BlockPos> blocksMined, PlayerEntity player) {
		// Apply damage to the drill bit.
		if (!player.getEntityWorld().isRemote) {
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
				handler.getStackInSlot(0).damageItem(blocksMined.size(), player, (entity) -> {
					entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
					handler.getStackInSlot(0).shrink(1);
				});
			});

			// Use Power.
			EnergyHandlerItemStackUtilities.useEnergyFromItemstack(stack, 1, false);
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return hasDrillBit(stack);
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
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		int remainingCharge = EnergyHandlerItemStackUtilities.getEnergyStored(stack);
		int capacity = EnergyHandlerItemStackUtilities.getEnergyStorageCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		// Add the inventory.
		return new MiningDrillCapability(stack, 4, nbt);
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return super.getRGBDurabilityForDisplay(stack);// EnergyHandlerItemStackUtilities.getRGBDurabilityForDisplay(stack);
	}

	public boolean hasDrillBit(ItemStack stack) {
		// Get the inventory.
		IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

		// If null, return false.
		if (inventory == null) {
			return false;
		}

		// Check to make sure we have a drill bit, and its not broken.
		return !inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getDamage() < inventory.getStackInSlot(0).getMaxDamage();
	}

	@Override
	public Set<Block> getEffectiveBlocks() {
		return EFFECTIVE_ON;
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

	public class MiningDrillCapability implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
		protected final ItemStack owningItemStack;
		protected final int inventorySize;
		protected final CompoundNBT initialNbt;
		protected final ItemInventoryHandler inventory;

		public MiningDrillCapability(ItemStack owner, int size, @Nullable CompoundNBT nbt) {
			inventorySize = size;
			owningItemStack = owner;
			initialNbt = nbt;
			inventory = new ItemInventoryHandler("default", owner, inventorySize);

			if (nbt != null) {
				inventory.deserializeNBT(nbt);
			}
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == null) {
				return LazyOptional.empty();
			}

			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				return net.minecraftforge.common.util.LazyOptional.of(() -> inventory).cast();
			} else if (cap == CapabilityEnergy.ENERGY) {
				// This SHOULD BE CACHED.
				return LazyOptional.of(() -> new EnergyHandlerItemStack(owningItemStack, 1000, 1000, 1000)).cast();
			}
			return LazyOptional.empty();
		}

		@Override
		public CompoundNBT serializeNBT() {
			return inventory.serializeNBT();
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			inventory.deserializeNBT(nbt);
		}
	}

}