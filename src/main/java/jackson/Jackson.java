package jackson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import jackson.enums.Actions;
import jackson.enums.Commands;
import jackson.exceptions.DuplicatedTaskException;
import jackson.exceptions.InvalidArgumentException;
import jackson.exceptions.OutOfListException;
import jackson.exceptions.SyntaxException;
import jackson.exceptions.UnsupportedCommandException;
import jackson.tasks.Deadline;
import jackson.tasks.Event;
import jackson.tasks.Task;
import jackson.tasks.Todo;
import jackson.utils.Parser;
import jackson.utils.Response;
import jackson.utils.Storage;
import jackson.utils.TaskList;
import jackson.utils.Ui;

/**
 * Main class for the chatbot.
 */
public class Jackson {

    // Expected number of tasks to store
    private static final int EXPECTED_SIZE = 100;

    // Path to save list data
    private static final String PATH = "data/data.txt";

    // Path list to read secret text from
    private static final String SECRET_TEXT = "TWFyaSBraXRhIHJha3lhdCBTaW5nYXB1cmEKU2FtYS1" +
            "zYW1hIG1lbnVqdSBiYWhhZ2lhCkNpdGEtY2l0YSBraXRhIHlhbmcgbXVsaWEKQmVyamF5YSBTaW5n" +
            "YXB1cmEKTWFyaWxhaCBraXRhIGJlcnNhdHUKRGVuZ2FuIHNlbWFuZ2F0IHlhbmcgYmFydQpTZW11Y" +
            "SBraXRhIGJlcnNlcnUKTWFqdWxhaCBTaW5nYXB1cmEKTWFqdWxhaCBTaW5nYXB1cmE=";

    // Stores previous command type for css style changing
    private Commands.CommandType commandType;

    // Instance variables for main loop
    private TaskList taskList;
    private Ui ui;
    private Storage storage;

    /**
     * Constructs Jackson instance.
     */
    public Jackson() {
        this.taskList = new TaskList(EXPECTED_SIZE);
        this.ui = new Ui();
        this.storage = new Storage(PATH);
        this.commandType = Commands.CommandType.INTRO;
    }

    /**
     * Prints secret text.
     * Due to jar issues, this cannot be read from a file.
     * @return String response.
     */

    public String printSecret() {
        // decode from base64 to utf8
        byte[] decoded = Base64.getDecoder().decode(SECRET_TEXT);

        // print secret msg!
        return new String(decoded);
    }

