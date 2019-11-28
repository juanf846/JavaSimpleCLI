package juanf846.javaSimpleCLI;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import juanf846.javaSimpleCLI.annotations.*;

/**
 * This class creates a Simple CLI. Can't run S.O. commands, only can run commands added with {@link #addCommand(Object)} 
 * method.<br/> 
 * You can change the prompt with {@link #setPrompt(String)}, the input with {@link #setInputStream(InputStream)} or 
 * the output with {@link #setOutputStream(OutputStream)}. <br/>
 * Start the CLI with {@link #run()}.
 * 
 * @author juanf846
 *
 */
public class Shell {
	private List<CommandData> commands = new ArrayList<>();
	
	private PrintStream output = System.out;
	private Scanner scan = new Scanner(System.in);
	
	private String prompt = ">";
	private boolean stop = false;
	
	/**
	 * Changes the prompt.
	 * 
	 * @param prompt A new prompt String.
	 * @throws IllegalArgumentException if <code>prompt</code> is null.
	 */
	public void setPrompt(String prompt) {
		if(prompt==null)throw new IllegalArgumentException(new NullPointerException());
		this.prompt=prompt;
	}
	
	/**
	 * Changes the InputStream, by default is <code>System.in</code>. 
	 * 
	 * @param input A new InputStream.
	 * @throws IllegalArgumentException if <code>input</code> is null.
	 */
	public void setInputStream(InputStream input) {
		if(input==null)throw new IllegalArgumentException(new NullPointerException());
		this.scan=new Scanner(input);
	}
	/**
	 * Changes the OutputStream, by default is <code>System.out</code>. 
	 * 
	 * @param output A new OutputStream.
	 * @throws IllegalArgumentException if <code>output</code> is null.
	 */
	public void setOutputStream(OutputStream output) {
		if(output==null)throw new IllegalArgumentException(new NullPointerException());
		this.output=new PrintStream(output);
	}
	
	public Shell() {
		addCommand(new InternalCommandHelp());
		addCommand(new InternalCommandExit());
	}
	
