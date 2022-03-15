use argon2;
use rand::Rng;

pub fn hash_user_password(password: &str) -> String {
    let byte_password = password.as_bytes();
    let salt: [u8; 32] = rand::thread_rng().gen();
    let config = argon2::Config::default();
    let password_hash = argon2::hash_encoded(byte_password, &salt, &config);
    password_hash.unwrap()
}

pub fn verify_user_password(password_hash: String, password: &str) -> bool {
    return argon2::verify_encoded(&password_hash, password.as_bytes()).unwrap();
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::password::{hash_user_password, verify_user_password};
    use argon2;

    #[test]
    fn can_hash_passwords() {
        let password = "password";
        let password_hash = hash_user_password(password);
        let matches = argon2::verify_encoded(&password_hash, password.as_bytes()).unwrap();
        assert!(matches)
    }

    #[test]
    fn can_verify_passwords_match() {
        let password = "password";
        let password_hash = hash_user_password(password);
        let matches = verify_user_password(password_hash, password);
        assert!(matches);
    }

    #[test]
    fn can_verify_passwords_do_not_match() {
        let password = "password";
        let password_hash = hash_user_password(password);
        let other_password = "grognak the barbarian";
        let matches = verify_user_password(password_hash, other_password);
        assert!(!matches);
    }
}
