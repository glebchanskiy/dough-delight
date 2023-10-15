# Kek
Kek - simple HTTP server

### Installation

1. Clone the repository:

    ```
    git clone https://github.com/glebchanskiy/kek
    ```

2. Navigate to the project directory:

    ```
    cd kek
    ```

3. Install project:

    ```
    ./gradlew publishToMavenLocal
    ```
   
   Now you can use this server in your Java application as dependencies.
   
   ```groovy
   dependencies {
     implementation 'org.glebchanskiy:kek:0.2.0'
   }
   ```

4. MVP usage:
   ```java
   package org.glebchanskiy;
   
   import org.glebchanskiy.kek.router.controllers.impl.ShareFilesController;
   import org.glebchanskiy.kek.Configuration;
   import org.glebchanskiy.kek.ConnectionsManager;
   import org.glebchanskiy.kek.Server;
   import org.glebchanskiy.kek.router.FilterRouter;
   import org.glebchanskiy.kek.router.filters.CorsFilter;
   import org.glebchanskiy.kek.utils.Mapper;
   
   import java.util.concurrent.Executors;
   
   public class Application {
   
       public static void main(String... args) {
   
           var config = Configuration.parseArgs(args);
   
           var router = new FilterRouter(config);
           router.addFilter(new CorsFilter(config));
           router.addController(new ShareFilesController("/", config));
   
           var server = Server.builder()
                   .connectionsManager(new ConnectionsManager(config))
                   .executorService(Executors.newFixedThreadPool(10))
                   .mapper(new Mapper())
                   .router(router)
                   .build();
   
           server.run();
       }
   }
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

You can override the configuration and pass the path using the --config argument.

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

### Template engine example:

To use the built-in template engine, just inherit from the TemplateController class.
You can implement `getMapping` and `postMapping`. Each method include a request and a model.

```java
package org.glebchanskiy.controller;

import org.glebchanskiy.kek.router.controllers.TemplateController;
import org.glebchanskiy.kek.templater.Model;
import org.glebchanskiy.kek.utils.Request;
import org.glebchanskiy.model.User;

import java.util.ArrayList;
import java.util.List;

public class TestTemplateController extends TemplateController {
   public TestTemplateController(String route) {
      super(route);
   }

   @Override
   public String getMapping(Model model, Request request) {
      List<User> users = new ArrayList<>(List.of(
              new User("Jotaro"),
              new User("Dio"),
              new User("Polnareff")
      ));

      model.put("users", users);
      model.put("rohan", new User("Rohan"));
      return "/landing.html";
   }
}
```

All the provided data in the template is accessed using key names.
Sample template:
```html
   <span>Users:</span>
   <ul>
      {for user in users : {<li>{user.username}</li>}}
   </ul>
   <span>
      {{rohan.username}}
   </span>
```