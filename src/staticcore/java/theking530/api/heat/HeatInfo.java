package theking530.api.heat;

public record HeatInfo(float mass, float temperature, float conductivity, float specificHeat, IHeatStorage heatStorage,
		HeatInfoType type) {

	public HeatInfo(IHeatStorage storage) {
		this(storage.getMass(), storage.getTemperature(), storage.getConductivity(), storage.getSpecificHeat(),
				storage, HeatInfoType.BLOCK);
	}

	public HeatInfo(float mass, float temperature, float conductivity, float specificHeat, HeatInfoType type) {
		this(mass, temperature, conductivity, specificHeat, null, type);
	}
}