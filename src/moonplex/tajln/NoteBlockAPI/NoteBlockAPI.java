package moonplex.tajln.NoteBlockAPI;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import moonplex.tajln.MiniPlugin;
import moonplex.tajln.NoteBlockAPI.songplayer.SongPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;


public class NoteBlockAPI extends MiniPlugin
{

    private static NoteBlockAPI plugin;

    public static JavaPlugin _plugin;

    private Map<UUID, ArrayList<SongPlayer>> playingSongs =
            Collections.synchronizedMap(new HashMap<>());
    private Map<UUID, Byte> playerVolume = Collections.synchronizedMap(new HashMap<UUID, Byte>());

    private boolean disabling = false;

    private HashMap<Plugin, Boolean> dependentPlugins = new HashMap<>();

    public NoteBlockAPI(JavaPlugin Jplugin)
    {
        super("NoteBlockAPI", Jplugin);

        _plugin = Jplugin;

        plugin = this;

        for (Plugin pl : Jplugin.getServer().getPluginManager().getPlugins()){
            if (pl.getDescription().getDepend().contains("NoteBlockAPI") || pl.getDescription().getSoftDepend().contains("NoteBlockAPI")){
                dependentPlugins.put(pl, false);
            }
        }

        new NoteBlockPlayerMain().onEnable();

        Jplugin.getServer().getScheduler().runTaskLater(Jplugin, new Runnable()
        {

            @Override
            public void run()
            {
                Plugin[] plugins = Jplugin.getServer().getPluginManager().getPlugins();
                Type[] types = new Type[]{PlayerRangeStateChangeEvent.class, SongDestroyingEvent.class, SongEndEvent.class, SongStoppedEvent.class};
                for (Plugin plugin : plugins)
                {
                    ArrayList<RegisteredListener> rls = HandlerList.getRegisteredListeners(plugin);
                    for (RegisteredListener rl : rls)
                    {
                        Method[] methods = rl.getListener().getClass().getDeclaredMethods();
                        for (Method m : methods)
                        {
                            Type[] params = m.getParameterTypes();
                            param:
                            for (Type paramType : params)
                            {
                                for (Type type : types)
                                {
                                    if (paramType.equals(type))
                                    {
                                        dependentPlugins.put(plugin, true);
                                        break param;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }, 1);
    }

    public static boolean isReceivingSong(Player player) {
        return ((plugin.playingSongs.get(player.getUniqueId()) != null)
                && (!plugin.playingSongs.get(player.getUniqueId()).isEmpty()));
    }

    public static void stopPlaying(Player player) {
        if (plugin.playingSongs.get(player.getUniqueId()) == null) {
            return;
        }
        for (SongPlayer songPlayer : plugin.playingSongs.get(player.getUniqueId())) {
            songPlayer.removePlayer(player);
        }
    }

    public static void setPlayerVolume(Player player, byte volume) {
        plugin.playerVolume.put(player.getUniqueId(), volume);
    }

    public static byte getPlayerVolume(Player player) {
        Byte byteObj = plugin.playerVolume.get(player.getUniqueId());
        if (byteObj == null) {
            byteObj = 100;
            plugin.playerVolume.put(player.getUniqueId(), byteObj);
        }
        return byteObj;
    }

    public static ArrayList<SongPlayer> getSongPlayersByPlayer(Player player){
        return plugin.playingSongs.get(player.getUniqueId());
    }

    public static void setSongPlayersByPlayer(Player player, ArrayList<SongPlayer> songs){
        plugin.playingSongs.put(player.getUniqueId(), songs);
    }

    public void doSync(Runnable runnable) {
        _plugin.getServer().getScheduler().runTask(_plugin, runnable);
    }

    public void doAsync(Runnable runnable) {
        _plugin.getServer().getScheduler().runTaskAsynchronously(_plugin, runnable);
    }

    public boolean isDisabling() {
        return disabling;
    }

    public static NoteBlockAPI getAPI(){
        return plugin;
    }

    protected void handleDeprecated(StackTraceElement[] ste){
        int pom = 1;
        String clazz = ste[pom].getClassName();
        while (clazz.startsWith("moonplex.tajln.NoteBlockAPI")){
            pom++;
            clazz = ste[pom].getClassName();
        }
        String[] packageParts = clazz.split("\\.");
        ArrayList<Plugin> plugins = new ArrayList<Plugin>();
        plugins.addAll(dependentPlugins.keySet());

        ArrayList<Plugin> notResult = new ArrayList<Plugin>();
        parts:
        for (int i = 0; i < packageParts.length - 1; i++){

            for (Plugin pl : plugins){
                if (notResult.contains(pl)){ continue;}
                if (plugins.size() - notResult.size() == 1){
                    break parts;
                }
                String[] plParts = pl.getDescription().getMain().split("\\.");
                if (!packageParts[i].equalsIgnoreCase(plParts[i])){
                    notResult.add(pl);
                    continue;
                }
            }
            plugins.removeAll(notResult);
            notResult.clear();
        }

        plugins.removeAll(notResult);
        notResult.clear();
        if (plugins.size() == 1){
            dependentPlugins.put(plugins.get(0), true);
        }
    }

}
