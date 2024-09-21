### 1. Starting command:
```bash
MYSQL_HOST=192.168.63.1
MYSQL_PORT=3306
MYSQL_DB=duongdx_db
MYSQL_USER=root
MYSQL_PASSWORD=password

java -jar target/todo-app.jar
```

### 2. Starting with `detach` mode command:
```bash
MYSQL_HOST=192.168.63.1
MYSQL_PORT=3306
MYSQL_DB=duongdx_db
MYSQL_USER=root
MYSQL_PASSWORD=password

nohup java -jar target/todo-app.jar > /var/logs/todo-app/output.log 2>&1
```

### 3. checking app log:
```bash
tail -f /var/logs/todo-app/output.log
```

### 4. Stop command:
```bash
# finding java process
ps aux | grep java

# kill process
kill -9 $PROCESS_ID
```

