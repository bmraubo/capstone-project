package site.bmraubo.todo;

import org.json.JSONObject;

public class Task {
    int id;
    public String taskInfo;
    public JSONObject taskJSON;

    public Task(String taskInfo) {
        this.taskInfo = taskInfo;
        System.out.println(taskInfo);
        taskJSON = convertTaskInfoToJSON();
    }

    public void updateTask(String taskInfo) {
        // Legacy
        this.taskInfo = taskInfo;
    }

    public void setTaskID(int id) {
        this.id = id;
        taskJSON.put("id", id);
    }

    private JSONObject convertTaskInfoToJSON() {
        return new JSONObject(taskInfo);
    }
}
