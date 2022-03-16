use crate::send_to_service::send_to_user_service;
use serde::{Deserialize, Serialize};
use std::env;

#[derive(Serialize, Deserialize)]
pub struct AddTask {
    username: String,
    tasks: Vec<i32>,
}

pub fn does_user_own_task(task_list: &Vec<i32>, task_id: &i32) -> bool {
    task_list.contains(&task_id)
}

pub async fn assign_task_to_user(task_list: Vec<i32>, task_id: i32) -> Vec<i32> {
    let mut new_task_list = task_list;
    new_task_list.push(task_id);
    new_task_list
}

pub async fn unassign_task_from_user(task_list: Vec<i32>, task_id: i32) -> Vec<i32> {
    let mut new_task_list = task_list;
    let index = new_task_list.iter().position(|x| x == &task_id).unwrap();
    new_task_list.remove(index);
    new_task_list
}

pub async fn update_user_task_list_on_database(task_list: Vec<i32>, username: String) {
    let task_information = AddTask {
        username: username,
        tasks: task_list,
    };
    println!("Add task endpoint send to user service");
    let request_body = serde_json::to_string(&task_information).unwrap();
    let user_service_url: String = format!("{}/add_task", &env::var("USER_SERVICE_URL").unwrap());
    let _add_task_outcome = send_to_user_service(user_service_url, request_body).await;
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn true_is_true() {
        assert_eq!(true, true)
    }

    #[test]
    fn can_check_if_task_id_is_in_tasklist() {
        let tasklist: Vec<i32> = vec![1, 2, 3];
        let check = does_user_own_task(&tasklist, &1);
        assert!(check)
    }
}
