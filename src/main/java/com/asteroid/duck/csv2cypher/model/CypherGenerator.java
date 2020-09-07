package com.asteroid.duck.csv2cypher.model;

import org.apache.commons.csv.CSVRecord;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CypherGenerator {

    protected final List<Field> propertyFields;
    protected final String label;

    protected CypherGenerator(List<Field> propertyFields, String label) {
        this.propertyFields = propertyFields;
        this.label = label;
    }

    public static String asCypherProperty(Field f, CSVRecord record) {
        final char delim = f.getType().equals("string") ? '\'' : ' ';
        return f.getName() +':'+ delim + record.get(f.getRecordName()) + delim;
    }

    public String cypherProperties(CSVRecord record) {
        return propertyFields.stream().map(field -> asCypherProperty(field, record)).collect(Collectors.joining(", ", "{", "}"));
    }

    public int createCypher(Iterable<CSVRecord> records, PrintStream out) {
        int count = 0;
        Iterator<CSVRecord> iter = records.iterator();
        while(iter.hasNext()) {
            out.println(createRowCypher(iter.next(), !iter.hasNext()));
            count++;
        }
        return count;
    };

    public abstract String createRowCypher(CSVRecord record, boolean last);

    public static String identifier(String idSpaceName, String id) {
        return "`"+idSpaceName+"-"+id+"`";
    }
}
