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
    private LinkedList<Instruction> instructionQ = new LinkedList<>();
    private State state;

    public void setName (String name){
        programName = name;
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

    public ProcessControlBlock loadProgram(String filename) throws Exception{
        try {
            String sCurrentLine;
            BufferedReader br = new BufferedReader(new FileReader(filename));

            programName = br.readLine().replace("#", "");

            while ((sCurrentLine = br.readLine()) != null) {
                //checks if # (ie comment)
                if (sCurrentLine.charAt(0)!='#'){
                    String[] splitString = sCurrentLine.split(" ");
                    //check if  CPU or IO
                    if(splitString[0].compareToIgnoreCase("CPU")==0){
                        instructionQ.add(new CPUInstruction(Integer.parseInt(splitString[1])));
                    }else if(splitString[0].compareToIgnoreCase("IO")==0){
                        IOInstruction tempInstruction = new IOInstruction(Integer.parseInt(splitString[1]),Integer.parseInt(splitString[2]));
                        instructionQ.add(tempInstruction);
                    }
                }
                System.out.println(sCurrentLine);
            }

        } catch (FileNotFoundException fileExp) {
            throw fileExp;
        } catch (IOException ioExp) {
            throw ioExp;
        }
        return this;
    }
}
