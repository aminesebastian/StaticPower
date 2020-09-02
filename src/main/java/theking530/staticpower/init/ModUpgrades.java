package theking530.staticpower.init;

import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.upgrades.AcceleratorUpgrade;
import theking530.staticpower.items.upgrades.BaseCentrifugeUpgrade;
import theking530.staticpower.items.upgrades.BaseOutputMultiplierUpgrade;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseQuarryingUpgrade;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;
import theking530.staticpower.items.upgrades.CraftingUpgrade;
import theking530.staticpower.items.upgrades.DigistoreVoidUpgrade;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.StackUpgrade;
import theking530.staticpower.items.upgrades.TeleportUpgrade;

public class ModUpgrades {
	// Upgrades
	public static TeleportUpgrade TeleportUpgrade;
	public static ExperienceVacuumUpgrade ExperienceVacuumUpgrade;
	public static DigistoreVoidUpgrade DigistoreVoidUpgrade;
	public static StackUpgrade StackUpgrade;
	public static AcceleratorUpgrade AcceleratorUpgrade;
	public static CraftingUpgrade CraftingUpgrade;

	public static BasePowerUpgrade BasicPowerUpgrade;
	public static BasePowerUpgrade StaticPowerUpgrade;
	public static BasePowerUpgrade EnergizedPowerUpgrade;
	public static BasePowerUpgrade LumumPowerUpgrade;

	public static BaseOutputMultiplierUpgrade BasicOutputMultiplierUpgrade;
	public static BaseOutputMultiplierUpgrade StaticOutputMultiplierUpgrade;
	public static BaseOutputMultiplierUpgrade EnergizedOutputMultiplierUpgrade;
	public static BaseOutputMultiplierUpgrade LumumOutputMultiplierUpgrade;

	public static BaseSpeedUpgrade BasicSpeedUpgrade;
	public static BaseSpeedUpgrade StaticSpeedUpgrade;
	public static BaseSpeedUpgrade EnergizedSpeedUpgrade;
	public static BaseSpeedUpgrade LumumSpeedUpgrade;

	public static BaseRangeUpgrade BasicRangeUpgrade;
	public static BaseRangeUpgrade StaticRangeUpgrade;
	public static BaseRangeUpgrade EnergizedRangeUpgrade;
	public static BaseRangeUpgrade LumumRangeUpgrade;

	public static BaseTankUpgrade BasicTankUpgrade;
	public static BaseTankUpgrade StaticTankUpgrade;
	public static BaseTankUpgrade EnergizedTankUpgrade;
	public static BaseTankUpgrade LumumTankUpgrade;

	public static BaseCentrifugeUpgrade BasicCentrifugeUpgrade;
	public static BaseCentrifugeUpgrade StaticCentrifugeUpgrade;
	public static BaseCentrifugeUpgrade EnergizedCentrifugeUpgrade;
	public static BaseCentrifugeUpgrade LumumCentrifugeUpgrade;

	public static BaseQuarryingUpgrade StaticQuarryingUpgrade;
	public static BaseQuarryingUpgrade EnergizedQuarryingUpgrade;
	public static BaseQuarryingUpgrade LumumQuarryingUpgrade;

	public static void init() {
		StaticPowerRegistry.preRegisterItem(TeleportUpgrade = new TeleportUpgrade("upgrade_teleport"));
		StaticPowerRegistry.preRegisterItem(ExperienceVacuumUpgrade = new ExperienceVacuumUpgrade("upgrade_experience_vacuum"));
		StaticPowerRegistry.preRegisterItem(DigistoreVoidUpgrade = new DigistoreVoidUpgrade("upgrade_digistore_void"));
		StaticPowerRegistry.preRegisterItem(StackUpgrade = new StackUpgrade("upgrade_stack"));
		StaticPowerRegistry.preRegisterItem(AcceleratorUpgrade = new AcceleratorUpgrade("upgrade_accelerator"));
		StaticPowerRegistry.preRegisterItem(CraftingUpgrade = new CraftingUpgrade("upgrade_crafting"));

		StaticPowerRegistry.preRegisterItem(BasicPowerUpgrade = new BasePowerUpgrade("upgrade_power_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(StaticPowerUpgrade = new BasePowerUpgrade("upgrade_power_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedPowerUpgrade = new BasePowerUpgrade("upgrade_power_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumPowerUpgrade = new BasePowerUpgrade("upgrade_power_lumum", StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(BasicOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(StaticOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_lumum", StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(BasicSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(StaticSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_lumum", StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(BasicRangeUpgrade = new BaseRangeUpgrade("upgrade_range_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(StaticRangeUpgrade = new BaseRangeUpgrade("upgrade_range_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedRangeUpgrade = new BaseRangeUpgrade("upgrade_range_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumRangeUpgrade = new BaseRangeUpgrade("upgrade_range_lumum", StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(BasicTankUpgrade = new BaseTankUpgrade("upgrade_tank_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(StaticTankUpgrade = new BaseTankUpgrade("upgrade_tank_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedTankUpgrade = new BaseTankUpgrade("upgrade_tank_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumTankUpgrade = new BaseTankUpgrade("upgrade_tank_lumum", StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(BasicCentrifugeUpgrade = new BaseCentrifugeUpgrade("upgrade_centrifuge_basic", StaticPowerTiers.BASIC));
		StaticPowerRegistry.preRegisterItem(StaticCentrifugeUpgrade = new BaseCentrifugeUpgrade("upgrade_centrifuge_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedCentrifugeUpgrade = new BaseCentrifugeUpgrade("upgrade_centrifuge_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumCentrifugeUpgrade = new BaseCentrifugeUpgrade("upgrade_centrifuge_lumum", StaticPowerTiers.LUMUM));

		StaticPowerRegistry.preRegisterItem(StaticQuarryingUpgrade = new BaseQuarryingUpgrade("upgrade_quarrying_static", StaticPowerTiers.STATIC));
		StaticPowerRegistry.preRegisterItem(EnergizedQuarryingUpgrade = new BaseQuarryingUpgrade("upgrade_quarrying_energized", StaticPowerTiers.ENERGIZED));
		StaticPowerRegistry.preRegisterItem(LumumQuarryingUpgrade = new BaseQuarryingUpgrade("upgrade_quarrying_lumum", StaticPowerTiers.LUMUM));
	}
}
