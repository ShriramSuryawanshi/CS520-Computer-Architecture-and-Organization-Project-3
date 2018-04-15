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
import static utilitytypes.EnumOpcode.*;
import utilitytypes.ICpuCore;
import utilitytypes.IGlobals;
import utilitytypes.IModule;
import utilitytypes.IPipeReg;
import static utilitytypes.IProperties.*;
import utilitytypes.IRegFile;
import utilitytypes.Logger;
import utilitytypes.Operand;
import voidtypes.VoidLabelTarget;

public class MemUnit  extends PipelineStageBase {

    public MemUnit(IModule parent, String name) {
        super(parent, name);
    }

    static class Addr extends FunctionalUnitBase {



        public Addr(IModule parent, String name) {
            super(parent, name);
        }

        @Override
        public void compute(Latch input, Latch output) {
            if (input.isNull()) {
                return;
            }
            ;
            doPostedForwarding(input);
            InstructionBase ins = input.getInstruction();
            setActivity(ins.toString());
            System.out.println(ins.toString());
        }
    }
}
