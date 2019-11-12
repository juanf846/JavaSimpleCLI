package juanf846.javaSimpleCLI.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.PrintStream;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import juanf846.javaSimpleCLI.Shell;

/**
 * A una variable anotada con {@link Output} se le inyectara el output usado por
 * el Shell, este es un objeto de tipo {@link PrintStream}.
 * 
 * @see Shell#addCommand(Object)
 * @author juanf846
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Output {

}
