# Webserver Project - Spring 2025

**Team Members**: Daniel Omstead, Ryan Heath

## Simple Java HTTP Server

A lightweight implementation of an HTTP/1.1 server written in Java, supporting basic HTTP operations and authentication.

### Overview

This project implements a simple web server with the following HTTP methods:
- **GET**: Retrieves files with a `200 OK` or `404 Not Found` response.
- **HEAD**: Retrieves headers only with a `200 OK` or `404 Not Found` response.
- **PUT**: Creates or overwrites files with a `201 Created` response.
- **DELETE**: Deletes files with a `204 No Content` response.

The server supports basic authentication for protected resources using a `.password` file and handles simultaneous requests with threading. 
 Do not look at the body.parse and HTTPRequest.parse methods, it's horrible and I made a billion debug messages because I kept messing things up.
 There is an ungodly number of debug prints in prod because I spent all night trying to fix my own user error because I don't understand curl syntax...

### Requirements

- Java JDK 8 or higher
- Windows Command Prompt (or any CLI)
- Optional: `curl` for testing

### Installation

1. **Clone or Download**:
   - Clone the repository or download the source code to `G:\gitHubG\web-server-daniel-omstead-ryan-heath\`.

2. **Directory Structure**:
   Ensure all `.java` files are in `server\src\server\` with subdirectories:

   G:\gitHubG\web-server-daniel-omstead-ryan-heath
   ├───server
   │   └───src
   │       └───server
   │           ├───WebServer.java
   │           ├───auth
   │           │   ├───AuthenticationManager.java
   │           │   └───AuthenticationResponse.java
   │           ├───config
   │           │   └───MimeTypes.java
   │           ├───err
   │           │   └───HTTPException.java
   │           ├───http
   │           │   ├───Body.java
   │           │   ├───Headers.java
   │           │   ├───HTTPMethod.java
   │           │   ├───HTTPRequest.java
   │           │   ├───RequestLine.java
   │           │   └───StatusLine.java
   │           ├───request
   │           │   └───RequestHandler.java
   │           └───response
   │               ├───BadRequest.java
   │               ├───Created.java
   │               ├───Forbidden.java
   │               ├───InternalServerError.java
   │               ├───NoContent.java
   │               ├───NotFound.java
   │               ├───Ok.java
   │               ├───Response.java
   │               └───Unauthorized.java
   ├───bin\            # Compiled output
   └───docroot\        # Document root for testing


	
## Building the Project

1. **Navigate to Source Directory**:

   cd G:\gitHubG\web-server-daniel-omstead-ryan-heath\server\src

2. **Compile**: 
				

   javac -d ..\bin server*.java server\request*.java server\response*.java server\http*.java server\auth*.java server\err*.java server\config*.java

### Running the Server

- Start the server from the `src` directory: (change to match your directories, feel free to run as admin or change paths if needed to obtain write access on your system) eg:

  java -cp ..\bin server.WebServer 9999 G:\gitHubG\web-server-daniel-omstead-ryan-heath\docroot

- `<port>`: Port number (e.g., 9999).
- `<document_root>`: Absolute path to serve files from (e.g., `G:\gitHubG\web-server-daniel-omstead-ryan-heath\docroot`).

### Features

#### Basic Request Handling
- **GET**:
- `200 OK`: File exists.
- `404 Not Found`: File doesn’t exist.
- **HEAD**:
- `200 OK`: Headers only for existing files.
- `404 Not Found`: Non-existent files.
- **PUT**:
- `201 Created`: File created or overwritten.
- `500 Internal Server Error`: Creation fails.
- **DELETE**:
- `204 No Content`: File deleted.
- `404 Not Found`: File doesn’t exist.
- `500 Internal Server Error`: Deletion fails.

#### Authentication
- Uses Basic HTTP Authentication with a `.password` file in protected directories.
- Format: `username:password` (e.g., `jrob:secret`).
- Responses:
- `401 Unauthorized`: No credentials provided.
- `403 Forbidden`: Invalid credentials.
- `200 OK`: Valid credentials.

#### Threading
- Handles simultaneous requests by spawning a new thread per client connection.

### Testing

#### Setup Test Environment
Create a test directory:

G:\gitHubG\web-server-daniel-omstead-ryan-heath\docroot
├───index.html         (echo "<h1>Hello</h1>" > docroot\index.html)
├───secure
│   ├───.password      (echo "jrob:secret" > docroot\secure.password)
│   └───secret.html    (echo "<h1>Secret</h1>" > docroot\secure\secret.html)
└───upload\

#### Basic Operations
- **GET Existing File**:

  curl -i http://localhost:9999/index.html

- **GET Non-Existent File**:

  curl -i http://localhost:9999/missing.txt

- **HEAD Existing File**:

  curl -I http://localhost:9999/index.html

- **PUT New File**:

  curl -i -X PUT --data "Test content" http://localhost:9999/upload/test.txt

- **DELETE File**:

  curl -i -X DELETE http://localhost:9999/upload/test.txt

#### Authentication
- **No Credentials**:

  curl -i http://localhost:9999/secure/secret.html

- **Wrong Credentials**:

  curl -i -u wrong:wrong http://localhost:9999/secure/secret.html

- **Correct Credentials**:

  curl -i -u jrob:supersecretpassword http://localhost:9999/secure/secret.html

### Response Status Codes
- `200 OK`: Successful request.
- `201 Created`: Resource created (PUT).
- `204 No Content`: Resource deleted (DELETE).
- `400 Bad Request`: Invalid syntax.
- `401 Unauthorized`: Authentication required.
- `403 Forbidden`: Authentication failed.
- `404 Not Found`: Resource not found.
- `500 Internal Server Error`: Server error.

### Troubleshooting
- **Server Won’t Start**:
- Check port 9999 availability (`netstat -a | find "9999"`).
- Verify `docroot` exists and is readable.
- **Authentication Issues**:
- Ensure `.password` file has no hidden extensions (e.g., `.password.txt` on Windows).
- Use `type docroot\secure\.password` to check contents.
- **PUT/DELETE Failures**:
- Confirm write permissions:
  ```
  icacls G:\gitHubG\web-server-daniel-omstead-ryan-heath\docroot /grant "Users:F"
  ```

### Notes
- Built and tested on Windows 10/11 with Command Prompt.
- Uses threading for concurrent request handling.


## Sample User session log including successful and failed test cases:

Microsoft Windows [Version 10.0.19045.5371]
(c) Microsoft Corporation. All rights reserved.

C:\Users\Daniel>curl -i http://localhost:9999/index.html
HTTP/1.1 200 OK
date: Mon, 03 Mar 2025 06:13:13 GMT
content-length: 35
content-type: text/html

<h1>Welcome to the Test Server</h1>
C:\Users\Daniel>curl -i http://localhost:9999/nonexistent.txt
HTTP/1.1 404 Not Found
date: Mon, 03 Mar 2025 06:13:18 GMT
content-type: text/html

<h1>404 Not Found</h1>
C:\Users\Daniel>curl -I http://localhost:9999/index.html
HTTP/1.1 200 OK
date: Mon, 03 Mar 2025 06:13:35 GMT
content-length: 35
content-type: text/html


C:\Users\Daniel>curl -I http://localhost:9999/nonexistent.txt
HTTP/1.1 404 Not Found
date: Mon, 03 Mar 2025 06:13:42 GMT
content-type: text/html


C:\Users\Daniel>curl -i -X PUT --data "This is a new file" http://localhost:9999/upload/newfile.txt
HTTP/1.1 201 Created
date: Mon, 03 Mar 2025 06:13:51 GMT


C:\Users\Daniel>curl -i -X DELETE http://localhost:9999/upload/newfile.txt
HTTP/1.1 204 No Content
date: Mon, 03 Mar 2025 06:14:00 GMT


C:\Users\Daniel>curl -i -X DELETE http://localhost:9999/upload/newfile.txt
HTTP/1.1 404 Not Found
date: Mon, 03 Mar 2025 06:14:08 GMT
content-type: text/html

<h1>404 Not Found</h1>
C:\Users\Daniel>curl -i http://localhost:9999/secure/secret.html
HTTP/1.1 401 Unauthorized
date: Mon, 03 Mar 2025 06:14:15 GMT
www-authenticate: Basic realm="667 Server"
content-type: text/html

<h1>401 Unauthorized</h1>
C:\Users\Daniel>curl -i -u wrong:wrong http://localhost:9999/secure/secret.html
HTTP/1.1 403 Forbidden
date: Mon, 03 Mar 2025 06:14:21 GMT
content-type: text/html

<h1>403 Forbidden</h1>
C:\Users\Daniel>curl -i -u jrob:supersecretpassword http://localhost:9999/secure/secret.html
HTTP/1.1 200 OK
date: Mon, 03 Mar 2025 06:14:37 GMT
content-length: 20
content-type: text/html

<h1>Secret Page</h1>
C:\Users\Daniel>curl -i -X POST http://localhost:9999/index.html
HTTP/1.1 400 Bad Request
date: Mon, 03 Mar 2025 06:14:45 GMT
content-type: text/html

<h1>400 Bad Request</h1>
C:\Users\Daniel>curl -i -X PUT http://localhost:9999/upload/empty.txt
HTTP/1.1 400 Bad Request
date: Mon, 03 Mar 2025 06:14:49 GMT
content-type: text/html

<h1>400 Bad Request</h1>
C:\Users\Daniel>
