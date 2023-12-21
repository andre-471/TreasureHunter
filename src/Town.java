/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean searched;
    private boolean dugForGold;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;



        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        int randTreasure = (int) (Math.random() * 10) + 1;

        switch (randTreasure) {
            case 1 -> treasure = "crown";
            case 2 -> treasure = "trophy";
            case 3 -> treasure = "gem";
            default -> treasure = "dust";
        }

        dugForGold = false;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        resetTownMessage();
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown(boolean mode) {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (!mode) {
                if (checkItemBreak()) {
                    hunter.removeItemFromKit(item);
                    printMessage += "\nUnfortunately, you lost your " + item + ".";
                }
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice, boolean samuraiMode) {
        shop.enter(hunter, choice, samuraiMode);
        printMessage = "You left the shop.";
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " +
                        Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " +
                        Colors.YELLOW + goldDiff + " gold" + Colors.RESET + ".";
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public void huntForTreasure() {
        if (searched) {
            printMessage = "You have already searched this town.";
            return;
        }

        searched = true;
            printMessage = "You found one " + Colors.BLUE + treasure + Colors.RESET + "!";
        if (!treasure.equals("dust") && !hunter.addTreasure(treasure)) {
            printMessage += "\nYou already have one " + Colors.BLUE + treasure + Colors.RESET + "!";
        }
    }

    public void digForGold() {
        if (dugForGold) {
            printMessage = "You have already dug for gold in this town.";
            return;
        }
        if (!hunter.itemInKit("shovel")) {
            printMessage = "You can't dig for gold without a shovel.";
            return;
        }

        dugForGold = true;
        if (Math.random() < .5) {
            printMessage = "You dug but only found dirt.";
        } else {
            int gold = (int) (Math.random() * 20) + 1;
            hunter.changeGold(gold);
            printMessage = "You dug up " + Colors.YELLOW + gold + " gold" + Colors.RESET + "!";
        }
    }

    public void resetTownMessage() {
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }


    public String toString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .16) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .33) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .66) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .83) {
            return new Terrain("Marsh", "Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}