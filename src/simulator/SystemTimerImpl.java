package simulator;

/**
 * Created by Jacques on 4/19/2016.
 */
public class SystemTimerImpl implements SystemTimer {
    private long systemTime;
    private long idleTime;
    private long userTime;
    private long kernelTime;
    //used for RR only
    private TimeOutEvent timeOutEvent;

    /**
     * Constructor method
     */
    public SystemTimerImpl() {
        kernelTime = 0;
        systemTime = 0;
        idleTime = 0;
        userTime = 0;

    }

    private void advanceSystemTime(long time) {
        Config.getSimulationClock().advanceSystemTime(time);
    }

    private void advanceIdleTime(long time) {
        Config.getSimulationClock().advanceIdleTime(time);
    }

    private void advanceUserTime(long time) {
        Config.getSimulationClock().advanceUserTime(time);
    }

    private void advanceKernelTime(long time) {
        Config.getSimulationClock().advanceKernelTime(time);
    }

    @Override
    public long getSystemTime() {
        return systemTime;
    }

    @Override
    public long getIdleTime() {
        return idleTime;
    }

    @Override
    public long getUserTime() {
        return userTime;
    }

    @Override
    public long getKernelTime() {
        return kernelTime;
    }

    @Override
    public void scheduleInterrupt(int timeUnits, InterruptHandler handler, Object... varargs) {
        Config.getSimulationClock().scheduleInterrupt(timeUnits, handler, varargs);
    }

    @Override
    public void cancelInterrupt(int pid) {
        Config.getSimulationClock().cancelInterrupt(pid);
    }
}
