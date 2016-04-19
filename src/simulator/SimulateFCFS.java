package simulator;

import java.util.Scanner;

/**
 * Created by Jacques on 4/18/2016.
 */
public class SimulateFCFS {
    public static void main(String[] args){
        print("*** FCFS ***");
        Scanner s = new Scanner(System.in);
        print("Enter configuration file name: ");
        String config_filename = s.nextLine();
        print("Enter cost of system call: ");
        int cost_syscall = s.nextInt();
        print("Enter cost of context switch: ");
        int cost_context_switch = s.nextInt();
        print("Enter trace level: ");
        int trace_level = s.nextInt();
        s.close();

        EventQueue eventQueue = new EventQueue();
        SystemTimerImpl systemTimer = new SystemTimerImpl();
        Kernel kernel = new FCFSKernel();

        //init trace
        TRACE.SET_TRACE_LEVEL(trace_level);

        //init config
        Config.init(kernel, cost_context_switch, cost_syscall);
        Config.buildConfiguration(config_filename);

        CPU cpu = Config.getCPU();
        SimulationClock simulationClock = Config.getSimulationClock();
    }

    private static void print(String s){
        System.out.println(s);
    }
}
