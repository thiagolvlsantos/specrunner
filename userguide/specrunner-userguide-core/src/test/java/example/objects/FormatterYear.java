package example.objects;

import org.specrunner.formatters.core.FormatterDateTemplate;

@SuppressWarnings("serial")
public class FormatterYear extends FormatterDateTemplate {

    public FormatterYear() {
        super("yyyy");
    }
}