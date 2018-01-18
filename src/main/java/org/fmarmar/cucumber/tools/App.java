package org.fmarmar.cucumber.tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.fmarmar.cucumber.tools.rerun.Rerun;

import com.beust.jcommander.JCommander;

public class App {

	private Collection<Command> commands;

	private App() {

		commands = Arrays.asList(
				new Help(),
				new Rerun()
				);
		
	}
	
	public static void main(String... args) {

		App app = new App();
		
		JCommander jc = buildJcommander(app);
		jc.parse(args);
		
		executeCommand(getCommand(jc));
	}

	private static JCommander buildJcommander(App app) {

		JCommander.Builder builder = JCommander.newBuilder()
				.addObject(app);

		for (Command command : app.commands) {
			builder.addCommand(command);
		}

		JCommander jc =  builder.build();
		jc.setCaseSensitiveOptions(true);
		
		return jc;
	}
	
	private static Command getCommand(JCommander jc) {
		
		Map<String, JCommander> commands = jc.getCommands();
		
		JCommander commandJc = commands.get(jc.getParsedCommand());
		
		for (Object obj : commandJc.getObjects()) {
			if (obj instanceof Command) {
				return (Command) obj;
			}
		}
		
		throw new RuntimeException(); //FIXME error;
	}

	private static void executeCommand(Command command) {
		
		command.initialize();
		command.run();
		
	}

}