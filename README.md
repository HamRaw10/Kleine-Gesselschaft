# Kleine-Gesselschaft

## Integrantes
* Castiñeira Thiago
* Gonzalez Maximo
* Rudaz Fabrizio

## Descripcion

El juego se va a llamar “kleine Gesellschaft” y el mismo va a consistir en un mundo abierto donde el personaje pueda interactuar libremente con quienes quiera(que sean jugadores), y realice acciones dentro del juego, ya sea comprar, jugar minijuegos, hacer intercambios, chatear, etc. El mismo además va a poseer monedas, con las cuales va a comprar objetos, mascotas, o también para obtener de las monedas, debería vender objetos de su posesión o jugar determinados minijuegos, y además va a poder realizar si quiere, determinadas misiones con las que va a obtener determinadas recompensas.

## Tecnologias Utilizadas
* Java SE 21
* LibGdx
* IDE: Android Studio
* KyroNet (todavia no aplicado)

## Plataformas Objetivo

Nuestro principal objetivo es que Kleine Gesellschaft sea jugable en android con una apk instalable en cualquier dispositivo

* Android 10.0+
* Generar tambien un ejecutable para windows

## Enlace a la Wiki del Proyecto

👉 [Ir a la Wiki del Proyecto](https://github.com/HamRaw10/Kleine-Gesselschaft/wiki)

Aquí se encuentra la propuesta detallada del juego, incluye el alcance, descripcion de la propuestas, personajes, elementos etc.


## Como compilar y ejecutar

### Como instalar los archivos
Primero tendremos que descargar los archivos del proyecto, nos iremos al repositorio y clickearemos en donde dice code en verde (![alt text](image.png)).

Ahi nos aparecera una ventana como esta copiaremos el link https con el boton que tiene el siguiente icono:
![alt text](image-1.png)

Nos dirigiremos a git bash ahi en nuestra carpeta preferida clonaremos el repositorio con el comando git clone y el link que copiamos (lo podemos pegar con click derecho) 
![alt text](image-2.png)

Listo ya tenemos el Kleine Gesselschaft instalado en nuestra computadora

### abrir proyecto en android studio 
Una vez que hayamos abierto la pestaña clickearemos en la opcion open
![alt text](image-3.png)

nos redirigiremos a nuestra carpeta donde anteriormente hayamos abierto clonado el repositorio del juego.
Una vez que ya hayamos hecho eso ya podemos ejecutarlo

### Compilacion y ejecucion del videojuego en android studio
Para compilar el proyecto en Android Studio, nos dirigiremos a la ventana que dice "gradle" (El de este icono ![img_1.png](img_1.png)) 
Y ahora vamos a seleccionar los siguientes pasos: lwjgl3 -> Tasks -> other -> run (En el ultimo "run" se recomienda darle con doble click)

![img.png](img.png)

### Nota:

Para compilar para Android, se deberá configurar un dispositivo virtual o físico con Android 10.0+ y usar los módulos específicos del proyecto (no incluidos aún en esta fase inicial).

## Estado Actual Del Proyecto
* Creamos el repositorio con los archivos del videojuego
* Se instalo y inicializo libgdx con gdx-liftoff y en android studio.
