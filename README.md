# JavaSimpleCLI 
Con JavaSimpleCLI podes crear una interfaz de linea de comandos simple.

## Como usar
* En tu clase principal, crea un nuevo objeto de tipo <code>Shell</code>.
* Invoca el metodo <code>addCommand(Object)</code> de tu objeto Shell y pasale como parametro una nueva instancia de los comandos que le quieras agregar.
* Invoca el metodo <code>run()</code> de tu objeto Shell, para iniciar la interfaz de linea de comandos.

## Crear un comando
* Crea una Clase.
* Agregale la anotacion <code>@Command(name="value")</code> donde "value" es el nombre de tu comando.
* Crea un metodo que no devuelva nada, reciba un <code>String[]</code> como parametro y tenga la anotacion <code>@Run</code>. 
Aquí agregá la logica.
* Agrega el comando al Shell mediante el metodo <code>addCommand(Object)</code>.

## TODO
* quitar Command del Shell
* opcion para que el Shell sea Case sensitive/insensitive
* Command duplicados
* metodo para ejecutar comandos sin el uso del InputStream
* cargar los paquetes dentro de un comando
