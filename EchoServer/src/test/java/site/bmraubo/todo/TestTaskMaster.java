package site.bmraubo.todo;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTaskMaster {

    @Test
    void openTaskListTest() {
        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(localMemoryTaskList);

        Assertions.assertEquals(localMemoryTaskList, taskMaster.taskList);
    }

    @Test
    void addTaskTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(localMemoryTaskList);
        taskMaster.addTask(todoRequest);

        Assertions.assertEquals(todoRequest, taskMaster.viewTask(1).taskInfo);
    }

    @Test
    void viewTaskTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(localMemoryTaskList);
        taskMaster.addTask(todoRequest);

        Assertions.assertEquals(todoRequest, taskMaster.viewTask(1).taskInfo);
    }

    @Test
    void updateTaskTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(localMemoryTaskList);
        taskMaster.addTask(todoRequest);

        JSONObject updatedTask = new JSONObject("{\"task\":\"an updated task\"}");
        taskMaster.updateTask(1, updatedTask);

        Assertions.assertEquals(updatedTask.toString(), taskMaster.viewTask(1).taskInfo);
    }

    @Test
    void removeTaskTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        LocalMemoryTaskList localMemoryTaskList = new LocalMemoryTaskList();
        TaskMaster taskMaster = new TaskMaster();
        taskMaster.openTaskList(localMemoryTaskList);

        taskMaster.addTask(todoRequest);

        taskMaster.removeTask(1);

        Assertions.assertNull(taskMaster.viewTask(1));
    }
}

