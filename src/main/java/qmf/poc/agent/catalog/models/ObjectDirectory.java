package qmf.poc.agent.catalog.models;

public class ObjectDirectory {
    final String owner;
    final String name;
    final String type;
    final String subType;
    final int objectLevel;
    final String restricted;
    final String model;
    final String created;
    final String modified;
    final String lastUsed;

    public ObjectDirectory(String owner, String name, String type, String subType, int objectLevel,
                           String restricted, String model, String created, String modified, String lastUsed) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.objectLevel = objectLevel;
        this.restricted = restricted;
        this.model = model;
        this.created = created;
        this.modified = modified;
        this.lastUsed = lastUsed;
    }

    @Override
    public String toString() {
        String s = "'" + owner + "'|'" + name + "'|'" + type + "'|'" + subType + "'|'" + objectLevel +
                "'|'" + restricted + "'|'" + model + "'|'" + created + "'|'" + modified + "'|'" + lastUsed;// + "'";
        return s.length() <= 131 ? s + "'" : s.substring(0, 129) + "...'";
    }
}
