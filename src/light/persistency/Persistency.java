package light.persistency;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import light.Light;
import light.Pool;
import light.executors.Executor;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.guipackage.cli.CLI;
import light.stores.Group;
import light.stores.Preset;
import light.stores.Sequence;
import light.stores.View;

public class Persistency {
    
    private static Persistency singleton;
    
    private static List<Class<?>> addressScopeMappings; //Used to provide an index of console addresses and their mappings when saving state
    
    private Persistency() {
        addressScopeMappings = new ArrayList<Class<?>>();
    }
    
    public static Persistency getInstance() {
        if (singleton==null) singleton = new Persistency();
        return singleton;
    }
    
    public int getScopeMapping(Class<? extends Addressable> scope) {
        if (!addressScopeMappings.contains(scope)) addressScopeMappings.add(scope);
        return addressScopeMappings.indexOf(scope);
    }

    public Class<?> getScopeFromMapping(int i) {
        return (i<0||i>addressScopeMappings.size()) ? null: addressScopeMappings.get(i);
    }
    
    
    
    /**
     * Saves the state of the program to the specified file.
     * 
     * @return Whether the save was succesfull or not.
     */
    public boolean saveToFile(String fileName) {
        addressScopeMappings.clear(); //Reset address scope mappings
        Light light = Light.getInstance();

        PersistencyWriter pW = new PersistencyWriter();
        
        //Fixtures
        //for (Fixture f : PatchManager.getInstance().allFixtureList()) pW.putObject(f);
        
        //Pools
        for (Pool<?> pool : light.allPoolSet()) {
            for (PersistencyCapable p : pool) pW.writeObject(p);
        }
        
        //Add address mapping index at start
        byte[] data = pW.getBytes();
        pW.empty();
        PersistencyCapable index = () -> {
            PersistencyWriter iW = new PersistencyWriter();
            iW.writeInt(addressScopeMappings.size()); //Num mappings
            for (Class<?> clazz : addressScopeMappings) iW.writeString(clazz.getName());
            return iW.getBytes();
        };
        pW.writeObject(index);
        pW.unsafelyWriteBytes(data);
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(pW.getBytes());
            fos.close();
            CLI.debug("mappings at write: "+addressScopeMappings.toString());
            CLI.debug("Program state has been succesfully saved to "+fileName);
            return true;
        }
        catch (IOException e) {}
        return false;
    }
    
    /**
     * Loads the program state from the specified file.
     * 
     * @param fileName
     * @return Whether the load was succesfull or not.
     */
    public boolean loadFromFile(String fileName) {
        //Get bytes from file
        byte[] bytes = null;
        try {bytes = Files.readAllBytes(Path.of(fileName));}
        catch (IOException e) {e.printStackTrace();}
        if (bytes==null) return false;
        
        Light light = Light.getInstance();
        PersistencyReader pR = new PersistencyReader(bytes);
        
        try {
            //Read address scope index
            if (!pR.hasNextInt()) throw new PersistencyReadException("Address scope mappings index could not be found");
            populateAddressScopeMappings(pR.readObject());
            
            //Read objects
            while (pR.hasNextInt()) {
                byte[] object = pR.readObject();
                PersistencyReader temp = new PersistencyReader(object);
                ConsoleAddress a = ConsoleAddress.generateFromBytes(temp.readObject());
                //CLI.debug("Found: "+a);
                
                //Create object
                Addressable o = null;
                if (a.matchesScope(Preset.class)) {
                    Preset p = Preset.generateFromBytes(object);
                    Pool<Preset> pool = light.getPresetPool(p.getType());
                    if (pool==null) throw new PersistencyReadException("A relevant preset pool could not be found for the created preset");
                    pool.add(p);
                }
                if (a.matchesScope(Group.class)) light.getGroupPool().add(Group.generateFromBytes(object));
                if (a.matchesScope(View.class)) light.getViewPool().add(View.generateFromBytes(object));
                if (a.matchesScope(Sequence.class)) light.getSequencePool().add(Sequence.generateFromBytes(object));
                if (a.matchesScope(Executor.class)) light.getExecutorPool().add(Executor.generateFromBytes(object));
            }

            CLI.debug("Program state has been succesfully read from "+fileName);
            return true;
        }
        catch (PersistencyReadException e) {CLI.error(e);}
        return false;
    }
    
    private void populateAddressScopeMappings(byte[] data) throws PersistencyReadException {
        addressScopeMappings.clear();
        PersistencyReader pR = new PersistencyReader(data);
        int numMappings = pR.readInt();
        for (int i=0; i<numMappings; i++) {
            Class<?> clazz;
            try {clazz = Class.forName(pR.readString());}
            catch (Exception e) {throw new PersistencyReadException("Stored class name didn't resolve to class in this program.");}
            addressScopeMappings.add(clazz);
        }
    }
}
