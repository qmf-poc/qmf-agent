package qmf.poc.agent.catalog.models;

import java.util.List;
import java.util.stream.Collectors;

public class Catalog {
    public final List<QMFObject> qmfObjects;

    public Catalog(List<QMFObject> qmfObjects) {
        this.qmfObjects = qmfObjects;
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "objects=" + qmfObjects.stream()
                .map(QMFObject::toString)
                .collect(Collectors.joining("\n")) +
                '}';
    }
}