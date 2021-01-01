# csv2cypher
A utility to create Neo4J cypher scripts to insert CSV data.

Takes the nodes/realtionships arguments as one would supply to `neo4j-admin import ...` call and converts this
into a Cypher script to insert the same data. The data is loaded from the indicated CSVs and then *copied* into
the resulting cypher (`.cql`) file.

This only works with the `--nodes=[<label>[:<label>]...=]<files>...]...` and `[--relationships=[<type>=]<files>...]...`
argument types; all others will cause an error.
