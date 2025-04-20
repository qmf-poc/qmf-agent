## QMF PoC Agent

### Build

put additional files in the local repo

for each `file.jar` and corresponding `artifactId`:

```shell
mvn install:install-file -Dfile=path/to/file.jar -DgroupId=s4y.solutions -DartifactId=file -Dversion=1.0.0 -Dpackaging=jar -DlocalRepositoryPath=lib
```

and after all the jars added
```shell
 mvn package -U
```

```shell
mvn package
```
### Run

```shell
java -jar target/agent-0.1.0-SNAPSHOT.jar --help
```

typical use (listen for locally running service):
```shell
java -jar target/agent-0.1.0-SNAPSHOT.jar -a
```

announce the agent under name `agent1`:
```shell
java -jar target/agent-0.1.0-SNAPSHOT.jar -d agent1
```

listen for remote service:
```shell
java -jar target/agent-0.1.0-SNAPSHOT.jar -w ws://localhost:8081/agent
```

diagnose a database
```shell
java -jar target/agent-0.1.0-SNAPSHOT.jar -g
```

stress test a database
```shell
java -jar target/agent-0.1.0-SNAPSHOT.jar -g --parallel --repeat 3
```

logging
```shell
java -Dorg.slf4j.simpleLogger.defaultLogLevel=INFO|DEBUG|TRACE -jar target/agent-0.1.0-SNAPSHOT.jar ...
```