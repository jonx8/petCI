package ru.etu.petci.handlers;


@FunctionalInterface
public interface CommandHandler {
    /**
     * This method should handle a certain command which is entered by user.
     *
     * @param arg argument after name of the command. You should check if arg is null.
     * @return exit code for the program
     */
    int handle(String arg);
}

