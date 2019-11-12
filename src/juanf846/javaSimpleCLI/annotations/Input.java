package juanf846.javaSimpleCLI.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Scanner;

import juanf846.javaSimpleCLI.Shell;

/**
 * A una variable anotada con {@link Input} se le inyectara el input usado por
 * el Shell, este es un objeto de tipo {@link Scanner}.
 * 
 * @see Shell#addCommand(Object)
 * @author juanf846
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Input {

}
