package light.commands;

import light.Light;
import light.Pool;
import light.Programmer;
import light.general.Addressable;
import light.general.ConsoleAddress;
import light.general.DataStore;
import light.stores.Cue;
import light.stores.Preset;
import light.stores.Preset.PresetType;
import light.stores.Sequence;

public class Store implements Command {

    ConsoleAddress address;

    public Store(ConsoleAddress address) {
        this.address = address;
    }

    @Override
    public void execute() {
        if (address.matchesScope(Preset.class)) storePreset();
        if (address.matchesScope(Sequence.class)) storePreset();
    }

    private void storePreset() {
        if (address==null) return;

        //Make new preset of correct type
        PresetType t = Preset.getTypeFromAddress(address);
        if (t==null) return;
        Preset preset = new Preset(address, t);

        //Store all relevant values of programmer in new preset
        DataStore filteredProg = Programmer.getInstance().getFilteredClone(preset.getValidAttributes());
        preset.set(filteredProg);

        Light.getInstance().getPresetPool(preset.getType()).add(preset);
    }

    private void storeCue() {
        if (address==null) return;

        //Find sequence
        Sequence sequence = (Sequence) Light.getInstance().resolveAddress(new ConsoleAddress(Sequence.class, address.getPrefix(), 0));
        if (sequence==null) return;

        //If address was given as base address of sequence then expectation is add as next cue
        if (address.equals(sequence.getAddress())) {
            Cue cue = new Cue(address);
            cue.setData(Programmer.getInstance().clone());
            sequence.addAsNext(cue);
            return;
        }

        //Check if cue is already present in sequence
        if (sequence.hasCue(address)) {
            //TODO prompt user for cue mode merge choice
        }
        else {
            //Make new cue
            Cue cue = new Cue(address);
            cue.setData(Programmer.getInstance().clone());
            sequence.add(cue);
        }
    }
}
