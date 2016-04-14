package com.alankhazam.secret;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    private final static String PROGRAM_NAME = "SecRet";
    private final static String VERSION = "v0.1.0";
    private final static String DESCRIPTION = "Calculates cumulative returns for securities.";

    private final static int READ_STATE_NEW_SECURITY = 0;
    private final static int READ_STATE_START = 1;
    private final static int READ_STATE_END = 2;
    private final static int DATA_OFFSET = 9;

    private static void execute(String infile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(infile))) {
            int state = READ_STATE_NEW_SECURITY;
            String symbol = "";
            String startDate = "";
            String endDate;
            for (String line; (line = br.readLine()) != null; ) {
                if (line.isEmpty()) {
                    continue;
                }

                // Process the line
                switch (state) {
                    case READ_STATE_NEW_SECURITY:
                        if (line.startsWith("[")) {
                            continue; // Ignore tags
                        } else if (line.startsWith("Ticker = ")) {
                            symbol = line.substring(DATA_OFFSET);
                            state++; // Advance to next state
                        } else {
                            throw new IOException("Invalid data when expecting Ticker field");
                        }
                        break;
                    case READ_STATE_START:
                        if (line.startsWith("From   = ")) {
                            startDate = line.substring(DATA_OFFSET);
                            state++; // Advance to next state
                        } else {
                            throw new IOException("Invalid data when expecting From field");
                        }
                        break;
                    case READ_STATE_END:
                        if (line.startsWith("To     = ")) {
                            endDate = line.substring(DATA_OFFSET);

                            // Plot returns
                            try {
                                Stock stock = new Stock(symbol, startDate, endDate);

                                // Output data
                                System.out.println(stock.generateCumulativeReturnsCSV());
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }

                            state = 0; // Reset state
                        } else {
                            throw new IOException("Invalid data when expecting To field");
                        }
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        // Create options
        Options options = new Options();

        Option.Builder optHelpBuilder = Option.builder("h");
        optHelpBuilder.longOpt("help");
        optHelpBuilder.desc("");
        Option optHelp = optHelpBuilder.build();
        options.addOption(optHelp);

        Option.Builder optVersionBuilder = Option.builder("v");
        optVersionBuilder.longOpt("version");
        optVersionBuilder.desc("");
        Option optVersion = optVersionBuilder.build();
        options.addOption(optVersion);

        Option.Builder optFileBuilder = Option.builder("f");
        optFileBuilder.longOpt("filename");
        optFileBuilder.desc("(REQUIRED) Securities .ini file");
        optFileBuilder.hasArg();
        Option optFile = optFileBuilder.build();
        options.addOption(optFile);

        HelpFormatter formatter = new HelpFormatter();
        String header = DESCRIPTION + "\n\n";
        String footer = "\n";

        // Parse command line arguments
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return;
        }

        // Process arguments
        if (cmd.hasOption(optHelp.getOpt())) {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return;
        }

        if (cmd.hasOption(optVersion.getOpt())) {
            System.out.println(PROGRAM_NAME + " " + VERSION);
            return;
        }

        String infile = null;
        if (cmd.hasOption(optFile.getOpt())) {
            infile = cmd.getOptionValue(optFile.getOpt());
        } else {
            formatter.printHelp(PROGRAM_NAME, header, options, footer, true);
            return;
        }

        // Execute program
        try {
            execute(infile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
