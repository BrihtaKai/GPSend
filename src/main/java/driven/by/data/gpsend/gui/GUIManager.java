package driven.by.data.gpsend.gui;

import driven.by.data.gpsend.GPSend;

public class GUIManager {

    private GPSend instance;
    private ChoosingGUI choosingGUI;
    private AmountGUI amountGUI;
    private PlayerListGUI playerListGUI;

    public GUIManager(GPSend instance) {
        this.choosingGUI = new ChoosingGUI();
        this.amountGUI = new AmountGUI();
        this.playerListGUI = new PlayerListGUI();
        this.instance = instance;

    }

    public ChoosingGUI getChoosingGUI() {
        return choosingGUI;
    }

    public AmountGUI getAmountGUI() {
        return amountGUI;
    }

    public PlayerListGUI getPlayerListGUI() {
        return playerListGUI;
    }

}
