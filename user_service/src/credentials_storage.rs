use crate::basic_authentication::Credentials;
use crate::password;
use native_tls::TlsConnector;
use postgres_native_tls::MakeTlsConnector;
use serde::{Deserialize, Serialize};
use std::env;
use tokio_postgres::Client;

struct UserData {
    username: String,
    password_hash: String,
    tasks: Vec<i32>,
}

#[derive(Serialize, Deserialize)]
pub struct CheckOutcome {
    success: bool,
    username: Option<String>,
    tasks: Option<Vec<i32>>,
}

#[derive(Serialize, Deserialize)]
pub struct AddTask {
    username: String,
    tasks: Vec<i32>,
}

pub async fn connect_to_database() -> Client {
    let connector = TlsConnector::builder()
        .danger_accept_invalid_certs(true)
        .build()
        .expect("create a TLS connector");
    let connector = MakeTlsConnector::new(connector);

    let (client, connection) =
        tokio_postgres::connect(&env::var("USER_DATABASE_URL").unwrap(), connector)
            .await
            .unwrap();

    tokio::spawn(async move {
        if let Err(e) = connection.await {
            eprintln!("connection error: {}", e);
        }
    });
    client
}

pub async fn authenticate_user(credentials: Credentials) -> CheckOutcome {
    let mut database_connection = connect_to_database().await;
    if user_exists(&mut database_connection, &credentials.username).await {
        let user_data = retrieve_user_data(&mut database_connection, &credentials.username).await;
        if password::verify_user_password(user_data.password_hash, &credentials.password) {
            CheckOutcome {
                success: true,
                username: Some(credentials.username),
                tasks: Some(user_data.tasks),
            }
        } else {
            unauthorised_outcome()
        }
    } else {
        unauthorised_outcome()
    }
}

fn unauthorised_outcome() -> CheckOutcome {
    CheckOutcome {
        success: false,
        username: None,
        tasks: None,
    }
}

pub async fn register_user(credentials: &Credentials) -> bool {
    let mut database_connection = connect_to_database().await;
    if !user_exists(&mut database_connection, &credentials.username).await {
        let hashed_password = password::hash_user_password(&credentials.password);
        add_user_credentials_to_db(
            &mut database_connection,
            &credentials.username,
            &hashed_password,
        )
        .await;
        true
    } else {
        false
    }
}

pub async fn add_user_credentials_to_db(
    client: &mut tokio_postgres::Client,
    username: &str,
    password: &String,
) {
    let empty_user_tasks_list: Vec<i32> = vec![];
    let prepared_statement = client
        .prepare("INSERT INTO user_data(username, password, tasks) VALUES($1, $2, $3)")
        .await
        .unwrap();
    let _outcome = client
        .execute(
            &prepared_statement,
            &[&username, &password, &empty_user_tasks_list],
        )
        .await;
}

async fn retrieve_user_data(client: &mut Client, username: &str) -> UserData {
    let prepared_statement = client
        .prepare("SELECT password, tasks FROM user_data WHERE username = $1")
        .await
        .unwrap();
    let outcome = client
        .query(&prepared_statement, &[&username])
        .await
        .unwrap();
    let user_password: String = outcome.get(0).unwrap().get(0);
    let user_tasks: Vec<i32> = outcome.get(0).unwrap().get(1);
    UserData {
        username: username.to_string(),
        password_hash: user_password,
        tasks: user_tasks,
    }
}

async fn user_exists(client: &mut Client, username: &str) -> bool {
    let prepared_statement = client
        .prepare("SELECT username FROM user_data WHERE username=$1")
        .await
        .unwrap();
    let outcome = client.query(&prepared_statement, &[&username]).await;
    return outcome.unwrap().len() > 0;
}

pub async fn write_task_list_to_database(client: &mut tokio_postgres::Client, task_info: &AddTask) {
    println!("Writing to db");
    let prepared_statement = client
        .prepare("UPDATE user_data SET tasks=$1 WHERE username = $2")
        .await
        .unwrap();
    client
        .query(
            &prepared_statement,
            &[&task_info.tasks, &task_info.username],
        )
        .await
        .ok();
}

