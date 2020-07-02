package theking530.common.utilities;

@FunctionalInterface
public interface QuadFunction<T, U, S, Q, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @param u the second function argument
	 * @param s the third function argument
	 * @param q the fourth function argument
	 * @return the function result
	 */
	R apply(T t, U u, S s, Q q);
}