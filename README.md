# NeuralNetworksClientServer
A server and client side to visualize the evolution of neural networks in a survival world

### How to use
#### Requirements
This project requires thoes to be installed on your system:
- Java 7+
- Maven (needs oracle java version for build, not openjdk)

To build the server part, use this command:
```bash
cd NeuralNetworksServer
mvn package
```
To run the server:
```bash
cd target
java -jar NeuralNetworksServer-0.0.1-SNAPSHOT.jar
```
To build the client part, use this command:
```bash
cd NeuralNetworksClient
mvn package
```
To run the client:
```bash
cd target
java -Djava.library.path=natives/ -jar neuralnetworkstest-0.0.1-SNAPSHOT.jar
```
Or directly by using the sh runscript:
```bash
./run.sh
```
