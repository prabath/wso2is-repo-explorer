package org.facilelogin.wso2is.repo.explorer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.facilelogin.wso2is.repo.explorer.bean.Component;

public class RepoExplorer {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Map<String, Set<String>> repos = new HashMap<String, Set<String>>();
		Map<String, Component> components = new HashMap<String, Component>();

		// populate all the data.
		Crawler crawler = new Crawler(repos, components);
		crawler.populateData();

		Printer printer = new Printer(crawler);

		if (args.length == 2 && "-c".equals(args[0]) && !args[1].isEmpty()) {
			printer.printPatchesByComponentName(args[1]);
		} else if (args.length == 2 && "-r".equals(args[0]) && !args[1].isEmpty()) {
			printer.printPatchesByRepo(args[1]);
		} else if (args.length == 4 && "-r".equals(args[0]) && !args[1].isEmpty() && "-p".equals(args[2])
				&& !args[3].isEmpty()) {
			printer.printPatchesByRepo(args[1], args[3]);
		} else {
			printer.printAllPatches();
		}
	}

}
