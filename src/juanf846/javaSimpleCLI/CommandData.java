package juanf846.javaSimpleCLI;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Esta clase es utilizada internamente para guardar informacion de los comandos disponibles
 *
 * @author juanf846
 *
 */
class CommandData {
	String name;
	Object obj;
	Class<?> clas;
	Method methodRun;
	Method methodHelp;
	Field input;
	Field output;
	
}
