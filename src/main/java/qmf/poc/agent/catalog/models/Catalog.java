package qmf.poc.agent.catalog.models;

import java.util.List;
import java.util.stream.Collectors;

public class Catalog {
    public final List<ObjectData> objectData;
    public final List<ObjectRemarks> objectRemarks;
    public final List<ObjectDirectory> objectDirectories;

    public Catalog(List<ObjectData> objectData, List<ObjectRemarks> objectRemarks, List<ObjectDirectory> objectDirectories) {
        this.objectData = objectData;
        this.objectRemarks = objectRemarks;
        this.objectDirectories = objectDirectories;
    }

    @Override
    public String toString() {
        return "objectData:\n" + objectData.stream()
                .map(ObjectData::toString)
                .collect(Collectors.joining("\n")) +
                "\nobjectRemarks:\n" + objectRemarks.stream()
                .map(ObjectRemarks::toString)
                .collect(Collectors.joining("\n")) +
                "\nobjectDirectories:\n" + objectDirectories.stream()
                .map(ObjectDirectory::toString)
                .collect(Collectors.joining("\n"));
    }
}