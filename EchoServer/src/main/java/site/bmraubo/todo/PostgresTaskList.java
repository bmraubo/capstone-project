package site.bmraubo.todo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostgresTaskList implements TaskList{
    Connection conn;
    boolean success;

    public PostgresTaskList() {
        connectToDatabase();
    }


    public void connectToDatabase() {
        try {
            String url = System.getenv("JDBC_DATABASE_URL");
            conn = DriverManager.getConnection(url);
            System.out.println("Database Connection Successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Task addTask(Task task) {
        try {
            PreparedStatement addTaskStatement = conn.prepareStatement("INSERT INTO Tasks(taskinfo, done) VALUES(?, ?) RETURNING taskid");
            addTaskStatement.setString(1, task.taskJSON.getString("task"));
            addTaskStatement.setBoolean(2, false);
            ResultSet resultSet = addTaskStatement.executeQuery();
            if (resultSet.next()) {
                task.setTaskID(resultSet.getInt("taskid"));
                task.taskJSON.put("done", false);
            }
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public Task viewTaskByID(int id) {
        try {
            PreparedStatement addTaskStatement = conn.prepareStatement("SELECT * FROM Tasks WHERE taskid=?");
            addTaskStatement.setInt(1, id);
            ResultSet resultSet = addTaskStatement.executeQuery();
            if (resultSet.next()) {
                int taskID = resultSet.getInt("taskid");
                String taskInfo = resultSet.getString("taskinfo");
                boolean doneStatus = resultSet.getBoolean("done");
                JSONObject taskData = new JSONObject();
                taskData.put("id", taskID);
                taskData.put("task", taskInfo);
                taskData.put("done", doneStatus);
                Task task = new Task(taskData.toString());
                task.setTaskID(taskID);
                return task;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray getAllTasks() {
        JSONArray jsonArray = new JSONArray();
        try {
            PreparedStatement addTaskStatement = conn.prepareStatement("SELECT * FROM Tasks");
            ResultSet resultSet = addTaskStatement.executeQuery();
            while (resultSet.next()) {
                JSONObject taskData = new JSONObject();
                int taskID = resultSet.getInt("taskid");
                String taskInfo = resultSet.getString("taskinfo");
                boolean done = resultSet.getBoolean("done");
                taskData.put("id", taskID);
                taskData.put("task", taskInfo);
                taskData.put("done", done);
                jsonArray.put(taskData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public void updateTask(int id, JSONObject taskData) {
        try {
            PreparedStatement addTaskStatement = conn.prepareStatement("UPDATE Tasks SET taskinfo=?, done=? WHERE taskid=?");
            addTaskStatement.setString(1, taskData.getString("task"));
            addTaskStatement.setBoolean(2, taskData.getBoolean("done"));
            addTaskStatement.setInt(3, taskData.getInt("id"));
            addTaskStatement.executeUpdate();
            System.out.println("Task Updated");
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
    }

    @Override
    public void removeTask(int id) {
        try {
            PreparedStatement addTaskStatement = conn.prepareStatement("DELETE FROM Tasks WHERE taskid=?");
            addTaskStatement.setInt(1, id);
            addTaskStatement.executeUpdate();
            System.out.println("Task "+id+" Removed");
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
    }

    @Override
    public boolean actionSuccessful() {
        return success;
    }


}
