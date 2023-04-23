# AA_Programacion_Multihilo
Trabajo de la asignatura de Programación de servicios y procesos (JavaFX)
Consiste en un programa de multidescarga que puede lanzar a la vez y de forma concurrente, varias descargas
a traves de enlaces directos o leyendo un archivo DLC con una lista de links.

Requisitos implementados:
	Obligatorios
    • Posibilidad de descargar múltiples ficheros al mismo tiempo 
    • Por cada descarga se irá indicando el progreso de descarga tanto en tamaño como en porcentaje total descargado
    • Todas las descargas deben poderse cancelar y eliminar de la ventana de la aplicación
    • La ruta donde se descargan los ficheros, que será fija, se podrá configurar desde la aplicación
    • Se mantendrá un historial de todos los ficheros descargados por la aplicación y todas las descargas fallidas/canceladas. Este fichero se almacenará como fichero de registro y podrá consultarse desde el interfaz de usuario
	Opcionales
    • Programar el comienzo de una descarga para un momento determinado
    • La aplicación podrá leer listas de enlaces de un fichero de texto y encolará las descargas
    • Al cancelar la descarga, opcionalmente para el usuario, se podrá eliminar el fichero que se estaba descargando o se había descargado
    • Mostrar la velocidad de descarga (MB/s) en todo momento
    • Realizar el seguimiento del proyecto utilizando la plataforma GitHub para almacenar el código y gestionando las issues (bug, mejoras, . . .) a medida que se vaya trabajando en él