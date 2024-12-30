package com.drivelab.outbox.pattern.cli;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.io.*;

@ShellComponent
public class OutboxCommands {

    private static final Logger logger = LoggerFactory.getLogger(OutboxCommands.class);

    @Autowired
    EntityManager entityManager;

    @ShellMethod(key = "generate-data")
    public String generateData(
            @ShellOption(value = {"-f", "--file"}, help = "Path of the output file.", defaultValue = "data.csv") String[] fileArguments)
            throws IOException {

        String filePath = fileArguments[0];
        int lines = Integer.parseInt(fileArguments[1]);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        BufferedWriter writer = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("File " + file.getAbsolutePath() + " could not be deleted");
                }
            }
            boolean created = file.createNewFile();
            if (!created) {
                throw new RuntimeException("File " + file.getAbsolutePath() + " could not be created");
            }

            writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < lines; i++) {
                if (i != 0) {
                    writer.newLine();
                }
                writer.append("TICKET_EVENT,{},2024-12-29 18:11:47.727");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        stopWatch.stop();

        logger.info(stopWatch.prettyPrint());

        return "Generated data file";
    }

    @Transactional
    @ShellMethod(key = "seed-outbox")
    public String seedOutbox(
            @ShellOption(value = {"-f", "--file"}, help = "Path of the file", defaultValue = "data.csv") String filePath)
            throws IOException {

        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("File " + filePath + " does not exist");
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            entityManager.createNativeQuery("TRUNCATE TABLE outbox");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                String sql = """
                        INSERT INTO outbox (channel, payload, occurred_at_utc)
                        VALUES (:channel, :payload, :occurred_at);
                        """;
                entityManager.createNativeQuery(sql)
                        .setParameter("channel", split[0])
                        .setParameter("payload", split[1])
                        .setParameter("occurred_at", split[2])
                        .executeUpdate();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        stopWatch.stop();

        logger.info(stopWatch.prettyPrint());

        return "Outbox Table Seed";
    }
}