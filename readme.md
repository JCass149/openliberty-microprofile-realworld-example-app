# ![RealWorld Example App](Images/logo.png)

> ### [OpenLiberty](https://openliberty.io/) + [MicroProfile](https://microprofile.io/) + JPA codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.


### [Demo](https://github.com/gothinkster/realworld)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)


This codebase was created to demonstrate a fully fledged fullstack application built with **OpenLiberty** including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Getting started

The code has been tested with Java 13 and Apache Maven 3.6.1, though should work with a minimum of Java 8.

To start the server in [dev mode](https://openliberty.io/docs/latest/development-mode.html), run: 

```
git clone https://github.com/JCass149/openliberty-microprofile-realworld-example-app.git
cd openliberty-microprofile-realworld-example-app
mvn liberty:dev
```

Then head to http://localhost:9080/openapi/ui to interact with the endpoints defined in [the spec](https://github.com/gothinkster/realworld/tree/master/api#single-article).

For more information on starting an OpenLiberty server, see [Getting started with Open Liberty](https://openliberty.io/guides/getting-started.html).

Some endpoints require authentication. To simulate signing in, from the OpenAPI UI:
1. *Create a User*: Click on the `/users` POST endpoint, then click `Try it out`. Update the `Request body` to the details you'd like then hit `Execute`.
2. *Authenticate*: Once your new User has been created, copy the `token` from the `Response Body`. Click the green `Authorize` button (with the pad lock icon) in the top right of the UI, and paste the token value into the `Value` box before hitting the `Authorize` button. This will make any requests made using the UI from the new User with their valid JWT token, until either the token expires or the page is refreshed.

Note: The `/users/login` endpoint doesn't actually authenticate the specified user. It simply returns a valid JWT token, which can then be used to authenticate the session as described in point #2.

# How it works

The application utilises MicroProfile RestClient to make https requests to the system. MicroProfile OpenAPI is used to visualise and describe the endpoints. 

The system uses the Java Persistence API to communicate with the Derby relational database.

### File structure
```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── api
│   │   │       ├── ApiApplication.java
│   │   │       ├── ApiClient.java
│   │   │       ├── ApiClientController.java
│   │   │       ├── dao
│   │   │       │   ├── ArticleDAO.java
│   │   │       │   ├── CommentDAO.java
│   │   │       │   ├── ProfileDAO.java
│   │   │       │   └── TagDAO.java
│   │   │       ├── model
│   │   │       │   ├── Article.java
│   │   │       │   ├── Comment.java
│   │   │       │   ├── Profile.java
│   │   │       │   └── Tag.java
│   │   │       ├── rest
│   │   │       │   ├── ArticleResource.java
│   │   │       │   ├── LoginResource.java
│   │   │       │   ├── ProfileResource.java
│   │   │       │   ├── TagResource.java
│   │   │       │   └── UserResource.java
│   │   │       └── utils
│   │   │           ├── BuildReturnObject.java
│   │   │           └── GenerateJWT.java
│   │   ├── liberty
│   │   │   └── config
│   │   │       ├── resources
│   │   │       │   └── security
│   │   │       │       └── key.p12
│   │   │       └── server.xml
│   │   ├── resources
│   │   │   └── META-INF
│   │   │       ├── microprofile-config.properties
│   │   │       └── persistence.xml
│   │   └── webapp
│   │       ├── WEB-INF
│   │       │   └── web.xml
│   │       └── index.html
│   └── test
│       └── java
│           └── it
│               └── api
│                   └── rest
│                       └── EndpointsIT.java
```