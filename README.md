# Kek
Kek - simple HTTP server

### Installation and Running

1. Clone the repository:

    ```
    git clone https://github.com/glebchanskiy/kek
    ```

2. Navigate to the project directory:

    ```
    cd kek
    ```

3. Build project:

    ```
    ./gradlew build
    ```

4. Run the server:

    ```
    java -jar build/libs/kek-server.jar
    ```


### Arguments

- `-c | --config`: Server config path.
- `-p | --port`: Server port.
- `-l | --location`: Work directory.

## YAML Configuration 

Default configuration:

```yaml
port: 8080
hostname: 127.0.0.1
location: path/to/work/directory
cors: "*"
```

You can override the configuration and pass the path using the -config argument.

### Usage Examples

1. Example:

    ```
    java -jar build/libs/kek-server.jar
    ```

   Running the server with the default configuration. The work directory is the directory in which the user runs the server. The port number is set to 8080.

2. Example:

    ```
    java -jar build/libs/kek-server.jar --config path/to/config.yml
    ```

   Running the server with custom configuration.

3. Example:

    ```
    java -jar build/libs/kek-server.jar -p 8090 -l ~/Documents --cors localhost:8090
    ```

   Configure the server using command line arguments.