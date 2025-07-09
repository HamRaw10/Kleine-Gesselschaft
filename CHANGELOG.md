# Changelog

Este archivo documenta los cambios importantes realizados en el proyecto a lo largo del tiempo.

## [Unreleased]

## [1.0.0] - 2025-05-25
### Added
- Inicialización del proyecto con libGDX y Android Studio.
- Creación del mapa básico.
- Creación de los archivos `README.md` y `CHANGELOG.md`.
- Configuración inicial de la Wiki del proyecto con imágenes ilustrativas.

### Changed
- Se agrego un circulo que proximamente representara un personaje que se puede mover con teclas y con clicks en pantalla.
- Se agrego movimiento para ese circulo, en el cual el mismo se mueve hacia donde el mouse lo indique, de forma gradual, al 
clickear en algun lugar de la pantalla, ese ciruclo se va a dirigir gradualmente ahi, ademas se agregaron limites para el circulo, dentro de la pantalla.
- Se agrego un asset, el cual es un personaje que reemplaza al circulo, y el mismo se mueve igual que el circulo que ya se ha configurado

### Fixed
- No se ha arreglado nada.

## [1.1.0] - 2025-07-06

### Added
- Incorporación de múltiples **assets** para animaciones de personajes.
- Implementación de **animaciones para el personaje principal**, con desplazamientos en las siguientes direcciones:
  - Arriba
  - Abajo
  - Derecha
  - Izquierda
  según la posición del **mouse**.
- Creación de nuevos **paquetes** para mejorar la estructura del proyecto:
  - `controles`
  - `entidades`
  - `pantalla`
  - `utilidades`
- Adición de **clases y métodos** específicos en cada paquete para manejar la animación y lógica del personaje controlado por el usuario.

### Changed
- Mejora significativa de la **estructura de Programación Orientada a Objetos** implementada anteriormente.
- Se reorganizó el código para distribuir responsabilidades entre distintas clases y paquetes, facilitando el mantenimiento y escalabilidad del proyecto.
- Se reemplazó el sistema anterior de control de personaje con formas geométricas por un personaje animado completo.

### Fixed
- No se ha corregido ningún error en esta versión.

## [1.2.0] - 2025-07-06

### Added
- Implementación de **animaciones para el personaje principal**, con el cual el personaje se desplaze mas rapido y el usuario también, ademas de que se ubique
de mejor manera.
- Creación de un nuevo **paquete** para mejorar incorporar objetos dentro del juego:
    - `objetos`
- Adición de **clases y métodos** específicos en cada paquete para manejar la localización y transporte del personaje controlado por el usuario.

### Changed
- Se agrego un mapa para que el personaje pueda localizarse mejor y transportarse mas rapido

### Fixed 
- No se ha corregido ningún error en esta versión.
