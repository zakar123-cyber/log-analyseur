package loganalyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class LogParser {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Set<String> VALID_LEVELS = Set.of("INFO", "WARN", "ERROR");

    public static LogEntry parseLogLine(String line) throws IllegalArgumentException {
        try {
            // Étape 2.1 : Vérifier la longueur minimale
            if (line == null || line.length() < 50) {
                return null;
            }

            // Étape 2.2 : Extraire le timestamp (positions fixes)
            String timestampStr = line.substring(0, 23);
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, dtf);

            // Étape 2.3 : Trouver le niveau de log [INFO]
            int start = line.indexOf('[', 24);
            if (start == -1) {
                return null;
            }

            int end = line.indexOf(']', start);
            if (end == -1) {
                return null;
            }

            String level = line.substring(start + 1, end);

            // Étape 2.4 : Extraire le logger
            int loggerEnd = line.indexOf(" - ", end);
            if (loggerEnd == -1) {
                return null;
            }

            String logger = line.substring(end + 2, loggerEnd).trim();

            // Étape 2.5 : Extraire le message
            String message = line.substring(loggerEnd + 3).trim();

            // Étape 2.6 : Créer et retourner l'objet LogEntry
            return new LogEntry(timestamp, level, logger, message);

        } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }
}