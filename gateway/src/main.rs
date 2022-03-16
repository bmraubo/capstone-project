#[macro_use]
extern crate rocket;
extern crate dotenv;

mod authentication;
mod send_to_service;
mod tasklist_operations;

use authentication::{extract_authentication_hash, Authentication};
use rocket::http::{ContentType, Status};
use send_to_service::send_to_user_service;
use serde::{Deserialize, Serialize};
use std::env;
use tasklist_operations::{
    assign_task_to_user, does_user_own_task, unassign_task_from_user,
    update_user_task_list_on_database,
};

#[derive(Serialize, Deserialize)]
struct Task {
    id: i32,
    task: String,
    done: bool,
}

#[get("/todos")]
async fn get_all_tasks(auth_token: Authentication) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    if validated_credentials.success {
        let mut user_tasks: Vec<String> = vec![];
        for task_id in validated_credentials.tasks.unwrap() {
            let request_url = format!("{}/todo/{}", &env::var("JAVA_SERVER_URL").unwrap(), task_id);
            let java_service_response = send_to_service::view_task(request_url).await.unwrap();
            user_tasks.push(java_service_response.text().await.unwrap());
        }
        let response_body = format!("[{}]", user_tasks.join(","));
        (Status::Ok, (ContentType::JSON, response_body))
    } else {
        unauthorized_response()
    }
}

#[get("/todo/<id>")]
async fn get_task_by_id(auth_token: Authentication, id: i32) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    if validated_credentials.success
        && does_user_own_task(&validated_credentials.tasks.unwrap(), &id)
    {
        let java_service_url = format!("{}/todo/{}", &env::var("JAVA_SERVER_URL").unwrap(), id);
        let java_service_response = send_to_service::view_task(java_service_url).await.unwrap();
        let response_body = java_service_response.text().await.unwrap();
        (Status::Ok, (ContentType::JSON, response_body))
    } else {
        unauthorized_response()
    }
}

#[post("/todo", data = "<request_body>")]
async fn add_task(
    auth_token: Authentication,
    request_body: String,
) -> (Status, (ContentType, String)) {
    println!("1");
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    if validated_credentials.success {
        println!("2");
        let java_service_url = format!("{}/todo", &env::var("JAVA_SERVER_URL").unwrap());
        let java_service_response = send_to_service::add_task(java_service_url, request_body)
            .await
            .unwrap();
        println!("3");    
        let response_body = java_service_response.text().await.unwrap();
        println!("{}", response_body);
        let added_task: Task = serde_json::from_str(&response_body).unwrap();
        println!("Adding task id {}", added_task.id);
        let new_task_list =
            assign_task_to_user(validated_credentials.tasks.unwrap(), added_task.id).await;
        update_user_task_list_on_database(new_task_list, validated_credentials.username.unwrap())
            .await;
        println!("Add task added to db");
        (Status::Created, (ContentType::JSON, response_body))
    } else {
        unauthorized_response()
    }
}

#[put("/todo/<id>", data = "<request_body>")]
async fn update_task(
    auth_token: Authentication,
    id: i32,
    request_body: String,
) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    if validated_credentials.success
        && does_user_own_task(&validated_credentials.tasks.unwrap(), &id)
    {
        let java_service_url = format!("{}/todo/{}", &env::var("JAVA_SERVER_URL").unwrap(), id);
        let java_service_response = send_to_service::update_task(java_service_url, request_body)
            .await
            .unwrap();
        let response_body = java_service_response.text().await.unwrap();
        (Status::Ok, (ContentType::JSON, response_body))
    } else {
        unauthorized_response()
    }
}

#[delete("/todo/<id>")]
async fn delete_task(auth_token: Authentication, id: i32) -> Status {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    if validated_credentials.success
        && does_user_own_task(&validated_credentials.tasks.as_ref().unwrap(), &id)
    {
        let java_service_url = format!("{}/todo/{}", &env::var("JAVA_SERVER_URL").unwrap(), id);
        let _java_service_response = send_to_service::delete_task(java_service_url)
            .await
            .unwrap();
        let new_task_list = unassign_task_from_user(validated_credentials.tasks.unwrap(), id).await;
        update_user_task_list_on_database(new_task_list, validated_credentials.username.unwrap())
            .await;
        Status::NoContent
    } else {
        Status::Unauthorized
    }
}

#[post("/register")]
async fn register(auth_token: Authentication) -> Status {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    register_user(authentication_hash).await
}

async fn register_user(auth_token: String) -> Status {
    let user_service_url: String = format!("{}/register", &env::var("USER_SERVICE_URL").unwrap());
    let registration_outcome = send_to_user_service(user_service_url, auth_token)
        .await
        .unwrap();
    if registration_outcome.status() == 200 {
        Status::Ok
    } else {
        Status::BadRequest
    }
}

#[derive(Serialize, Deserialize)]
pub struct CheckOutcome {
    success: bool,
    username: Option<String>,
    tasks: Option<Vec<i32>>,
}

async fn validate_credentials(credentials: String) -> CheckOutcome {
    let user_service_url: String = format!(
        "{}/check_credentials",
        &env::var("USER_SERVICE_URL").unwrap()
    );
    let user_service_response =
        send_to_service::send_to_user_service(user_service_url, credentials)
            .await
            .unwrap();
    return serde_json::from_str(&user_service_response.text().await.unwrap()).unwrap();
}

fn unauthorized_response() -> (Status, (ContentType, String)) {
    (
        Status::Unauthorized,
        (
            ContentType::JSON,
            "{\"error\":\"unauthorised\"}".to_string(),
        ),
    )
}

#[launch]
fn rocket() -> _ {
    dotenv::from_filename(".env").ok();
    rocket::build().mount(
        "/",
        routes![
            get_all_tasks,
            get_task_by_id,
            add_task,
            update_task,
            delete_task,
            register
        ],
    )
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
