package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.InvalidKeyException;
import it.polimi.ingsw.exceptions.NegativeResAmountException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.DepotSetting;
import it.polimi.ingsw.network.Processable;
import it.polimi.ingsw.network.messages.GameSizeMessage;
import it.polimi.ingsw.network.requests.*;
import it.polimi.ingsw.network.updates.*;
import it.polimi.ingsw.utils.ANSIColor;

import java.util.*;

public class ClientCLI extends PlayerInterface {
    private final Scanner stdin;

    public ClientCLI(Client player){
        super(player);
        this.stdin = new Scanner(System.in);
    }

    public void setup(){
        System.out.println("Game is starting...\n");
    }

    public String getIP(){
        System.out.println("Choose ip address: ");
        return stdin.next();
    }

    public int getPort(){
        System.out.println("Choose socket port: ");
        return stdin.nextInt();
    }

    public String chooseNickname(){
        String nickname = "";

        do {
            System.out.println("\nChoose your username (symbols are not allowed): ");
            if (stdin.hasNextLine()){
                nickname = stdin.nextLine();
            }
            if (nickname.equals("")){
                System.out.println("Please choose a valid username" );
            }
        } while (!nickname.matches("[a-zA-Z0-9]+"));

        return nickname;
    }

    public void getGameSize(){
        int gameSize;
        do {
            System.out.println("\nHow many players do you want?");
            System.out.print("[MIN: 1, MAX: 4]: ");
            try {
                gameSize = Integer.parseInt(stdin.nextLine());
            } catch(Exception e){
                gameSize = -1;
            }
        } while (gameSize < 1 || gameSize > 4);
        Processable rsp = new GameSizeMessage(getNickname(), gameSize);
        getPlayer().sendMessage(rsp);
    }

    @Override
    public void viewInitialResources(int numPlayer) {
        Map<ResourceType, Integer> res = new HashMap<>();
        switch (numPlayer){
            case 0:
                break;
            case 1:
            case 2:
                System.out.println("\nChoose one initial resource:");
                int numRes = getInitialResources();
                res.put(parseToResourceType(numRes), 1);
                break;
            case 3:
                System.out.println("\nChoose two initial resources:");
                int res1 = getInitialResources();
                int res2 = getInitialResources();
                res.put(parseToResourceType(res1), 1);
                if(res1 == res2) {
                    res.put(parseToResourceType(res2), 2);
                } else {
                    res.put(parseToResourceType(res2), 1);
                }
                break;
            default:
                System.out.println("ko");
                break;
        }
        InitialResRequest request = new InitialResRequest(res);
        request.setNumPlayer(numPlayer);
        request.setPlayer(getNickname());
        getPlayer().sendMessage(request);
    }

    private int getIntegerSelection(String[] options){
        int selection;
        do {
            for(int i = 0; i < options.length; i++){
                System.out.println(options[i]);
            }
            try {
                selection = Integer.parseInt(stdin.nextLine());
            } catch(Exception e){
                selection = -1;
            }
        } while(selection < 1 || selection > options.length);
        return selection;
    }

    private String getStringSelection(String[] selections, String[] options){
        String selection;
        List<String> selectionList = Arrays.asList(selections);
        do {
            for(int i = 0; i < options.length; i++){
                System.out.println(options[i]);
            }
            try {
                selection = stdin.nextLine();
            } catch(Exception e){
                selection = "";
            }
        } while(!selectionList.contains(selection));
        return selection;
    }

    private int getResourceMenu(){
        String[] options = {"1: STONE", "2: COIN", "3: SHIELD", "4: SERVANT"};
        return getIntegerSelection(options);
    }

    public int getInitialResources(){
        return getResourceMenu();
    }

