package Test_neo4j;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

public class Test_neo4j {
    // Path to Neo4j database files.
    private static String databasePath = "neo4j_db";
    // Neo4j attributes.
    private final GraphDatabaseService database;

    enum RelationshipTypes implements RelationshipType {
        Child,
        Parent
    }

    public Test_neo4j() {
        // Open database.
        database = new GraphDatabaseFactory().newEmbeddedDatabase(databasePath);

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
        printAllLabels();
        System.out.println();
        printAllNodes();
    }

    private void createTestData() {
        try (Transaction tx = database.beginTx()) {
            System.out.println("createTestData");

            Label label = DynamicLabel.label("test_node");

            Node node1 = database.createNode();
            node1.setProperty("name", "Test 1");
            node1.addLabel(label);

            Node node2 = database.createNode();
            node2.setProperty("name", "Test 2");
            node2.addLabel(label);

            Node node3 = database.createNode();
            node3.setProperty("name", "Test 3");
            node3.addLabel(label);

            node1.createRelationshipTo(node2, RelationshipTypes.Child);
            node1.createRelationshipTo(node3, RelationshipTypes.Child);

            tx.success();
        }
    }

    // label : my_label
    // label name = my_label
    private void printAllLabels() {
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
    private void printAllNodes() {
        try (Transaction tx = database.beginTx()) {
            GlobalGraphOperations operator = GlobalGraphOperations.at(database);
            System.out.println("getAllNodes");
            for (Node node : operator.getAllNodes()) {
                printNode("", node);

                for (Relationship relationship : node.getRelationships(RelationshipTypes.Child)) {
                    Node otherNode = relationship.getEndNode();
                    if (!node.equals(otherNode)) {
                        printNode("   ", otherNode);
                    }
                }

                System.out.println();
            }
        }
    }

    private void printNode(String tab, Node node) {
        System.out.println(tab + "node : " + node.toString());
        System.out.println(tab + "node id : " + node.getId());

        for (String key : node.getPropertyKeys()) {
            System.out.println(tab + "\"" + key + "\" = \"" + node.getProperty(key) + "\"");
        }

        for (Label label : node.getLabels()) {
            System.out.println(tab + "label : " + label.toString());
        }
    }

    public static void main(String[] args) {
        Test_neo4j test_neo4j = new Test_neo4j();
    }
}