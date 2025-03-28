package qmf.poc.agent.catalog.models;

public class ObjectData {
    public final String owner;
    public final String name;
    public final String type;
    public final String appldata;

    public ObjectData(String owner, String name, String type, String appldata) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.appldata = appldata;
    }

    @Override
    public String toString() {
        final String s = "'" + owner + "'|'" + name + "'|'" + type + "'|'" + appldata;// + "'";

        if (s.length() < 131) {
            return s + "'";
        } else {
            return s.substring(0, 128) + "...'";
        }
    }
}
