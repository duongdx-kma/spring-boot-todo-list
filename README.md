### 1. Starting command:
```bash
MYSQL_HOST=192.168.63.1 \
MYSQL_PORT=3306 \
MYSQL_DB=duongdx_db \
MYSQL_USER=root \
MYSQL_PASSWORD=password \
java -jar todo-app.jar
```

### 2. Starting with `detach` mode command:
```bash
mkdir -p /var/logs/todo-app/
chmod -R 777 /var/logs/todo-app/

nohup sh -c 'MYSQL_HOST=192.168.63.1 \
            MYSQL_PORT=3306 \
            MYSQL_DB=duongdx_db \
            MYSQL_USER=root \
            MYSQL_PASSWORD=password \
            java -jar /home/deploy/spring-todo-app/target/todo-app-0.0.2-RELEASE.jar' > /var/logs/todo-app/output.log 2>&1 &
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

### 5. create mysql
```sql
-- Drop tables if they exist (to avoid conflicts)
DROP TABLE IF EXISTS user_todo_list;
DROP TABLE IF EXISTS todo;
DROP TABLE IF EXISTS user;

-- Create 'todo' table
CREATE TABLE IF NOT EXISTS todo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    completed TINYINT(1) DEFAULT '0',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create 'user' table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create 'user_todo_list' table with foreign keys (fields can be NULL)
CREATE TABLE IF NOT EXISTS user_todo_list (
    user_id BIGINT NULL,
    todo_id BIGINT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (todo_id) REFERENCES todo (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Optional: Create sequence table for Hibernate (if necessary)
CREATE TABLE IF NOT EXISTS hibernate_sequence (
    next_val BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Initialize the sequence table (if needed)
INSERT INTO hibernate_sequence (next_val) VALUES (1) ON DUPLICATE KEY UPDATE next_val = next_val;
```