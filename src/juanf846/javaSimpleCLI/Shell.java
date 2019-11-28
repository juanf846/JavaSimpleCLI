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
 * Esta clase crea una interfaz de linea de comandos simple. Este no puede ejecutar comandos del 
 * sistema operativo, solo puede ejecutar los comandos que se le agregan con la funcion {@link #addCommand(Object)}.<br/>
 * Una vez instanciado, se puede cambiar el prompt ({@link #setPrompt(String)}), el input ({@link #setInputStream(InputStream)}) 
 * o el output ({@link #setOutputStream(OutputStream)}). <br/>
 * Para iniciar la consola, use la funcion {@link #run()}
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
	 * Cambia el prompt de la consola
	 * @param prompt
	 */
	public void setPrompt(String prompt) {
		if(prompt==null)prompt="";
		this.prompt=prompt;
	}
	
	/**
	 * Cambia el InputStream del shell, por defecto es <code>System.in</code>.
	 * Si se envia <code>null</code> se establece el valor por defecto
	 * @param input
	 */
	public void setInputStream(InputStream input) {
		if(input==null)input=System.in;
		this.scan=new Scanner(input);
	}
	/**
	 * Cambia el OutputStream del shell, por defecto es <code>System.out</code>.
	 * Si se envia <code>null</code> se establece el valor por defecto
	 * @param output
	 */
	public void setOutputStream(OutputStream output) {
		if(output==null)output=System.out;
		this.output=new PrintStream(output);
	}
	
	public Shell() {
		addCommand(new InternalCommandHelp());
		addCommand(new InternalCommandExit());
	}
	
	/**
	 * Agrega un comando al shell.
	 * Para que una clase sea valida debe tener lo siguiente:
	 * <ul>
	 * <li>La anotacion {@link Command} en su clase</li>
	 * <li>Un metodo anotado con la anotacion {@link Run} la cual debe recibir un parametro de tipo 
	 * {@link String[]} y no debe devolver nada</li> 
	 * </ul>
	 * Opcionalmente tambien puede tener:
	 * <ul>
	 * <li>Una variable de tipo {@link PrintStream} con la anotacion {@link Input}</li>
	 * <li>Una variable de tipo {@link Scanner} con la anotacion {@link Output}</li>
	 * <li>Un metodo anotado con la anotacion {@link Help} la cual no debe recibir parametros y debe 
	 * devolver un String</li> 
	 * </ul>
	 * 
	 * Si no cumple con las condiciones obligatorias, se lanza una RuntimeException
	 * 
	 * @param obj Un objeto de cualquier tipo que cumpla con las condiciones
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
	 * Inicia la ejecucion del shell.
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
					throw new RuntimeException(e);
				}
			}else {
				output.println("Command not found: "+command);
			}
		}
	}
	
	/**
	 * Separa el texto usando los espacios, si el texto está entre comillas ignora los espacios.
	 * 
	 * @param text
	 * @return
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
	 * Busca un comando en la lista de comandos y lo devuelve, si no lo encuentra devuelve null
	 * @param command
	 * @return 
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
	 * Detiene la ejecucion del shell, el shell se detendra despues de ejecutar un comando
	 */
	public void stop() {
		stop=true;
	}
	
	
	/**
	 * Este comando muestra la ayuda de otros comandos
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
	 * Este comando termina la ejecucion actual del shell
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
