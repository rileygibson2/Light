package light.fixtures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import light.persistency.PersistencyCapable;

public class FixtureProfile implements PersistencyCapable {
    
    private List<Attribute> attributes;
    private String name;

    public FixtureProfile(String name) {
        this.name = name;
        attributes = new ArrayList<Attribute>();
    }

    public FixtureProfile(String name, Collection<Attribute> attributes) {
        this.name = name;
        this.attributes = new ArrayList<Attribute>();
        this.attributes.addAll(attributes);
    }

    public void setName(String name) {this.name = name;}

    public String getName() {return this.name;}

    public boolean hasAttribute(Attribute attribute) {
        if (attributes==null) return false;
        return attributes.contains(attribute);
    }

    public List<Attribute> getAttributes() {return attributes;}

    public void addAttribute(Attribute a) {attributes.add(a);}

    public void addAttributes(Collection<Attribute> a) {attributes.addAll(a);}

    @Override
    public byte[] getBytes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    @Override
    public void generateFromBytes(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateFromBytes'");
    }
}
