# DisasterMesh

DisasterMesh is a Java-based decentralized emergency-reporting system built for disaster scenarios where a central server or stable internet connection can't be relied on. Instead of a client talking to one server, every participant (a citizen, a rescue team, a hospital) runs the same kind of network node. Nodes discover each other automatically over the local network and pass emergency reports (SOS messages) along from node to node until they reach someone who can act — even if the sender and the responder are never online at the same time.

## Key features

- **Peer-to-peer mesh network** over UDP multicast — no central server
- **Automatic peer discovery** via a periodic HELLO broadcast
- **Store-and-forward delivery** — a node holds a message it can't deliver yet and passes it on the moment a new peer appears
- **TTL-based flooding** so messages don't circulate forever, with duplicate detection
- **Delivery acknowledgements (ACK)** so the original sender knows a message was received
- **Role-based domain model** — Emergency Operators, Rescue Officers, Hospital Liaisons, Operations Monitors
- **Four emergency types** (Flood, Fire, Earthquake, Accident), each with its own priority-scoring rule
- **CSV-based persistence** — no database required

## Project structure

```
src/
├── mesh/          → the network layer: nodes, message routing, SOS/ACK, discovery
├── model/         → domain objects: User, Emergency, Hospital, RescueTeam, Assignment
├── protocol/      → command/request/response wrapper types
├── persistence/   → CsvDataManager — reads/writes all data as CSV
├── interfaces/    → shared contracts: Notifiable, Reportable, Assignable, StatusUpdatable
├── util/          → shared constants, ID generation, input validation
└── demo/          → runnable entry points (see below)

data/              → seeded CSV files (users, hospitals, rescue teams, emergencies, assignments)
```

## How the network works, in short

Every node opens the same UDP multicast socket and both broadcasts and listens on it — there's no dedicated server. Each node runs three threads: one that listens for incoming messages, one that broadcasts a "HELLO" every 1.5 seconds so peers can find each other, and one that retries delivery of anything it's still holding. When a node can't reach a recipient yet, it queues the message locally instead of dropping it, and delivers it automatically the moment a new peer comes online.

## Requirements

- Java JDK 11 or later
- No external libraries or build tool needed — plain `javac`/`java`

## How to compile

From the project root:

```bash
javac -d out $(find src -name "*.java")
```

## How to run

Each class in `src/demo/` is an independent, runnable entry point.

**Interactive console apps** (open a real socket, run in the background, and give you a menu):

```bash
java -cp out demo.MeshIntegrationDemo USER-1
java -cp out demo.RescueNodeDemo      RESCUE-1
java -cp out demo.HospitalNodeDemo    HOSPITAL-1
java -cp out demo.MeshRouterDemo      RELAY-1
```

Run two or more of these in separate terminals — they'll discover each other automatically over the network.

**Scripted demos** (no input needed, print proof that a mechanism works):

```bash
java -cp out demo.HelloDemo
java -cp out demo.SosMessageDemo
java -cp out demo.AckMessageDemo
java -cp out demo.SosForwardingDemo
java -cp out demo.AckForwardingDemo
java -cp out demo.MeshMultiHopDemo
java -cp out demo.FileHandlingDemo
```

`MeshMultiHopDemo` is the most complete single demo — it proves discovery, store-and-forward, TTL handling, duplicate suppression, and the ACK round trip all in one run, with no keyboard input required.

## Data

All data lives in `data/` as plain CSV files, managed entirely by `CsvDataManager` — no database is used. Each `data/*.csv` file is created automatically (with the correct header row) the first time it's needed if it doesn't already exist.
