# Capstone Project

## Development Environment

The application uses `docker-compose` to create a development environment that includes API testing using a Postman Collection.

```
// clone the github repo
$ git clone https://github.com/bmraubo/capstone-project.git

// navigate to the repo directory
$ cd capstone_project

// start dev environment
$ docker compose up

// to shut down dev environment and clear the test databases
$ docker compose down --volumes
```

### Postman collections

The `Capstone` Postman Collection has been prepared for the purpose of End2End Testing the application features. In order to pass the tests, the collection has to be run in order; the correct order should load up by default.

A further postman collection `Review-Board` is provided for the purposes of experimentation through the review board. It does not include the tests of the Local Collection above, in order to allow some more fluidity in interacting with the app.

### User Auth Tokens

As encoding the username/password for the purposes of the Authorization header would have happened on the frontend, currently the header has to be manually updated for each user. Registering twice as the same user, for example, would make the tests not pass - the server would refuse to register a user a second time.

Here are some pre-encoded authorization headers:

```
BASIC cGV0ZXI6cGFya2Vy
BASIC Z3Jvb3Z5OmdvcmlsbGEK
BASIC d2F2eTpncmF2eQo=
BASIC YnVnczpidW5ueQ==
BASIC bWlja2V5Om1vdXNl
BASIC ZG9uYWxkOmR1Y2s=
```

Alternatively, when testing locally, `docker compose down --volumes && docker compose up` can be used to reset the databases to an empty state. This will not work for the deployed application - each task has to be deleted by the user that owns it.

## Architecture

The application consists of three services:

-   Gateway service - for handling user requests and forwarding them to the appropriate service.
-   User Service - for handling and storing user credentials and task-ownership lists.
-   Java Service - the legacy Java Service that stores all tasks.

### Guide to Application Processes

#### Registration Process

It was envisioned that the frontend would allow the user to enter a username and password, which would be encrypted for transport using base64 encoding to the HTTP Basic Authentication standard.

As the FrontEnd has not been updated to make use of the new microservices architecture, test requests have to manually be created with proper Authorization headers.

1. A request containing the Authorization header with the encoded username and password is sent to the `/register` endpoint of the Gateway.
2. The encoded username/password is extracted from the header by the Gateway, and a request is made to the `/register` endpoint of the User Service.
3. The User Service checks whether that username is already in use. If it is, a 400 status code is returned.
4. If the username is available, the User Service proceeds to encrypt the password using argon2id with a randomly generated salt, with the randomness relying on Rust's more secure thread_rng generator.
5. The username and hashed password as saved to a user_database, with a generated empty list of user tasks.
6. The user is informed of a successful registration with a 200 status code.

#### Add Task Process

1. Request containing Authorization header and body with task information is received by the Gateway.
2. The Gateway extracts the Authorization header and sends the credentials to the User Service for verification.
3. If the credentials are verified, the User Service responds with the decoded username and tasklist from database. The password is dropped to prevent unencoded passwords being sent between services.
4. If the user has ownership of the task, the Gateway forward the add task request to the Java Service for processing. The Java Service responds with the task information which now includes `id` and `done` status.
5. The Gateway generates a new user tasklist that includes the new task id, and forwards that to the User Service for storage in the Database.
6. The Gateway forwards the task information received from the Java Service to the user.

The add task process required changes to the Java Service code. The code initially did not return the task JSON correctly in accordance with the previously provided schema; `{"task": "string"}` as opposed to `{"id": 1, "task": "string", "done": false}`. As a result:

-   Tasks that were added could not be assigned to users due to lack of id.
-   Update, Delete, View and View All operations could not be completed as the user had no owned tasks.

A further issue arose with the Java Server checks for `Content-Type` headers - the checks being case-sensitive - as the local and production deployments of the Gateway sent `content-type` and `Content-Type` respectively. I never identified the specific reason for this, and in the interests of time the Java code was changed to make the check case-insensitive.

#### Update Task Process

1. Request containing Authorization header and body with task information is received by the Gateway.
2. The Gateway extracts the Authorization header and sends the credentials to the User Service for verification.
3. If the credentials are verified, the User Service responds with the decoded username and tasklist from database. The password is dropped to prevent unencoded passwords being sent between services.
4. If the user has ownership of the task, the Gateway forwards the update task request to the Java Service for processing. The Java Service responds with the task information.
5. The Gateway forwards the task information received from the Java Service to the user.

