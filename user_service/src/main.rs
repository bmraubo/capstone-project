#[macro_use]
extern crate rocket;
extern crate serde;

mod basic_authentication;
mod credentials_storage;
mod password;

use basic_authentication::process_authentication;
use credentials_storage::{
    authenticate_user, connect_to_database, register_user, write_task_list_to_database, AddTask,
};
use rocket::http::{ContentType, Status};

#[post("/register", data = "<data>")]
async fn register(data: &str) -> Status {
    let credentials = process_authentication(data);
    if register_user(&credentials).await {
        Status::Ok
    } else {
        Status::BadRequest
    }
}

#[post("/check_credentials", data = "<data>")]
async fn check_credentials(data: &str) -> (Status, (ContentType, String)) {
    let credentials = process_authentication(data);
    let user_authentication_outcome = authenticate_user(credentials).await;
    let response_body = serde_json::to_string(&user_authentication_outcome).unwrap();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[post("/add_task", data = "<data>")]
async fn add_task(data: &str) -> Status {
    let task_info: AddTask = serde_json::from_str(data).unwrap();
    let mut database_client = connect_to_database().await;
    write_task_list_to_database(&mut database_client, &task_info).await;
    Status::Ok
}

#[launch]
fn rocket() -> _ {
    dotenv::from_filename(".env").ok();
    rocket::build().mount("/", routes![register, check_credentials, add_task])
}
