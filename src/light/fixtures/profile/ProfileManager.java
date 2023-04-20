package light.fixtures.profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import light.fixtures.Attribute;
import light.fixtures.Feature;
import light.fixtures.FixtureType;
import light.general.Utils;
import light.guipackage.cli.CLI;
import light.stores.Preset.PresetType;

public class ProfileManager {
    
    private static ProfileManager singleton;
    
    private Set<Profile> defaultProfiles;
    private Set<Profile> userLoadedProfiles;
    
    private ProfileManager() {
        defaultProfiles = new HashSet<Profile>();
        userLoadedProfiles = new HashSet<Profile>();
        loadDefaultProfiles();
    }
    
    public static ProfileManager getInstance() {
        if (singleton==null) singleton = new ProfileManager();
        return singleton;
    }
    
    private void loadDefaultProfiles() {
        
    }
    
    public Profile parseProfile(String filePath) throws ProfileParseException {
        Profile profile = parse(filePath);
        if (profile!=null) userLoadedProfiles.add(profile);
        return profile;
    }
    
    public Set<Profile> getProfilesWithName(String name) {
        Set<Profile> result = new HashSet<>();
        for (Profile profile : userLoadedProfiles) {
            if (profile.getName().equals(name)) result.add(profile);
        }
        return result;
    }
    
    public Set<Profile> getProfilesWithManufacturer(String manufacturerName) {
        Set<Profile> result = new HashSet<>();
        for (Profile profile : userLoadedProfiles) {
            if (profile.getManufacturerName().equals(manufacturerName)) result.add(profile);
        }
        return result;
    }
    
