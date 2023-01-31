package com.asteroid.duck.csv2cypher.model;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class CypherGenerator {

    protected final List<Field> propertyFields;
    protected final String label;

    private static final Random rnd = new Random();

    protected CypherGenerator(List<Field> propertyFields, String label) {
        this.propertyFields = propertyFields;
        this.label = label;
    }

    public static String asCypherProperty(Field f, CSVRecord record) {
        char start, end;
        String csvValue;
        String type = f.getType();
        if(type.startsWith("fixed")) {
            start = end = '\'';
            csvValue = "?";
            int begin = type.indexOf('(');
            if (begin > 0) {
                int finish = type.indexOf(')', begin);
                if (finish > 0) {
                    final String[] data = type.substring(begin + 1, finish).split(";");
                    if (!data[0].equals("string")) {
                        start = end = ' ';
                    }
                    csvValue = data[1];
                }
            }
        }
        else if (type.startsWith("random")){
            start = end = ' ';
            if (type.contains("boolean")) {
                csvValue = Boolean.toString(rnd.nextBoolean());
            }
            else if(type.contains("enum")) {
                start = end = '\'';
                csvValue = "?";
                int begin = type.indexOf('(');
                if (begin > 0) {
                    int finish = type.indexOf(')', begin);
                    if (finish > 0) {
                        final String[] data = type.substring(begin + 1, finish).split(";");
                        csvValue = data[rnd.nextInt(data.length)];
                    }
                }
            }
            else {
                csvValue = Double.toString(rnd.nextDouble());
            }
        }
        else {
            csvValue = record.get(f.getRecordName());
            switch (type) {
                case "string":
                    start = end = '\'';
                    break;
                case "string[]":
                    start = '[';
                    end = ']';
                    String[] values = csvValue.split(";");
                    csvValue = Stream.of(values).map(s -> "'" + s + "'").collect(Collectors.joining(","));
                    break;
                default:
                    start = end = ' ';
            }
        }
        return f.getName() +':'+ start + csvValue + end;
    }

    public String cypherProperties(CSVRecord record) {
        return propertyFields.stream().map(field -> asCypherProperty(field, record)).collect(Collectors.joining(", ", "{", "}"));
    }

    public int createCypher(Iterable<CSVRecord> records, PrintStream out) throws IOException {
        int count = 0;
        Iterator<CSVRecord> iter = records.iterator();
        while(iter.hasNext()) {
            CSVRecord record = iter.next();
            try {
                out.println(createRowCypher(record, !iter.hasNext()));
                count++;
            }
            catch(IllegalArgumentException | IndexOutOfBoundsException e) {
                throw new IOException("Error in CSV line "+count+". "+ record.toString(), e);
            }
        }
        return count;
    };

    public abstract String createRowCypher(CSVRecord record, boolean last);

    public static String identifier(String idSpaceName, String id) {
        return "`"+idSpaceName+"-"+id+"`";
    }
}
