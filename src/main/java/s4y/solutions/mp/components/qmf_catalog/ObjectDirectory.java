package s4y.solutions.mp.components.qmf_catalog;

import java.util.Date;

// https://www.ibm.com/docs/en/qmf/13.1.0?topic=catalog-structure-qobject-directory-table
/**
 * <p>ObjectDirectory class.</p>
 *
 * @author dsa
 */
public class ObjectDirectory {
    final String owner;
    final String name;
    final Type type;
    final SubType subType;
    final Integer objectLevel;
    final Boolean restricted;
    final String model;
    final Date created;
    final Date modified;
    final Date lastUser;

    /**
     * <p>Constructor for ObjectDirectory.</p>
     *
     * @param owner a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     * @param type a {@link java.lang.String} object
     * @param subType a {@link java.lang.String} object
     * @param objectLevel a {@link java.lang.Integer} object
     * @param restricted a {@link java.lang.Boolean} object
     * @param model a {@link java.lang.String} object
     * @param created a {@link java.util.Date} object
     * @param modified a {@link java.util.Date} object
     * @param lastUser a {@link java.util.Date} object
     */
    public ObjectDirectory(String owner, String name, String type, String subType, Integer objectLevel, Boolean restricted, String model, Date created, Date modified, Date lastUser) {
        this.owner = owner;
        this.name = name;
        switch (type.toUpperCase()) {
            case "SQL":
                this.type = Type.SQL;
                break;
            case "QBE":
                this.type = Type.QBE;
                break;
            case "PROMPTED":
                this.type = Type.PROMPTED;
                break;
            default:
                this.type = Type.UNKNOWN;
        }
        switch (subType.toUpperCase()) {
            case "PLOT":
                this.subType = SubType.PLOT;
                break;
            case "HIST":
                this.subType = SubType.HIST;
                break;
            case "PIE":
                this.subType = SubType.PIE;
                break;
            case "TOWER":
                this.subType = SubType.TOWER;
                break;
            case "MIXED":
                this.subType = SubType.MIXED;
                break;
            case "MAP":
                this.subType = SubType.MAP;
                break;
            case "UNIVAR":
                this.subType = SubType.UNIVAR;
                break;
            case "LINEAR":
                this.subType = SubType.LINEAR;
                break;
            case "DCF":
                this.subType = SubType.DCF;
                break;
            case "BASIC":
                this.subType = SubType.BASIC;
                break;
            case "BIVAR":
                this.subType = SubType.BIVAR;
                break;
            case "WILCOSR":
                this.subType = SubType.WILCOSR;
                break;
            case "MANNWHIT":
                this.subType = SubType.MANNWHIT;
                break;
            default:
                this.subType = SubType.PLOT;
        }
        this.objectLevel = objectLevel;
        this.restricted = restricted;
        this.model = model;
        this.created = created;
        this.modified = modified;
        this.lastUser = lastUser;
    }

    enum Type {
        UNKNOWN,
        SQL,
        QBE,
        PROMPTED
    }
    enum SubType {
        PLOT,
        HIST,
        PIE,
        TOWER,
        MIXED,
        MAP,
        UNIVAR,
        LINEAR,
        DCF,
        BASIC,
        BIVAR,
        WILCOSR,
        MANNWHIT,
    }
}
