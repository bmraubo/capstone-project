#[macro_use]
extern crate rocket;

mod send_to_service;

use rocket::http::{ContentType, Status};
use rocket::request::{FromRequest, Outcome, Request};
use send_to_service::send_to_user_service;
use serde::{Deserialize, Serialize};
use std::convert::Infallible;
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

pub struct Authentication {
    pub token: String,
}

#[rocket::async_trait]
impl<'r> FromRequest<'r> for Authentication {
    type Error = Infallible;

    async fn from_request(request: &'r Request<'_>) -> Outcome<Authentication, Infallible> {
        let auth_token = request.headers().get_one("Authorization");
        Outcome::Success(Authentication {
            token: auth_token.unwrap().to_string(),
        })
    }
}

fn extract_authentication_hash(token: String) -> String {
    token
        .split(" ")
        .collect::<Vec<&str>>()
        .get(1)
        .unwrap()
        .to_string()
}

#[derive(Serialize, Deserialize)]
struct CredentialsValidation {
    credentials_valid: bool,
    user_tasks: Option<Vec<i32>>,
}

async fn validate_credentials(credentials: String) -> CredentialsValidation {
    let user_service_url: String = format!(
        "{}/check_credentials",
        &env::var("USER_SERVICE_URL").unwrap()
    );
    let user_service_response = send_to_user_service(user_service_url, credentials)
        .await
        .unwrap();
    return serde_json::from_str(&user_service_response.text().await.unwrap()).unwrap();
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn true_is_true() {
        assert_eq!(true, true)
    }

    #[test]
    fn can_extract_authentication_hash_from_header() {
        let token = "BASIC aaaa".to_string();
        let extracted_value = extract_authentication_hash(token);
        assert_eq!("aaaa".to_string(), extracted_value);
    }
}
