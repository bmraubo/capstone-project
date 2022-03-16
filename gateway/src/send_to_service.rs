extern crate reqwest;

pub async fn send_to_user_service(
    url: String,
    body: String,
) -> Result<reqwest::Response, reqwest::Error> {
    let client = reqwest::Client::new();
    let response = client.post(url).body(body).send().await?;
    Ok(response)
}

pub async fn add_task(url: String, body: String) -> Result<reqwest::Response, reqwest::Error> {
    println!("Add task request");
    let client = reqwest::Client::new();
    let response = client
        .post(&url)
        .header("Content-Type", "application/json; charset=UTF-8")
        .header("Content-Length", body.chars().count())
        .body(body)
        .send()
        .await?;
    Ok(response)
}

pub async fn delete_task(url: String) -> Result<reqwest::Response, reqwest::Error> {
    let client = reqwest::Client::new();
    let response = client.delete(&url).send().await?;
    Ok(response)
}

pub async fn update_task(url: String, body: String) -> Result<reqwest::Response, reqwest::Error> {
    let client = reqwest::Client::new();
    let response = client
        .put(&url)
        .header("Content-Type", "application/json; charset=UTF-8")
        .header("Content-Length", body.chars().count())
        .body(body)
        .send()
        .await?;
    Ok(response)
}

pub async fn view_task(url: String) -> Result<reqwest::Response, reqwest::Error> {
    let client = reqwest::Client::new();
    let response = client.get(&url).send().await?;
    Ok(response)
}

#[cfg(test)]
mod tests {
    use super::*;
    use httpmock::prelude::*;

    #[tokio::test]
    async fn can_check_credentials_with_user_service() {
        let request_body = "YWE6YmI=";
        let mocked_service = MockServer::start();
        let hello_mock = mocked_service.mock(|when, then| {
            when.method(POST).path("/user_service").body(request_body);
            then.status(200)
                .header("content-type", "application/json; charset=UTF-8")
                .body("{\"tasks\":\"[1,2,3]\"}");
        });
        let response = send_to_user_service(
            mocked_service.url("/user_service"),
            request_body.to_string(),
        )
        .await
        .unwrap();
        hello_mock.assert();
        assert_eq!(response.status(), 200);
        assert_eq!(response.text().await.unwrap(), "{\"tasks\":\"[1,2,3]\"}")
    }

    #[tokio::test]
    async fn test_add_task() {
        let mocked_service = MockServer::start();
        let request_body: String = "{\"task\":\"do_thing\"".to_string();
        let add_task_mock = mocked_service.mock(|when, then| {
            when.method(POST)
                .path("/todo")
                .body(&request_body.to_string());
            then.status(200)
                .header("content-type", "application/json; charset=UTF-8")
                .body("{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}");
        });
        let response = add_task(mocked_service.url("/todo"), request_body)
            .await
            .unwrap();
        add_task_mock.assert();
        assert_eq!(response.status(), 200);
        assert_eq!(
            response.text().await.unwrap(),
            "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}"
        )
    }

    #[tokio::test]
    async fn test_delete_task() {
        let mocked_service = MockServer::start();
        let delete_mock = mocked_service.mock(|when, then| {
            when.method(DELETE).path("/todo/1");
            then.status(204);
        });
        let response = delete_task(mocked_service.url("/todo/1")).await.unwrap();
        delete_mock.assert();
        assert_eq!(response.status(), 204);
    }

    #[tokio::test]
    async fn test_change_done_status() {
        let mocked_service = MockServer::start();
        let request_body: String =
            "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}".to_string();
        let done_status_mock = mocked_service.mock(|when, then| {
            when.method(PUT)
                .path("/todo/1")
                .body(&request_body.to_string());
            then.status(200)
                .header("content-type", "application/json; charset=UTF-8")
                .body("{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}");
        });
        let response = update_task(mocked_service.url("/todo/1"), request_body)
            .await
            .unwrap();
        done_status_mock.assert();
        assert_eq!(response.status(), 200);
        assert_eq!(
            response.text().await.unwrap(),
            "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}"
        )
    }

    #[tokio::test]
    async fn test_update_task() {
        let mocked_service = MockServer::start();
        let request_body: String =
            "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}".to_string();
        let update_mock = mocked_service.mock(|when, then| {
            when.method(PUT)
                .path("/todo/1")
                .body(&request_body.to_string());
            then.status(200)
                .header("content-type", "application/json; charset=UTF-8")
                .body("{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}");
        });
        let response = update_task(mocked_service.url("/todo/1"), request_body)
            .await
            .unwrap();
        update_mock.assert();
        assert_eq!(response.status(), 200);
        assert_eq!(
            response.text().await.unwrap(),
            "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}"
        )
    }

    #[tokio::test]
    async fn view_task_by_id() {
        let mocked_service = MockServer::start();
        let task: &str = "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}";
        let view_task_mock = mocked_service.mock(|when, then| {
            when.method(GET).path("/todo/1");
            then.status(200)
                .header("content-type", "text/html; charset=UTF-8")
                .body(task);
        });
        let response = view_task(mocked_service.url("/todo/1")).await.unwrap();
        view_task_mock.assert();
        assert_eq!(response.status(), 200);
        assert_eq!(
            response.text().await.unwrap(),
            "{\"id\": \"1\", \"task\":\"do_thing\", \"done\": \"false\"}"
        )
    }
}
