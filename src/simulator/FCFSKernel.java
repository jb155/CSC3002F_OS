package simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
//
import java.util.*;

import static simulator.ProcessControlBlock.State.TERMINATED;

/**
 * Concrete Kernel type
 * 
 * @author Stephan Jamieson
 * @version 8/3/15
 */
public class FCFSKernel implements Kernel {

    //private Deque<ProcessControlBlock> readyQueue;    --- overly complicated. Decided to take the easier route.
    private LinkedList<ProcessControlBlock> readyQueue;
    private CPU cpu;
    private IODevice[] ioDevices;

    public FCFSKernel(CPU cpu, IODevice[] ioDev) {
		// Set up the ready queue.
        this.cpu = cpu;
        ioDevices = ioDev;
        readyQueue = new LinkedList<ProcessControlBlock>();
    }
    
    private ProcessControlBlock dispatch() {
		// Perform context switch, swapping process
		// currently on CPU with one at front of ready queue.
		// If ready queue empty then CPU goes idle ( holds a null value).
		// Returns process removed from CPU.
        ProcessControlBlock returnedFromCPU = (cpu.contextSwitch(readyQueue.poll())); //Give it next process (pop it off) and retrieve prev process;
        return returnedFromCPU;
	}
            
    
                
    public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
             case MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);
                    Config.addDevice(device);
                }
                break;
             case EXECVE: 
                {
                    ProcessControlBlock pcb = this.loadProgram((String)varargs[0]);
                    if (pcb!=null) {
                        // Loaded successfully.
						// Now add to end of ready queue.
                        readyQueue.add(pcb);
						// If CPU idle then call dispatch.
                        if (cpu.isIdle()){
                            dispatch();
                        }
                    }
                    else {
                        result = -1;
                    }
                }
                break;
             case IO_REQUEST: 
                {
					// IO request has come from process currently on the CPU.
					// Get PCB from CPU.
                    ProcessControlBlock tempProcess = cpu.getCurrentProcess();
					// Find IODevice with given ID: Config.getDevice((Integer)varargs[0]);
                    IODevice temppIODevice = Config.getDevice((Integer)varargs[0]);
					// Make IO request on device providing burst time (varages[1]),

					// the PCB of the requesting process, and a reference to this kernel (so // that the IODevice can call interrupt() when the request is completed.
					//
					// Set the PCB state of the requesting process to WAITING.
					// Call dispatch().
                }
                break;
             case TERMINATE_PROCESS:
                {
					// Process on the CPU has terminated.
					// Get PCB from CPU.
					// Set status to TERMINATED.
                    cpu.getCurrentProcess().setState(TERMINATED);
                    // Call dispatch().
                    dispatch(); //Need to check here...dispatch returns a pcb
                }
                break;
             default:
                result = -1;
        }
        return result;
    }
   
    
    public void interrupt(int interruptType, Object... varargs){
        switch (interruptType) {
            case TIME_OUT:
                throw new IllegalArgumentException("FCFSKernel:interrupt("+interruptType+"...): this kernel does not suppor timeouts.");
            case WAKE_UP:
				// IODevice has finished an IO request for a process.
				// Retrieve the PCB of the process (varargs[1]), set its state
				// to READY, put it on the end of the ready queue.
				// If CPU is idle then dispatch().
                break;
            default:
                throw new IllegalArgumentException("FCFSKernel:interrupt("+interruptType+"...): unknown type.");
        }
    }
    
    private static ProcessControlBlock loadProgram(String filename) {
        try {
            ProcessControlBlockImpl temp = new ProcessControlBlockImpl();
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
