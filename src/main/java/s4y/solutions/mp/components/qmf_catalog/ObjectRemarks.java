package s4y.solutions.mp.components.qmf_catalog;

// https://www.ibm.com/docs/en/qmf/13.1.0?topic=catalog-structure-qobject-remarks-table
/**
 * <p>ObjectRemarks class.</p>
 *
 * @author dsa
 */
public class ObjectRemarks {
    final String owner;
    final String name;
    final Type type;
    final String remarks;

    /**
     * <p>Constructor for ObjectRemarks.</p>
     *
     * @param owner a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     * @param type a {@link java.lang.String} object
     * @param remarks a {@link java.lang.String} object
     */
    public ObjectRemarks(String owner, String name, String type, String remarks) {
        this.owner = owner;
        this.name = name;
        switch (type.toUpperCase()) {
            case "FORM":
                this.type = Type.FORM;
                break;
            case "PROC":
                this.type = Type.PROC;
                break;
            case "ANALYTICS":
                this.type = Type.ANALYTICS;
                break;
            case "QUERY":
                this.type = Type.QUERY;
                break;
            case "FOLDER":
                this.type = Type.FOLDER;
                break;
            default:
                this.type = Type.UNKNOWN;
        }
        this.remarks = remarks;
    }

    enum Type {
        UNKNOWN,
        FORM,
        PROC,
        ANALYTICS,
        QUERY,
        FOLDER
    }
}
