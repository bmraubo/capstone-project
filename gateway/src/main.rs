#[macro_use]
extern crate rocket;

mod authentication;
mod send_to_service;

use authentication::{extract_authentication_hash, validate_credentials, Authentication};
use rocket::http::{ContentType, Status};

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
fn register(auth_token: Authentication) -> Status {
    let authentication_hash = extract_authentication_hash(auth_token.token);
    Status::Ok
}

#[launch]
fn rocket() -> _ {
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
