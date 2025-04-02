package qmf.poc.agent.catalog.models;

public class QMFObject {
    public final String owner;
    public final String name;
    public final String type;
    public final String subType;
    public final int objectLevel;
    public final String restricted;
    public final String model;
    public final String created;
    public final String modified;
    public final String lastUsed;
    public final String appldata;
    final String remarks;

    public QMFObject(String owner, String name, String type, String subType, int objectLevel,
                     String restricted, String model, String created, String modified, String lastUsed, String appldata, String remarks) {
        this.owner = owner.trim();
        this.name = name.trim();
        this.type = type.trim();
        this.subType = subType == null ? "" : subType.trim();
        this.objectLevel = objectLevel;
        this.restricted = restricted == null ? "" : restricted.trim();
        this.model = model == null ? "" : model.trim();
        this.created = created == null ? "" : created.trim();
        this.modified = modified == null ? "" : modified.trim();
        this.lastUsed = lastUsed == null ? "" : lastUsed.trim();
        this.appldata = appldata == null ? "" : appldata.trim();
        this.remarks = remarks == null ? "" : remarks.trim();
    }

    @Override
    public String toString() {
        final String ad = appldata.length() > 200 ? appldata.substring(0, 200) + "..." : appldata;
        return owner + "|" + name + "|" + type + "|" + ad;
    }
}
