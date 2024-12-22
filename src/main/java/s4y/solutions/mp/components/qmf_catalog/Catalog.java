package s4y.solutions.mp.components.qmf_catalog;

import java.util.List;

// Assume the essential use case of the Catalog component is to read it at once
// and then work with the data in memory
/**
 * <p>Catalog class.</p>
 *
 * @author dsa
 */
public class Catalog {
    final List<ObjectData> objectData;
    final List<ObjectRemarks> objectRemarks;
    final List<ObjectDirectory> objectDirectory;

    /**
     * <p>Constructor for Catalog.</p>
     *
     * @param objectData a {@link java.util.List} object
     * @param objectRemarks a {@link java.util.List} object
     * @param objectDirectory a {@link java.util.List} object
     */
    public Catalog(List<ObjectData> objectData, List<ObjectRemarks> objectRemarks, List<ObjectDirectory> objectDirectory) {
        this.objectData = objectData;
        this.objectRemarks = objectRemarks;
        this.objectDirectory = objectDirectory;
    }
}
