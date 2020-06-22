package theking530.staticpower.tileentities.powered.basicfarmer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.MelonBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityBasicFarmer extends TileEntityMachine {
	public static final int DEFAULT_RANGE = 2;

	private int range;
	private int blocksFarmedPerTick;
	private int growthBonusChance;
	private BlockPos currentCoordinate;
	private Random rand;
	private ArrayList<ItemStack> farmedStacks;
	private boolean shouldDrawRadiusPreview;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidTankComponent fluidTankComponent;
	private final int processingCost;

	public TileEntityBasicFarmer() {
		super(ModTileEntityTypes.BASIC_FARMER);
		this.disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 9, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::processingCompleted));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory, this::inputServoFilter, 0));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory, 0, 1, 2, 3, 4, 5, 6, 7, 8));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, MachineSideMode.Input));
		range = DEFAULT_RANGE;
		shouldDrawRadiusPreview = false;
		blocksFarmedPerTick = 1;
		growthBonusChance = 0;
		processingCost = 10;
		currentCoordinate = getStartingCoord();
		rand = new Random();
		farmedStacks = new ArrayList<ItemStack>();
	}

	@Override
	public void process() {
		if (!canFarm()) {
			processingComponent.pauseProcessing();
		} else {
			if (processingComponent.isProcessing()) {
				processingComponent.continueProcessing();
			} else {
				processingComponent.startProcessing();
			}
			energyStorage.getStorage().extractEnergy(processingCost, false);
		}
	}

	protected boolean processingCompleted() {
		if (farmedStacks.size() <= 1 && canFarm()) {
			for (int i = 0; i < blocksFarmedPerTick; i++) {
				incrementPosition();
				checkFarmingPlot(currentCoordinate);
			}
			fluidTankComponent.drain(1 * blocksFarmedPerTick, FluidAction.EXECUTE);
		} else {
			if (!getWorld().isRemote) {
				for (int i = farmedStacks.size() - 1; i >= 0; i--) {
					ItemStack insertedStack = InventoryUtilities.insertItemIntoInventory(outputInventory, farmedStacks.get(i), 0, 8, false);
					if (insertedStack == ItemStack.EMPTY) {
						farmedStacks.remove(i);
					} else {
						farmedStacks.set(i, insertedStack);
					}
				}
			}
		}
		markTileEntityForSynchronization();
		return true;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentCoordinate = BlockPos.fromLong(nbt.getLong("current_position"));
		if (nbt.contains("FARMED_COUNT")) {
			for (int i = 0; i < nbt.getInt("FARMED_COUNT"); i++) {
				farmedStacks.add(ItemStack.read(nbt.getCompound("FARMED" + i)));
			}
		}
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putLong("current_position", currentCoordinate.toLong());
		nbt.putInt("FARMED_COUNT", farmedStacks.size());
		for (int i = 0; i < farmedStacks.size(); i++) {
			nbt.put("FARMED" + i, farmedStacks.get(i).write(new CompoundNBT()));
		}
		return nbt;
	}

	public int getRadius() {
		return range;
	}

	public int getGrowthBonus() {
		return growthBonusChance;
	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		shouldDrawRadiusPreview = shouldDraw;
	}

	private void incrementPosition() {
		if (currentCoordinate == getEndingCoord()) {
			currentCoordinate = getStartingCoord();
			return;
		}
		if (getAdjustedCurrentPos().getX() >= getAdjustedEndingPos().getX() - 1) {
			currentCoordinate = new BlockPos(getStartingCoord().getX(), currentCoordinate.getY(), currentCoordinate.getZ() + getZDirection());
		}
		if (getAdjustedCurrentPos().getZ() >= getAdjustedEndingPos().getZ() + 1) {
			currentCoordinate = getStartingCoord();
		}
		if (getAdjustedCurrentPos().getX() <= getAdjustedEndingPos().getX()) {
			currentCoordinate = currentCoordinate.add(getXDirection(), 0, 0);
			return;
		}
	}

	private BlockPos getAdjustedCurrentPos() {
		BlockPos temp1 = currentCoordinate.subtract(getStartingCoord());
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.abs(temp1.getY()), Math.abs(temp1.getZ()));
		return absPos;
	}

	private BlockPos getAdjustedEndingPos() {
		BlockPos temp1 = getEndingCoord().subtract(getStartingCoord());
		BlockPos absPos = new BlockPos(Math.abs(temp1.getX()), Math.abs(temp1.getY()), Math.abs(temp1.getZ()));
		return absPos;
	}

	private BlockPos getEndingCoord() {
		return new BlockPos(pos.getX() + range + 1, pos.getY(), pos.getZ() + range);
	}

	private BlockPos getStartingCoord() {
		return new BlockPos(pos.getX() - range - 1, pos.getY(), pos.getZ() - range);
	}

	private int getXDirection() {
		if (getStartingCoord().getX() > getEndingCoord().getX()) {
			return -1;
		} else {
			return 1;
		}
	}

	private int getZDirection() {
		if (getStartingCoord().getZ() > getEndingCoord().getZ()) {
			return -1;
		} else {
			return 1;
		}
	}

	public boolean canFarm() {
		if (energyStorage.getStorage().getEnergyStored() >= processingCost * blocksFarmedPerTick && !inputInventory.getStackInSlot(0).isEmpty()
				&& inputInventory.getStackInSlot(0).getItem() instanceof HoeItem && !inputInventory.getStackInSlot(1).isEmpty() && inputInventory.getStackInSlot(1).getItem() instanceof AxeItem) {
			if (fluidTankComponent.getFluid() != null) {
				if (fluidTankComponent.getFluid().getAmount() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	public void useHoe() {
		if (inputInventory.getStackInSlot(0) != ItemStack.EMPTY && inputInventory.getStackInSlot(0).getItem() instanceof HoeItem) {
			if (inputInventory.getStackInSlot(0).attemptDamageItem(1, rand, null)) {
				inputInventory.setStackInSlot(0, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
		}
	}

	public void useAxe() {
		if (inputInventory.getStackInSlot(1) != ItemStack.EMPTY && inputInventory.getStackInSlot(1).getItem() instanceof AxeItem) {
			if (inputInventory.getStackInSlot(1).attemptDamageItem(1, rand, null)) {
				inputInventory.setStackInSlot(1, ItemStack.EMPTY);
				getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}
		}
	}

	public boolean checkFarmingPlot(BlockPos pos) {
		boolean grown = false;
		if (!getWorld().isRemote) {
			grown = growCrop(pos);
		}
		if (grown) {
			getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		}
		getWorld().addParticle(ParticleTypes.TOTEM_OF_UNDYING, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
		if (isFarmableBlock(pos)) {
			harvestGenericCrop(pos);
			harvestSugarCane(pos);
			harvestCactus(pos);
			harvestNetherWart(pos);
			harvestMelonOrPumpkin(pos);
		}
		return true;
	}

	public boolean isFarmableBlock(BlockPos pos) {
		if (getWorld().getBlockState(pos) == null) {
			return false;
		}
		if (getWorld().getBlockState(pos).getBlock() instanceof CropsBlock) {
			return true;
		}
		if (getWorld().getBlockState(pos).getBlock() instanceof SugarCaneBlock) {
			return true;
		}
		if (getWorld().getBlockState(pos).getBlock() instanceof CactusBlock) {
			return true;
		}
		if (getWorld().getBlockState(pos).getBlock() instanceof NetherWartBlock) {
			return true;
		}
		if (getWorld().getBlockState(pos).getBlock() instanceof MelonBlock) {
			return true;
		}
		if (getWorld().getBlockState(pos).getBlock() instanceof PumpkinBlock) {
			return true;
		}
		return false;
	}

	public boolean harvestGenericCrop(BlockPos pos) {
		if (getWorld().getBlockState(pos).getBlock() instanceof CropsBlock) {
			CropsBlock tempCrop = (CropsBlock) getWorld().getBlockState(pos).getBlock();
			if (tempCrop.isMaxAge(getWorld().getBlockState(pos))) {
				getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 1.0F,
						1.0F);
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
				if (!getWorld().isRemote) {
					farmedStacks.addAll(getBlockDrops(pos));
					getWorld().setBlockState(pos, tempCrop.withAge(0), 3);
					useHoe();
					if (farmedStacks.size() > 0) {
						for (int i = farmedStacks.size() - 1; i >= 0; i--) {
							if (farmedStacks.get(i).getItem() instanceof IPlantable) {
								farmedStacks.remove(i);
								break;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean harvestSugarCane(BlockPos pos) {
		if (getWorld().getBlockState(pos.add(0, 1, 0)).getBlock() instanceof SugarCaneBlock) {
			if (!getWorld().isRemote) {
				farmedStacks.addAll(getBlockDrops(pos.add(0, 1, 0)));
				getWorld().setBlockState(pos.add(0, 1, 0), Blocks.AIR.getDefaultState());
				getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 2);
				useAxe();

				if (getWorld().getBlockState(pos.add(0, 2, 0)).getBlock() instanceof SugarCaneBlock) {
					farmedStacks.addAll(getBlockDrops(pos.add(0, 2, 0)));
					getWorld().setBlockState(pos.add(0, 2, 0), Blocks.AIR.getDefaultState());
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 2);
					useAxe();
				}
				getWorld().playSound(null, pos,
						getWorld().getBlockState(pos.add(0, 1, 0)).getBlock().getSoundType(getWorld().getBlockState(pos.add(0, 1, 0)), world, pos.add(0, 1, 0), null).getBreakSound(),
						SoundCategory.BLOCKS, 1.0F, 1.0F);
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.add(0, 1, 0).getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
				return true;
			}
		}
		return false;
	}

	public boolean harvestCactus(BlockPos pos) {
		if (getWorld().getBlockState(pos.add(0, 1, 0)).getBlock() instanceof CactusBlock) {
			if (!getWorld().isRemote) {
				farmedStacks.addAll(getBlockDrops(pos.add(0, 1, 0)));
				getWorld().setBlockState(pos.add(0, 1, 0), Blocks.AIR.getDefaultState());
				useAxe();
				getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);

				if (getWorld().getBlockState(pos.add(0, 2, 0)).getBlock() instanceof CactusBlock) {
					farmedStacks.addAll(getBlockDrops(pos.add(0, 2, 0)));
					getWorld().setBlockState(pos.add(0, 2, 0), Blocks.AIR.getDefaultState());
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
					useAxe();
				}
				getWorld().playSound(null, pos,
						getWorld().getBlockState(pos.add(0, 1, 0)).getBlock().getSoundType(getWorld().getBlockState(pos.add(0, 1, 0)), world, pos.add(0, 1, 0), null).getBreakSound(),
						SoundCategory.BLOCKS, 1.0F, 1.0F);
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.add(0, 1, 0).getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
				return true;
			}
		}
		return false;
	}

	public boolean harvestMelonOrPumpkin(BlockPos pos) {
		if (getWorld().getBlockState(pos).getBlock() instanceof MelonBlock || getWorld().getBlockState(pos).getBlock() instanceof PumpkinBlock) {
			if (!getWorld().isRemote) {
				farmedStacks.addAll(getBlockDrops(pos));
				getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
				useAxe();
				getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
			}
			getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
			getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			return true;
		}
		return false;
	}

	public boolean harvestNetherWart(BlockPos pos) {
		if (getWorld().getBlockState(pos).getBlock() instanceof NetherWartBlock) {
			NetherWartBlock tempNetherwart = (NetherWartBlock) getWorld().getBlockState(pos).getBlock();
			if (tempNetherwart.getPlant(getWorld(), pos).get(NetherWartBlock.AGE) >= 3) {
				getWorld().playSound(null, pos, getWorld().getBlockState(pos).getBlock().getSoundType(getWorld().getBlockState(pos), world, pos, null).getBreakSound(), SoundCategory.BLOCKS, 1.0F,
						1.0F);
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
				if (!getWorld().isRemote) {
					farmedStacks.addAll(getBlockDrops(pos));
					getWorld().setBlockState(pos, Blocks.NETHER_WART.getDefaultState(), 2);
					useHoe();
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
					if (farmedStacks.size() > 0) {
						for (int i = farmedStacks.size() - 1; i >= 0; i--) {
							if (farmedStacks.get(i).getItem() instanceof IPlantable) {
								farmedStacks.remove(i);
								break;
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public boolean growCrop(BlockPos pos) {
		if (rand.nextInt(100) <= growthBonusChance) {
			if (getWorld().getBlockState(pos) != null && getWorld().getBlockState(pos).getBlock() instanceof IGrowable) {
				IGrowable tempCrop = (IGrowable) getWorld().getBlockState(pos).getBlock();
				if (tempCrop.canGrow(getWorld(), pos, getWorld().getBlockState(pos), true)) {
					if (!world.isRemote) {
						tempCrop.grow((ServerWorld) getWorld(), rand, pos, getWorld().getBlockState(pos));
						getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 1 | 2);
					}
					return true;
				}
			}
		}
		return false;
	}

	public List<ItemStack> getBlockDrops(BlockPos pos) {
		if (!world.isRemote) {
			NonNullList<ItemStack> output = NonNullList.create();
			output.addAll(Block.getDrops(getWorld().getBlockState(pos), (ServerWorld) getWorld(), pos, null));
			return output;
		}
		return Collections.emptyList();
	}

	private boolean inputServoFilter(ItemStack stack) {
		return true;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerBasicFarmer(windowId, inventory, this);
	}
}
