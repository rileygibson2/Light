package light.general;

@FunctionalInterface
public interface Submitter<S> {
	public void submit(S s);
}
