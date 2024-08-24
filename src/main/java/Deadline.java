/**
 * Deadline class containing name and deadline.
 */
public class Deadline extends Task {
    private Temporal deadline;

    public Deadline(String name, String deadline) {
        super(name);
        this.deadline = new Temporal(deadline);
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.deadline);
    }
}
