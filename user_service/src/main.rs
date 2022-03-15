#[macro_use]
extern crate rocket;
extern crate serde;

mod basic_authentication;
mod credentials_storage;
mod password;

use basic_authentication::process_authentication;
use credentials_storage::{authenticate_user, register_user};
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

#[launch]
fn rocket() -> _ {
    dotenv::from_filename(".env").ok();
    rocket::build().mount("/", routes![register, check_credentials,])
}
