package com.github.fmarmar.cucumber.tools.jcommander;

import java.util.Collection;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.github.fmarmar.cucumber.tools.Command;

public class JcommanderUtils {
	
	private JcommanderUtils() { }
	
	public static JCommander buildJcommander(Object commonOptions, Collection<Command> commands) {

		JCommander.Builder builder = JCommander.newBuilder();
		
		if (commonOptions != null) {
			builder.addObject(commonOptions);
		}

		for (Command command : commands) {
			builder.addCommand(command);
		}

		JCommander jc =  builder.build();
		jc.setCaseSensitiveOptions(true);
		jc.setExpandAtSign(false);
		
		return jc;
	}
	
	public static Command getCommandObject(JCommander jc, String commandName) {

		Map<String, JCommander> commands = jc.getCommands();

		JCommander commandJc = commands.get(commandName);

		for (Object obj : commandJc.getObjects()) {
			if (obj instanceof Command) {
				return (Command) obj;
			}
		}

		throw new RuntimeException("Unknown command " + commandName);

	}
	
}