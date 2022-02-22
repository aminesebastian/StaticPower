package theking530.staticpower.data;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;

public class JsonUtilities {
	public static String nbtToPrettyJson(CompoundTag tag) {
		return new StringTagVisitor().visit(tag);
	}

	public static class SDStringTagVisitor extends StringTagVisitor {
		private final StringBuilder builder = new StringBuilder();

		public String visit(Tag p_178188_) {
			p_178188_.accept(this);
			return this.builder.toString();
		}

		public void visitString(StringTag p_178186_) {
			this.builder.append(StringTag.quoteAndEscape(p_178186_.getAsString()));
		}

		public void visitByteArray(ByteArrayTag p_178162_) {
			this.builder.append("[");
			byte[] abyte = p_178162_.getAsByteArray();

			for (int i = 0; i < abyte.length; ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append((int) abyte[i]).append('B');
			}

			this.builder.append(']');
		}

		public void visitIntArray(IntArrayTag p_178174_) {
			this.builder.append("[");
			int[] aint = p_178174_.getAsIntArray();

			for (int i = 0; i < aint.length; ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append(aint[i]);
			}

			this.builder.append(']');
		}

		public void visitLongArray(LongArrayTag p_178180_) {
			this.builder.append("[");
			long[] along = p_178180_.getAsLongArray();

			for (int i = 0; i < along.length; ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append(along[i]).append('L');
			}

			this.builder.append(']');
		}

		public void visitList(ListTag p_178178_) {
			this.builder.append('[');

			for (int i = 0; i < p_178178_.size(); ++i) {
				if (i != 0) {
					this.builder.append(',');
				}

				this.builder.append((new SDStringTagVisitor()).visit(p_178178_.get(i)));
			}

			this.builder.append(']');
		}

		public void visitCompound(CompoundTag p_178166_) {
			this.builder.append('{');
			List<String> list = Lists.newArrayList(p_178166_.getAllKeys());
			Collections.sort(list);

			for (String s : list) {
				if (this.builder.length() != 1) {
					this.builder.append(',');
				}

				this.builder.append(handleEscape(s)).append(':').append((new SDStringTagVisitor()).visit(p_178166_.get(s)));
			}

			this.builder.append('}');
		}

		public void visitEnd(EndTag p_178170_) {
			this.builder.append("END");
		}
	}
}
