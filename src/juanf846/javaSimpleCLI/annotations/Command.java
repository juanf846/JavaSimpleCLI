package juanf846.javaSimpleCLI.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import juanf846.javaSimpleCLI.Shell;

/**
 * Una clase debe ser anotada con esta anotacion para que sea considerada un
 * comando valido. {@link #name()} es el nombre del comando, el comando se
 * ejecutará si el usuario escribe este nombre en el shell.
 *
 * @see Shell#addCommand(Object)
 * @author juanf846
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Command {
	String name();

}
