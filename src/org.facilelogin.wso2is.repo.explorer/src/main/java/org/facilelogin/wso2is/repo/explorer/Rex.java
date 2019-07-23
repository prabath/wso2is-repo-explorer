package org.facilelogin.wso2is.repo.explorer;

import java.io.IOException;

public class Rex {

    protected static final String IS_580 = "IS_5.8.0";
    protected static final String IS_570 = "IS_5.7.0";
    protected static final String IS_560 = "IS_5.6.0";
    protected static final String IS_550 = "IS_5.5.0";
    protected static final String IS_541 = "IS_5.4.1";
    protected static final String IS_540 = "IS_5.4.0";
    protected static final String IS_530 = "IS_5.3.0";
    protected static final String IS_520 = "IS_5.2.0";
    protected static final String IS_510 = "IS_5.1.0";
    protected static final String IS_500 = "IS_5.0.0";
    protected static final String IS_460 = "IS_4.6.0";

    /**
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // populate all the data.
        Reader crawler = new Reader();
        crawler.populateData();

        Printer printer = new Printer(crawler);

        if (args.length == 2 && "-c".equals(args[0]) && !args[1].isEmpty()) {
            printer.printPatchesByComponentName(args[1]);
        } else if (args.length == 4 && "-c".equals(args[0]) && !args[1].isEmpty() && "-p".equals(args[2])
                && !args[3].isEmpty()) {
            printer.printPatchesByComponentName(args[1], args[3].toLowerCase());
        } else if (args.length == 1 && "-c".equals(args[0])) {
            printer.printPatchesCountByComponentTop10();
        } else if (args.length == 2 && "-r".equals(args[0]) && !args[1].isEmpty()) {
            printer.printPatchesByRepo(args[1]);
        } else if (args.length == 1 && "-p".equals(args[0])) {
            printer.printPatchesByProduct(null);
        } else if (args.length == 2 && "-p".equals(args[0]) && !args[1].isEmpty()) {
            printer.printPatchesByProduct(args[1].toUpperCase());
        } else if (args.length == 4 && "-r".equals(args[0]) && !args[1].isEmpty() && "-p".equals(args[2])
                && !args[3].isEmpty()) {
            printer.printPatchesByRepo(args[1], args[3]);
        } else if (args.length == 1 && "-t".equals(args[0])) {
            printer.printPatchCountByTime();
        } else if (args.length == 1 && "-r".equals(args[0])) {
            printer.printPatchesCountByRepoTop10();
        } else if (args.length == 1 && "-r2".equals(args[0])) {
            printer.printPatchesWithMoreThanOneRepo();
        } else if (args.length == 1 && "-a".equals(args[0])) {
            printer.printAllPatches(true);
        } else {
            printer.printAllPatches(false);
        }
    }

}
