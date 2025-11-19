package loganalyzer;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogAnalyzer {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Path logDir = Paths.get("LogAnalyser tp/logexercice-tp/logs");

        List<Path> files;
        try (Stream<Path> paths = Files.list(logDir)) {
            files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erreur accès dossier logs : " + e.getMessage());
            return;
        }

        System.out.println("Traitement avec Threads simples...");
        List<LogEntry> allEntriesThread = Collections.synchronizedList(new ArrayList<>());

        List<Thread> threads = new ArrayList<>();

        for (Path file : files) {
            LogFileThread task = new LogFileThread(file, allEntriesThread);
            Thread t = new Thread(task);
            t.start();
            threads.add(t);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        afficherStatistiques(allEntriesThread);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Traitement avec ExecutorService...");
        List<LogEntry> allEntriesExecutor = new CopyOnWriteArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (Path file : files) {
            executor.submit(new LogFileThread(file, allEntriesExecutor));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        afficherStatistiques(allEntriesExecutor);
    }

    private static void afficherStatistiques(List<LogEntry> entries) {
        System.out.println("Nombre total d'entrées : " + entries.size());

        Map<String, Long> countByLevel = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getLevel, Collectors.counting()));

        countByLevel.forEach((level, count) ->
                System.out.println("Niveau " + level + " : " + count)
        );

        Map<String, Long> countByLogger = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getLogger, Collectors.counting()));

        countByLogger.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry ->
                        System.out.println("Logger le plus actif : " + entry.getKey() + " avec " + entry.getValue() + " logs")
                );

        long errorCount = entries.stream()
                .filter(e -> "ERROR".equals(e.getLevel()))
                .count();
        System.out.println("Nombre de logs ERROR : " + errorCount);
    }
}