package theking530.staticcore.blockentity.components.multiblock.fixed;

import java.util.ArrayList;
import java.util.List;

public class FixedMultiblockPatternLayer {
	private List<String> rows;

	public FixedMultiblockPatternLayer() {
		rows = new ArrayList<>();
	}

	public String getRow(int i) {
		return rows.get(i);
	}

	public int getRowCount() {
		return rows.size();
	}

	public FixedMultiblockPatternLayer addRow(String row) {
		rows.add(row);
		return this;
	}

	@Override
	public String toString() {
		return "FixedMultiblockPatternLayer [rows=" + rows + "]";
	}
}
