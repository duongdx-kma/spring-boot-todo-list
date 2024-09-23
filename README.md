### 1. Starting command:
```bash
export MYSQL_HOST=192.168.63.1
export MYSQL_PORT=3306
export MYSQL_DB=duongdx_db
export MYSQL_USER=root
export MYSQL_PASSWORD=password

java -jar todo-app.jar
```

### 2. Starting with `detach` mode command:
```bash
sudo mkdir -p /var/log/todo-app/
sudo chmod -R 777 /var/log/todo-app/

export MYSQL_HOST=192.168.63.1
export MYSQL_PORT=3306
export MYSQL_DB=duongdx_db
export MYSQL_USER=root
export MYSQL_PASSWORD=password

java -jar /home/deploy/spring-todo-app/target/todo-app-0.0.2-RELEASE.jar > /var/log/todo-app/output.log 2>&1 &
```

### 3. checking app log:
```bash
tail -f /var/log/todo-app/output.log
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

### 6. trying push nexus artifact using `maven`:

#### 6.1 export necessary variables
```bash
#export NEXUS_USERNAME="your_nexus_username"
#export NEXUS_PASSWORD="your_nexus_password"

export NEXUS_DOMAIN="https://nexus.duongdx.com"
export SNAPSHOT_REPO="custom-maven-snapshots"
export RELEASE_REPO="custom-maven-releases"
export PROXY_REPO="custom-maven-proxy"
export NEXUS_GROUP_REPO="custom-maven-group"
export GROUP_ID="io.john.programming"
export ARTIFACT_ID="todo-app"
export ARTIFACT_VERSION="0.0.3-RELEASE"
export IS_SNAPSHOT=false  # Set to true if deploying a snapshot version
```

#### 6.2 install and package artifacts:
```bash
mvn -s settings.xml clean install -DskipTests
```

#### 6.3 publish artifacts to `RELEASE` repo: `.jar` and `pom` file
```bash
# must set (before 6.2)
export ARTIFACT_VERSION="0.0.3-RELEASE"

mvn -s settings.xml deploy:deploy-file \
  -DgroupId=${GROUP_ID} \
  -DartifactId=${ARTIFACT_ID} \
  -Dversion=${ARTIFACT_VERSION} \
  -Dpackaging=jar \
  -Dfile=target/${ARTIFACT_ID}-${ARTIFACT_VERSION}.jar \
  -DpomFile=pom.xml \
  -DrepositoryId=${RELEASE_REPO} \
  -Durl=${NEXUS_DOMAIN}/repository/${RELEASE_REPO}
```

#### 6.4 publish artifacts to `SNAPSHOT` repo: `.jar` and `pom` file
```bash
# must set (before 6.2)
export ARTIFACT_VERSION="0.0.3-SNAPSHOT"

mvn -s settings.xml deploy:deploy-file \
  -DgroupId=${GROUP_ID} \
  -DartifactId=${ARTIFACT_ID} \
  -Dversion=${ARTIFACT_VERSION} \
  -Dpackaging=jar \
  -Dfile=target/${ARTIFACT_ID}-${ARTIFACT_VERSION}.jar \
  -DpomFile=pom.xml \
  -DrepositoryId=${SNAPSHOT_REPO} \
  -Durl=${NEXUS_DOMAIN}/repository/${SNAPSHOT_REPO}
```