    /**
     * Runs the main loop of the chatbot.
     */
    public String getResponse(String input) {
        // Variables for main loop
        Task task;
        Response response;
        Actions.ActionType action;
        Matcher matcher;
        ArrayList<Task> tasks;

        int index;
        boolean isAscending;
        String output;
        System.out.println(input); // for debugging purposes

        // main loop starts
        try {
            // first parse input and get response (or throw error here)
            response = Parser.parse(input);
            action = response.getAction();
            matcher = response.getMatcher();

            // decide what action to take based on action object received from parser
            switch (action) {
            case LIST:
                output = this.ui.printList(this.taskList);
                this.commandType = Commands.CommandType.NORMAL;
                break;
            case TODO:
                task = new Todo(matcher.group(1));
                this.taskList.addTask(task);
                output = this.ui.printAfterAddList(task, this.taskList);
                this.commandType = Commands.CommandType.LIST;
                break;
            case DEADLINE:
                task = new Deadline(matcher.group(1), matcher.group(2));
                this.taskList.addTask(task);
                output = this.ui.printAfterAddList(task, this.taskList);
                this.commandType = Commands.CommandType.LIST;
                break;
            case EVENT:
                task = new Event(matcher.group(1), matcher.group(2), matcher.group(3));
                this.taskList.addTask(task);
                output = this.ui.printAfterAddList(task, this.taskList);
                this.commandType = Commands.CommandType.LIST;
                break;
            case MARK:
                index = Integer.parseInt(matcher.group(1)) - 1;
                task = this.taskList.mark(index);
                output = this.ui.printAfterMark(task);
                this.commandType = Commands.CommandType.TASK;
                break;
            case UNMARK:
                index = Integer.parseInt(matcher.group(1)) - 1;
                task = this.taskList.unmark(index);
                output = this.ui.printAfterUnmark(task);
                this.commandType = Commands.CommandType.TASK;
                break;
            case DELETE:
                index = Integer.parseInt(matcher.group(1)) - 1;
                task = this.taskList.deleteTask(index);
                output = this.ui.printAfterDeleteList(task, this.taskList);
                this.commandType = Commands.CommandType.LIST;
                break;
            case FIND:
                tasks = this.taskList.findTasks(matcher.group(1));
                output = this.ui.printAfterFindList(tasks, matcher.group(1));
                this.commandType = Commands.CommandType.LIST;
                break;
            case SORT:
                isAscending = matcher.group(2) == null || matcher.group(2).equals("/a");
                this.taskList.sort(matcher.group(1), isAscending);
                output = this.ui.printSortedList(this.taskList);
                this.commandType = Commands.CommandType.LIST;
                break;
            case HELP:
                output = matcher.group(1) == null
                        ? this.ui.printCommandList()
                        : this.ui.printFormatGuide(matcher.group(1));
                this.commandType = Commands.CommandType.NORMAL;
                break;
            case BYE:
                output = this.storage.save(this.taskList);
                this.commandType = Commands.CommandType.EXIT;
                break;
            case SECRET:
                output = this.printSecret();
                this.commandType = Commands.CommandType.SECRET;
                break;
            case INVALID:
                throw new UnsupportedCommandException(input);
            default:
                output = "Unknown error! Contact the developer...\n";
                break;
            }

            // save task list to storage after every command
            this.storage.save(this.taskList);

        } catch (UnsupportedCommandException e) {
            // if user input not recognised, print command list
            output = this.ui.printUnrecognizedMessage();
            this.commandType = Commands.CommandType.ERROR;
        } catch (SyntaxException e) {
            // if the user input is in the wrong format for the command, print format guide
            output = this.ui.printWrongFormat(e.getMessage());
            this.commandType = Commands.CommandType.ERROR;
        } catch (OutOfListException e) {
            // if user inputs an invalid index for mark/unmark/delete, print index guide
            output = this.ui.printIndexGuide(this.taskList);
            this.commandType = Commands.CommandType.ERROR;
        } catch (DuplicatedTaskException e) {
            // if user tries to add task with a name that already exists in taskList, print de-conflict advice
            output = this.ui.printDeconflictAdvice(e.getMessage());
            this.commandType = Commands.CommandType.ERROR;
        } catch (InvalidArgumentException e) {
            output = this.ui.printInvalidDates();
            this.commandType = Commands.CommandType.ERROR;
        } catch (IOException e) {
            output = this.ui.printFileIssue();
            this.commandType = Commands.CommandType.ERROR;
        } catch (Exception e) {
            // some other error unaccounted for, print generic warning
            // here we pass an error so the stack trace can be extracted
            output = this.ui.printUnknownError(e);
            this.commandType = Commands.CommandType.ERROR;
        }

        return output;
    }

    /**
     * Returns welcome message.
     * Used for GUI.
     * @return String representation of welcome message.
     */
    public String start() {
        return this.ui.printWelcome();
    }

    /**
     * Loads task list from save file (if it exists).
     * Used for GUI.
     * @return String representation of success or failure of loading.
     */
    public String load() {
        return this.storage.load(this.taskList);
    }

    /**
     * Returns goodbye String.
     * Used for GUI.
     * @return String representation of goodbye.
     */
    public String sayGoodbye() {
        return this.ui.printGoodbye();
    }

    /**
     * Returns last executed commandType for chatbot text box drawing.
     * Used for GUI.
     * @return {@code Commands.CommandType} of last executed command.
     */
    public Commands.CommandType getCommandType() {
        return this.commandType;
    }
}
