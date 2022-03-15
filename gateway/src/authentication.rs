use crate::send_to_service;

use rocket::request::{FromRequest, Outcome, Request};
use serde::{Deserialize, Serialize};
use std::convert::Infallible;
use std::env;

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

pub fn extract_authentication_hash(token: String) -> String {
    token
        .split(" ")
        .collect::<Vec<&str>>()
        .get(1)
        .unwrap()
        .to_string()
}

#[derive(Serialize, Deserialize)]
pub struct CredentialsValidation {
    credentials_valid: bool,
    user_tasks: Option<Vec<i32>>,
}

pub async fn validate_credentials(credentials: String) -> CredentialsValidation {
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

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn can_extract_authentication_hash_from_header() {
        let token = "BASIC aaaa".to_string();
        let extracted_value = extract_authentication_hash(token);
        assert_eq!("aaaa".to_string(), extracted_value);
    }
}
