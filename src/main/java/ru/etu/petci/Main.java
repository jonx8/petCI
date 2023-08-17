package ru.etu.petci;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                switch (args[0].strip()) {
                    case "init":
                        System.out.println("Repository init");
                        break;
                    case "continue":
                        System.out.println("Continue observing");
                        break;
                    case "add":
                        System.out.println("Add job");
                        break;
                    default:
                        System.out.println("Command has not been found");
                        System.exit(1);
                }
            } else {
                System.exit(1);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }


    }
}
