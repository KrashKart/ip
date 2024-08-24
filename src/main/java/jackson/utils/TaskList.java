package jackson.utils;

import java.util.ArrayList;

import jackson.exceptions.OutOfListException;
import jackson.tasks.Task;

/**
 * Class used to store tasks.
 */
public class TaskList {
    /* ArrayList to store tasks */
    private ArrayList<Task> tasks;

    /**
     * Constructor for task list.
     * @param expectedSize expected number of tasks to store
     */
    public TaskList(int expectedSize) {
        this.tasks = new ArrayList<>(expectedSize);
    }

    /**
     * Returns number of tasks in the list
     * @return integer containing how many tasks are in the list
     */
    public int getSize() {
        return this.tasks.size();
    }

    /**
     * Adds task to the list and prints list adding message.
     * @param task {@code Task} object to be added
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * Gets a specified task according to index
     * @param index index of task (from 0) to get
     * @return {@code Task} object at index {@code index}
     */
    public Task getTask(int index) {
        return this.tasks.get(index);
    }

    /**
     * Deletes Task from the list at specified index.
     * @param index Integer index to delete at
     * @return {@code Task} object that was deleted
     * @throws OutOfListException Thrown if invalid index is given, contains current task size
     */
    public Task deleteTask(int index) throws OutOfListException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new OutOfListException(String.valueOf(this.tasks.size()));
        }
        Task curr = this.tasks.remove(index);
        return curr;
    }

    /**
     * Marks task as completed at specified index.
     * @param index Integer index to mark task at
     * @return {@code Task} object that was marked
     * @throws OutOfListException Thrown if invalid index is given, contains current task size
     */
    public Task mark(int index) throws OutOfListException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new OutOfListException(String.valueOf(this.tasks.size()));
        }
        Task curr = this.tasks.get(index);
        curr.mark();
        return curr;
    }

    /**
     * Returns tasks that have names that contain keywords
     * @param keywords String of keyword(s) to search for
     * @return {@code ArrayList} of tasks that match the keyword
     */
    public ArrayList<Task> findTasks(String keywords) {
        ArrayList<Task> filtered = new ArrayList<>(this.tasks);
        filtered.removeIf(x -> !x.getName().contains(keywords));
        return filtered;
    }

    /**
     * Unmarks task as completed at specified index.
     * @param index Integer index to unmark task at
     * @return {@code Task} object that was unmarked
     * @throws OutOfListException Thrown if invalid index is given, contains current task size
     */
    public Task unmark(int index) throws OutOfListException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new OutOfListException(String.valueOf(this.tasks.size()));
        }
        Task curr = this.tasks.get(index);
        curr.unmark();
        return curr;
    }

    /**
     * Returns string representation of the list.
     * Includes task index, task type and whether it is marked or unmarked.
     */
    @Override
    public String toString() {
        if (this.tasks.isEmpty()) {
            return "Nothing in list lah!";
        } else {
            StringBuilder output = new StringBuilder("Your list here leh!\n");
            Task curr;
            for (int i = 0; i < tasks.size(); i++) {
                curr = tasks.get(i);
                output.append(String.format("%d. %s\n", i + 1, curr));
            }
            return output.toString().strip();
        }
    }
}
