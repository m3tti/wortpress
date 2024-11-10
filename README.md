# WortPress
A simple web minimal blog engine. The goal is to just provide the totally necessary stuff 
to get the word out. The main workflow for wortpress is.

1. Create admin user with `bb -m commands.user/create-admin <email> <password>`
2. Go to `http://localhost:8080/login` and log in
3. Go to settings `http://localhost:8080/setting` and chose *primary* color, *secondary* color and *font* (from google fonts)
4. Go to posts `http://localhost:8080/post` and start writing.

## Used technology
- [borkweb](https://borkweb.org)
- [postgres](https://www.postgresql.org/)

# Hosting
You'll need a reverse proxy and a postgres database. A postgres container is already part of the project and can be started with `docker-compose`. The initial configs can be found in the `src/config.clj` Here you have to setup your postgres connection and the name of your blog.
After that you can setup the database with `bb -m database.core/initialize-db`. 
After that you have to create an admin user with `bb -m commands.user/create-admin <email> <password>` 
Now you are ready to go. The login can be found at `/login`.
