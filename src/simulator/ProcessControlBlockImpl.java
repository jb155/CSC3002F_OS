package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jacques on 4/18/2016.
 */
public class ProcessControlBlockImpl implements ProcessControlBlock {
    private static int PID = 1;
    private String programName;
    private int priority = -1;
    private Instruction instruction;
    private ArrayList<Instruction> instructionQ;
    private int pc;
    private State state;

    public ProcessControlBlockImpl (String name){
        this.PID = PID;
        programName = name;
        instructionQ = new ArrayList<Instruction>();
        pc = 0;
    }

    public void start(){
        instruction = instructionQ.get(pc);
    }

    public void setName (String name){
        programName = name;
    }

    public void add (Instruction in){
        instructionQ.add(in);
    }

    /**
     * Obtain process ID.
     */
    @Override
    public int getPID() {
        return PID;
    }

    /**
     * Obtain program name.
     *
     */
    @Override
    public String getProgramName() {
        return programName;
    }

    /**
     * Obtain process priority();
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Set process priority(), returning the old value.
     */
    @Override
    public int setPriority(int value) {
        int temp = priority;
        priority = value;
        return temp;
    }

    /**
     * Obtain current program 'instruction'.
     */
    @Override
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * Determine if there are any more instructions.
     */
    @Override
    public boolean hasNextInstruction() {
        if(instructionQ.size()>pc +1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Advance to next instruction.
     */
    @Override
    public void nextInstruction() {
        if(hasNextInstruction()) {
            pc++;
            instruction = instructionQ.get(pc);
        }
    }

    /**
     * Obtain process state.
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     * Set process state.
     * Requires <code>getState()!=State.TERMINATED</code>.
     */
    @Override
    public void setState(State state) {
        this.state = state;
    }

    public static ProcessControlBlock loadProgram(String filename) throws Exception{
        ProcessControlBlockImpl pcb = new ProcessControlBlockImpl (filename);
        try {
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new FileReader(filename));

            while ((sCurrentLine = br.readLine()) != null) {
                //checks if # (ie comment)
                if (sCurrentLine.charAt(0)!='#'){
                    String[] splitString = sCurrentLine.split(" ");
                    //check if  CPU or IO
                    if(splitString[0].compareToIgnoreCase("CPU")==0){
                        pcb.add(new CPUInstruction(Integer.parseInt(splitString[1])));
                    }else if(splitString[0].compareToIgnoreCase("IO")==0){
                        IOInstruction tempInstruction = new IOInstruction(Integer.parseInt(splitString[1]),Integer.parseInt(splitString[2]));
                        pcb.add(tempInstruction);
                    }
                }
                System.out.println(sCurrentLine);
            }

        } catch (FileNotFoundException fileExp) {
            throw fileExp;
        } catch (IOException ioExp) {
            throw ioExp;
        }
        PID++;
        pcb.start();
        return pcb;
    }
}
