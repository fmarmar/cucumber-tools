package com.github.fmarmar.cucumber.tools;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.JCommander;
import com.github.fmarmar.cucumber.tools.jcommander.JcommanderUtils;
import com.github.fmarmar.cucumber.tools.report.csv.CsvReport;
import com.github.fmarmar.cucumber.tools.report.html.HtmlReport;
import com.github.fmarmar.cucumber.tools.split.SplitFeatures;
import com.google.common.base.Stopwatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

	private Collection<Command> commands;

	private final JCommander jc;

	private App() {

		commands = Arrays.asList(
				new Help(),
				new SplitFeatures(),
				new HtmlReport(),
				new CsvReport()
				);

		jc = JcommanderUtils.buildJcommander(this, commands);

	}

	private void run(String... args) {

		jc.parse(args);
		String commandName = jc.getParsedCommand();
		Command command = JcommanderUtils.getCommandObject(jc, commandName);

		Stopwatch stopwatch = Stopwatch.createStarted();
		executeCommand(command);
		System.out.println("Command " + commandName + " executed in " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms");

	}

	private static void executeCommand(Command command) {

		command.initialize();
		command.run();

	}

	public static void main(String... args) {

		configureExceptionHandler();

		App app = new App();
		app.run(args);

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

}