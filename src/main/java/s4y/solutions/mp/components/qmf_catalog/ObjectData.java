package s4y.solutions.mp.components.qmf_catalog;

// https://www.ibm.com/docs/en/qmf/13.1.0?topic=catalog-structure-qobject-data-table
/**
 * <p>ObjectData class.</p>
 *
 * @author dsa
 */
public class ObjectData {
    final String owner;
    final String name;
    final Type type;
    final Short seq ;
    final byte[] appldata;

    /**
     * <p>Constructor for ObjectData.</p>
     *
     * @param owner a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     * @param type a {@link java.lang.String} object
     * @param seq a {@link java.lang.Short} object
     * @param appldata an array of {@link byte} objects
     */
    public ObjectData(String owner, String name, String type, Short seq, byte[] appldata) {
        this.owner = owner;
        this.name = name;
        switch (type.toUpperCase()) {
            case "FORM":
                this.type = Type.FORM;
                break;
            case "PROC":
                this.type = Type.PROC;
                break;
            case "ANALYSIS":
                this.type = Type.ANALYSIS;
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
        this.seq = seq;
        this.appldata = appldata;
    }

    enum Type {
        UNKNOWN,
        FORM,
        PROC,
        ANALYSIS,
        QUERY,
        FOLDER
    }
}