    private Profile parse(String filePath) throws ProfileParseException {
        CLI.debug("Parsing file "+filePath+" for fixture profile...");
        Profile profile = new Profile();
        
        try {
            File xml = new File("profiles/"+filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xml);
            doc.getDocumentElement().normalize();
            
            //Fixture info
            NodeList nodes = doc.getElementsByTagName("FixtureInfo");
            if (nodes.getLength()!=1) throw new ProfileParseException("No FixtureInfo element present");
            Node node = nodes.item(0);
            if (node==null||node.getNodeType()!=Node.ELEMENT_NODE) throw new ProfileParseException("Malformed FixtureInfo element");
            
            Element element = (Element) node;
            node = element.getElementsByTagName("FixtureName").item(0);
            if (node!=null) profile.setName(node.getTextContent());
            
            node = element.getElementsByTagName("FixtureMode").item(0);
            if (node!=null) profile.setModeName(node.getTextContent());
            
            node = element.getElementsByTagName("FixtureManufacturer").item(0);
            if (node!=null) profile.setManufacturerName(node.getTextContent());
            
            node = element.getElementsByTagName("FixtureType").item(0);
            if (node!=null) profile.setFixtureType(FixtureType.getFixtureType(node.getTextContent()));
            
            //Fixture data
            nodes = doc.getElementsByTagName("Channel");
            Node attr;
            ProfileChannel channel;
            
            for (int i=0; i<nodes.getLength(); i++) {
                //Channel
                node = nodes.item(i);
                NamedNodeMap attrs = node.getAttributes();
                if (attrs.getLength()==0) continue;
                
                channel = new ProfileChannel();
                
                attr = attrs.getNamedItem("index");
                if (attr!=null) channel.setIndex(parseInt(attr.getTextContent()));
                
                attr = attrs.getNamedItem("feature");
                if (attr!=null) channel.setFeature(Feature.getFeature(attr.getTextContent()));
                
                attr = attrs.getNamedItem("attribute");
                if (attr!=null) channel.setAttribute(Attribute.getAttribute(attr.getTextContent()));
                
                attr = attrs.getNamedItem("preset");
                if (attr!=null) channel.setPresetType(PresetType.getPresetType(attr.getTextContent()));
                
                attr = attrs.getNamedItem("attribute_user_name");
                if (attr!=null) channel.setAttributeUserName(attr.getTextContent());
                
                profile.addChannel(channel);
                
                //Functions
                NodeList functionNodes = node.getChildNodes();
                Node functionNode;
                ProfileChannelFunction function;
                
                for (int z=0; z<functionNodes.getLength(); z++) {
                    functionNode = functionNodes.item(z);
                    if (!functionNode.getNodeName().equals("ChannelFunction")) continue;
                    
                    attrs = functionNode.getAttributes();
                    if (attrs.getLength()==0) continue;
                    function = new ProfileChannelFunction();
                    
                    attr = attrs.getNamedItem("index");
                    if (attr!=null) function.setIndex(parseInt(attr.getTextContent()));
                    
                    attr = attrs.getNamedItem("name");
                    if (attr!=null) function.setName(attr.getTextContent());
                    
                    attr = attrs.getNamedItem("min_dmx");
                    if (attr!=null) function.setMinDMX(parseDouble(attr.getTextContent()));
                    
                    attr = attrs.getNamedItem("max_dmx");
                    if (attr!=null) function.setMaxDMX(parseDouble(attr.getTextContent()));
                    
                    attr = attrs.getNamedItem("min_value");
                    if (attr!=null) function.setMinValue(parseDouble(attr.getTextContent()));
                    
                    attr = attrs.getNamedItem("max_value");
                    if (attr!=null) function.setMaxValue(parseDouble(attr.getTextContent()));
                    
                    attr = attrs.getNamedItem("wheel");
                    if (attr!=null) function.setWheelIndex(parseInt(attr.getTextContent()));
                    
                    channel.addFunction(function);
                    
                    //Macros
                    NodeList macroNodes = functionNode.getChildNodes();
                    Node macroNode;
                    ProfileChannelMacro macro;
                    
                    for (int q=0; q<macroNodes.getLength(); q++) {
                        macroNode = macroNodes.item(q);
                        if (!macroNode.getNodeName().equals("ChannelMacro")) continue;
                        
                        attrs = macroNode.getAttributes();
                        if (attrs.getLength()==0) continue;
                        macro = new ProfileChannelMacro();
                        
                        attr = attrs.getNamedItem("index");
                        if (attr!=null) macro.setIndex(parseInt(attr.getTextContent()));
                        
                        attr = attrs.getNamedItem("name");
                        if (attr!=null) macro.setName(attr.getTextContent());
                        
                        attr = attrs.getNamedItem("from_dmx");
                        if (attr!=null) macro.setFromDMX(parseDouble(attr.getTextContent()));
                        
                        attr = attrs.getNamedItem("to_dmx");
                        if (attr!=null) macro.setToDMX(parseDouble(attr.getTextContent()));
                        
                        attr = attrs.getNamedItem("slot");
                        if (attr!=null) macro.setSlotIndex(parseInt(attr.getTextContent()));
                        
                        function.addMacro(macro);
                    }
                }
            }
            
            //Wheels
            nodes = doc.getElementsByTagName("Wheel");
            attr = null;
            ProfileWheel wheel = null;
            
            for (int i=0; i<nodes.getLength(); i++) {
                //Wheel
                node = nodes.item(i);
                NamedNodeMap attrs = node.getAttributes();
                if (attrs.getLength()==0) continue;
                
                wheel = new ProfileWheel();
                attr = attrs.getNamedItem("index");
                if (attr!=null) wheel.setIndex(parseInt(attr.getTextContent()));
                profile.addWheel(wheel);
                
                //Slots
                NodeList slotNodes = node.getChildNodes();
                Node slotNode;
                ProfileWheelSlot slot;
                
                for (int z=0; z<slotNodes.getLength(); z++) {
                    slotNode = slotNodes.item(z);
                    if (!slotNode.getNodeName().equals("Slot")) continue;
                    
                    attrs = slotNode.getAttributes();
                    if (attrs.getLength()==0) continue;
                    slot = new ProfileWheelSlot();
                    
                    attr = attrs.getNamedItem("index");
                    if (attr!=null) slot.setIndex(parseInt(attr.getTextContent()));
                    
                    attr = attrs.getNamedItem("media_name");
                    if (attr!=null) slot.setMediaName(attr.getTextContent());
                    
                    attr = attrs.getNamedItem("media_filename");
                    if (attr!=null) slot.setMediaFileName(attr.getTextContent());
                    
                    wheel.addSlot(slot);
                }
            }
        } catch (Exception e) {
            CLI.error("Profile state at error:\n"+profile.toString());
            throw new ProfileParseException(e.getMessage());
        }
        
        if (!profile.validate()) throw new ProfileParseException("Profile did not pass validation");
        else CLI.debug("Profile "+profile.toString()+" passed validation");
        return profile;
    }
    
    public void printXML(String filePath) {
        try {
            // Create a URL object and open a connection to it
            URL url = Utils.getURL("profiles/"+filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            
            // Read the contents of the URL into a string
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }
            CLI.debug("Content:\n"+builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int parseInt(String text) throws ProfileParseException {
        try {return Integer.parseInt(text);}
        catch (NumberFormatException e) {
            throw new ProfileParseException("Could not parse "+text+" as an int");
        }
    }
    
    public double parseDouble(String text) throws ProfileParseException {
        try {return Double.parseDouble(text);}
        catch (NumberFormatException e) {
            throw new ProfileParseException("Could not parse "+text+" as an int");
        }
    }
}