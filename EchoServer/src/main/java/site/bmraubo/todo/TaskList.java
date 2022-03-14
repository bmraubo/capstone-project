package site.bmraubo.todo;

import org.json.JSONArray;
import org.json.JSONObject;

public interface TaskList {

    Task addTask(Task task);
    Task viewTaskByID(int id);
    JSONArray getAllTasks();
    void updateTask(int id, JSONObject taskData);
    void removeTask(int id);
    boolean actionSuccessful();
}
