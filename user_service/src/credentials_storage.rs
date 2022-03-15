use std::env;
use tokio_postgres::{Client, NoTls};

pub async fn add_user_credentials_to_db(
    client: &mut tokio_postgres::Client,
    username: &str,
    password: &String,
) {
    let prepared_statement = client
        .prepare("INSERT INTO user_credentials(username, password) VALUES($1, $2)")
        .await
        .unwrap();
    let _outcome = client
        .execute(&prepared_statement, &[&username, &password])
        .await;
}

pub async fn retrieve_hashed_password(client: &mut Client, username: &str) -> String {
    let prepared_statement = client
        .prepare("SELECT password FROM user_credentials WHERE username = $1")
        .await
        .unwrap();
    let outcome = client.query(&prepared_statement, &[&username]).await;
    outcome.unwrap().get(0).unwrap().get(0)
}

pub async fn user_exists(client: &mut Client, username: &str) -> bool {
    let prepared_statement = client
        .prepare("SELECT username FROM user_credentials WHERE username=$1")
        .await
        .unwrap();
    let outcome = client.query(&prepared_statement, &[&username]).await;
    return outcome.unwrap().len() > 0;
}

pub async fn connect_to_database() -> Client {
    let (client, connection) =
        tokio_postgres::connect(&env::var("USER_DATABASE_URL").unwrap(), NoTls)
            .await
            .unwrap();

    tokio::spawn(async move {
        if let Err(e) = connection.await {
            eprintln!("connection error: {}", e);
        }
    });
    client
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
            CREATE TABLE user_credentials (username varchar(255) PRIMARY KEY, password varchar(255));
            ",
            &[],
        ).await;
    }

    async fn drop_table(client: &mut Client) {
        let _outcome = client.execute("DROP TABLE user_credentials;", &[]).await;
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
    async fn can_retrieve_passwords() {
        dotenv::from_filename(".env").ok();
        let mut database_client = connect_to_database().await;
        create_test_table(&mut database_client).await;
        add_user_credentials_to_db(&mut database_client, "jonny", &"be_good".to_string()).await;
        let password = retrieve_hashed_password(&mut database_client, "jonny").await;
        drop_table(&mut database_client).await;
        assert_eq!(password, "be_good".to_string())
    }
}
