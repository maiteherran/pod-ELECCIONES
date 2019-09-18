# Compilar
mvn clean install

# Extraer Server files
tar -xf server/target/tpe-server-1.0-SNAPSHOT-bin.tar.gz -C server/target
chmod u+x server/target/tpe-server-1.0-SNAPSHOT/run-server.sh

# Extraer Client files
tar -xf client/target/tpe-client-1.0-SNAPSHOT-bin.tar.gz -C client/target
chmod u+x client/target/tpe-client-1.0-SNAPSHOT/run-ManagementClient.sh
chmod u+x client/target/tpe-client-1.0-SNAPSHOT/run-VoteClient.sh
chmod u+x client/target/tpe-client-1.0-SNAPSHOT/run-QueryClient.sh