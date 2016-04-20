package simulator;

import java.util.Scanner;

/**
 * Created by Jacques on 4/18/2016.
 */
public class SimulateRR {
    public static void main(String[] args){
        //Read info from screen/user
        System.out.println("*** FCFS Simulator ***");
        Scanner scan = new Scanner(System.in);
        //File name
        System.out.print("Enter configuration file name: ");
        String config_filename = scan.nextLine();
        //System call cost
        System.out.print("Enter cost of system call: ");
        int cost_syscall = scan.nextInt();
        //Content switch cost
        System.out.print("Enter cost of context switch: ");
        int cost_context_switch = scan.nextInt();
        //Trace level
        System.out.print("Enter trace level: ");
        int trace_level = scan.nextInt();
        scan.close();

        //Determine if there is a trace level, if so print trace
        if (trace_level>0){
            System.out.println("*** Trace ***");
        }

        //init eventQ, SystemTimer and kernel
        EventQueue eventQueue = new EventQueue();
        SystemTimerImpl systemTimer = new SystemTimerImpl();
        Kernel kernel = new FCFSKernel();

        //init trace
        TRACE.SET_TRACE_LEVEL(trace_level);

        //init config
        Config.init(kernel, cost_context_switch, cost_syscall);
        Config.buildConfiguration(config_filename);
        Config.run();

        System.out.println("*** Results ***");
        System.out.println(Config.getSimulationClock().toString());
    }
}
