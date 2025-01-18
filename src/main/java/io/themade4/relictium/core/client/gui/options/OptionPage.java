package io.themade4.relictium.core.client.gui.options;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class OptionPage {
    private final ITextComponent name;
    private final ImmutableList<OptionGroup> groups;
    private final ImmutableList<Option<?>> options;

    public OptionPage(String name, ImmutableList<OptionGroup> groups) {
        this(new TextComponentString(name), groups);
    }

    public OptionPage(ITextComponent name, ImmutableList<OptionGroup> groups) {
        this.name = name;
        this.groups = groups;

        ImmutableList.Builder<Option<?>> builder = ImmutableList.builder();

        for (OptionGroup group : groups) {
            builder.addAll(group.getOptions());
        }

        this.options = builder.build();
    }

    public ImmutableList<OptionGroup> getGroups() {
        return this.groups;
    }

    public ImmutableList<Option<?>> getOptions() {
        return this.options;
    }

    public ITextComponent getNewName() {
        return this.name;
    }

    public String getName() {
        return this.getNewName().getFormattedText();
    }

}
