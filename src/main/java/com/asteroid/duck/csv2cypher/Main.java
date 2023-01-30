package com.asteroid.duck.csv2cypher;

import com.asteroid.duck.csv2cypher.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class Main implements IdSpaceManager {

    private Map<String, Set<String>> idSpaces = new HashMap<>();
    private final Path outputPath;

    public static void main(String[] args) throws IOException {
        String outputFileName = "generated.cql";
        List<ImportArg> argList = new ArrayList<>();
        for(String arg : args) {
            if (arg.startsWith("--")) {
                argList.add(ImportArg.parseArg(arg));
            }
            else {
                outputFileName = arg;
            }
        }
        Main main = new Main(Paths.get(outputFileName));
        main.run(argList);
    }

    public Main(Path outputPath) {
        this.outputPath = outputPath;
    }

    public void run(List<ImportArg> argList) throws IOException {
        try(PrintStream output = new PrintStream(Files.newOutputStream(outputPath, CREATE, TRUNCATE_EXISTING))) {
            for (ImportArg arg : argList) {
                createCypher(arg, output);
            }
        }
    }

    private void createCypher(ImportArg arg, PrintStream out) throws IOException {
        Path path = Paths.get(arg.getSource());
        log.debug(path.toAbsolutePath().toString());
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            CSVParser csvParser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(bufferedReader);
            List<String> headerNames = csvParser.getHeaderNames();
            log.debug(headerNames.stream().collect(Collectors.joining(",")));
            ArrayList<Field> fields = new ArrayList<>(headerNames.size());
            for (int i = 0; i < headerNames.size(); i++) {
                fields.add(Field.parseField(i, headerNames.get(i)));
            }
            CypherGenerator generator;
            switch (arg.getType()) {
                case NODES:
                    generator = new Node(arg.getName(),this, fields);
                    break;
                case RELATIONSHIPS:
                    generator = new Relationship(arg.getName(), this, fields);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type "+arg.getType());
            }
            int rows = generator.createCypher(csvParser, out);
            log.debug("Wrote "+rows+" records to file");
        }
        catch (IOException e) {
            System.err.println(path);
            e.printStackTrace();
        }
    }


    public Set<String> getIdSpace(String idSpaceName) {
        if (!idSpaces.containsKey(idSpaceName)) {
            idSpaces.put(idSpaceName, new HashSet<>());
        }
        return idSpaces.get(idSpaceName);
    }


}
