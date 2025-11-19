package loganalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogFileThread extends Thread {
    private final Path file;
    private final List<LogEntry> globalLogList;

    public LogFileThread(Path file, List<LogEntry> globalLogList) {
        this.file = file;
        this.globalLogList = globalLogList;
    }

    @Override
    public void run() {
        try {
            List<LogEntry> local = Files.lines(file)
                    .map(LogParser::parseLogLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            synchronized (globalLogList) {
                globalLogList.addAll(local);
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture fichier " + file + " : " + e.getMessage());
        }
    }
}