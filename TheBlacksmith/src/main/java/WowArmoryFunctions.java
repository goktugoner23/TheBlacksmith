public class WowArmoryFunctions {
    public String parseArmoryLink(String message) {
        //parse the string that contains !armory and constructs a link and returns it
        //so the armory code should look like e.g: https://worldofwarcraft.com/en-gb/character/eu/dragonmaw/geliyorum
        String[] command = message.split(" ");
        //command[0] is the !armory
        String charName = command[1];
        String serverName = command[2];
        String serverType = command[3];
        String[] parsedLink = {"https://worldofwarcraft.com/en-gb/character", serverType, serverName, charName}; //will add dictionary for server names.
        if (!serverType.equals("eu") && !serverType.equals("us")) {
            return "error";
        }
        //construct the final link
        StringBuilder finalLink = new StringBuilder();
        for (String var : parsedLink) {
            finalLink.append(var);
            finalLink.append('/');
        }
        return finalLink.toString();
    }
}
