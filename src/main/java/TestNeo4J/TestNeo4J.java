package TestNeo4J;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

public class TestNeo4J {
    // Path to Neo4j database files.
    private static String database_path = "neo4j_db";
    // Neo4j attributes.
    private final GraphDatabaseService database;

    public TestNeo4J() {
        // Open database.
        database = new GraphDatabaseFactory().newEmbeddedDatabase(database_path);

        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(
            new Thread() {
                @Override
                public void run() {
                    database.shutdown();
                }
            }
        );

        createTestData();
        System.out.println();
        outAllLabels();
        System.out.println();
        outAllNodes();
    }

    private void createTestData() {
        try (Transaction tx = database.beginTx()) {
            System.out.println("createTestData");

            Label label = DynamicLabel.label("my_label");

            Node node = database.createNode();
            node.setProperty("class", "test");
            node.addLabel(label);

            tx.success();
        }
    }

    // label : my_label
    // label name = my_label
    private void outAllLabels() {
        try (Transaction tx = database.beginTx()) {
            GlobalGraphOperations operator = GlobalGraphOperations.at(database);
            System.out.println("getAllLabels");
            for (Label label : operator.getAllLabels()) {
                System.out.println("label : " + label.toString());
            }
        }
    }

    // Search for all nodes with the given label.
    // No result.
    // May be I cannot pass null as a property.
    private void outAllNodes() {
        try (Transaction tx = database.beginTx()) {
            GlobalGraphOperations operator = GlobalGraphOperations.at(database);
            System.out.println("getAllNodes");
            for (Node node : operator.getAllNodes()) {
                System.out.println("node : " + node.toString());
                System.out.println("node id : " + node.getId());
                for (String key : node.getPropertyKeys()) {
                    System.out.println("\"" + key + "\" = \"" + node.getProperty(key) + "\"");
                }
                for (Label label : node.getLabels()) {
                    System.out.println("label : " + label.toString());
                }

                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        TestNeo4J testNeo4J = new TestNeo4J();
    }
}