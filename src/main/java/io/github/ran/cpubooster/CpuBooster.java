package io.github.ran.cpubooster;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;

import java.util.HashMap;
import java.util.Map;

public class CpuBooster {
    private static String balancedPowerID;
    private static String ultimatePowerID;
    private static PowerShell powerShell;

    public static void main(String[] args) throws InterruptedException {
        try (PowerShell session = PowerShell.openSession()) {
            String[] response = session.executeCommand("powercfg list").getCommandOutput().split("\n");

            for (String line : response) {
                if (line.contains("Balanced")) {
                    balancedPowerID = line.split("\\s+")[3];
                }
                if (line.contains("Ultimate")) {
                    ultimatePowerID = line.split("\\s+")[3];
                }
            }
        } catch (PowerShellNotAvailableException exception) {
            System.out.println("PowerShell is not available");
            return;
        }
        Map<String, String> config = new HashMap<String, String>();
        config.put("maxWait", "1");
        config.put("waitPause", "1");


        powerShell = PowerShell.openSession();
        powerShell.configuration(config);
        if (!balancedPowerID.isEmpty() && !ultimatePowerID.isEmpty()) changePowerPlans();
    }

    private static void changePowerPlans() throws InterruptedException {
        System.gc(); // I don't know why but there's a memory leak somewhere
        powerShell.executeCommand("powercfg -setactive " + balancedPowerID);
        Thread.sleep(2350);
        powerShell.executeCommand("powercfg -setactive " + ultimatePowerID);
        Thread.sleep(3550);
        changePowerPlans();
    }
}
