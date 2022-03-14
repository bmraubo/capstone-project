package site.bmraubo.todo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTaskList {

    @Test
    void generateTaskListTest() {
        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();

        Assertions.assertNotNull(localMemoryTaskList.taskList);
    }

    @Test
    void addTaskTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        Task task = new Task(todoRequest);
        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();

        localMemoryTaskList.addTask(task);

        Assertions.assertEquals(task, localMemoryTaskList.taskList.get(1));

    }

    @Test
    void findTaskByID() {
        String todoRequest = "{\"task\":\"a new task\"}";

        Task task = new Task(todoRequest);
        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();

        localMemoryTaskList.addTask(task);

        Assertions.assertEquals(todoRequest, localMemoryTaskList.viewTaskByID(1).taskInfo);
    }

    @Test
    void removeTaskTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        Task task = new Task(todoRequest);
        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();

        localMemoryTaskList.addTask(task);

        localMemoryTaskList.removeTask(1);

        Assertions.assertNull(localMemoryTaskList.viewTaskByID(1));
    }
}
