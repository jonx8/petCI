package ru.etu.petci.handlers;


public interface Command {
    /**
     * This method should handle a certain command which is entered by user.
     *
     * @param args from the main method
     * @return exit code for the program
     */
    int handle(String[] args);
}


