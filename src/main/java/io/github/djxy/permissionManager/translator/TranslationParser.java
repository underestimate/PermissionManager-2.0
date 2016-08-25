package io.github.djxy.permissionmanager.translator;

import com.google.common.base.Preconditions;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Map;

/**
 * Created by Samuel on 2016-08-13.
 */
public class TranslationParser {

    private Text prefix = Text.of();
    private TextStyle textStyle = TextStyles.NONE;
    private TextColor textColor = TextColors.WHITE;
    private TextColor clickColor = TextColors.RED;
    private TextStyle clickStyle = TextStyles.UNDERLINE;
    private TextColor variableColor = TextColors.YELLOW;
    private TextStyle variableStyle = TextStyles.NONE;

    public TranslationParser(Text prefix) {
        Preconditions.checkNotNull(prefix);

        this.prefix = prefix;
    }

    public Text getPrefix() {
        return prefix;
    }

    public void setPrefix(Text prefix) {
        Preconditions.checkNotNull(prefix);

        this.prefix = prefix;
    }

    public TextColor getClickColor() {
        return clickColor;
    }

    public void setClickColor(TextColor clickColor) {
        Preconditions.checkNotNull(clickColor);

        this.clickColor = clickColor;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        Preconditions.checkNotNull(textStyle);

        this.textStyle = textStyle;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public void setTextColor(TextColor textColor) {
        Preconditions.checkNotNull(textColor);

        this.textColor = textColor;
    }

    public TextStyle getClickStyle() {
        return clickStyle;
    }

    public void setClickStyle(TextStyle clickStyle) {
        Preconditions.checkNotNull(clickStyle);

        this.clickStyle = clickStyle;
    }

    public TextColor getVariableColor() {
        return variableColor;
    }

    public void setVariableColor(TextColor variableColor) {
        Preconditions.checkNotNull(variableColor);

        this.variableColor = variableColor;
    }

    public TextStyle getVariableStyle() {
        return variableStyle;
    }

    public void setVariableStyle(TextStyle variableStyle) {
        Preconditions.checkNotNull(variableStyle);

        this.variableStyle = variableStyle;
    }

    public Text parse(String translation, Map<String, String> subTranslations, Map<String, String> variables, Map<String, TextAction> actions){
        Preconditions.checkNotNull(translation);
        Preconditions.checkNotNull(subTranslations);
        Preconditions.checkNotNull(variables);
        Preconditions.checkNotNull(actions);
        Preconditions.checkArgument(!translation.isEmpty());

        Text text = Text.of();
        int startIndex = 0;
        int index;

        while(startIndex != translation.length() && (index = translation.substring(startIndex).indexOf('{')) != -1){
            index = index+startIndex;
            int i = startIndex + translation.substring(startIndex).indexOf('}');
            String currentVariable = translation.substring(index+1, i);

            text = text.concat(transformText(translation.substring(startIndex, index)));

            if(currentVariable.startsWith("click")){
                TextAction action = actions.get(currentVariable);
                String click = subTranslations.get(currentVariable);
                text = text.concat(transformClick(click, action));
            }
            else {
                String variable = variables.get(currentVariable);
                text = text.concat(transformVariable(variable));
            }

            startIndex = i+1;
        }

        text = text.concat(transformText(translation.substring(startIndex, translation.length())));

        return prefix.concat(text);
    }

    public Text transformText(String text){
        return Text.of(textColor, textStyle, text, TextColors.RESET, TextStyles.RESET);
    }

    public Text transformVariable(String text){
        return Text.of(variableColor, variableStyle, text, TextColors.RESET, TextStyles.RESET);
    }

    public Text transformClick(String text, TextAction action){
        return Text.of(clickColor, TextStyles.UNDERLINE, action, text, TextColors.RESET, TextStyles.RESET);
    }

}
