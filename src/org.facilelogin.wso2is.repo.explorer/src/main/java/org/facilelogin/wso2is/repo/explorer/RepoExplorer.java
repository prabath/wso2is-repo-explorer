package org.facilelogin.wso2is.repo.explorer;

import java.io.IOException;

public class RepoExplorer {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// populate all the data.
		Crawler crawler = new Crawler();
		crawler.populateData();

		Printer printer = new Printer(crawler);

		if (args.length == 2 && "-c".equals(args[0]) && !args[1].isEmpty()) {
			printer.printPatchesByComponentName(args[1]);
		} else if (args.length == 2 && "-r".equals(args[0]) && !args[1].isEmpty()) {
			printer.printPatchesByRepo(args[1]);
		} else if (args.length == 1 && "-p".equals(args[0])) {
			printer.printPatchesByProduct(null);
		} else if (args.length == 4 && "-r".equals(args[0]) && !args[1].isEmpty() && "-p".equals(args[2])
				&& !args[3].isEmpty()) {
			printer.printPatchesByRepo(args[1], args[3]);
		} else {
			printer.printAllPatches();
		}
	}

}
