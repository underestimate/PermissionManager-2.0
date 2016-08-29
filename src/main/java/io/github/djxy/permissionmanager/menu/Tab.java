package io.github.djxy.permissionmanager.menu;

import com.google.common.base.Preconditions;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by Samuel on 2016-08-25.
 */
public abstract class Tab {

    private Menu menu = null;
    private boolean open = false;
    private String text = "";
    private TextAction action = TextActions.executeCallback(source -> {
        open = !open;
        menu.sendToPlayer();
    });

    abstract protected void renderContent(String margin, List<Text> lines);

    public Tab(Menu menu) {
        Preconditions.checkNotNull(menu);

        this.menu = menu;
    }

    public void setText(String text) {
        Preconditions.checkNotNull(text);

        this.text = text;
    }

    public final void render(String margin, List<Text> lines) {
        lines.add(Text.of(margin+"- ", TextColors.GREEN, action, text, TextColors.DARK_GRAY, open?" [-]":" [+]", TextColors.RESET));

        if(open)
            renderContent(margin+"  ", lines);
    }

    protected Text createOption(TextAction action, String title){
        return Text.of("- ", TextColors.GREEN, action, title);
    }

    protected TextAction suggestCommand(String command){
        return TextActions.suggestCommand("/" + command);
    }

}
