package site.bmraubo.todo;

import org.json.JSONObject;

public class TaskMaster {
    TaskList taskList;

    public void openTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public Task addTask(String taskInfo) {
        Task task = new Task(taskInfo);
        taskList.addTask(task);
        return task;
    }

    public Task viewTask(int id) {
        return taskList.viewTaskByID(id);
    }

    public void updateTask(int taskID, JSONObject taskData) {
        taskList.updateTask(taskID, taskData);
    }

    public void removeTask(int id) {
        taskList.removeTask(id);
    }

    public boolean checkActionOutcome() {
        return taskList.actionSuccessful();
    }
}
