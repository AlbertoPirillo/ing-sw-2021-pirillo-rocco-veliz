package it.polimi.ingsw.client;

import it.polimi.ingsw.client.model.ClientModel;
import it.polimi.ingsw.exceptions.DevSlotEmptyException;
import it.polimi.ingsw.exceptions.NegativeResAmountException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.DepotSetting;
import it.polimi.ingsw.network.Processable;
import it.polimi.ingsw.network.messages.GameSizeMessage;
import it.polimi.ingsw.network.messages.LoginMessage;
import it.polimi.ingsw.network.requests.*;
import it.polimi.ingsw.network.updates.*;
import it.polimi.ingsw.utils.ANSIColor;

import java.util.*;

/**
 * <p>Command line interface implementation</p>
 * <p>Shows the player everything related to the game using text</p>
 * <p>Uses System.in to get input and System.out to show output</p>
 */
public class ClientCLI implements UserInterface {

    private String nickname;
    private final Client client;
    private final Scanner stdin;
    private final ClientModel clientModel;
    private boolean productionDone;
    private boolean secondProductionDone;
    private boolean marketActionDone;
    private boolean mainActionDone;

    /**
     * Constructs a new ClientCLI, initializing all the references
     * @param client the client associated to this CLI
     */
    public ClientCLI(Client client){
        this.client = client;
        this.stdin = new Scanner(System.in);
        this.clientModel = new ClientModel();
        this.productionDone = false;
        this.secondProductionDone = false;
        this.mainActionDone = false;
        this.marketActionDone = false;
    }

    @Override
    public ClientModel getClientModel() {
        return clientModel;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.clientModel.setNickname(nickname);
    }

    @Override
    public void endOfUpdate(EndOfUpdate update) {
        if (update.getActivePlayer().equals(this.nickname)) {
            //Call Client method to make requests;
            gameMenu();
        }
    }

    @Override
    public void readUpdate(ServerUpdate updateMessage) {
        if (updateMessage != null) {
            updateMessage.update(this);
        }
    }

    @Override
    public void setup(){
        System.out.println("Game is starting...\n");
    }

    @Override
    public void loginMessage(){
        String nickname = chooseNickname();
        Processable login = new LoginMessage(nickname, nickname);
        setNickname(nickname);
        getClient().sendMessage(login);
    }

    @Override
    public void changeNickname(){
        errorPrint("The nickname already exists");
        loginMessage();
    }

    @Override
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