#[cfg(test)]
mod tests {
    use super::*;
    use serial_test::serial;
    use std::any::type_name;

    fn type_of<T>(_: T) -> &'static str {
        type_name::<T>()
    }

    async fn create_test_table(client: &mut Client) {
        drop_table(client).await;
        let _outcome = client.execute(
            "
            CREATE TABLE user_data (username varchar(255) PRIMARY KEY, password varchar(255), tasks int[]);
            ",
            &[],
        ).await;
    }

    async fn drop_table(client: &mut Client) {
        let _outcome = client.execute("DROP TABLE user_data;", &[]).await;
    }

    #[tokio::test]
    #[serial]
    async fn test_database_connection() {
        dotenv::from_filename(".env").ok();
        let database_client = connect_to_database().await;
        assert_eq!("tokio_postgres::client::Client", type_of(database_client))
    }

    #[tokio::test]
    #[serial]
    async fn can_add_credentials_to_credentials_database() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        add_user_credentials_to_db(&mut database_client, "jonny", &"be_good".to_string()).await;
        let check = user_exists(&mut database_client, "jonny").await;
        drop_table(&mut database_client).await;
        assert!(check)
    }

    #[tokio::test]
    #[serial]
    async fn can_retrieve_user_information() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        add_user_credentials_to_db(&mut database_client, "jonny", &"be_good".to_string()).await;
        let user_data: UserData = retrieve_user_data(&mut database_client, "jonny").await;
        drop_table(&mut database_client).await;
        assert_eq!(user_data.password_hash, "be_good".to_string())
    }

    #[tokio::test]
    #[serial]
    async fn can_register_user() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        let credentials = Credentials {
            username: "jonny".to_string(),
            password: "be_good".to_string(),
        };
        let registration = register_user(&credentials).await;
        drop_table(&mut database_client).await;
        assert!(registration)
    }

    #[tokio::test]
    #[serial]
    async fn if_user_exists_registration_fails() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        let credentials = Credentials {
            username: "jonny".to_string(),
            password: "be_good".to_string(),
        };
        register_user(&credentials).await;
        let second_registration = register_user(&credentials).await;
        drop_table(&mut database_client).await;
        assert!(!second_registration)
    }

    #[tokio::test]
    #[serial]
    async fn can_authenticate_existing_user() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        let credentials = Credentials {
            username: "jonny".to_string(),
            password: "be_good".to_string(),
        };
        register_user(&credentials).await;
        let authentication_outcome = authenticate_user(credentials).await;
        drop_table(&mut database_client).await;
        assert!(authentication_outcome.success)
    }

    #[tokio::test]
    #[serial]
    async fn unregistered_user_cannot_authenticate() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        let credentials = Credentials {
            username: "jonny".to_string(),
            password: "be_good".to_string(),
        };
        let authentication_outcome = authenticate_user(credentials).await;
        drop_table(&mut database_client).await;
        assert!(!authentication_outcome.success)
    }

    #[tokio::test]
    #[serial]
    async fn wrong_credentials_do_not_authenticate() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        let credentials = Credentials {
            username: "jonny".to_string(),
            password: "be_good".to_string(),
        };
        register_user(&credentials).await;
        let bad_credentials = Credentials {
            username: "jonny".to_string(),
            password: "very_bad".to_string(),
        };
        let authentication_outcome = authenticate_user(bad_credentials).await;
        drop_table(&mut database_client).await;
        assert!(!authentication_outcome.success)
    }

    #[tokio::test]
    #[serial]
    async fn can_write_task_list_to_database() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        let credentials = Credentials {
            username: "jonny".to_string(),
            password: "be_good".to_string(),
        };
        register_user(&credentials).await;
        let new_task_list = AddTask {
            username: "jonny".to_string(),
            tasks: vec![1, 2],
        };
        write_task_list_to_database(&mut database_client, &new_task_list).await;
        let authentication_outcome = authenticate_user(credentials).await;
        drop_table(&mut database_client).await;
        assert_eq!(authentication_outcome.tasks.unwrap(), new_task_list.tasks)
    }
}
