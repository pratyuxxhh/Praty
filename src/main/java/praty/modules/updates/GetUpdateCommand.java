package praty.modules.updates;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GetUpdateCommand implements praty.command.Command {
    private static final String[] SPINNER = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};

    @Override
    public void execute(praty.command.CommandContext ctx) {
        try {
            Process checkProcess = new ProcessBuilder(
                    "powershell",
                    "-Command",
                    "(New-Object -ComObject Microsoft.Update.Session).CreateUpdateSearcher().Search('IsInstalled=0').Updates.Count")
                    .start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(checkProcess.getInputStream()));

            String count = reader.readLine();
            int updateCount = Integer.parseInt(count);

            if (updateCount > 0) {
                System.out.println("\n✓ " + updateCount + " update(s) available");
                System.out.println("Starting system update...\n");

                showAnimationAndUpdate();
            } else {
                System.out.println("✓ System is up to date");
            }

        } catch (Exception e) {
            System.err.println("✗ Error checking updates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAnimationAndUpdate() throws Exception {
        Process updateProcess = new ProcessBuilder(
                "powershell",
                "-Command",
                "(New-Object -ComObject Microsoft.Update.Session).CreateUpdateSearcher().Search('IsInstalled=0').Updates | ForEach-Object { $downloader = New-Object -ComObject Microsoft.Update.Session; $downloader.CreateUpdateDownloader().Download($_); } ; Write-Host 'Updates downloaded'; (New-Object -ComObject Microsoft.Update.Session).CreateUpdateInstaller() | ForEach-Object { $_.Updates = (New-Object -ComObject Microsoft.Update.Session).CreateUpdateSearcher().Search('IsInstalled=0').Updates; $_.Install(); }")
                .start();

        int spinnerIndex = 0;
        long startTime = System.currentTimeMillis();

        while (updateProcess.isAlive()) {
            System.out.print("\r" + SPINNER[spinnerIndex % SPINNER.length] + " Installing updates...");
            System.out.flush();
            spinnerIndex++;
            Thread.sleep(100);
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.print("\r✓ Updates installed successfully! (" + duration / 1000 + "s)\n");
        System.out.flush();
    }
}