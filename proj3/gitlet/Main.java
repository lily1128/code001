package gitlet;

import java.io.File;
import java.util.ConcurrentModificationException;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Xinyun(Lily) Zhang
 */


public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */

    static final String[] COMMAND = {
        "init",
        "add",
        "commit",
        "rm",
        "log",
        "global-log",
        "find",
        "status",
        "checkout",
        "branch",
        "rm-branch",
        "reset",
        "merge",
        "add-remote",
        "rm-remote",
        "push",
        "fetch",
        "pull"
    };

    /**
     * initCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void initCommand(Gitlet g, String... x) {
        if (x.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        g.init();
        return;
    }

    /**
     * addCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void addCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        g.add(x[1]);
    }

    /**
     * commitCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void commitCommand(Gitlet g, String... x) {
        if (x.length == 1) {
            System.out.println("Please enter a commit message.");
            return;
        } else {
            String message = "";
            for (int i = 1; i < x.length - 1; i++) {
                message += x[i] + " ";
            }
            message += x[x.length - 1];
            g.commit(message);
        }
    }

    /**
     * rmCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void rmCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        g.rm(x[1]);
    }

    /**
     * logCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void logCommand(Gitlet g, String... x) {
        if (x.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        g.log();
    }

    /**
     * globalLogCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void globalLogCommand(Gitlet g, String... x) {
        if (x.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        g.globalLog();
    }

    /**
     * checkoutCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void checkoutCommand(Gitlet g, String... x) {
        if (x.length == 2) {
            g.checkoutB(x[1]);
        } else if (x.length == 3) {
            if (x[1].equals("--")) {
                g.checkoutF(x[2]);
            }
        } else if (x.length == 4) {
            if (x[2].equals("--")) {
                g.checkoutCF(x[1], x[3]);
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        } else {
            System.out.println("Incorrect operands.");
        }
    }

    /**
     * statusCommand.
     * @param g is gitlet value
     */
    public static void statusCommand(Gitlet g) {
        g.status();
    }

    /**
     * rmBranchCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void rmBranchCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
        } else {
            g.rmBranch(x[1]);
        }
    }

    /**
     * findCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void findCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
        }
        g.find(x[1]);
    }

    /**
     * branchCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void branchCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
        }
        g.branch(x[1]);
    }

    /**
     * resetCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void resetCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
        }
        g.reset(x[1]);
    }

    /**
     * mergeCommand.
     * @param g is gitlet value
     * @param x is input value
     */
    public static void mergeCommand(Gitlet g, String... x) {
        if (x.length != 2) {
            System.out.println("Incorrect operands.");
        }
        g.merge(x[1]);
    }

    /**
     * main method.
     * @param args is input value
     */
    public static void main(String... args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter a command.");
                return;
            }
            boolean temp = false;
            for (String command : COMMAND) {
                temp = temp || (command.equals(args[0]));
            }
            if (!temp) {
                System.out.println("No command with that name exists.");
                return;
            }
            Gitlet mygitlet = new Gitlet();
            if (args[0].equals("init")) {
                initCommand(mygitlet, args);
            }
            if (!new File(".gitlet").isDirectory()) {
                System.out.println("Not in an initialized gitlet directory.");
                return;
            }
            if (args[0].equals("add")) {
                addCommand(mygitlet, args);

            } else if (args[0].equals("commit")) {
                commitCommand(mygitlet, args);
            } else if (args[0].equals("rm")) {
                rmCommand(mygitlet, args);
            } else if (args[0].equals("log")) {
                logCommand(mygitlet, args);
            } else if (args[0].equals("global-log")) {
                globalLogCommand(mygitlet, args);
            } else if (args[0].equals("checkout")) {
                checkoutCommand(mygitlet, args);
            } else if (args[0].equals("status")) {
                statusCommand(mygitlet);
            } else if (args[0].equals("rm-branch")) {
                rmBranchCommand(mygitlet, args);
            } else if (args[0].equals("find")) {
                findCommand(mygitlet, args);
            } else if (args[0].equals("branch")) {
                branchCommand(mygitlet, args);
            } else if (args[0].equals("reset")) {
                resetCommand(mygitlet, args);
            } else if (args[0].equals("merge")) {
                mergeCommand(mygitlet, args);
            }
            mygitlet.save();
        } catch (ConcurrentModificationException e) {
            System.out.println();
        }
    }
}
