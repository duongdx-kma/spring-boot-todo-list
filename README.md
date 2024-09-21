### 1. Starting command:
```bash
MYSQL_HOST=your-mysql-host
MYSQL_DB=your-database
MYSQL_USER=your-username
MYSQL_PASSWORD=your-password

java -jar target/todo-app.jar
```

### 2. Starting with `detach` mode command:
```bash
MYSQL_HOST=your-mysql-host
MYSQL_DB=your-database
MYSQL_USER=your-username
MYSQL_PASSWORD=your-password

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