    @Override
    public void viewInitialsLeaderCards(List<LeaderCard> leaderCards) {
        System.out.println("\nYou must select two cards out of four:");
        int index = 0;
        for(LeaderCard leaderCard: leaderCards){
            System.out.println("\nCard " + index++ + " (" + leaderCard.getImg() + "):");
            System.out.println(leaderCard);
        }
        System.out.println();

        int num1 = getInitialLeaderCards(-1);
        int num2 = getInitialLeaderCards(num1);
        ChooseLeaderRequest request = new ChooseLeaderRequest(num1, num2);
        request.setPlayer(getNickname());
        getPlayer().sendMessage(request);
    }

    public int getInitialLeaderCards(int exclude){
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(i != exclude){
                list.add(i);
            }
        }
        StringBuilder sb = new StringBuilder("Choose card number [");
        String sep = "";
        for(Integer element: list){
            sb.append(sep).append(element);
            sep = ", ";
        }
        sb.append("]:");
        int selection;
        do {
            System.out.println(sb);
            try {
                selection = Integer.parseInt(stdin.nextLine());
            } catch(Exception e){
                selection = -1;
            }
        } while (!list.contains(selection));
        return selection;
    }

    public ResourceType parseToResourceType(int choice){
        switch (choice){
            case 1: return ResourceType.STONE;
            case 2: return ResourceType.COIN;
            case 3: return ResourceType.SHIELD;
            case 4: return ResourceType.SERVANT;
            default: break;
        }
        return null;
    }

    public int getPosition(){
        System.out.println("Choose num of position : [0-3 to column 4-6 to rows, 6 is the first row]");
        return Integer.parseInt(stdin.nextLine());
    }

    public void simulateGame() {
        int selection;
        do {
            System.out.println("\nIt's your turn!");
            System.out.println("What do you want to do now?");
            System.out.println("1: Show faith track");
            System.out.println("2: Show depot and strongbox");
            System.out.println("3: Show leader cards");
            System.out.println("4: Show pending resources from the Market");
            System.out.println("5: Show market");
            System.out.println("6: Show market tray");
            System.out.println("7: Show Development Slots");
            System.out.println("8: Buy from market");
            System.out.println("9: Buy a development card");
            System.out.println("10: Activate basic production");
            System.out.println("11: Activate extra production");
            System.out.println("12: Activate a development card production");
            System.out.println("13: Use leader card 1");
            System.out.println("14: Use leader card 2");
            System.out.println("15: Discard leader card 1");
            System.out.println("16: Discard leader card 2");
            System.out.println("17: Reorder depot");
            System.out.println("18: Place pending resources from the Market");
            System.out.println("19: End Turn");
            System.out.println("20: Quit Game");
            System.out.println();
            try {
                selection = Integer.parseInt(stdin.nextLine());
            } catch(Exception e){
                selection = -1;
                System.out.println("Invalid selection");
            }
        } while (selection < 1 || selection > 20);

        Request request = null;
        switch(selection) {
            case 1:
                request = new ShowFaithTrackRequest();
                break;
            case 2:
                request = new ShowStoragesRequest();
                break;
            case 3:
                request = new ShowLeaderCardsRequest();
                break;
            case 4:
                request = new ShowTempResRequest();
                break;
            case 5:
                request = new ShowMarketRequest();
                break;
            case 6:
                request = new ShowMarketTrayRequest();
                break;
            case 7:
                request = new ShowDevSlotsRequest();
                break;
            case 8:
                request = new InsertMarbleRequest(getPosition(), AbilityChoice.STANDARD, 0,0);
                break;
            case 9:
                //request = new BuyDevCardRequest(getLevel(),getCardColo(),getAbilityChoice(),..);
                break;
            case 10:
                request = basicProductionMenu();
                break;
            case 11:
                //request = new ExtraProductionRequest(); @Riccardo
                break;
            case 12:
                //request = new DevProductionRequest(); @Riccardo
                break;
            case 13:
                request = new UseLeaderRequest(0, LeaderAction.USE_ABILITY);
                break;
            case 14:
                request = new UseLeaderRequest(1, LeaderAction.USE_ABILITY);
                break;
            case 15:
                request = new UseLeaderRequest(0, LeaderAction.DISCARD);
                break;
            case 16:
                request = new UseLeaderRequest(1, LeaderAction.DISCARD);
                break;
            case 17:
                request = reorderDepotMenu();
                break;
            case 18:
                request = placeResourceMenu();
                break;
            case 19:
                request = new EndTurnRequest();
                break;
            case 20:
                request = new QuitGameRequest();
                break;
        }
        if(request != null){
            getPlayer().sendMessage(request);
        } else {
            simulateGame();
        }
    }


    private Request basicProductionMenu(){
        Request request = null;
        Resource depotResource = new Resource(0, 0, 0, 0);
        Resource strongboxResource = new Resource(0, 0, 0, 0);

        String[] options = {"d: DEPOT", "s: STRONGBOX"};
        String[] selections = {"d", "s"};

        System.out.println("\nSelect first input resource type:");
        ResourceType input1 = parseToResourceType(getResourceMenu());
        System.out.println("\nWhere are you taking this resource from:");
        String inputPlace1 = getStringSelection(selections, options);
        System.out.println("\nSelect second input resource type:");
        ResourceType input2 = parseToResourceType(getResourceMenu());
        System.out.println("\nWhere are you taking this resource from:");
        String inputPlace2 = getStringSelection(selections, options);
        System.out.println("\nSelect output resource type:");
        ResourceType output = parseToResourceType(getResourceMenu());

        try {
            if(inputPlace1.equals("d")) {
                depotResource.modifyValue(input1, 1);
            } else if(inputPlace1.equals("s")){
                strongboxResource.modifyValue(input1, 1);
            }
            if(inputPlace2.equals("d")) {
                depotResource.modifyValue(input2, 1);
            } else if(inputPlace2.equals("s")){
                strongboxResource.modifyValue(input2, 1);
            }
            request = new BasicProductionRequest(input1, input2, output, depotResource, strongboxResource);
        } catch (InvalidKeyException | NegativeResAmountException e) {
            e.printStackTrace();
        }
        return request;
    }

    private Request placeResourceMenu() {
        Resource toDiscard = toDiscardMenu();
        List<DepotSetting> toPlace = toPlaceMenu();
        return new PlaceResourceRequest(toDiscard, toPlace);
    }

    public ResourceType strToResType(String input){
        switch (input) {
            case "stone": return ResourceType.STONE;
            case "coin": return ResourceType.COIN;
            case "shield": return ResourceType.SHIELD;
            case "servant": return ResourceType.SERVANT;
            default: return null;
        }
    }

    private Resource toDiscardMenu() {
        Resource toDiscard = new Resource();
        String inputStr;
        int amountSelection;
        ResourceType resSelection;
        while (true) {
            try {
                System.out.print("What resources would you like to DISCARD?");
                System.out.println(" (Press q to abort or k to choose the resources to place)");
                inputStr = stdin.nextLine().toLowerCase();
                if(inputStr.equals("q")) return null;
                else if(inputStr.equals("k")) return toDiscard;
                else {
                    resSelection = strToResType(inputStr);
                    if (resSelection == null) throw new Exception();
                    System.out.println("How many of this type of resource?");
                    amountSelection = Integer.parseInt(stdin.nextLine());
                    toDiscard.addResource(resSelection, amountSelection);
                }
            } catch (Exception e) {
                System.out.println("Invalid input, retry");
            }
        }
    }

    private List<DepotSetting> toPlaceMenu() {
        List<DepotSetting> toPlace = new ArrayList<>();
        ResourceType resSelection;
        String inputStr;
        int amountSelection, layerSelection;
        while(true) {
            try{
                System.out.print("What resources would you like to PLACE?");
                System.out.println(" (Press q to abort or k to send)");
                inputStr = stdin.nextLine().toLowerCase();
                if (inputStr.equals("q")) return null;
                else if (inputStr.equals("k")) return toPlace;
                else {
                    resSelection = strToResType(inputStr);
                    if (resSelection == null) throw new Exception();
                    System.out.println("How many of this type of resource?");
                    amountSelection = Integer.parseInt(stdin.nextLine());
                    System.out.println("In which layer would you like to place it?");
                    layerSelection = Integer.parseInt(stdin.nextLine());
                    toPlace.add(new DepotSetting(layerSelection, resSelection, amountSelection));
                }
            } catch (Exception e) {
                System.out.println("Invalid input, retry");
            }
        }
    }

    private ReorderDepotRequest reorderDepotMenu() {
        int fromLayer, toLayer, amount;
        while(true) {
            try {
                System.out.println("Move resources FROM which layer?");
                fromLayer = Integer.parseInt(stdin.nextLine());
                System.out.println("Move resources TO which layer?");
                toLayer = Integer.parseInt(stdin.nextLine());
                System.out.println("How many resources would you like to move?");
                amount = Integer.parseInt(stdin.nextLine());
                return new ReorderDepotRequest(fromLayer, toLayer, amount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, retry");
            }
        }
    }

    @Override
    public void updateTempResource(TempResourceUpdate update) {
        if (update.getResource() != null) {
            System.out.println("Resources obtained from the Market that need to be placed: ");
            System.out.println(update.getResource());
        }
        else System.out.println(ANSIColor.RED + "There are no resources that need to be placed" + ANSIColor.RESET);
    }

    @Override
    public void updateStorages(StorageUpdate update) {
        Map<String, List<DepotSetting>> depotMap = update.getDepotMap();
        Map<String, Resource> strongboxMap = update.getStrongboxMap();
        for(String playerNick: depotMap.keySet()) {
            System.out.println("\nShowing " + playerNick + "'s resources:");
            System.out.println("Depot:");
            for(DepotSetting layer: depotMap.get(playerNick)) {
                System.out.print("\t" + layer.getLayerNumber() + ": ");
                if (layer.getResType() == null) System.out.println("Empty");
                else System.out.println(layer.getResType() + " x" + layer.getAmount());
            }
            System.out.print("Strongbox: ");
            if(strongboxMap.get(playerNick).getTotalAmount() == 0) System.out.println("Empty");
            else System.out.println("\n\t" + strongboxMap.get(playerNick) + "\n");
        }
    }

    @Override
    public void updateLeaderCards(LeaderUpdate update) {
        List<LeaderCard> leaderCards = update.getLeaderMap().get(getNickname());
        if(leaderCards.size() > 0){
            System.out.println("\nYou have the following leader cards:");
            int index = 0;
            for(LeaderCard leaderCard: leaderCards){
                System.out.println("\nCard " + index++ + " (" + leaderCard.getImg() + "):");
                System.out.println("active: " + leaderCard.isActive());
                System.out.println(leaderCard);
            }
            System.out.println();
        } else {
            System.out.println("\nYou have no more leader cards.");
        }
    }

    @Override
    public void updateDevSlots(DevSlotsUpdate update) {
        Map<String, List<DevelopmentSlot>> devSlotList = update.getDevSlotMap();
        for(String playerNick: devSlotList.keySet()) {
            System.out.println("\nShowing " + playerNick + "'s development slots:");
            int slotNumber = 1;
            for (DevelopmentSlot slot : devSlotList.get(playerNick)) {
                if (slot.getCards().size() == 0) System.out.println("\tSlot number " + slotNumber + " is empty");
                else System.out.println("  - Showing slot number " + slotNumber + ":");
                for (DevelopmentCard card : slot.getCards()) {
                    System.out.println("  >" + card + "\n");
                }
                slotNumber++;
            }
            System.out.println("\n");
        }
    }

    public void updateDevSlotsPretty(DevSlotsUpdate update) {
        Map<String, List<DevelopmentSlot>> devSlotList = update.getDevSlotMap();
        for(String playerNick: devSlotList.keySet()) {
            List<DevelopmentSlot> currentSlots = devSlotList.get(playerNick);
            System.out.println("Showing " + playerNick + "'s development slots:");
            System.out.println("\t\t\t\tSlot number 1:\t\t\t\t\t\t\t\t\t\tSlot number2:\t\t\t\t\t\t\t\t\t\t\tSlot number3:");
            for (int layer = 0; layer < 3; layer++) {
                for(int slot = 0; slot < 3; slot++) {
                    if (currentSlots.get(slot).numberOfElements() > layer)
                        System.out.print("  > Cost: " + currentSlots.get(slot).getCards().get(layer).getCost() + "\t\t\t\t");
                    else System.out.print("\t\t\t\t\t\t\t\t\t\t\t");
                }
                System.out.print("\n\t");
                for(int slot = 0; slot < 3; slot++) {
                    if (currentSlots.get(slot).numberOfElements() > layer)
                        System.out.print("Color: " + currentSlots.get(slot).getCards().get(layer).getType() + "\t\t\t\t\t\t\t\t\t\t");
                    else System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t");
                }
                System.out.print("\n\t");
                for(int slot = 0; slot < 3; slot++) {
                    if (currentSlots.get(slot).numberOfElements() > layer)
                        System.out.print("Level: " + currentSlots.get(slot).getCards().get(layer).getLevel() + "\t\t\t\t\t\t\t\t\t\t\t");
                    else System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t");
                }
                System.out.print("\n\t");
                for(int slot = 0; slot < 3; slot++) {
                    if (currentSlots.get(slot).numberOfElements() > layer)
                        System.out.print("Production power: " + "\t\t\t\t\t\t\t\t\t");
                    else System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t");
                }
                System.out.print("\n\t");
                for(int slot = 0; slot < 3; slot++) {
                    if (currentSlots.get(slot).numberOfElements() > layer)
                        System.out.print("\tInput : " + currentSlots.get(slot).getCards().get(layer).getProdPower().getInput() + "\t\t\t");
                     else System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t");
                }
                System.out.print("\n\t");
                for(int slot = 0; slot < 3; slot++) {
                    if (currentSlots.get(slot).numberOfElements() > layer)
                        System.out.print("\tOutput: " + currentSlots.get(slot).getCards().get(layer).getProdPower().getOutput() + "\t");
                    else System.out.print("\t\t\t\t\t\t\t\t\t\t\t\t\t");
                }
                System.out.println("\n");
            }
            System.out.println();
        }
    }

    @Override
    public void displayError(ErrorUpdate update) {
        System.out.println(ANSIColor.RED + "\nReceived an error message from the server:" + ANSIColor.RESET);
        System.out.println(ANSIColor.RED + update.getClientError().getError() + ANSIColor.RESET + "\n");
    }

    @Override
    public void updateFaithTrack(FaithTrackUpdate faithTrackUpdate) {
        Map<String, FaithTrack> map = faithTrackUpdate.getFaithTrackInfoMap();
        System.out.println("\nFaith track information: ");
        for(Map.Entry<String, FaithTrack> entry: map.entrySet()){
            System.out.println("\nPlayer: " + entry.getKey());
            System.out.println(entry.getValue().toString());
        }
    }

    @Override
    public void updateMarket(MarketUpdate update) {
        List<DevelopmentCard> devCards = update.getDevCardList();
        int index = 0;
        for(DevelopmentCard devCard: devCards){
            System.out.println("\nCard " + index++ + " (url.pdf) ");
            System.out.println(devCard);
        }
        System.out.println();
    }

    @Override
    public void updateMarketTray(MarketTrayUpdate update) {
        System.out.println("\nMarket: ");
        System.out.println(update);
    }

    @Override
    public void updateDiscardedCards(DiscardedCardsUpdate update) {
        int index = 1;
        System.out.print("\nLorenzo discarded the following cards: ");
        for(DevelopmentCard devCard: update.getCardList()){
            System.out.println("\nCard " + index++ + ":");
            System.out.println(devCard);
        }
    }

    @Override
    public void updateSoloTokens(ActionTokenUpdate actionTokenUpdate) {
        System.out.println("\nAction tokens have been updated, the next one on the list is:");
        System.out.println(actionTokenUpdate.getNextToken());
    }
}