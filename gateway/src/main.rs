#[macro_use]
extern crate rocket;
extern crate dotenv;

mod authentication;
mod send_to_service;

use authentication::{extract_authentication_hash, Authentication};
use rocket::http::{ContentType, Status};
use send_to_service::send_to_user_service;
use serde::{Deserialize, Serialize};
use std::env;

#[get("/todos")]
async fn get_all_tasks(auth_token: Authentication) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    let response_body = "[{\"id\":\"1\",\"task\":\"a task\",\"done\":\"false\"},{\"id\":\"2\",\"task\":\"another task\",\"done\":\"false\"}]".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[get("/todo/<id>")]
async fn get_task_by_id(auth_token: Authentication, id: i32) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    let response_body = "{\"id\":\"1\",\"task\":\"a task\",\"done\":\"false\"}".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[post("/todo", data = "<request_body>")]
async fn add_task(
    auth_token: Authentication,
    request_body: &str,
) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    let response_body = "{\"id\":\"3\",\"task\":\"string\",\"done\":\"false\"}".to_string();
    (Status::Created, (ContentType::JSON, response_body))
}

#[put("/todo/<id>", data = "<request_body>")]
async fn update_task(
    auth_token: Authentication,
    id: i32,
    request_body: &str,
) -> (Status, (ContentType, String)) {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    let response_body =
        "{\"id\":\"3\",\"task\":\"an updated task\",\"done\":\"false\"}".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[delete("/todo/<id>")]
async fn delete_task(auth_token: Authentication, id: i32) -> Status {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    let validated_credentials = validate_credentials(authentication_hash).await;
    Status::NoContent
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

    #[test]
    fn true_is_true() {
        assert_eq!(true, true)
    }
}
