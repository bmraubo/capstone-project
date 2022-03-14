package site.bmraubo.todo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class LocalMemoryTaskList implements TaskList{
    public LinkedHashMap<Integer, Task> taskList;
    boolean success;

    public LocalMemoryTaskList() {
        generateTaskList();
    }

    @Override
    public Task addTask(Task task) {
        task.setTaskID(calculateNextAvailableID());
        taskList.put(task.id, task);
        success = true;
        return task;
    }

    @Override
    public Task viewTaskByID(int id) {
        return taskList.get(id);
    }

    @Override
    public JSONArray getAllTasks() {
        return null;
    }

    @Override
    public void updateTask(int id, JSONObject taskData) {
        Task task = viewTaskByID(id);
        task.updateTask(taskData.toString());
        success = true;
    }

    @Override
    public void removeTask(int id) {
        taskList.remove(id);
        success = true;
    }

    @Override
    public boolean actionSuccessful() {
        return success;
    }

    private void generateTaskList() {
        taskList = new LinkedHashMap<>();
    }

    private int calculateNextAvailableID() {
        return taskList.size()+1;
    }
}
