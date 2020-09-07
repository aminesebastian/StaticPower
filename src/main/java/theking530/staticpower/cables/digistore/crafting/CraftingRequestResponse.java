package theking530.staticpower.cables.digistore.crafting;

public class CraftingRequestResponse {
	private final long id;
	private final int craftableAmount;

	public CraftingRequestResponse(long id, int craftableAmount) {
		this.id = id;
		this.craftableAmount = craftableAmount;
	}

	public long getId() {
		return id;
	}

	public int getCraftableAmount() {
		return craftableAmount;
	}

}
