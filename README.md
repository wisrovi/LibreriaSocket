# LibreriaSocket

Libreria para Android donde se Procesan todas las funcionalidades requeridas para lograr una comunicación socket con un servidor en JAVA, a su vez la libreria mantendra actualizada la conexión, esta aplicacion usa tanto WIFI como Datos Moviles para lograr esta comunicación, pero si se desea, pediente un cambio de configuración se puede restringir para que no use los datos moviles y solo utilice el WIFI para las transacciones.

Contiene un metodo definido para recibir los datos, ya sean datos automaticos o datos respuesta de una solicitud.

Contiene un metodo para enviar datos y hacer solicitudes de información al servidor.

Adicionalmente, y como valor agregado, la libreria contiene un modulo donde se pueden recibir notificaciones PUSH del servidor, aún la app este cerrada, pues la libreria crea un Service en segundo plano.

Este modulo de notificaciones PUSH tambien puede ser usado por el usuario en su propia aplicacion para generar sus propias notificaciones.

Igualmente el usuario puede manipular el inicio y fin del proceso Service en segundo plano, en caso de que se requiera.

Finalmente la libreria cuenta con un statusService que hace que cuando se apague y prenda el dispositivo, el service Socket entre a funcionar, tal como se configuro inicialmente, este servicio inicia en segundo plano, por lo que aunque el usuario no habra la aplicacion, el servicio se mantendra abierto para recibir notificaciones o para ser usado cuando se inicie la aplicación.

Nota: esta libreria fue probada en Android Android 4 en adelante
