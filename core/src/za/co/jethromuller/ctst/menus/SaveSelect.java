package za.co.jethromuller.ctst.menus;


import za.co.jethromuller.ctst.CtstGame;

public class SaveSelect extends Menu {

    private MainMenu menu;

    public SaveSelect(CtstGame game, MainMenu menu) {
        super(game, null, "save_select.png");
        yCoords = new int[] {277, 202, 133, 63};
        this.menu = menu;
        xCoord = 0;
    }

    @Override
    protected void handleMenuOptions() {
        switch (option) {
            case 0:
                game.musicController.playSelectSound(1F, 0.25F, 0F);
                break;
            case 1:
                game.musicController.playSelectSound(1F, 0.25F, 0F);
                break;
            case 2:
                game.musicController.playSelectSound(1F, 0.25F, 0F);
                break;
            case 3:
                game.setScreen(menu);
                break;
        }
    }

    @Override
    protected void additionalRender() {
        if (option == 3) {
            xCoord = 106;
        } else {
            xCoord = 56;
        }
    }
}
