package juanf846.javaSimpleShell.test;

import org.junit.Test;

import juanf846.javaSimpleCLI.Shell;
import juanf846.javaSimpleCLI.annotations.Help;

public class Manual {
	
	@Test
	public void test() {
		System.out.println("Test manual de javaSimpleCLI");
		Shell s = new Shell();
		s.run();
	}
}
