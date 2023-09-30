package ru.etu.petci.handlers;

import java.util.HashMap;
import java.util.Map;

public class JobCommand implements Command {
    private static final Map<String, Command> commandMap = new HashMap<>();

    static {
        commandMap.put("add", new AddJobCommand());
        commandMap.put("activate", new ActivateJobCommand(true));
        commandMap.put("deactivate", new ActivateJobCommand(false));
        commandMap.put("delete", new DeleteJobCommand());
        commandMap.put("list", new ListJobCommand());
    }


    @Override
    public int handle(String[] args) {
        if (args.length > 1) {
            Command cmd = commandMap.get(args[1]);
            if (cmd != null) {
                return cmd.handle(args);
            }
        }
        showJobHelp();
        return 1;
    }


    private void showJobHelp() {
        System.out.println("Job command help:");
        System.out.println("add - add new job");
        System.out.println("delete (job name) - delete job");
        System.out.println("activate (job name) - activate job");
        System.out.println("deactivate (job name) - activate job");
        System.out.println("list - show list of jobs");
    }
}
