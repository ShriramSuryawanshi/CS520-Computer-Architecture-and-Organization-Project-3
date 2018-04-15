/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementation;

import tools.MyALU;
import utilitytypes.EnumOpcode;
import baseclasses.InstructionBase;
import baseclasses.PipelineRegister;
import baseclasses.PipelineStageBase;
import voidtypes.VoidLatch;
import baseclasses.CpuCore;
import baseclasses.FunctionalUnitBase;
import baseclasses.Latch;
import cpusimulator.CpuSimulator;
import tools.MultiStageDelayUnit;
import static utilitytypes.EnumOpcode.*;
import utilitytypes.ICpuCore;
import utilitytypes.IFunctionalUnit;
import utilitytypes.IGlobals;
import utilitytypes.IModule;
import utilitytypes.IPipeReg;
import static utilitytypes.IProperties.*;
import utilitytypes.IRegFile;
import utilitytypes.Logger;
import utilitytypes.Operand;
import voidtypes.VoidLabelTarget;


public class MemUnit extends FunctionalUnitBase {

    public MemUnit(IModule parent, String name) {
        super(parent, name);
    }

    /**
     * * Addr Stage **
     */
    static class Addr extends PipelineStageBase {

        public Addr(IModule parent) {
            // For simplicity, we just call this stage "in".
            super(parent, "Addr");
        }

        @Override
        public void compute(Latch input, Latch output) {
            if (input.isNull()) {
                return;
            }

            doPostedForwarding(input);
            InstructionBase ins = input.getInstruction();
            setActivity(ins.toString());

            Operand oper0 = ins.getOper0();
            int oper0val = ins.getOper0().getValue();
            int source1 = ins.getSrc1().getValue();
            int source2 = ins.getSrc2().getValue();

            // The Memory stage no longer follows Execute.  It is an independent
            // functional unit parallel to Execute.  Therefore we must perform
            // address calculation here.
            int addr = source1 + source2;

            output.setResultValue(addr);
            output.setInstruction(ins);
        }
    }

    static class LSQ extends PipelineStageBase {

        public LSQ(IModule parent) {
            // For simplicity, we just call this stage "in".
            super(parent, "LSQ");
        }

        @Override
        public void compute(Latch input, Latch output) {
            if (input.isNull()) {
                return;
            }

            doPostedForwarding(input);
            InstructionBase ins = input.getInstruction();
            setActivity(ins.toString());

            output.setResultValue(input.getResultValue());
            output.setInstruction(input.getInstruction());
        }
    }

    static class DCache extends PipelineStageBase {

        public DCache(IModule parent) {
            // For simplicity, we just call this stage "in".
            super(parent, "DCache");
        }

        @Override
        public void compute(Latch input, Latch output) {
            if (input.isNull()) {
                return;
            }

            doPostedForwarding(input);
            InstructionBase ins = input.getInstruction();
            setActivity(ins.toString());

            int oper0val = ins.getOper0().getValue();
            int addr = input.getResultValue();

            int value = 0;
            IGlobals globals = (GlobalData) getCore().getGlobals();
            int[] memory = globals.getPropertyIntArray(MAIN_MEMORY);

            switch (ins.getOpcode()) {
                case LOAD:
                    // Fetch the value from main memory at the address
                    // retrieved above.
                    value = memory[addr];
                    output.setResultValue(value);
                    output.setInstruction(ins);
                    addStatusWord("Mem[" + addr + "]");
                    break;

                case STORE:
                    // For store, the value to be stored in main memory is
                    // in oper0, which was fetched in Decode.
                    memory[addr] = oper0val;
                    addStatusWord("Mem[" + addr + "]=" + ins.getOper0().getValueAsString());
                    return;

                default:
                    throw new RuntimeException("Non-memory instruction got into Memory stage");
            }
        }
    }

    @Override
    public void createPipelineRegisters() {
        createPipeReg("AddrToLSQ");
        createPipeReg("LSQToDCache");
    }

    @Override
    public void createPipelineStages() {
        addPipeStage(new MemUnit.Addr(this));
        addPipeStage(new MemUnit.LSQ(this));
        addPipeStage(new MemUnit.DCache(this));
    }

    @Override
    public void createChildModules() {
        // @shree - nothing yet
        
//        addChildUnit(new MemUnit(this, "Addr"));
//        addChildUnit(new MemUnit(this, "LSQ"));
//        addChildUnit(new MemUnit(this, "DCache"));
    }

    @Override
    public void createConnections() {
//        addRegAlias("DCache.out", "out");
        connect("Addr", "AddrToLSQ", "LSQ");
        connect("LSQ", "LSQToDCache", "DCache");
        connect("LSQToDCache", "out");
    }

    @Override
    public void specifyForwardingSources() {
        addForwardingSource("out");
    }

}