#### Delete Task Process

1. Request containing Authorization header and body with task information is received by the Gateway.
2. The Gateway extracts the Authorization header and sends the credentials to the User Service for verification.
3. If the credentials are verified, the User Service responds with the decoded username and tasklist from database. The password is dropped to prevent unencoded passwords being sent between services.
4. If the user has ownership of the task, the Gateway forward the delete task request to the Java Service for processing. The Java service responds with a 204 status code.
5. The Gateway generates a new user tasklist without the deleted task id, and forwards that to the User Service for storage in the Database.
6. The Gateway responds to the user with the 204 status code.

#### View Task By ID Process

1. Request containing Authorization header and body with task information is received by the Gateway.
2. The Gateway extracts the Authorization header and sends the credentials to the User Service for verification.
3. If the credentials are verified, the User Service responds with the decoded username and tasklist from database. The password is dropped to prevent unencoded passwords being sent between services.
4. If the user has ownership of the task, the Gateway forward the view task request to the Java Service for processing. The Java service responds with task information.
5. The Gateway forwards the task information received from the Java Service to the user.

#### View All Tasks Process

1. Request containing Authorization header and body with task information is received by the Gateway.
2. The Gateway extracts the Authorization header and sends the credentials to the User Service for verification.
3. If the credentials are verified, the User Service responds with the decoded username and tasklist from database. The password is dropped to prevent unencoded passwords being sent between services.
4. The Gateway loops over each owned task id and makes a View Task By ID request to the Java Service. The Java Service responds with the Task Information for each task.
5. The Gateway compiles all the Task Information into a list, which is then forwarded to the User.

## Challenges and Retrospective

### Communication

Key issue throughout this project was properly communicating when I was blocked on a problem. While this was done when I did not know how to proceed after several attempts, especially in the later parts of the project where I understood that my ability to complete the work relied on it, failing to do so early on resulted in a lot of wasted time that could have been put to better use refactoring the app, or building a better suite of tests.

Also, as has been noted by my mentors, the communicating of goals and managing of expectations was lacklustre. In a client setting, I could see there being problems with how I approached progress updates. This was partly due to the learning curve of the project being such that I did not know in any significant sense what was deliverable and what was not in the timeframe, and a sense of optimism - 'perhaps this is the last problem I will have with docker' - that relatively rarely met with subsequent reality. I can see this as being an area on which I need to work; even if it is communicating problems with development so that the client is not later surprised at the demo with a litany of issues at the same time.

### Organisation

Working on several services simultaneously stretched my organizational ability - although I did use a Kanban board to keep track of tickets and tasks, it did not include enough information to give a clear overview of what was being done, and what needed to be done. The structure of the tickets did not sufficiently demonstrate how they were interrelated, and which relied on what.

Although I became aware of these shortcomings relatively quickly, the administrative overhead required to achieve what I wanted from the Kanban board was too great to make the changes required, given the time pressure for completing the code. Moving forward, I would have put greater emphasis of properly designing the board at the start of the project, with modifications made as new issues became apparent.

### Technical Challenges

These are of lesser importance than the organisational and communication challenges described above, as they are a function of familiarity with given tools.

1. Docker - close to 50% of the time spent working on this project involved dealing with creating and fixing the dev environment and the use of Docker containers. I do, to a certain extent, see the pain as worthwhile due to the large number of possibilities that Docker offers in terms of both testing and deployment. In particular towards the end of the project it became essential for delivering a working application. At the same time, it was a major timesink, and the time could have been spent well elsewhere. Additionally, the 6-10 minute build time of the docker containers was a major lag on the feedback loop, particularly for small changes, and generally slowed down development.

2. Rust - the underestimated the complexity of building this project entirely in Rust. Again, the experience was beneficial, but a used up a lot of the limited time I had. Furthermore, the immaturity of the language was somewhat apparent when it came to the tooling, which was a little underdeveloped when compared to Java especially, and the fluidity of some of the libraries used - Rocket being a good example. This state of flux also meant that a lot of resources were out of date - indeed the solution to a lot of problems lay in finding out how a similar problem was solved in another language, then transliterating the solution into Rust.
