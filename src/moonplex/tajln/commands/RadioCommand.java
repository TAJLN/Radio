package moonplex.tajln.commands;

import moonplex.tajln.NoteBlockAPI.NBSDecoder;
import moonplex.tajln.NoteBlockAPI.Song;
import moonplex.tajln.Radio;
import moonplex.tajln.utils.CommandInfo;
import moonplex.tajln.utils.CommandManager;
import moonplex.tajln.utils.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

@CommandInfo(description="Plays music", usage=" /<command>", permission="radio.command", onlyIngame = true)
public class RadioCommand extends SimpleCommand {

    private String radio = Radio.getPlugin().getConfig().getString("prefix");

    private CommandManager _commandManager;

    public RadioCommand(CommandManager commandManager) {
        super(commandManager, "radio");

        _commandManager = commandManager;

    }

    @Override
    public boolean onCommand(CommandSender caller, String paramString, String[] args) {
        Player sender = (Player) caller;
        if (args.length != 0)
            switch (args[0]) {
                case "play":
                    playcommand(sender, args);
                    return true;
                case "list":
                    listmusic(sender);
                    return true;
                case "stop":
                    stopmusic(sender, args);
                    return true;
            }
        sender.sendMessage( radio + Radio.getPlugin().getConfig().getString("commandinfo1"));
        sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("commandinfo2"));
        sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("commandinfo3"));
        return true;
    }

	private void playcommand(Player sender, String[] args){
        try
        {
            boolean perms = false;
            if (sender.hasPermission("radio.admin"))
                perms = true;
            String param = args[1];
            StringBuilder file = new StringBuilder(args[2]);
            int n = 3;
            while (n < args.length) {
                file.append(" ").append(args[n]);
                n = n + 1;
            }
            file = new StringBuilder(file.toString().replaceAll(".nbs", ""));

            File data = new File(Radio.getPlugin().getDataFolder() + "/music/");
            if (!(Objects.requireNonNull(data.list()).length > 0))
            {
                sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("nosongs"));
                return;
            }
            File F = new File(Radio.getPlugin().getDataFolder() + "/music/" + file.toString().replaceAll(".nbs", "") + ".nbs");
            if (!(F.isFile()))
            {
                sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("nofile").replaceAll("<file>", file.toString()));
                return;
            }

            String playfile = Radio.getPlugin().getDataFolder() + "/music/" + file.toString().replaceAll(".nbs", "") + ".nbs";
            Song gimmesong = NBSDecoder.parse(new File(playfile));


            if (param.equalsIgnoreCase("me"))
            {
                Radio.play(new Player[]{sender}, file.toString());
                sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("playyou").replaceAll("<author>", gimmesong.getAuthor()).replaceAll("<title>", gimmesong.getTitle()).replaceAll("<player>","yourself"));
            }

            else if (param.equalsIgnoreCase("all"))
            {
                if (perms) {
                    Radio.play(Bukkit.getOnlinePlayers().toArray(new Player[0]), file.toString());
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(radio + Radio.getPlugin().getConfig().getString("playall").replaceAll("<author>", gimmesong.getAuthor()).replaceAll("<title>", gimmesong.getTitle()).replaceAll("<player>", sender.getName()).replaceAll("<player2>", "everyone"));
                    }
                }else{
                    sender.sendMessage(radio + _commandManager.NOPERMS);
                }
            }

            else if (param.equalsIgnoreCase("world"))
            {
                if (perms) {
                    Radio.play(sender.getWorld().getPlayers().toArray(new Player[0]), file.toString());
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(radio + Radio.getPlugin().getConfig().getString("playall").replaceAll("<author>", gimmesong.getAuthor()).replaceAll("<title>", gimmesong.getTitle()).replaceAll("<player>", sender.getName()).replaceAll("<player2>", "everyone in their world"));
                    }
                }else{
                    sender.sendMessage(radio + _commandManager.NOPERMS);
                }
            }

            else
            {
                Player p;
                if ((p = Bukkit.getPlayer(param)) != null )
                {
                    if (perms)
                    {
                        Radio.play(new Player[]{p}, file.toString());
                        sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("playyou").replaceAll("<author>", gimmesong.getAuthor()).replaceAll("<title>", gimmesong.getTitle()).replaceAll("<player>", sender.getName()).replaceAll("<player2>", p.getName()));
                        p.sendMessage(radio + Radio.getPlugin().getConfig().getString("playall").replaceAll("<author>", gimmesong.getAuthor()).replaceAll("<title>", gimmesong.getTitle()).replaceAll("<player>", sender.getName()).replaceAll("<player2>", "you"));
                    } else {
                        sender.sendMessage(radio + _commandManager.NOPERMS);
                    }
                }
                else
                {
                    sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("noplayer").replaceAll("<player>", param));
                }
            }

        } catch (Exception e){
            sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("playerror"));
        }
}

	private void stopmusic(Player sender, String[] args){
        try{
            boolean perms = false;
            if (sender.hasPermission("radio.admin"))
                perms = true;
            String param = args[1];

            if (param.equalsIgnoreCase("me"))
            {
                Radio.play(new Player[]{sender}, "stop");
            }

            else if (param.equalsIgnoreCase("all")) {
                if(perms) {
                    Radio.play(Bukkit.getOnlinePlayers().toArray(new Player[0]), "stop");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(radio + Radio.getPlugin().getConfig().getString("stopall").replaceAll("<player>", sender.getName()).replaceAll("<player2>", "everyone"));
                    }
                }else{
                    sender.sendMessage(radio + _commandManager.NOPERMS);
                }
            }

            else if (param.equalsIgnoreCase("world")) {
                if(perms) {
                    Radio.play(Bukkit.getOnlinePlayers().toArray(new Player[0]), "stop");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(radio + Radio.getPlugin().getConfig().getString("stopall").replaceAll("<player>", sender.getName()).replaceAll("<player2>", "everyone in their world"));
                    }
                }else{
                    sender.sendMessage(radio + _commandManager.NOPERMS);
                }
            }

            else
            {
                Player p;
                if ((p = Bukkit.getPlayer(param)) != null)
                {
                    if (perms)
                    {
                        Radio.play(new Player[]{p}, "stop");
                        sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("stopyou").replaceAll("<player>", sender.getName()).replaceAll("<player2>", p.getName()));
                        p.sendMessage(radio + Radio.getPlugin().getConfig().getString("stopall").replaceAll("<player>", sender.getName()).replaceAll("<player2>", "you"));
                    }
                    else
                    {
                        sender.sendMessage(radio + _commandManager.NOPERMS);
                    }
                }
                else
                {
                    sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("noplayer").replaceAll("<player>", param));
                }
            }
        } catch (Exception e){
            sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("stoperror"));
        }
	}

    private void listmusic(Player sender){
		File file = new File(Radio.getPlugin().getDataFolder() + "/music/");
		if(Objects.requireNonNull(file.list()).length>0){
			sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("listprefix"));
			String[] fileList = file.list();
			assert fileList != null;
			for(String name:fileList) sender.sendMessage(radio + name.replaceAll(".nbs",""));

		}else {
			sender.sendMessage(radio + Radio.getPlugin().getConfig().getString("nosongs"));
		}
	}
}
