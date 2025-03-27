package qmf.poc.agent.catalog.models;

public class ObjectRemarks {
    final String owner;
    final String name;
    final String type;
    final String remarks;

    public ObjectRemarks(String owner, String name, String type, String remarks) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        String s = "'" + owner + "'|'" + name + "'|'" + type + "'|'" + remarks;// + "'";
        return s.length() <= 131 ? s + "'" : s.substring(0, 129) + "...'";
    }
}