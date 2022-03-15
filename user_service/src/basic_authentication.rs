use base64::decode;
use std::str;

pub struct Credentials {
    pub username: String,
    pub password: String,
}

pub fn process_authentication(auth_token: &str) {
    let decoded_token = decode_authentication_hash(auth_token);
    let user_credentials = parse_credentials(decoded_token);
}

fn decode_authentication_hash(input: &str) -> String {
    return str::from_utf8(&decode(input).unwrap()).unwrap().to_string();
}

fn parse_credentials(decoded_credentials: String) -> Credentials {
    let split_credentials = decoded_credentials.split(":").collect::<Vec<&str>>();
    let username = split_credentials.get(0);
    let password = split_credentials.get(1);
    Credentials {
        username: username.unwrap().to_string(),
        password: password.unwrap().to_string(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use base64::encode;

    #[test]
    fn can_decode_base64_authentication_information() {
        let userpass = "sam:gamgee";
        let encoded_userpass = encode(userpass);
        let decoded_userpass = decode_authentication_hash(&encoded_userpass);
        assert_eq!(decoded_userpass, userpass)
    }

    #[test]
    fn can_parse_decoded_credentials() {
        let token = "aa:bb".to_string();
        let parsed_credentials = parse_credentials(token);
        assert_eq!(parsed_credentials.username, "aa");
        assert_eq!(parsed_credentials.password, "bb");
    }
}
