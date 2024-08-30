package jackson.tasks;

/**
 * Task class for task representation.
 */
public abstract class Task {
    /* variables to store task information */
    private String taskName;
    private Boolean completed;

    /**
     * Constructs Task abstract class.
     * Used for subclasses to call only.
     * @param name String name of the task.
     */
    public Task(String name) {
        this.taskName = name;
        this.completed = false;
    }

    /**
     * Marks the task as completed.
     */
    public void mark() {
        this.completed = true;
    }

    /**
     * Reverts task to incomplete.
     */
    public void unmark() {
        this.completed = false;
    }

    /**
     * Returns status of task; true if complete, false otherwise.
     * @return String representation of complete or incomplete.
     */
    public String getStatus() {
        return this.completed ? "X" : " ";
    }

    /**
     * Returns name of task.
     * @return String representation of task name.
     */
    public String getName() {
        return this.taskName;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatus(), this.taskName);
    }
}
