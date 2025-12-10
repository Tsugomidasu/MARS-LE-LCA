package mars.mips.instructions.customlangs;
import mars.mips.instructions.*;
import mars.*;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.util.*;
import java.util.Random;

/**
 * Lobotomy Corporation Assembly (LCA)
 * A custom assembly language for managing Abnormalities and facility emergencies
 */

public class LobotomyCorpAssembly extends CustomAssembly {

    private Random random = new Random();

    @Override
    public String getName() {
        return "Lobotomy Corporation Assembly";
    }

    @Override
    public String getDescription() {
        return "Custom assembly language for managing Abnormalities, extracting Enkephalin, " +
                "handling Ordeals and Qliphoth Meltdowns in Lobotomy Corporation.";
    }

    @Override
    protected void populate() {

        // Basic Mips Instructions
        instructionList.add(
                new BasicInstruction("add $t1,$t2,$t3",
                        "Addition with overflow: set $t1 to ($t2 plus $t3)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[1]);
                                int add2 = RegisterFile.getValue(operands[2]);
                                int sum = add1 + add2;

                                if ((add1 >= 0 && add2 >= 0 && sum < 0)
                                        || (add1 < 0 && add2 < 0 && sum >= 0)) {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("sub $t1,$t2,$t3",
                        "Subtraction with overflow: set $t1 to ($t2 minus $t3)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int sub1 = RegisterFile.getValue(operands[1]);
                                int sub2 = RegisterFile.getValue(operands[2]);
                                int dif = sub1 - sub2;

                                if ((sub1 >= 0 && sub2 < 0 && dif < 0)
                                        || (sub1 < 0 && sub2 >= 0 && dif >= 0)) {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], dif);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("mul $t1,$t2,$t3",
                        "Multiplication: set $t1 to ($t2 times $t3)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                long product = (long) RegisterFile.getValue(operands[1]) *
                                        (long) RegisterFile.getValue(operands[2]);
                                RegisterFile.updateRegister(operands[0], (int) product);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("and $t1,$t2,$t3",
                        "Bitwise AND: set $t1 to ($t2 AND $t3)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100100",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int result = RegisterFile.getValue(operands[1]) &
                                        RegisterFile.getValue(operands[2]);
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("or $t1,$t2,$t3",
                        "Bitwise OR: set $t1 to ($t2 OR $t3)",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100101",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int result = RegisterFile.getValue(operands[1]) |
                                        RegisterFile.getValue(operands[2]);
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("slt $t1,$t2,$t3",
                        "Set less than: set $t1 to 1 if $t2 < $t3 else 0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 101010",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int result = (RegisterFile.getValue(operands[1]) <
                                        RegisterFile.getValue(operands[2])) ? 1 : 0;
                                RegisterFile.updateRegister(operands[0], result);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("j target",
                        "Jump unconditionally: jump to statement at target address",
                        BasicInstructionFormat.J_FORMAT,
                        "000010 ffffffffffffffffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                Globals.instructionSet.processJump(
                                        ((RegisterFile.getProgramCounter() & 0xF0000000)
                                                | (operands[0] << 2)));
                            }
                        }));

        instructionList.add(
                new BasicInstruction("jal target",
                        "Jump and link: set $ra to Program Counter then jump to target address",
                        BasicInstructionFormat.J_FORMAT,
                        "000011 ffffffffffffffffffffffffff",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                Globals.instructionSet.processReturnAddress(31);
                                Globals.instructionSet.processJump(
                                        (RegisterFile.getProgramCounter() & 0xF0000000)
                                                | (operands[0] << 2));
                            }
                        }));

        instructionList.add(
                new BasicInstruction("beq $t1,$t2,label",
                        "Branch if equal: branch to statement at label's address if $t1 == $t2",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0])
                                        == RegisterFile.getValue(operands[1])) {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("bne $t1,$t2,label",
                        "Branch if not equal: branch to statement at label's address if $t1 != $t2",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000101 fffff sssss tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0])
                                        != RegisterFile.getValue(operands[1])) {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("addi $t1,$t2,-100",
                        "Addition immediate: set $t1 to ($t2 plus signed 16-bit immediate)",
                        BasicInstructionFormat.I_FORMAT,
                        "001000 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[1]);
                                int add2 = operands[2] << 16 >> 16;
                                int sum = add1 + add2;

                                if ((add1 >= 0 && add2 >= 0 && sum < 0)
                                        || (add1 < 0 && add2 < 0 && sum >= 0)) {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));

        instructionList.add(
                new BasicInstruction("lw $t1,-100($t2)",
                        "Load word: set $t1 to contents of memory word address",
                        BasicInstructionFormat.I_FORMAT,
                        "100011 ttttt fffff ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                try {
                                    RegisterFile.updateRegister(operands[0],
                                            Globals.memory.getWord(
                                                    RegisterFile.getValue(operands[2]) + operands[1]));
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));

        instructionList.add(
                new BasicInstruction("sw $t1,-100($t2)",
                        "Store word: store contents of $t1 into memory word address",
                        BasicInstructionFormat.I_FORMAT,
                        "101011 ttttt fffff ssssssssssssssss",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                try {
                                    Globals.memory.setWord(
                                            RegisterFile.getValue(operands[2]) + operands[1],
                                            RegisterFile.getValue(operands[0]));
                                } catch (AddressErrorException e) {
                                    throw new ProcessingException(statement, e);
                                }
                            }
                        }));

        // L. Corp Custum Instructions

        // 1. EXTRACT - Extract PE-Boxes
        instructionList.add(
                new BasicInstruction(
                        "extract $t1,$t2,1",
                        "Extract PE-Box: set $t1 to ($t2 plus a random amount scaled by risk)",
                        BasicInstructionFormat.I_FORMAT,
                        "010000 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rd   = operands[0];
                                int rs   = operands[1];
                                int risk = operands[2] << 16 >> 16;

                                int base = RegisterFile.getValue(rs);

                                // Risk multipliers: 0=ZAYIN(1.0), 1=TETH(0.9), 2=HE(0.8), 3=WAW(0.6), 4=ALEPH(0.5)
                                float[] multipliers = {1.0f, 0.9f, 0.8f, 0.6f, 0.5f};
                                int idx = Math.max(0, Math.min(risk, multipliers.length - 1));
                                float mult = multipliers[idx];

                                int delta = 1 + (int)(mult * (1 + (int)(Math.random() * 10))); // 1..something
                                int result = base + delta;

                                RegisterFile.updateRegister(rd, result);
                            }
                        }));

        // 2. WINST - Work: Instinct
        instructionList.add(
                new BasicInstruction("winst $t1,$t2,15",
                        "Work: Instinct (Fortitude/HP): $t1 = $t2 + sign_ext(imm)",
                        BasicInstructionFormat.I_FORMAT,
                        "010001 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int current = RegisterFile.getValue(operands[1]);
                                int increase = operands[2] << 16 >> 16;
                                RegisterFile.updateRegister(operands[0], current + increase);
                                SystemIO.printString("WINST: Fortitude increased by " + increase + "\n");
                            }
                        }));

        // 3. WINSIGHT - Work: Insight
        instructionList.add(
                new BasicInstruction("winsight $t1,$t2,10",
                        "Work: Insight (Prudence/SP): $t1 = $t2 + sign_ext(imm)",
                        BasicInstructionFormat.I_FORMAT,
                        "010010 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int current = RegisterFile.getValue(operands[1]);
                                int increase = operands[2] << 16 >> 16;
                                RegisterFile.updateRegister(operands[0], current + increase);
                                SystemIO.printString("WINSIGHT: Prudence increased by " + increase + "\n");
                            }
                        }));

        // 4. WATTACH - Work: Attachment
        instructionList.add(
                new BasicInstruction("wattach $t1,$t2,8",
                        "Work: Attachment (Temperance/Success Rate): $t1 = $t2 + sign_ext(imm)",
                        BasicInstructionFormat.I_FORMAT,
                        "010011 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int current = RegisterFile.getValue(operands[1]);
                                int increase = operands[2] << 16 >> 16;
                                RegisterFile.updateRegister(operands[0], current + increase);
                                SystemIO.printString("WATTACH: Temperance increased by " + increase + "\n");
                            }
                        }));

        // 5. WREPRESS - Work: Repression
        instructionList.add(
                new BasicInstruction("wrepress $t1,$t2,12",
                        "Work: Repression (Justice/Movement Speed): $t1 = $t2 + sign_ext(imm)",
                        BasicInstructionFormat.I_FORMAT,
                        "010100 sssss fffff tttttttttttttttt",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int current = RegisterFile.getValue(operands[1]);
                                int increase = operands[2] << 16 >> 16;
                                RegisterFile.updateRegister(operands[0], current + increase);
                                SystemIO.printString("WREPRESS: Justice increased by " + increase + "\n");
                            }
                        }));

        // 6. SUPPRESS - Suppress breach
        instructionList.add(
                new BasicInstruction("suppress $t1,$t2,3",
                        "Suppress breach: $v0 = 1 if $t1 > adjusted_abno, else 0",
                        BasicInstructionFormat.I_FORMAT,
                        "010101 fffff sssss tttttttttttttttt",

                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int agentReg = operands[0];
                                int abnoReg = operands[1];
                                int risk = operands[2] << 16 >> 16;

                                int agentPower = RegisterFile.getValue(agentReg);
                                int abnoPower = RegisterFile.getValue(abnoReg);

                                // Risk difficulty multipliers: ZAYIN=1.0, TETH=1.2, HE=1.5, WAW=2.0, ALEPH=3.0
                                float[] multipliers = {1.0f, 1.2f, 1.5f, 2.0f, 3.0f};
                                int idx = Math.max(0, Math.min(risk, multipliers.length - 1));
                                float mult = multipliers[idx];

                                int effectiveAbno = (int)(abnoPower * mult);
                                int success = (agentPower > effectiveAbno) ? 1 : 0; RegisterFile.updateRegister(2, success);
                            }
                        }));

        // 7. ORDEAL - Trigger ordeal
        instructionList.add(
                new BasicInstruction("ordeal 0",
                        "Trigger an Ordeal event (0 = Dawn, 1 = Noon, 2 = Dusk, 3 = Midnight)",
                        BasicInstructionFormat.I_FORMAT,
                        "010110 00000 00000 ffffffffffffffff",
                        new SimulationCode() {
                            @Override
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int level = operands[0] << 16 >> 16;

                                int r = 8 + (int)(Math.random() * 8);
                                int val = RegisterFile.getValue(r);
                                int[] dmg = {5, 10, 20, 40};
                                int idx = Math.max(0, Math.min(level, dmg.length - 1));
                                int newVal = val - dmg[idx]; RegisterFile.updateRegister(r, newVal);
                                SystemIO.printString("Ordeal level " + level + " struck $" + r + "!\n"); } }));

        // 8. EGO - Equip E.G.O
        instructionList.add(
                new BasicInstruction("ego $t0,3",
                        "Equip E.G.O: $t0 = $t0 + bonus(ego_id)",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "010111 00000 fffff ssssssssssssssss",

                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int rt = operands[0];
                                int egoId = operands[1] << 16 >> 16;

                                int[] bonuses = {1, 2, 3, 5, 8};
                                int idx = Math.max(0, Math.min(egoId, bonuses.length - 1));
                                int bonus = bonuses[idx];
                                int base = RegisterFile.getValue(rt);

                                RegisterFile.updateRegister(rt, base + bonus);
                                SystemIO.printString("Equipped E.G.O id " + egoId +
                                        " on agent $" + rt +
                                        " (+" + bonus + ").\n");
                            }
                }));

        // 9. PANIC - Clerk panic
        instructionList.add(
                new BasicInstruction("panic $t1",
                        "Clerk panic: $t1 = 0, print panic message",
                        BasicInstructionFormat.I_FORMAT,
                        "011000 fffff 00000 0000000000000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int clerkReg = operands[0];

                                RegisterFile.updateRegister(clerkReg, 0);
                                SystemIO.printString("PANIC: Clerk in register $" + clerkReg +
                                        " has panicked! Register cleared to 0.\n");
                            }
                        }));

        // 10. MELTDOWN - Trigger meltdown
        instructionList.add(
                new BasicInstruction("meltdown $s1",
                        "Trigger Qliphoth Meltdown in department",
                        BasicInstructionFormat.I_FORMAT,
                        "011001 fffff 00000 0000000000000000",
                        new SimulationCode() {
                            public void simulate(ProgramStatement statement) throws ProcessingException {
                                int[] operands = statement.getOperands();
                                int department = operands[0];

                                SystemIO.printString("MELTDOWN: Department " + department +
                                        " in full meltdown!\n");

                                // Penalize all agents
                                for (int i = 8; i <= 15; i++) { // $t0-$t7
                                    int current = RegisterFile.getValue(i);
                                    RegisterFile.updateRegister(i, Math.max(0, current - 10));
                                }
                                SystemIO.printString("All agents penalized by 10 points\n");

                                // Increase global risk
                                try {
                                    int gpValue = RegisterFile.getValue(28); // $gp
                                    int riskAddress = gpValue + 8; // $risk at 8($gp)
                                    int currentRisk = Globals.memory.getWord(riskAddress);
                                    Globals.memory.setWord(riskAddress, Math.min(4, currentRisk + 1));
                                    SystemIO.printString("Global risk level increased\n");
                                } catch (AddressErrorException e) {
                                    // Continue if memory access fails
                                }
                            }
                        }));
    }
}