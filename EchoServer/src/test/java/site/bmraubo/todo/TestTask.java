package site.bmraubo.todo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTask

{
    @Test
    void createTaskTest() {
        String todoRequest1 = "{\"task\":\"a new task\"}";
        String todoRequest2 = "{\"task\":\"a new task\", \"done\": \"false\"}";
        String todoRequest3 = "{\"id\": \"23\",\"task\":\"a new task\"}";

        Task task1 = new Task(todoRequest1);
        Task task2 = new Task(todoRequest2);
        Task task3 = new Task(todoRequest3);

        Assertions.assertEquals("a new task", task1.taskJSON.get("task"));
        Assertions.assertFalse(task2.taskJSON.getBoolean("done"));
        Assertions.assertEquals(23, task3.taskJSON.getInt("id"));
    }

    @Test
    void setTaskIDTest() {
        String todoRequest = "{\"task\":\"a new task\"}";

        Task task = new Task(todoRequest);
        task.setTaskID(233);

        Assertions.assertEquals(233, task.id);
    }
}
