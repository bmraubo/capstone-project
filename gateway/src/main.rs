#[macro_use]
extern crate rocket;

mod send_to_service;

use rocket::http::{ContentType, Status};

#[get("/todos")]
fn get_all_tasks() -> (Status, (ContentType, String)) {
    let response_body = "[{\"id\":\"1\",\"task\":\"a task\",\"done\":\"false\"},{\"id\":\"2\",\"task\":\"another task\",\"done\":\"false\"}]".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[get("/todo/<id>")]
fn get_task_by_id(id: i32) -> (Status, (ContentType, String)) {
    let response_body = "{\"id\":\"1\",\"task\":\"a task\",\"done\":\"false\"}".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[post("/todo", data = "<request_body>")]
fn add_task(request_body: &str) -> (Status, (ContentType, String)) {
    let response_body = "{\"id\":\"3\",\"task\":\"string\",\"done\":\"false\"}".to_string();
    (Status::Created, (ContentType::JSON, response_body))
}

#[put("/todo/<id>", data = "<request_body>")]
fn update_task(id: i32, request_body: &str) -> (Status, (ContentType, String)) {
    let response_body =
        "{\"id\":\"3\",\"task\":\"an updated task\",\"done\":\"false\"}".to_string();
    (Status::Ok, (ContentType::JSON, response_body))
}

#[delete("/todo/<id>")]
fn delete_task(id: i32) -> Status {
    Status::NoContent
}

#[post("/register")]
fn register() -> Status {
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
