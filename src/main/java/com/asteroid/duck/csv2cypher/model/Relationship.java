package com.asteroid.duck.csv2cypher.model;

import org.apache.commons.csv.CSVRecord;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Relationship extends CypherGenerator {
    private final Field fromField;
    private final Field toField;

    private final Set<String> fromIds;
    private final Set<String> toIds;

    public Relationship(String label, IdSpaceManager idSpaceManager, List<Field> fields) {
        super(fields.stream().filter(Predicate.not(Field.byType(Field.SpecialType.START_ID, Field.SpecialType.END_ID))).collect(Collectors.toList()), label);
        this.fromField = fields.stream().filter(Field.byType(Field.SpecialType.START_ID)).findFirst().orElseThrow();
        this.toField = fields.stream().filter(Field.byType(Field.SpecialType.END_ID)).findFirst().orElseThrow();
        this.fromIds = idSpaceManager.getIdSpace(fromField.getIdSpace());
        this.toIds = idSpaceManager.getIdSpace(toField.getIdSpace());
    }

    @Override
    public int createCypher(Iterable<CSVRecord> records, PrintStream out) {
        out.println("CREATE ");
        return super.createCypher(records, out);
    }

    @Override
    public String createRowCypher(CSVRecord record, boolean last) {
        //CREATE
        String from = record.get(fromField.getRecordName());
        if (!fromIds.contains(from)) {
            throw new IllegalArgumentException("Unknown ID in from="+from);
        }
        String to = record.get(toField.getRecordName());
        if (!toIds.contains(to)) {
            throw new IllegalArgumentException("Unknown ID in to="+to);
        }
        //(Keanu)-[:ACTED_IN {roles:['Neo']}]->(TheMatrix), ...
        return "\t("+identifier(fromField.getIdSpace(), from)+")"+
                "-[:"+label+ cypherProperties(record)+"]->"+
                "("+identifier(toField.getIdSpace(), to)+")" +
                (last ? "" : ",");
    }
}
