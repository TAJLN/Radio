package moonplex.tajln;

import moonplex.tajln.NoteBlockAPI.*;
import moonplex.tajln.utils.CommandManager;
import moonplex.tajln.commands.RadioCommand;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Radio extends JavaPlugin
{
    private static Radio instance;
    private CommandManager cm;

    public void onEnable(){

        instance = this;

	    new NoteBlockAPI(this);

	    this.getConfig().addDefault("prefix","§9Radio>§7 ");
        this.getConfig().addDefault("commandinfo1","/radio list");
        this.getConfig().addDefault("commandinfo2","/radio play");
        this.getConfig().addDefault("commandinfo3","/radio stop");
        this.getConfig().addDefault("nosongs","There are no songs available!");
        this.getConfig().addDefault("nofile","Song <file> does not exist!");
        this.getConfig().addDefault("playme","You have started playing <author> - <title> to yourself");
        this.getConfig().addDefault("playall","<player> has started playing <author> - <title> to everyone");
        this.getConfig().addDefault("playerror","/radio play <all, me> <songname>");
        this.getConfig().addDefault("stoperror","/radio stop <all, me>");
        this.getConfig().addDefault("stopme","You have stopped playing for yourself");
        this.getConfig().addDefault("stopall","<player> has stopped playing for everyone");
        this.getConfig().addDefault("listprefix","List of available songs:");
        this.getConfig().addDefault("updatesongs",true);
        this.getConfig().addDefault("songurl","https://www.dropbox.com/s/70ibl6nyqd7kwdd/music.zip?dl=1");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        cm = new CommandManager(this);
        this.cm.registerCommand(new RadioCommand(this.cm));

        //Update song
        if (this.getConfig().getBoolean("updatesongs"))
		try
		{
			UpdateSongs(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void UpdateSongs(JavaPlugin plugin) throws IOException
	{
	    String musicfolder = plugin.getDataFolder() + "/music";
        Files.createDirectories(Paths.get(musicfolder));

        //File music = new File(musicfolder);
        //FileUtils.cleanDirectory(music);

		File destDir = new File(plugin.getDataFolder() + "/music/");
		URL url = new URL(Radio.getPlugin().getConfig().getString("songurl"));
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(url.openStream());
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

    private static Map<String, SongPlayer> songMap = new HashMap<>();
    public static void play(Player[] players, String file)
    {
        try
        {
            boolean stop = false;
            if ((file.equalsIgnoreCase("stop")))
            {
                stop = true;
            }
            if (!stop)
            {
                String playfile = getPlugin().getDataFolder() + "/music/" + file.replaceAll(".nbs", "") + ".nbs";
                Song s = NBSDecoder.parse(new File(playfile));
                SongPlayer sp = new RadioSongPlayer(s);
                sp.setAutoDestroy(true);
                for (Player p : players)
                {

                    if (songMap.containsKey(p.getName()))
                    {

                        songMap.get(p.getName()).removePlayer(p);
                    }

                    sp.addPlayer(p);
                    songMap.put(p.getName(), sp);
                }
                sp.setPlaying(true);
            }
            else
            {
                for (Player p : players)
                {
                    if (songMap.containsKey(p.getName()))
                    {

                        songMap.get(p.getName()).removePlayer(p);
                    }
                    NoteBlockPlayerMain.stopPlaying(p);
                }
            }
        }catch (Exception e){
            System.out.println();
        }
    }


	public static Radio getPlugin() {
        return instance;
    }
}

