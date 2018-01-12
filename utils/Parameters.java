package dicograph.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Created by Fynn Leitow on 09.01.18.
 */
public class Parameters {
    private String[] args;
    private Options options;
    private CommandLine input;


    // Editing-Parameters with Default values:
    private int timeOut = 120;
    private static final int maxCost = 20; // from parameter k
    private int softThreshold = 3; // If no succesful edit found, discard edits with forbiddenSub-score <= this value - atm for my bad tests.
    private int hardThreshold = 0; // Exclude edges with a forbiddenSubgraph-score <= this threshold from the edge-edit-set.
    // prev: Force stop if forbiddenSub-Score <= this value during first run and use brute-force/branching/ILP in second run to complete.
    private boolean skipPaths = true;
    private boolean skipExistingVertices = false;
    private double weightMultiplier = 1.0;
    private int solutionGap = -1; // only one solution by default.
    private boolean requireGlobal = false; // only accept local edit if global also improves?

    private boolean useGlobal = false;

    public boolean isUseGlobal() {
        return useGlobal;
    }

    // When to start brute force:
    private int bruteForceThreshold = 12; // default: 5

    // methods
    private boolean lazy = true;


    public Parameters(String[] args) {

        this.args = args;
        options = new Options();

        // globals:
        options.addOption("h", false, "show help.");

        options.addOption("md","Just compute the modular decomposition.");
        options.addOption("v","if not, console outputs only the editgraph or MDTree as .dot");
        options.addOption("test", true, "Test run...");

        Option input = new Option("i",true,"Input file: .dot, .txt (Matrix) or .jtxt (JGraph)");
        input.setArgName("infile");
        options.addOption(input);

        Option output = new Option("o", true,"Output file: .dot, .txt (Matrix) or .jtxt (JGraph)");
        output.setArgName("outfile");
        options.addOption(output);
        // default: same as input, with timestamp.

        Option log = new Option("log",true,"Choose log level: warning/info/fine/off");
        log.setArgName("value");
        options.addOption(log);

        //methods:
        options.addOption("glazy", "Use lazy greedy method");
        options.addOption("gforce","Use Brute Force in Step 2. Exit step 1 when bf- or hard thr reached");
        options.addOption("gilp", "Use ILP in step 2. Exit step 1 when bf- or hard thr reached");
        options.addOption("ilp", "Use MD and ILP");
        options.addOption("ilpglobal","Use ILP withoud MD");

        // todo: is param
        options.addOption("ghard","Step 2 when subgraph-score < hard-trh");


        // method parameters:
        Option gap = new Option("gap",true,"Solution gap: Also used internally!");
        gap.setArgName("value");
        options.addOption(gap);

        options.addOption("noeskip", "Disables skipping edges in graph of the edit-edge-set");
        options.addOption("vskip", "Skips (u,v) if u,v in vertex set of edit-edge-set's graph");

        options.addOption("sth", "Soft threshold: No edit found in 1st run => discards edges with subgraph-score <= this");
        options.addOption("hth", "Hard threshold: Stops at this subgraph-score");
        options.addOption("bfth","Brute Force threshold"); // todo...

        Option weightm = new Option("wm",true,"Weight multiplier. Default: 1.0; Set lower if no solution, higher if too expensive");
        gap.setArgName("value");
        options.addOption(weightm);

    }

    public void parse() throws ParseException{
        CommandLineParser parser = new DefaultParser();
        input = parser.parse(options, args);

        if (input.hasOption("h") || args.length == 0) {
            help();
            return;
        }

        if (input.hasOption("test")) {
            // use my default testing options
        } else{
            if (!isMDOnly()){
                // init for Editing
                if(isBruteForce() || isGreedyPlusILP()|| isIlpMD() || isIlpOnly()){
                    lazy = input.hasOption("glazy");
                }
                if(input.hasOption("gap")){
                    solutionGap = Integer.valueOf( input.getOptionValue("gap"));
                }
                if(input.hasOption("t")){
                    timeOut = Integer.valueOf( input.getOptionValue("t"));
                }
            }
        }


    }

    private void help(){
        HelpFormatter helpF = new HelpFormatter();
        String usage = "dmdedit -i <infile> [-options] or dmdedit -test <n m k> [-options]";
        String header = "Global flags: -i, -o, -log, -v, -md, -test\n" +
                "General editing flags: -t -gap\n" +
                "Editing methods (If several, chooses best solution):  \n" +
                "  -glazy, -gforce, -gsoft, -ghard; -ilp, -ilpglobal\n" +
                "Other parameters adjust the greedy methods.\n\n";
        String footer = "\nRefer to thesis for details.";
        helpF.printHelp(usage,header,options,footer,false);
    }

    public String[] getArgs() {
        return args;
    }

    public Options getOptions() {
        return options;
    }

    public boolean isMDOnly(){
        return input.hasOption("md");
    }

    public boolean isVerbose(){
        return input.hasOption("v");
    }


    public static int getMaxCost() {
        return maxCost;
    }

    public int getSoftThreshold() {
        return softThreshold;
    }

    public int getHardThreshold() {
        return hardThreshold;
    }

    public boolean isSkipPaths() {
        return skipPaths;
    }

    public boolean isSkipExistingVertices() {
        return skipExistingVertices;
    }

    public double getWeightMultiplier() {
        return weightMultiplier;
    }

    public int getSolutionGap() {
        return solutionGap;
    }

    public boolean isRequireGlobal() {
        return requireGlobal;
    }

    public int getBruteForceThreshold() {
        return bruteForceThreshold;
    }

    public boolean isLazy() {
        return lazy;
    }

    public boolean isBruteForce() {
        return input.hasOption("gforce");
    }

    public boolean isStopOnlyAtHardThreshold() { // todo...
        return input.hasOption("ghard");
    }

    public boolean isGreedyPlusILP(){
        return input.hasOption("gilp");
    }


    public boolean isIlpMD() {
        return input.hasOption("ilp");
    }

    public boolean isIlpOnly() {
        return input.hasOption("ilpglobal");
    }

    public int getTimeOut() {
        return timeOut;
    }
}
