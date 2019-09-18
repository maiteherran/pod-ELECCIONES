#TP1 - PROGRAMACIÓN DE OBJETOS DISTRIBUIDOS 
##ELECCIONES

#Pre-requisitos
Instalar maven

#Compilación
1. Situarse en el directorio del proyecto
2. Ejecutar, desde la terminal:
  $> chmod u+x make.sh
  $>./make.sh
 
 #Ejecución
 
 ####Server
Ejecutar en la terminal desde el directorio del proyecto:
    $> cd server/target/tpe-server-1.0-SNAPSHOT
    $> ./run-server.sh
    
El puerto default por el que escucha el servidor es el 1099. Si se quiere utilizar otro puerto, pasarlo como parámetro de la siguiente forma:
    $> ./run-server.sh -Dport=portNumber
    
        donde portNumber es el número de puerto por el que escuchará el servidor
    
####Management Client
Ejecutar en la terminal desde el directorio del proyecto:
    $> cd client/target/tpe-client-1.0-SNAPSHOT
    $> ./run-ManagementClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName 
    
         donde
                - xx.xx.xx.xx:yyyy: es la dirección IP y el puerto donde está publicado el servicio de
                                    administración de los comicios.
                - actionName: es el nombre de la acción a realizar.
                     ○ open ​: Abre los comicios. 
                     ○ state ​: Consulta el estado de los comicios.
                     ○ close ​: Cierra los comicios.
                     
#####Vote Client
Ejecutar en la terminal desde el directorio del proyecto:
    $> cd client/target/tpe-client-1.0-SNAPSHOT
    $> ./run-VoteClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -DvotesPath= ​fileName
    
        donde
                - xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de
                 votación.
                - fileName ​ ​es el path del archivo csv de entrada con los votos de los ciudadanos
            
#####Query Client
Ejecutar en la terminal desde el directorio del proyecto:
    $> cd client/target/tpe-client-1.0-SNAPSHOT
    $> ./run-QueryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy [ -Dstate= ​stateName ​| -Did= ​pollingPlaceNumber ​ ] -DoutPath= ​fileName ​ ar.edu.itba.pod.client.QueryClient
               
     donde
                - xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de
                 votación.     
                - Si no se indica ​-Dstate ​ ni ​-Did ​ se resuelve la consulta 1
                - Si se indica ​-Dstate ​, ​stateName es el nombre de la provincia elegida para resolver la
                  consulta 2.
                - Si se indica ​-Did ​, ​pollingPlaceNumber ​es el número de la mesa elegida para resolver la
                   consulta 3
                - fileName ​ es el path del archivo csv de salida con los resultados de la consulta elegida       
