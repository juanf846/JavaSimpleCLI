package juanf846.javaSimpleCLI.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import juanf846.javaSimpleCLI.Shell;

/**
 * Un metodo anotado con {@link Run} será invocado cuando el usuario escriba el
 * nombre del comando en la anotacion {@link Command} en el shell. Este metodo
 * no debe devolver nada y debe recibir un {@code String[]} como parametro.
 *
 * @see Shell#addCommand(Object)
 * @author juanf846
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Run {
}
