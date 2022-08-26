package com.labs.devo.tfidf.app;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "tfidf",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        description = "Daemon that watches a dir and computes at a fixed rate TfIdf for all docs in such dir and a set of terms passed as param and returns the K best")
public class CliRunner implements Callable<Integer> {

    @Option(names = {"-d", "--dir"}, description = "Directory to watch")
    private String dir = ".";

    @Option(names = {"-n", "--numResults"}, description = "Number of results to return in each execution")
    private int numResults = 5;

    @Option(names = {"-p", "--period"}, description = "Rate in seconds to compute")
    private int period = 10;

    @Option(names = {"-t", "--terms"}, description = "Set of terms separated by spaces to compute tfidf")
    private String tt = "";

    @Override
    public Integer call() throws Exception {
        List<String> terms = Arrays.stream(tt.split(" ")).toList();
        computeTfIdfAtFixedRate(dir, terms, numResults, period);
        return 0;
    }

    private void computeTfIdfAtFixedRate(String dir, List<String> terms, int numResults, int rate) {
        TfIdfService service = new TfIdfService();
        new FixedRateScheduler(rate)
                .scheduleTask(() -> service.computeTfidf(dir, terms, numResults));
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new CliRunner()).execute(args);
        if(Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("-h"))
                || Arrays.stream(args).anyMatch(s -> s.equalsIgnoreCase("-V"))) {
            System.exit(exitCode);
        }
        runAsDaemon(exitCode);
    }

    private static void runAsDaemon(int exitCode) {
        while(exitCode == 0) {}
    }
}
