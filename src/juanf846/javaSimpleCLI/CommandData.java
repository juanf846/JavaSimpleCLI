package juanf846.javaSimpleCLI;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class saves the information of a command.
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