    @Override
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
        getClient().sendMessage(rsp);
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
        getClient().sendMessage(request);
    }

    private int getIntegerSelection(String[] options){
        int selection;
        do {
            for (String option : options) {
                System.out.println(option);
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
            for (String option : options) {
                System.out.println(option);
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

    private int getInitialResources(){
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
        getClient().sendMessage(request);
    }

    private int getInitialLeaderCards(int exclude){
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(i != exclude){
                list.add(i);
            }
        }
        StringBuilder sb = new StringBuilder("Choose card number [");
        return printAndSelect(list, sb);
    }

    private int printAndSelect(List<Integer> list, StringBuilder sb) {
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

    private int getDevCardsSlot (List<Integer> exclude){
        List<Integer> list = new ArrayList<>();
        for(int i = 1; i < 4; i++){
            if(!exclude.contains(i-1)){
                list.add(i);
            }
        }
        StringBuilder sb = new StringBuilder("Which development card production do you want to use [");
        return printAndSelect(list, sb);
    }

    private ResourceType parseToResourceType(int choice){
        switch (choice){
            case 1: return ResourceType.STONE;
            case 2: return ResourceType.COIN;
            case 3: return ResourceType.SHIELD;
            case 4: return ResourceType.SERVANT;
            default: break;
        }
        return null;
    }

    private AbilityChoice parseToAbility(int choice){
        return switch (choice) {
            case 1 -> AbilityChoice.STANDARD;
            case 2 -> AbilityChoice.FIRST;
            case 3 -> AbilityChoice.SECOND;
            case 4 -> AbilityChoice.BOTH;
            default -> null;
        };
    }

    private int getPosition(){
        int position;
        do {
            System.out.print("\nChoose num of position : [MIN : "+ 0 + " MAX: " + 6 + "] ");
            System.out.println("(Press q to abort)");
            String input = stdin.nextLine();
            if (input.equals("q")) return - 1;
            else position = Integer.parseInt(input);
            System.out.println(position);
        }while (position < 0 || position > 6);
        return position;
    }

    @Override
    public void gameMenu() {
        int selection;
        do {
            System.out.println("\nIt's your turn!");
            System.out.println("What do you want to do now?");
            System.out.println("1: Show faith track");
            System.out.println("2: Show depot and strongbox");
            System.out.println("3: Show leader cards");
            System.out.println("4: Show market development cards");
            System.out.println("5: Show market marbles tray");
            System.out.println("6: Show Development Slots");
            System.out.println("7: Buy from marbles tray");
            System.out.println("8: Buy a development card");
            System.out.println("9: Activate a production");
            System.out.println("10: Leader Card options");
            System.out.println("11: Reorder depot");
            System.out.println("12: End Turn");
            System.out.println("13: Quit Game");
            System.out.println();
            try {
                selection = Integer.parseInt(stdin.nextLine());
            } catch(Exception e){
                selection = -1;
                System.out.println("Invalid selection");
            }
        } while (selection < 1 || selection > 13);

        Request request = null;
        switch(selection) {
            case 1:
                updateFaithTrack(clientModel.getPersonalBoardModel().buildFaithTrackUpdate());
                break;
            case 2:
                updateStorages(clientModel.getStoragesModel().buildStorageUpdate());
                break;
            case 3:
                updateLeaderCards(clientModel.getPersonalBoardModel().buildLeaderUpdate());
                break;
            case 4:
                updateMarket(clientModel.getMarketModel().buildMarketUpdate());
                break;
            case 5:
                updateMarketTray(clientModel.getMarketModel().buildMarketTrayUpdate());
                break;
            case 6:
                updateDevSlots(clientModel.getPersonalBoardModel().buildDevSlotsUpdate());
                break;
            case 7:
                if (!mainActionDone) {
                    updateMarketTray(clientModel.getMarketModel().buildMarketTrayUpdate());
                    int position = getPosition();
                    if (position != -1) {
                        request = new InsertMarbleRequest(position);
                    }
                } else errorPrint("\nYou already performed an action this turn");
                break;
            case 8:
                if (!mainActionDone) {
                    request = buyDevCardMenu();
                }
                else errorPrint("\nYou already performed an action this turn");
                break;
            case 9:
                try{
                    request = productionMenu();
                } catch (DevSlotEmptyException e) {
                    errorPrint("The slot is empty or the slot number is invalid");
                }
                break;
            case 10:
                request = useLeaderMenu();
                break;
            case 11:
                request = reorderDepotMenu();
                break;
            case 12:
                if (mainActionDone) {
                    request = new EndTurnRequest();
                    this.productionDone = false;
                    this.secondProductionDone = false;
                    this.marketActionDone = false;
                    this.mainActionDone = false;
                }
                else errorPrint("\nYou have to perform an action before ending the turn");
                break;
            case 13:
                System.out.println("Are you sure you want to quit?");
                System.out.println("All data will be lost.");
                System.out.print("Quit? (Y/n) ");
                if (stdin.nextLine().equalsIgnoreCase("y")) {
                    request = new QuitGameRequest();
                }
                break;
        }
        if(request != null){
            getClient().sendMessage(request);
        } else {
            gameMenu();
        }
    }

    private void errorPrint(String str) {
        System.out.println(ANSIColor.RED + str + ANSIColor.RESET);
    }

    @Override
    public void waitForHostError(String text) {
        errorPrint(text);
    }

    private Request buyDevCardMenu() {
        Resource depotResource = new Resource(0, 0, 0, 0);
        Resource strongboxResource = new Resource(0, 0, 0, 0);
        String[] abilityOptions = {"1: NO ABILITY", "2: FIRST ABILITY", "3: SECOND ABILITY", "4: BOTH ABILITY"};
        String[] levelOptions = {"1: Level 1", "2: Level 2", "3: Level 3"};
        String[] colorOptions = {"1: GREEN", "2: BLUE", "3: YELLOW", "4: PURPLE"};
        String[] slotOptions = {"1: Slot 1", "2: Slot 2", "3: Slot 3"};
        while(true) {
            try {
                System.out.println("Choose what ability you want to use");
                int ability = getIntegerSelection(abilityOptions);
                System.out.println("Choose the DevCard's level");
                int level = getIntegerSelection(levelOptions);
                System.out.println("Choose the DevCard's color");
                int color = getIntegerSelection(colorOptions);
                System.out.println("Choose the slot's number");
                int slot = getIntegerSelection(slotOptions);
                DevelopmentCard card = getDevCard(level, color);
                if (card == null) throw new Exception();
                Resource options = card.getCost();
                System.out.println("Card cost : "+options);
                System.out.println("\nSelect where are you taking the resource from");
                chooseResources(depotResource, strongboxResource, options);
                return new BuyDevCardRequest(level,CardColor.parseColorCard(color), parseToAbility(ability),depotResource, strongboxResource, slot-1);
            } catch (Exception e){
                errorPrint("Invalid input, retry");
            }
        }
    }

    private void chooseResources(Resource depotResource, Resource strongboxResource, Resource options) throws Exception {
        for (ResourceType res : options.keySet()) {
            if(options.getValue(res)>0){
                System.out.println(res);
                System.out.println("How many resources to take from Depot");
                int amountDepot = Integer.parseInt(stdin.nextLine());
                if (amountDepot < 0 || amountDepot > 3) throw new Exception();
                System.out.println("How many resources to take from Strongbox");
                int amountStrongbox = Integer.parseInt(stdin.nextLine());
                if (amountStrongbox < 0) throw new Exception();
                depotResource.modifyValue(res,amountDepot);
                strongboxResource.modifyValue(res,amountStrongbox);
            }
        }
    }

    private DevelopmentCard getDevCard(int level, int color){
        List<DevelopmentCard> cards = clientModel.getMarketModel().getDevCardList();
        return switch (level) {
            case 1 -> cards.get(7 + color);
            case 2 -> cards.get(3 + color);
            case 3 -> cards.get(-1 + color);
            default -> null;
        };
    }

    private Request productionMenu() throws DevSlotEmptyException {
        Request request = null;

        String[] options = {"b: Use a Basic Production", "d: Use a Development Card Production", "e: Use a Leader Card Extra Production", "q: Exit this menu"};
        String[] selections = {"b", "d", "e", "q"};

        System.out.println("\nWhat type of production do you want to activate?");
        String selection = getStringSelection(selections, options);

        switch (selection) {
            case "b":
                if (!this.productionDone) {
                    request = basicProductionMenu();
                }
                else errorPrint("\nYou already performed an action this turn");
                break;
            case "d":
                request = devProductionMenu();
                break;
            case "e":
                request = extraProductionMenu();
                break;
        }
        return request;
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
        } catch (NegativeResAmountException e) {
            System.out.println("Invalid input, retry");
        }
        return request;
    }

    private Request extraProductionMenu() {
        Request request = null;

        if (this.productionDone || this.secondProductionDone){

            Resource depotResource = new Resource(0, 0, 0, 0);
            Resource strongboxResource = new Resource(0, 0, 0, 0);
            String[] abilityOptions = {"1: FIRST", "2: SECOND", "3: Exit this menu"};

            System.out.println("\nWhat leader card production do you want to use");
            int ability = getIntegerSelection(abilityOptions);
            if (ability == 3){
                return null;
            } else {

                //Take corresponding card from local model
                LeaderCard productionLeaderCard = clientModel.getPersonalBoardModel().getLeaderMap().get(this.nickname).get(ability-1);

                //Check if it's a card with production power
                if(isProductionCard(productionLeaderCard)){

                    //Selection of resources for that specific card
                    System.out.println("\nSelect the input resources:");
                    try {
                        Resource options = ((ExtraProductionAbility) productionLeaderCard.getSpecialAbility()).getProduction().getInput();
                        chooseResources(depotResource, strongboxResource, options);
                    } catch (Exception e) {
                        errorPrint("Invalid input, retry");
                        return null;
                    }

                    System.out.println("\nWhat resource do you want to get?");
                    ResourceType res = parseToResourceType(getResourceMenu());

                    request = new ExtraProductionRequest(parseToAbility(ability+1), depotResource, strongboxResource, res);

                } else {
                    errorPrint("The card you have selected has no production ability");
                }

            }

        } else {
            errorPrint("You have to use a basic or development production before using a leader production");
            request = null;
        }

        return request;
    }

    /**
     * Checks if the player has selected a leader card with a production power (based on card id)
     * @return true if the player has selected a leader with production power
     */
    private boolean isProductionCard(LeaderCard playerCard){
        List<Integer> productionID = new ArrayList<>(Arrays.asList(13, 14, 15, 16));

        for ( Integer id: productionID ){
                if (id == playerCard.getId()) return true;
        }
        return false;
    }

    private Request devProductionMenu() throws DevSlotEmptyException {
        Request request;

        if (!this.secondProductionDone && !this.marketActionDone) {
            List<Integer> cards = new ArrayList<>();
            Resource depotResource = new Resource(0, 0, 0, 0);
            Resource strongboxResource = new Resource(0, 0, 0, 0);
            String[] amountOptions = {"1", "2", "3", "4: Quit this menu"};

            System.out.println("\nHow many development card production do you want to use?");
            int amount = getIntegerSelection(amountOptions);

            if (amount == 4) {
                return null;
            } else {
                for (int i = 0; i < amount; i++) {
                    //Print Development Slots to make player choose which one to activate
                    updateDevSlots(clientModel.getPersonalBoardModel().buildDevSlotsUpdate());
                    //Ask player which card from which slot
                    int card = getDevCardsSlot(cards);
                    //Save slot in list for Request Build
                    cards.add(card - 1);
                    //Take corresponding card from local model
                    DevelopmentCard productionDevCard = getProductionDevCard(card-1);
                    //Selection of resources for that specific card
                    System.out.println("\nSelect the input resources:");
                    try {
                        Resource options = productionDevCard.getProdPower().getInput();
                        chooseResources(depotResource, strongboxResource, options);
                    } catch (Exception e) {
                        errorPrint("Invalid input, retry");
                        return null;
                    }
                }
            }

            request = new DevProductionRequest(cards, depotResource, strongboxResource);

        } else {
            errorPrint("You have already performed an action in this turn");
            request = null;
        }

        return request;
    }

    private DevelopmentCard getProductionDevCard(int slot) throws DevSlotEmptyException {
        List<DevelopmentSlot> devSlots = clientModel.getPersonalBoardModel().getDevSlotMap().get(this.nickname);
        return  devSlots.get(slot).getTopCard();
    }

    private Request useLeaderMenu() {
        Request request = null;

        String[] firstActionOptions = {"1: Activate a leader card", "2: Discard a leader card", "3: Exit this menu"};
        String[] leaderOptions = {"1: The first", "2: The second"};

        System.out.println("\nWhat do you want to do with you leader cards?");
        int selection = getIntegerSelection(firstActionOptions);

        if (selection == 1) {
            System.out.println("\nWhich leader card do you want to activate?");
            int leader = getIntegerSelection(leaderOptions);
            request = new UseLeaderRequest(leader-1, LeaderAction.USE_ABILITY);
        } else if (selection == 2) {
            System.out.println("\nWhich leader card do you want to activate?");
            int leader = getIntegerSelection(leaderOptions);
            request = new UseLeaderRequest(leader-1, LeaderAction.DISCARD);
        }
        return request;
    }

    @Override
    public void updateTempResource(TempResourceUpdate update) {
        //Calling selectPlayerList() is not needed
        clientModel.getStoragesModel().saveTempRes(update);
        Resource resource = update.getResource();
        if (resource == null || resource.getActualSize() == 0) {
            System.out.println("\nThere are no resources from the market that need to be placed");
            this.gameMenu();
        }
        else {
            System.out.println("\nResources obtained from the Market that need to be placed: ");
            System.out.println(resource);
            Request request = this.placeResourceMenu();
            this.getClient().sendMessage(request);
        }
    }

    private Request placeResourceMenu() {
        int amountSelection, layerSelection;
        List<DepotSetting> toPlace = new ArrayList<>();
        Resource toDiscard = new Resource(0,0,0,0);
        Resource tempRes = clientModel.getStoragesModel().getTempResource();
        try {
            for(ResourceType key: tempRes.keySet()) {
                if (tempRes.getValue(key) != 0) {
                    System.out.println("\nYou have " + key + "x" + tempRes.getValue(key));
                    System.out.println("How many resources to discard?");
                    amountSelection = Integer.parseInt(stdin.nextLine());
                    toDiscard.modifyValue(key, amountSelection);
                    tempRes.modifyValue(key, -amountSelection);

                    while (tempRes.getValue(key) > 0) {
                        System.out.println("How many resources to place? (Remaining: " + tempRes.getValue(key) + ")");
                        amountSelection = Integer.parseInt(stdin.nextLine());
                        System.out.println("In which layer would you like to place them?");
                        layerSelection = Integer.parseInt(stdin.nextLine());
                        toPlace.add(new DepotSetting(layerSelection, key, amountSelection));
                        tempRes.modifyValue(key, -amountSelection);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input, retry");
        }
        return new PlaceResourceRequest(toDiscard, toPlace, false);
    }

    private ReorderDepotRequest reorderDepotMenu() {
        int fromLayer, toLayer, amount;
        while(true) {
            try {
                System.out.println("Move resources FROM which layer? (Press q to abort)");
                String input = stdin.nextLine();
                if (input.equals("q")) return null;
                fromLayer = Integer.parseInt(input);
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

    /**
     * Choose whose player information should be printed
     * @param playerList    the complete list of players received with the update
     * @param activePlayer  the activePlayer received with the update
     * @return  playerList if the owner of the client is the active player,
     * otherwise a list with the active player only
     */
    private Set<String> selectPlayerList(Set<String> playerList, String activePlayer) {
        if (this.getNickname().equals(activePlayer)) {
            return playerList;
        }
        else {
            Set<String> newSet = new HashSet<>();
            newSet.add(activePlayer);
            return newSet;
        }
    }

    @Override
    public void updateStorages(StorageUpdate update) {
        clientModel.getStoragesModel().saveStorages(update);
        Set<String> toPrint = this.selectPlayerList(update.getStrongboxMap().keySet(), update.getActivePlayer());
        Map<String, List<DepotSetting>> depotMap = update.getDepotMap();
        Map<String, Resource> strongboxMap = update.getStrongboxMap();
        for(String playerNick: toPrint) {
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
        clientModel.getPersonalBoardModel().saveLeaderCards(update);
        Set<String> toPrint = this.selectPlayerList(update.getLeaderMap().keySet(), update.getActivePlayer());
        Map<String, List<LeaderCard>> leaderMap = update.getLeaderMap();
        for(String playerNick: toPrint) {
            List<LeaderCard> leaderCards = leaderMap.get(playerNick);
            if (leaderCards.size() > 0) {
                System.out.println("\nThe player " + playerNick + " has the following leader cards:");
                int index = 0;
                for (LeaderCard leaderCard : leaderCards) {
                    if(playerNick.equals(this.getNickname()) || leaderCard.isActive()) {
                        System.out.println("\nCard " + index++ + " (" + leaderCard.getImg() + "):");
                        System.out.println("active: " + leaderCard.isActive());
                        System.out.println(leaderCard);
                    }
                    else {
                        System.out.println("\nThis card is hidden");
                    }
                }
                System.out.println();
            } else {
                System.out.println("\nYou have no more leader cards.");
            }
        }
    }

    @Override
    public void updateDevSlots(DevSlotsUpdate update) {
        clientModel.getPersonalBoardModel().saveDevSlots(update);
        Set<String> toPrint = this.selectPlayerList(update.getDevSlotMap().keySet(), update.getActivePlayer());
        Map<String, List<DevelopmentSlot>> devSlotList = update.getDevSlotMap();
        for(String playerNick: toPrint) {
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

    @Override
    public void displayError(ErrorUpdate update) {
        if(update.getActivePlayer().equals(this.getNickname())) {
            System.out.println(ANSIColor.RED + "\nReceived an error message from the server:" + ANSIColor.RESET);
            System.out.println(ANSIColor.RED + update.getClientError().getError() + ANSIColor.RESET + "\n");
        }
    }

    @Override
    public void updateFaithTrack(FaithTrackUpdate update) {
        clientModel.getPersonalBoardModel().saveFaithTrack(update);
        Map<String, FaithTrack> map = update.getFaithTrackInfoMap();
        System.out.println("\nFaith track information: ");
        for(Map.Entry<String, FaithTrack> entry: map.entrySet()){
            System.out.println("\nPlayer: " + entry.getKey());
            System.out.println(entry.getValue().toString());
        }
    }

    @Override
    public void updateMarket(MarketUpdate update) {
        clientModel.getMarketModel().saveMarket(update);
        List<DevelopmentCard> devCards = update.getDevCardList();
        int index = 0;
        for(DevelopmentCard devCard: devCards){
            if(devCard!=null) {
                System.out.println("\nCard " + index++ + " (" + devCard.getImg() + ".png) ");
                System.out.println(devCard);
            }else{
                index++;
                System.out.println("\nEmpty Column");
            }
        }
        System.out.println();
    }

    @Override
    public void updateMarketTray(MarketTrayUpdate update) {
        clientModel.getMarketModel().saveTray(update);
        System.out.println("\nShowing Market tray: ");
        System.out.println(update);
    }

    @Override
    public void updateDiscardedCards(DiscardedCardsUpdate update) {
        clientModel.getSoloGameModel().saveDiscardedCards(update);
        int index = 1;
        System.out.print("\nLorenzo discarded the following cards: ");
        for(DevelopmentCard devCard: update.getCardList()){
            System.out.println("\nCard " + (index++) + ":");
            System.out.println(devCard);
        }
    }

    @Override
    public void updateSoloTokens(ActionTokenUpdate update) {
        clientModel.getSoloGameModel().saveSoloTokens(update);
        System.out.println("\nThe following action token has been activated:");
        System.out.println(update.getLastToken());
    }

    @Override
    public void updateTempMarbles(TempMarblesUpdate update) {
        clientModel.getMarketModel().saveTempMarbles(update);
        //No need to call selectPlayerList() here
        if(update.getActivePlayer().equals(this.getNickname())) {
            System.out.println(update);
            Request request = toChangeMarble(update.getResources().get(0), update.getResources().get(1), update.getNumWhiteMarbles());
            getClient().sendMessage(request);
        }
    }

    private Request toChangeMarble(ResourceType res1, ResourceType res2, int numMarbles){
        while(true) {
            try{
                System.out.println("Choice the num of "+res1);
                int amount1 = Integer.parseInt(stdin.nextLine());
                if (amount1 > numMarbles) throw new Exception();
                System.out.println("Choice the num of "+res2);
                int amount2 = Integer.parseInt(stdin.nextLine());
                if (amount2 > numMarbles) throw new Exception();
                if (amount1 + amount2 != numMarbles) throw new Exception();
                return new ChangeMarblesRequest(amount1, amount2);
            } catch (Exception e) {
                System.out.println(ANSIColor.RED + "The number of white marbles does not match" + ANSIColor.RESET);
            }
        }
    }

    @Override
    public void updateProductionDone(ProductionDoneUpdate update){
        if(update.getActivePlayer().equals(this.nickname)){
            this.productionDone = true;
        }
    }

    @Override
    public void updateActionDone(MainActionDoneUpdate update){
        if(update.getActivePlayer().equals(this.nickname)){
            this.mainActionDone = true;
        }
    }

    @Override
    public void updateSecondProductionDone(SecondProductionDoneUpdate update){
        if(update.getActivePlayer().equals(this.nickname)){
            this.secondProductionDone = true;
        }
    }

    @Override
    public void updateMarketActionDone(MarketActionDoneUpdate update){
        if(update.getActivePlayer().equals(this.nickname)){
            this.marketActionDone = true;
        }
    }

    @Override
    public void startMainGame(EndOfInitialUpdate update) {
        //Print leader cards and storages of all player
        updateLeaderCards(update.getLeaderUpdate());
        updateStorages(update.getStorageUpdate());
        updateMarket(update.getMarketUpdate());
        updateMarketTray(update.getMarketTrayUpdate());
        updateFaithTrack(update.getFaithTrackUpdate());
        updateDevSlots(update.getDevSlotsUpdate());
        //Give the control to the main player
        if (update.getActivePlayer().equals(this.nickname)) {
            gameMenu();
        }
    }

    @Override
    public void updateGameOver(GameOverUpdate update){
        int numPlayers = update.getScores().keySet().size();
        if ( numPlayers == 1){
            if(update.isWin()){
                System.out.println("\n***** CONGRATULATIONS YOU WON THE GAME******");
                System.out.println("\nYour final score is: " + update.getScores().get(getNickname()));
            } else {
                System.out.println("\n YOU LOST");
            }
        }

        if(numPlayers > 1){
            System.out.println("\nThe final scores are the following:");
            for(int i = 0; i < numPlayers; i++){
                String playerName = update.getRanking().get(i);
                System.out.println("\n" + i + ": " + playerName + " - " + update.getScores().get(playerName));
            }
        }
    }
}