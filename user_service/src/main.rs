#[macro_use]
extern crate rocket;

mod credentials_storage;

use rocket::http::{ContentType, Status};

#[post("/register", data = "<data>")]
fn register(data: &str) -> Status {
    Status::Ok
}

#[post("/check_credentials", data = "<data>")]
fn check_credentials(data: &str) -> (Status, (ContentType, String)) {
    let response_body = "user tasks".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[launch]
fn rocket() -> _ {
    dotenv::from_filename(".env").ok();
    rocket::build().mount("/", routes![register, check_credentials,])
}
