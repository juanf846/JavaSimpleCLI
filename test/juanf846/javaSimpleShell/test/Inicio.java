package juanf846.javaSimpleShell.test;

import org.junit.Test;

import juanf846.javaSimpleCLI.Shell;
import juanf846.javaSimpleCLI.annotations.Help;

public class Inicio {
	
	@Test
	public void test() {
		System.out.println("Hola");
		Shell s = new Shell();
		s.run();
	}
}
