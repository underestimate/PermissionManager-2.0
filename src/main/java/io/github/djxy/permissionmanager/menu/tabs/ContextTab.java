package io.github.djxy.permissionmanager.menu.tabs;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.menu.Tab;
import io.github.djxy.permissionmanager.subjects.Subject;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public class ContextTab extends Tab {

    private final Subject subject;
    private final String commandSuggested;
    private final Consumer<String> callback;

    public ContextTab(Menu menu, String text, Subject subject, String commandSuggested) {
        super(menu);
        this.commandSuggested = "/"+commandSuggested;
        this.callback = null;
        this.subject = subject;

        setText(text);
    }

    public ContextTab(Menu menu, String text, Subject subject, Consumer<String> callback) {
        super(menu);
        this.callback = callback;
        this.commandSuggested = null;
        this.subject = subject;

        setText(text);
    }

    @Override
    protected void renderContent(String margin, List<Text> lines) {
        for(Context context : subject.getContexts())
            lines.add(Text.of(margin).concat(createOption(callback(context.getValue()), context.getValue())));
    }

    protected TextAction callback(String value){
        return commandSuggested == null? TextActions.executeCallback(source -> {
            callback.accept(value);
        }):TextActions.suggestCommand(commandSuggested.replace("#", value));
    }

}
