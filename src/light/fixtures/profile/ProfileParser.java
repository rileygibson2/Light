package light.fixtures.profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import light.general.Utils;
import light.guipackage.cli.CLI;

public class ProfileParser {
    
    public ProfileParser() {
        
    }
    
    public void parse(String filePath) throws ProfileParseException {
        CLI.debug("Parsing profile "+filePath+"...");
        
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
            Node name = element.getElementsByTagName("FixtureName").item(0);
            Node mode = element.getElementsByTagName("FixtureMode").item(0);
            Node manufacturer = element.getElementsByTagName("FixtureManufacturer").item(0);
            
            if (name!=null) profile.setName(name.getTextContent());
            if (mode!=null) profile.setModeName(mode.getTextContent());
            if (manufacturer!=null) profile.setManufacturerName(manufacturer.getTextContent());
            
            CLI.debug(profile.toString());
            
            //Channels
            nodes = doc.getElementsByTagName("Channel");
            for (int i=0; i<nodes.getLength(); i++) {
                node = nodes.item(i);
                NamedNodeMap attrs = node.getAttributes();
                Node feature = attrs.getNamedItem("feature");
                Node attribute = attrs.getNamedItem("attribute");
                CLI.debug(feature.getTextContent());
                CLI.debug(attribute.getTextContent());
            }

        } catch (Exception e) {
            throw new ProfileParseException(e.getMessage());
        }
    }
}