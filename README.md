# POSTS PROJECTS

The compiled projects can be found in the ``build`` directory. To run them open a terminal in the directory, and use the command ``java -jar posts_backend.jar & serve -p 8080 -s frontend`` or the script ``start.sh`` provided.

#### BACKEND

###### How to run

To run this project use the command ``java -jar build/posts_backend.jar``.  
By default the server runs on port 38080. 


###### APIs

This app exposes only one API :

GET ``/post/posts``  
Returns :
- Status code 202 (Accepted) : JSON string containing the list of posts sorted by title
- Status code 204 (No Content) : Empty body. There is no post stored in DB.
- Status code 500 (Internal Error) : empty body, an error occurred.

###### Default Configuration :
The default configuration used by the App. It can be overriden by  
providing your own ``application.properties`` file, or pass the  
desired properties as parameters in the launching command.
````
  server.port=38080
  server.servlet.context-path=/post
  
  management.endpoints.enabled-by-default=true
  management.endpoints.web.exposure.include=info,health
  
  ## MongoDB server
  # URL
  spring.data.mongodb.host=localhost
  # Port
  spring.data.mongodb.port=27017
  # DB name
  spring.data.mongodb.database=posts
  # MongoDB storage dir on local filesystem
  spring.mongodb.embedded.storage.database-dir=.db
  ##

  # URL to retrieve posts
  posts.retrieval.url=https://jsonplaceholder.typicode.com/posts
  # Set to true to force the retrieval of posts at startup. Else the posts are only retrieved if the BD is empty.
  posts.retrieval.force-at-startup=false
  # Retrieval retry mechanism max number of attempts
  posts.retrieval.max-retry=5
  # Delay between two retries in ms
  posts.retrieval.backoff-delay=500 
````

###### Run the tests
To run all the unit tests, use the gradle task ``unitTest`` :  
``./gradlew clean unitTest``.

To run the integration tests, use the gradle task ``integrationTest`` :  
``./gradlew clean integrationTest``.

---
#### Front-end
###### How to run
To launch this project run the command ``serve -p 8080 -s frontend``.
The posts are then visibles by going to ``http://localhost:8080/`` with 
your web browser.  
Node.js must be installed in order to run the front-end.

By default, the app will request the API ``http://localhost:38080/post/posts``
to retrieve the posts and display them.

#### Source code

Backend project : https://github.com/Rocknar94/posts_backend.git
Frontend project :  https://github.com/Rocknar94/frontend.git