	/**
	 * Add a command.</br></br>
	 * A class is valid if it have:
	 * <ul>
	 * 	<li>A {@link Command} annotation in its class.</li>
	 *  <li>A method with {@link Run} annotation, it must have a parameter {@link String[]} and must 
	 * not return anything.</li>
	 * </ul>
	 * If these conditions aren't met, a {@link RuntimeException} will be throw.</br></br>
	 * 
	 * Optionally can have:
	 * <ul>
	 * 	<li>A {@link PrintStream} field with {@link Input} annotation.</li>
	 * 	<li>A {@link Scanner} field with {@link Output} annotation.</li>
	 *  <li>A method with {@link Help} annotation, it must not have parameters and must return a {@link String}.</li>
	 * </ul>
	 * 
	 * @param obj An object that meets the conditions
	 * @throws RuntimeException If conditions aren't met
	 * 
	 */
	public void addCommand(Object obj){
		CommandData commandData = new CommandData();
		Class<?> clas = obj.getClass();
		commandData.clas = clas;
		commandData.obj = obj;
		//Obtiene la anotacion Command
		Command c = clas.getAnnotation(Command.class);
		if(c!=null)
			commandData.name = c.name();
		else
			throw new RuntimeException("Class "+clas.getName()+" don't have "+Command.class.getName()+" annotation");
		//Obtiene los campos input y output
		for(Field field : clas.getDeclaredFields()) {
			Input i = field.getAnnotation(Input.class);
			if(i!=null)
				commandData.input = field;
			
			Output o = field.getAnnotation(Output.class);
			if(o!=null)
				commandData.output = field;
		}
		//Obtiene los metodos run y help
		boolean runMethod = false;
		for(Method method : clas.getDeclaredMethods()) {
			Run r = method.getAnnotation(Run.class);
			if(r!=null) {
				commandData.methodRun = method;
				runMethod = true;
			}
			Help h = method.getAnnotation(Help.class);
			if(h!=null)
				commandData.methodHelp = method;
		}
		//Si no se encuentra el metodo Run(), lanza una exception
		if(!runMethod)
			throw new RuntimeException("method Run() not found in: "+clas.getName());
		//Inyecta los recursos
		try {
			if(commandData.input != null) {
				commandData.input.setAccessible(true);
				commandData.input.set(commandData.obj, scan);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		try {
			if(commandData.output != null) {
				commandData.output.setAccessible(true);
				commandData.output.set(commandData.obj, output);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		commands.add(commandData);
	}
	
	
	/**
	 * Starts the shell.
	 */
	public void run() {
		stop = false;
		while(!stop) {
			output.print(prompt);
			String text = scan.nextLine().trim();
			
			List<String> args = splitText(text);
			String command = args.remove(0);
			
			CommandData c = findCommand(command);
			if(c!=null) {
				try {
					c.methodRun.invoke(c.obj, (Object)args.toArray(new String[args.size()]));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					scan.close();
					throw new RuntimeException(e);
				} catch(Exception e) {
					e.printStackTrace(output);
					
				}
			}else {
				output.println("Command not found: "+command);
			}
		}
		scan.close();
	}
	
	/**
	 * Splits the text in parameters.
	 * 
	 * @param text
	 * @return A List with parameters.
	 */
	private List<String> splitText(String text){
		boolean comando = false;
		List<String> args = new ArrayList<>();
		String ultimaPalabra="";
		boolean comillas=false;
		//lee caracter por caracter y guarda separa las palabras por espacios (si hay comillas, se ignoran los espacios)
		for(int i=0;i<text.length();i++) {
			char caracter = text.charAt(i);
			if(caracter==' ' && !comillas) {
				if(!comando) {
					comando = true;
					args.add(new String(ultimaPalabra));
					ultimaPalabra="";
				}else {
					args.add(new String(ultimaPalabra));
					ultimaPalabra="";
				}
			}else if(caracter=='"'){
				if(!comillas) {
					comillas = true;
				}else {
					if(!comando) {
						comando = true;
						args.add(new String(ultimaPalabra));
						ultimaPalabra="";
					}else {
						args.add(new String(ultimaPalabra));
						ultimaPalabra="";
					}
				}
			}else {
				ultimaPalabra+=caracter;
			}
			if(i==text.length()-1) {
				args.add(new String(ultimaPalabra));
				ultimaPalabra="";
			}
		}
		return args;
	}
	
	/**
	 * Search a command.
	 *
	 * @param command
	 * @return If found a {@link CommandData}, if not found <code>null</code>.
	 */
	CommandData findCommand(String command) {
		for(CommandData c : commands) {
			if(c.name.equals(command)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Stops the shell.
	 */
	public void stop() {
		stop=true;
	}
	
	
	/**
	 * This command shows the help of another command
	 * @author JuanF
	 *
	 */
	@Command(name="help")
	public class InternalCommandHelp {
		@Output
		private PrintStream output;
		
		@Input
		private Scanner input;
		
		@Run
		public void run(String[] args) {
			if(args.length != 1) {
				output.println(getHelp());
			}else{
				CommandData c = findCommand(args[0]);
				if(c==null || c.methodHelp==null) {
					output.println("Can't found this command or don't have help");
				}else {
					try {
						String text = (String) c.methodHelp.invoke(c.obj, new Object[0]);
						output.println(text);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		@Help
		public String getHelp() {
			return "This command shows the help of another command\n"
					+ "HELP command";
		}
	}
	
	/**
	 * This command stops the shell
	 * @author JuanF
	 *
	 */
	@Command(name="exit")
	public class InternalCommandExit {
		@Output
		private PrintStream output;
		
		@Input
		private Scanner input;
		
		
		@Run
		public void run(String[] args) {
			stop();
		}
		
		@Help
		public String getHelp() {
			return "This command stops the shell";
		}
	}
}
