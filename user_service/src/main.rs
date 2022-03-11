#[macro_use]
extern crate rocket;

use rocket::http::{ContentType, Status};

#[post("/register")]
fn register() -> Status {
    Status::Ok
}

#[post("/check_credentials")]
fn check_credentials() -> (Status, (ContentType, String)) {
    let response_body = "user tasks".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[launch]
fn rocket() -> _ {
    rocket::build().mount("/", routes![register, check_credentials,])
}
