package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import static simulator.ProcessControlBlock.State.*;

//

/**
 * Concrete Kernel type
 * 
 * @author Stephan Jamieson
 * @version 8/3/15
 */
public class RRKernel implements Kernel {
    private LinkedList<ProcessControlBlock> readyQueue;
    private int timeSlice;

    public RRKernel(int time) {
		// Set up the ready queue.
        readyQueue = new LinkedList<ProcessControlBlock>();
        timeSlice = time;
    }
    
    private ProcessControlBlock dispatch() {
        ProcessControlBlockImpl curr = (ProcessControlBlockImpl) Config.getCPU().getCurrentProcess();

        if(!readyQueue.isEmpty()) {
            ProcessControlBlockImpl next = (ProcessControlBlockImpl) readyQueue.poll();
            next.setState(RUNNING);
            Config.getCPU().contextSwitch(next);
            //Call scheduler
            Config.getSimulationClock().scheduleInterrupt(timeSlice,this,next.getPID());
        } else{
              Config.getCPU().contextSwitch(null);
        }


        return curr;
	}

    public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
            //case 1:
            case SystemCall.MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);
                    Config.addDevice(device);
                }
                break;
            //case 2:
            case SystemCall.EXECVE:
                {
                    ProcessControlBlock pcb = loadProgram((String)varargs[0]);
                    if (pcb!=null) {
                        // Loaded successfully.
						// Now add to end of ready queue.
                        readyQueue.add(pcb);
						// If CPU idle then call dispatch.
                        if (Config.getCPU().isIdle()){
                            dispatch();
                        }
                        //advance system time?
                    }
                    else {
                        result = -1;
                    }
                }
                break;
            //case 3:
            case SystemCall.IO_REQUEST:
                {
					// IO request has come from process currently on the CPU.
					// Get PCB from CPU.
                    ProcessControlBlock tempProcess = Config.getCPU().getCurrentProcess();
					// Find IODevice with given ID: Config.getDevice((Integer)varargs[0]);
					// Make IO request on device providing burst time (varages[1]),
                    IODevice ioDevice = Config.getDevice((Integer)varargs[0]);
					// the PCB of the requesting process, and a reference to this kernel (so // that the IODevice can call interrupt() when the request is completed.
                    ioDevice.requestIO((Integer)varargs[1], tempProcess, this);
                    // Set the PCB state of the requesting process to WAITING.
                    tempProcess.setState(WAITING);
                    //IO request it needs to cancel the current one and create a new one
                    Config.getSimulationClock().cancelInterrupt(Config.getCPU().getCurrentProcess().getPID());
                    // Call dispatch().
                    dispatch();
                }
                break;
            //case 4:
            case SystemCall.TERMINATE_PROCESS:
                {
					// Process on the CPU has terminated.
					// Get PCB from CPU.
					// Set status to TERMINATED.
                    Config.getCPU().getCurrentProcess().setState(TERMINATED);
                    Config.getSimulationClock().cancelInterrupt(Config.getCPU().getCurrentProcess().getPID());
                    // Call dispatch().
                    dispatch(); //Need to check here...dispatch returns a pcb
                }
                break;
             default:
                result = -1;
        }
        //advance time
        //Config.getSimulationClock().logSystemCall();
        return result;
    }

    public void interrupt(int interruptType, Object... varargs){
        switch (interruptType) {
            case TIME_OUT:
                //Times out, start next process
                Config.getSimulationClock().scheduleInterrupt(timeSlice,this,varargs[0]);
            case WAKE_UP:
                // IODevice has finished an IO request for a process.
                // Retrieve the PCB of the process (varargs[1]), set its state
                //ProcessControlBlock pcb = Config.getCPU().getCurrentProcess();
                // to READY, put it on the end of the ready queue.
                ProcessControlBlockImpl pcb = (ProcessControlBlockImpl)varargs[1];
                readyQueue.add(pcb);
                // If CPU is idle then dispatch().
                if(Config.getCPU().isIdle()){
                    dispatch();
                    Config.getSimulationClock().cancelInterrupt(Config.getCPU().getCurrentProcess().getPID());
                }
                break;
            default:
                throw new IllegalArgumentException("RRKernel:interrupt("+interruptType+"...): unknown type.");
        }
        //Config.getSimulationClock().logInterrupt();
    }
    
    private static ProcessControlBlock loadProgram(String filename) {
        try {
            ProcessControlBlockImpl temp = new ProcessControlBlockImpl(filename);
            return temp.loadProgram(filename);
        }
        catch (FileNotFoundException fileExp) {
            return null;
        }
        catch (IOException ioExp) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
