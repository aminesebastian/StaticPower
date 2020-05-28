package theking530.staticpower.initialization;

import theking530.staticpower.Registry;
import theking530.staticpower.items.upgrades.BaseDigistoreCapacityUpgrade;
import theking530.staticpower.items.upgrades.BaseOutputMultiplierUpgrade;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseQuarryingUpgrade;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.BaseSpeedUpgrade;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;
import theking530.staticpower.items.upgrades.DigistoreVoidUpgrade;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.TeleportUpgrade;
import theking530.staticpower.utilities.Tier;

public class ModUpgrades {
	// Upgrades
	public static TeleportUpgrade TeleportUpgrade;
	public static ExperienceVacuumUpgrade ExperienceVacuumUpgrade;
	public static DigistoreVoidUpgrade DigistoreVoidUpgrade;

	public static BaseDigistoreCapacityUpgrade BasicDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade IronDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade GoldDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade LeadDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade ObsidianDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade StaticDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade EnergizedDigistoreCapacityUpgrade;
	public static BaseDigistoreCapacityUpgrade LumumDigistoreCapacityUpgrade;

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

	public static BaseQuarryingUpgrade StaticQuarryingUpgrade;
	public static BaseQuarryingUpgrade EnergizedQuarryingUpgrade;
	public static BaseQuarryingUpgrade LumumQuarryingUpgrade;

	public static void init() {
		Registry.preRegisterItem(TeleportUpgrade = new TeleportUpgrade("upgrade_teleport"));
		Registry.preRegisterItem(ExperienceVacuumUpgrade = new ExperienceVacuumUpgrade("upgrade_experience_vacuum"));
		Registry.preRegisterItem(DigistoreVoidUpgrade = new DigistoreVoidUpgrade("upgrade_digistore_void"));

		Registry.preRegisterItem(BasicDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_basic", Tier.BASIC));
		Registry.preRegisterItem(IronDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_iron", Tier.IRON));
		Registry.preRegisterItem(GoldDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_gold", Tier.GOLD));
		Registry.preRegisterItem(LeadDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_lead", Tier.LEAD));
		Registry.preRegisterItem(ObsidianDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_obsidian", Tier.OBSIDIAN));
		Registry.preRegisterItem(StaticDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumDigistoreCapacityUpgrade = new BaseDigistoreCapacityUpgrade("upgrade_digistore_capacity_lumum", Tier.LUMUM));

		Registry.preRegisterItem(BasicPowerUpgrade = new BasePowerUpgrade("upgrade_power_basic", Tier.BASIC));
		Registry.preRegisterItem(StaticPowerUpgrade = new BasePowerUpgrade("upgrade_power_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedPowerUpgrade = new BasePowerUpgrade("upgrade_power_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumPowerUpgrade = new BasePowerUpgrade("upgrade_power_lumum", Tier.LUMUM));

		Registry.preRegisterItem(BasicOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_basic", Tier.BASIC));
		Registry.preRegisterItem(StaticOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumOutputMultiplierUpgrade = new BaseOutputMultiplierUpgrade("upgrade_output_multiplier_lumum", Tier.LUMUM));

		Registry.preRegisterItem(BasicSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_basic", Tier.BASIC));
		Registry.preRegisterItem(StaticSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumSpeedUpgrade = new BaseSpeedUpgrade("upgrade_speed_lumum", Tier.LUMUM));

		Registry.preRegisterItem(BasicRangeUpgrade = new BaseRangeUpgrade("upgrade_range_basic", Tier.BASIC));
		Registry.preRegisterItem(StaticRangeUpgrade = new BaseRangeUpgrade("upgrade_range_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedRangeUpgrade = new BaseRangeUpgrade("upgrade_range_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumRangeUpgrade = new BaseRangeUpgrade("upgrade_range_lumum", Tier.LUMUM));

		Registry.preRegisterItem(BasicTankUpgrade = new BaseTankUpgrade("upgrade_tank_basic", Tier.BASIC));
		Registry.preRegisterItem(StaticTankUpgrade = new BaseTankUpgrade("upgrade_tank_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedTankUpgrade = new BaseTankUpgrade("upgrade_tank_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumTankUpgrade = new BaseTankUpgrade("upgrade_tank_lumum", Tier.LUMUM));

		Registry.preRegisterItem(StaticQuarryingUpgrade = new BaseQuarryingUpgrade("upgrade_quarrying_static", Tier.STATIC));
		Registry.preRegisterItem(EnergizedQuarryingUpgrade = new BaseQuarryingUpgrade("upgrade_quarrying_energized", Tier.ENERGIZED));
		Registry.preRegisterItem(LumumQuarryingUpgrade = new BaseQuarryingUpgrade("upgrade_quarrying_lumum", Tier.LUMUM));
	}
}
