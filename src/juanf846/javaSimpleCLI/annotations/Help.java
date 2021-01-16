package juanf846.javaSimpleCLI.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import juanf846.javaSimpleCLI.Shell;

/**
 * Un metodo anotado con {@link Help} será invocado cuando el usuario escriba
 * <code>help nombreComando</code> en el shell, este metodo debe devolver un
 * String el cual se imprime en el shell.
 *
 * @see Shell#addCommand(Object)
 * @author juanf846
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Help {
}