package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Jacques on 4/18/2016.
 */
public class ProcessControlBlockImpl implements ProcessControlBlock {
    private int PID = -1;
    private String programName;
    private int priority = -1;
    public LinkedList<Instruction> instructionQ = new LinkedList<>();
    private State state;
    public static int PIDCount = 1;

    public void setName (String name){
        programName = name;
    }

    public ProcessControlBlockImpl(String programName){
        this.PIDCount++;
        this.PID = this.PIDCount;
        this.programName = programName;
        this.setState(state.READY);
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
        return instructionQ.peek();
    }

    /**
     * Determine if there are any more instructions.
     */
    @Override
    public boolean hasNextInstruction() {
        if (instructionQ.isEmpty()){    //if it is empty...means it does not have next (due to the get Instruction method [poll])
            return false;
        }else{
            return true;
        }
    }

    /**
     * Advance to next instruction.
     */
    @Override
    public void nextInstruction() {
        instructionQ.poll();
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

    public static ProcessControlBlockImpl loadProgram(String filename) throws Exception{
        try {
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new FileReader(filename));

            ProcessControlBlockImpl pcb = new ProcessControlBlockImpl(filename);

            //name
            br.readLine().replace("#", "");

            while ((sCurrentLine = br.readLine()) != null) {
                //checks if # (ie comment)
                if (sCurrentLine.charAt(0)!='#'){
                    String[] splitString = sCurrentLine.split(" ");
                    //check if  CPU or IO
                    if(splitString[0].compareToIgnoreCase("CPU")==0){
                        CPUInstruction tempInstruction = new CPUInstruction(Integer.parseInt(splitString[1]));
                        pcb.instructionQ.add(tempInstruction);
                    }else if(splitString[0].compareToIgnoreCase("IO")==0){
                        IOInstruction tempInstruction = new IOInstruction(Integer.parseInt(splitString[1]),Integer.parseInt(splitString[2]));
                        pcb.instructionQ.add(tempInstruction);
                    }
                }
                //System.out.println(sCurrentLine);
            }
            return pcb;

        } catch (FileNotFoundException fileExp) {
            throw fileExp;
        } catch (IOException ioExp) {
            throw ioExp;
        }
    }
}
