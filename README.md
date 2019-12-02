# google-file-system
An implementation of the Google File System which aims at the performing the following operations
* Creating a file
* Reading an existing file from a given offset
* Appending to an existing file with the provided datasize

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
### Softwares/SDKs
1. Java 8 or later
2. Maven setup to build the project

### Steps to Run the Application
#### Build the jar
1. Go to the root of the project and execute ``mvn clean install``
2. Note: If you are using eclipse for viewing and running the source code, you should have the lombok setup for eclipse.

```
For lombok setup, go to: https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/
```
#### Execute the jar
The jar should be executed on all the clients and server as defined in the applicationConfig.json file. Within 60 seconds of time the jar should be executed on all the clients. In the below command the log.file should be different for each client and server
1. ``java -jar -Dlog.file=meta.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
2. ``java -jar -Dlog.file=client1.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
3. ``java -jar -Dlog.file=client2.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
4. ``java -jar -Dlog.file=chunk4.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
5. ``java -jar -Dlog.file=chunk5.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
6. ``java -jar -Dlog.file=chunk6.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
7. ``java -jar -Dlog.file=chunk7.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``
8. ``java -jar -Dlog.file=chunk8.txt -Dgfs.config=applicationConfig.json gfs-0.0.1-SNAPSHOT.jar``

### Verification
* To verify, go to each logfile generated above and verify the contents.
* The logs are generated under ``logfile/`` folder from where the run has been triggered.

### Authors
* **Amtul Nazneen** - [Amtul](https://github.com/amtul-nazneen)
* **Pankaj Kr Jhawar** - [Pankaj](https://github.com/jhawarpankaj)