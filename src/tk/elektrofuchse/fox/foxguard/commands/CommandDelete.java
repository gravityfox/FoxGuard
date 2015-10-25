package tk.elektrofuchse.fox.foxguard.commands;


import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.ArgumentParseException;
import org.spongepowered.api.world.World;
import tk.elektrofuchse.fox.foxguard.FoxGuardMain;
import tk.elektrofuchse.fox.foxguard.FoxGuardManager;
import tk.elektrofuchse.fox.foxguard.commands.util.CommandParseHelper;
import tk.elektrofuchse.fox.foxguard.commands.util.InternalCommandState;
import tk.elektrofuchse.fox.foxguard.factory.FGFactoryManager;
import tk.elektrofuchse.fox.foxguard.flags.IFlagSet;
import tk.elektrofuchse.fox.foxguard.regions.IRegion;

import java.util.List;
import java.util.Optional;

/**
 * Created by Fox on 10/22/2015.
 */
public class CommandDelete implements CommandCallable {

    String[] regionsAliases = {"regions", "region", "reg", "r"};
    String[] flagSetsAliases = {"flagsets", "flagset", "flags", "flag", "f"};

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        String[] args = {};
        if (!arguments.isEmpty()) args = arguments.split(" ");
        if (source instanceof Player) {
            Player player = (Player) source;
            if (args.length == 0) {
                source.sendMessage(Texts.builder()
                        .append(Texts.of(TextColors.GREEN, "Usage: "))
                        .append(getUsage(source))
                        .build());
                return CommandResult.empty();
            } else if (CommandParseHelper.contains(regionsAliases, args[0])) {
                int flag = 0;
                Optional<World> optWorld = CommandParseHelper.parseWorld(args[1], FoxGuardMain.getInstance().getGame().getServer());
                World world;
                if (optWorld != null && optWorld.isPresent()) {
                    world = optWorld.get();
                    flag = 1;
                    args = arguments.split(" ", 5);
                } else {
                    world = player.getWorld();
                }
                if (args.length < 2 + flag) throw new CommandException(Texts.of("Must specify a name!"));
                IRegion region = FoxGuardManager.getInstance().getRegion(world, args[1 + flag]);
                if (region == null)
                    throw new ArgumentParseException(Texts.of("No Region exists with that name!"), args[1 + flag], 1 + flag);

                player.sendMessage(Texts.of("Region deleted successfully!"));

            } else if (CommandParseHelper.contains(flagSetsAliases, args[0])) {
                if (args.length < 1) throw new CommandException(Texts.of("Must specify a name!"));
                if (args[1].matches("^[^a-zA-Z].*"))
                    throw new CommandException(Texts.of("Name must start with a letter!"));
                if (args[1].matches("^.*[^0-9a-zA-Z].*$"))
                    throw new CommandException(Texts.of("Name must be alphanumeric!"));
                if (args.length < 2) throw new CommandException(Texts.of("Must specify a type!"));

                IFlagSet newFlagSet = FGFactoryManager.getInstance().createFlagSet(
                        args[2], args[1].toLowerCase(),
                        args[3], FoxGuardCommandDispatcher.getInstance().getStateMap().get(player), player);

                FoxGuardManager.getInstance().addFlagSet(newFlagSet);
                player.sendMessage(Texts.of("FlagSet deleted successfully!"));
            } else {
                throw new ArgumentParseException(Texts.of("Not a valid category!"), args[0], 0);
            }
        } else {

        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("foxguard.command.modify.delete");
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("delete (region [w:<world>] | flagset) <name>");
    }
}
