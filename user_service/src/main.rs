#[macro_use]
extern crate rocket;

mod basic_authentication;
mod credentials_storage;

use basic_authentication::process_authentication;
use rocket::http::{ContentType, Status};

#[post("/register", data = "<data>")]
fn register(data: &str) -> Status {
    process_authentication(data);
    Status::Ok
}

#[post("/check_credentials", data = "<data>")]
fn check_credentials(data: &str) -> (Status, (ContentType, String)) {
    process_authentication(data);
    let response_body = "user tasks".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[launch]
fn rocket() -> _ {
    dotenv::from_filename(".env").ok();
    rocket::build().mount("/", routes![register, check_credentials,])
}
