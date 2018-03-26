package com.github.fmarmar.cucumber.tools;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.JCommander;
import com.github.fmarmar.cucumber.tools.report.html.HtmlReport;
import com.github.fmarmar.cucumber.tools.split.SplitFeatures;
import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

	private Collection<Command> commands;

	private App() {

		commands = Arrays.asList(
				new Help(),
				new SplitFeatures(),
				new HtmlReport()
				);
		
	}
	
	public static void main(String... args) {

		configureExceptionHandler();
		
		App app = new App();
		
		JCommander jc = buildJcommander(app, app.commands);
		jc.parse(args);
				
		Stopwatch stopwatch = Stopwatch.createStarted();
		executeCommand(getCommand(jc));
		System.out.println("Command " + jc.getParsedCommand() + " executed in " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms");
	}
	
	private static void configureExceptionHandler() {

		final long mainThreadId = Thread.currentThread().getId();
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				String message = e.getMessage();
				
				log.error(message, e);

				if (message != null) {
					System.err.println(e.getMessage());
				}
				e.printStackTrace(System.err);
				
				if (mainThreadId == t.getId()) {
					System.exit(1);
				}
			}
		});
		
	}

	static JCommander buildJcommander(Object commonOptions, Collection<Command> commands) {

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
	
	static Command getCommand(JCommander jc) {
		
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