package site.bmraubo.todo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestPostgres {

    @Test
    void DatabaseConnectionTest() {
        PostgresSpy postgresSpy = new PostgresSpy();

        Assertions.assertTrue(postgresSpy.connectionEstablished);
    }

    @Test
    void addTaskToDatabaseTest() {
        Task testTask = new Task("{\"task\":\"test task info\"}");

        PostgresSpy postgresSpy = new PostgresSpy();
        seedDatabase(postgresSpy);
        postgresSpy.addTask(testTask);

        Assertions.assertTrue(postgresSpy.addedTask);
        Assertions.assertEquals(2, testTask.taskJSON.getInt("id"));
        Assertions.assertFalse(testTask.taskJSON.getBoolean("done"));
        tearDownDatabase(postgresSpy);
    }

    @Test
    void viewTaskTest() {
        PostgresSpy postgresSpy = new PostgresSpy();
        seedDatabase(postgresSpy);
        Task testTask = postgresSpy.viewTaskByID(1);

        Assertions.assertTrue(postgresSpy.viewedTask);
        Assertions.assertEquals("seed task info", testTask.taskJSON.get("task"));
        Assertions.assertEquals(1, testTask.taskJSON.get("id"));
        Assertions.assertEquals(false, testTask.taskJSON.get("done"));
        tearDownDatabase(postgresSpy);
    }

    @Test
    void updateTaskTest() {
        PostgresSpy postgresSpy = new PostgresSpy();
        seedDatabase(postgresSpy);

        postgresSpy.updateTask(1, new JSONObject("{\"task\":\"updated task info\", \"done\": \"false\"}"));

        Assertions.assertTrue(postgresSpy.updatedTask);
        Assertions.assertEquals("updated task info", postgresSpy.viewTaskByID(1).taskJSON.getString("task"));
        Assertions.assertFalse(postgresSpy.viewTaskByID(1).taskJSON.getBoolean("done"));
        tearDownDatabase(postgresSpy);
    }

    @Test
    void removeTaskTest() {
        PostgresSpy postgresSpy = new PostgresSpy();
        seedDatabase(postgresSpy);

        postgresSpy.removeTask(1);

        Assertions.assertTrue(postgresSpy.removedTask);
        tearDownDatabase(postgresSpy);
    }

    @Test
    void retrieveAllTasksTest() {
        PostgresSpy postgresSpy = new PostgresSpy();
        seedDatabase(postgresSpy);

        JSONArray taskListJSON = postgresSpy.getAllTasks();

        JSONArray testArray = new JSONArray();
        JSONObject expectedList = new JSONObject();
        expectedList.put("id", 1);
        expectedList.put("task", "seed task info");
        expectedList.put("done", false);
        testArray.put(expectedList);

        Assertions.assertEquals(testArray.toString(), taskListJSON.toString());
    }

    private void seedDatabase(PostgresSpy postgresSpy) {
        postgresSpy.seedDatabase();
    }

    private void tearDownDatabase(PostgresSpy postgresSpy) {
        postgresSpy.tearDownDatabase();
    }
}
