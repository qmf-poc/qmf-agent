## QMF PoC Agent

### Build

```
mvn package
```

### Run

```
java -jar target/agent-0.1.0-SNAPSHOT.jar --help
```

typical use (listen for locally running service):
```
java -jar target/agent-0.1.0-SNAPSHOT.jar -a
```

listen for remote service:
```
java -jar target/agent-0.1.0-SNAPSHOT.jar -w ws://localhost:8081/agent
```

diagnose a database
```
java -jar target/agent-0.1.0-SNAPSHOT.jar -g
```

stress test a database
```
java -jar target/agent-0.1.0-SNAPSHOT.jar -g --parallel --repeat 3
```

logging
```
java -Dorg.slf4j.simpleLogger.defaultLogLevel=INFO|DEBUG|TRACE -jar target/agent-0.1.0-SNAPSHOT.jar ...
```