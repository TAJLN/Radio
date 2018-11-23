package moonplex.tajln.NoteBlockAPI.songplayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import moonplex.tajln.NoteBlockAPI.NoteBlockAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import moonplex.tajln.NoteBlockAPI.event.SongDestroyingEvent;
import moonplex.tajln.NoteBlockAPI.event.SongEndEvent;
import moonplex.tajln.NoteBlockAPI.event.SongLoopEvent;
import moonplex.tajln.NoteBlockAPI.event.SongNextEvent;
import moonplex.tajln.NoteBlockAPI.event.SongStoppedEvent;
import moonplex.tajln.NoteBlockAPI.model.CustomInstrument;
import moonplex.tajln.NoteBlockAPI.model.FadeType;
import moonplex.tajln.NoteBlockAPI.model.Layer;
import moonplex.tajln.NoteBlockAPI.model.Note;
import moonplex.tajln.NoteBlockAPI.model.Playlist;
import moonplex.tajln.NoteBlockAPI.model.Song;
import moonplex.tajln.NoteBlockAPI.model.SoundCategory;


/**
 * Plays a Song for a list of Players
 *
 */
public abstract class SongPlayer {

	protected Song song;
	protected Playlist playlist;
	protected int actualSong = 0;

	protected boolean playing = false;
	protected short tick = -1;
	protected Map<UUID, Boolean> playerList = Collections.synchronizedMap(new HashMap<UUID, Boolean>());

	protected boolean autoDestroy = false;
	protected boolean destroyed = false;

	protected Thread playerThread;

	protected byte volume = 100;
	protected Fade fadeIn;
	protected Fade fadeOut;
	protected boolean loop = false;

	private final Lock lock = new ReentrantLock();

	protected NoteBlockAPI plugin;

	protected SoundCategory soundCategory;
	
	moonplex.tajln.NoteBlockAPI.SongPlayer oldSongPlayer;

	public SongPlayer(Song song) {
		this(new Playlist(song), SoundCategory.MASTER);
	}

	public SongPlayer(Song song, SoundCategory soundCategory) {
		//this.song = song;
		this(new Playlist(song), soundCategory);
	}
	
	public SongPlayer(Playlist playlist){
		this(playlist, SoundCategory.MASTER);
	}
	
	public SongPlayer(Playlist playlist, SoundCategory soundCategory){
		this.playlist = playlist;
		this.song = playlist.get(actualSong);
		this.soundCategory = soundCategory;
		plugin = NoteBlockAPI.getAPI();
		
		fadeIn = new Fade(FadeType.NONE, 60);
		fadeIn.setFadeStart((byte) 0);
		fadeIn.setFadeTarget(volume);
		
		fadeOut = new Fade(FadeType.NONE, 60);
		fadeOut.setFadeStart(volume);
		fadeOut.setFadeTarget((byte) 0);
		
		start();
	}
	
	SongPlayer(moonplex.tajln.NoteBlockAPI.SongPlayer songPlayer){
		oldSongPlayer = songPlayer;
		moonplex.tajln.NoteBlockAPI.Song s = songPlayer.getSong();
		HashMap<Integer, Layer> layerHashMap = new HashMap<Integer, Layer>();
		for (Integer i : s.getLayerHashMap().keySet()){
			moonplex.tajln.NoteBlockAPI.Layer l = s.getLayerHashMap().get(i);
			HashMap<Integer, Note> noteHashMap = new HashMap<Integer, Note>();
			for (Integer iL : l.getHashMap().keySet()){
				moonplex.tajln.NoteBlockAPI.Note note = l.getHashMap().get(iL);
				noteHashMap.put(iL, new Note(note.getInstrument(), note.getKey()));
			}
			Layer layer = new Layer();
			layer.setNotesAtTicks(noteHashMap);
			layer.setVolume(l.getVolume());
			layerHashMap.put(i, layer);
		}
		CustomInstrument[] instruments = new CustomInstrument[s.getCustomInstruments().length];
		for (int i = 0; i < s.getCustomInstruments().length; i++){
			moonplex.tajln.NoteBlockAPI.CustomInstrument ci = s.getCustomInstruments()[i];
			instruments[i] = new CustomInstrument(ci.getIndex(), ci.getName(), ci.getSoundfile());
		}
		song = new Song(s.getSpeed(), layerHashMap, s.getSongHeight(), s.getLength(), s.getTitle(), s.getAuthor(), s.getDescription(), s.getPath(), instruments);
		playlist = new Playlist(song);
		
		fadeIn = new Fade(FadeType.NONE, 60);
		fadeIn.setFadeStart((byte) 0);
		fadeIn.setFadeTarget(volume);
		
		fadeOut = new Fade(FadeType.NONE, 60);
		fadeOut.setFadeStart(volume);
		fadeOut.setFadeTarget((byte) 0);
	}

	void update(String key, Object value){
		switch (key){
			case "playing":
				playing = (boolean) value;
				break;
			case "fadeType":
				fadeIn.setType(FadeType.valueOf(((String) value).replace("FADE_", "")));
				break;
			case "fadeTarget":
				fadeIn.setFadeTarget((byte) value);
				break;
			case "fadeStart":
				fadeIn.setFadeStart((byte) value);
				break;
			case "fadeDuration":
				fadeIn.setFadeDuration((int) value);
				break;
			case "fadeDone":
				fadeIn.setFadeDone((int) value);
				break;
			case "tick":
				tick = (short) value;
				break;
			case "addplayer":
				addPlayer((Player) value, false);
				break;
			case "removeplayer":
				removePlayer((Player) value, false);
				break;
			case "autoDestroy":
				autoDestroy = (boolean) value;
				break;
			case "volume":
				volume = (byte) value;
				break;
			case "soundCategory":
				soundCategory = SoundCategory.valueOf((String) value);
				break;
				
		}
	}
	
	/**
	 * Gets the FadeType for this SongPlayer (unused)
	 * @return FadeType
	 * @deprecated returns fadeIn value
	 */
	@Deprecated
	public FadeType getFadeType() {
		return fadeIn.getType();
	}

	/**
	 * Sets the FadeType for this SongPlayer
	 * @param fadeType
	 * @deprecated set fadeIn value
	 */
	@Deprecated
	public void setFadeType(FadeType fadeType) {
		fadeIn.setType(fadeType);
		CallUpdate("fadetype", "FADE_" + fadeType.name());
	}

	/**
	 * Target volume for fade
	 * @return byte representing fade target
	 * @deprecated returns fadeIn value
	 */
	@Deprecated
	public byte getFadeTarget() {
		return fadeIn.getFadeTarget();
	}

	/**
	 * Set target volume for fade
	 * @param fadeTarget
	 * @deprecated set fadeIn value
	 */
	@Deprecated
	public void setFadeTarget(byte fadeTarget) {
		fadeIn.setFadeTarget(fadeTarget);
		CallUpdate("fadeTarget", fadeTarget);
	}

	/**
	 * Gets the starting volume for the fade
	 * @return
	 * @deprecated returns fadeIn value
	 */
	@Deprecated
	public byte getFadeStart() {
		return fadeIn.getFadeStart();
	}

	/**
	 * Sets the starting volume for the fade
	 * @param fadeStart
	 * @deprecated set fadeIn value
	 */
	@Deprecated
	public void setFadeStart(byte fadeStart) {
		fadeIn.setFadeStart(fadeStart);
		CallUpdate("fadeStart", fadeStart);
	}

	/**
	 * Gets the duration of the fade
	 * @return duration of the fade
	 * @deprecated returns fadeIn value
	 */
	@Deprecated
	public int getFadeDuration() {
		return fadeIn.getFadeDuration();
	}

	/**
	 * Sets the duration of the fade
	 * @param fadeDuration
	 * @deprecated set fadeIn value
	 */
	@Deprecated
	public void setFadeDuration(int fadeDuration) {
		fadeIn.setFadeDuration(fadeDuration);
		CallUpdate("fadeDuration", fadeDuration);
	}

	/**
	 * Gets the tick when fade will be finished
	 * @return tick
	 * @deprecated returns fadeIn value
	 */
	@Deprecated
	public int getFadeDone() {
		return fadeIn.getFadeDone();
	}

	/**
	 * Sets the tick when fade will be finished
	 * @param fadeDone
	 * @deprecated set fadeIn value
	 */
	@Deprecated
	public void setFadeDone(int fadeDone) {
		fadeIn.setFadeDone(fadeDone);
		CallUpdate("fadeDone", fadeDone);
	}

	/**
	 * Starts this SongPlayer
	 */
	private void start() {
		plugin.doAsync(() -> {
			while (!destroyed) {
				long startTime = System.currentTimeMillis();
				lock.lock();
				try {
					if (destroyed || NoteBlockAPI.getAPI().isDisabling()){
						break;
					}

					if (playing) {
						if (tick < fadeIn.getFadeDuration()){
							int fade = fadeIn.calculateFade();
							if (fade != -1){
								volume = (byte) fade;
							}
							CallUpdate("fadeDone", fadeIn.getFadeDone());
						} else if (tick >= song.getLength() - fadeOut.getFadeDuration()){
							int fade = fadeOut.calculateFade();
							if (fade != -1){
								volume = (byte) fade;
							}
						}
						
						tick++;
						if (tick > song.getLength()) {
							tick = -1;
							fadeIn.setFadeDone(0);
							CallUpdate("fadeDone", fadeIn.getFadeDone());
							fadeOut.setFadeDone(0);
							if (playlist.hasNext(actualSong)){
								actualSong++;
								song = playlist.get(actualSong);
								CallUpdate("song", song);
								SongNextEvent event = new SongNextEvent(this);
								plugin.doSync(() -> Bukkit.getPluginManager().callEvent(event));
								continue;
							} else{
								actualSong = 0;
								song = playlist.get(actualSong);
								CallUpdate("song", song);
								if (loop){
									SongLoopEvent event = new SongLoopEvent(this);
									plugin.doSync(() -> Bukkit.getPluginManager().callEvent(event));
									
									if (!event.isCancelled()){
										continue;
									}
								}
							}
							playing = false;
							SongEndEvent event = new SongEndEvent(this);
							plugin.doSync(() -> Bukkit.getPluginManager().callEvent(event));
							if (autoDestroy) {
								destroy();
							}
							continue;
						}
						CallUpdate("tick", tick);
						
						plugin.doSync(() -> {
							for (UUID uuid : playerList.keySet()) {
								Player player = Bukkit.getPlayer(uuid);
								if (player == null) {
									// offline...
									continue;
								}
								playTick(player, tick);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}

				if (destroyed) {
					break;
				}

				long duration = System.currentTimeMillis() - startTime;
				float delayMillis = song.getDelay() * 50;
				if (duration < delayMillis) {
					try {
						Thread.sleep((long) (delayMillis - duration));
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		});
	}
	
	/**
	 * Returns {@link Fade} for Fade in effect
	 * @return Fade
	 */
	public Fade getFadeIn(){
		return fadeIn;
	}
	
	/**
	 * Returns {@link Fade} for Fade out effect
	 * @return Fade
	 */
	public Fade getFadeOut(){
		return fadeOut;
	}
	
	/**
	 * Gets list of current Player UUIDs listening to this SongPlayer
	 * @return list of Player UUIDs
	 */
	public Set<UUID> getPlayerUUIDs() {
		Set<UUID> uuids = new HashSet<>();
		uuids.addAll(playerList.keySet());
		return Collections.unmodifiableSet(uuids);
	}

	/**
	 * Adds a Player to the list of Players listening to this SongPlayer
	 * @param player
	 */
	public void addPlayer(Player player) {
		addPlayer(player, true);
	}
	
	private void addPlayer(Player player, boolean notify){
		lock.lock();
		try {
			if (!playerList.containsKey(player.getUniqueId())) {
				playerList.put(player.getUniqueId(), false);
				ArrayList<SongPlayer> songs = NoteBlockAPI.getSongPlayersByPlayer(player);
				if (songs == null) {
					songs = new ArrayList<SongPlayer>();
				}
				songs.add(this);
				NoteBlockAPI.setSongPlayersByPlayer(player, songs);
				if (notify){
					CallUpdate("addplayer", player);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns whether the SongPlayer is set to destroy itself when no one is listening 
	 * or when the Song ends
	 * @return if autoDestroy is enabled
	 */
	public boolean getAutoDestroy() {
		lock.lock();
		try {
			return autoDestroy;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Sets whether the SongPlayer is going to destroy itself when no one is listening 
	 * or when the Song ends
	 * @param autoDestroy if autoDestroy is enabled
	 */
	public void setAutoDestroy(boolean autoDestroy) {
		lock.lock();
		try {
			this.autoDestroy = autoDestroy;
			CallUpdate("autoDestroy", autoDestroy);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Plays the Song for the specific player
	 * @param player to play this SongPlayer for
	 * @param tick to play at
	 */
	public abstract void playTick(Player player, int tick);

	/**
	 * SongPlayer will destroy itself
	 */
	public void destroy() {
		lock.lock();
		try {
			SongDestroyingEvent event = new SongDestroyingEvent(this);
			Bukkit.getPluginManager().callEvent(event);
			//Bukkit.getScheduler().cancelTask(threadId);
			if (event.isCancelled()) {
				return;
			}
			destroyed = true;
			playing = false;
			setTick((short) -1);
			CallUpdate("destroyed", destroyed);
			CallUpdate("playing", playing);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns whether the SongPlayer is actively playing
	 * @return is playing
	 */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * Sets whether the SongPlayer is playing
	 * @param playing
	 */
	public void setPlaying(boolean playing) {
		this.playing = playing;
		if (!playing) {
			SongStoppedEvent event = new SongStoppedEvent(this);
			Bukkit.getPluginManager().callEvent(event);
		}
		CallUpdate("playing", playing);
	}

	/**
	 * Gets the current tick of this SongPlayer
	 * @return
	 */
	public short getTick() {
		return tick;
	}

	/**
	 * Sets the current tick of this SongPlayer
	 * @param tick
	 */
	public void setTick(short tick) {
		this.tick = tick;
		CallUpdate("tick", tick);
	}

	/**
	 * Removes a player from this SongPlayer
	 * @param player to remove
	 */
	public void removePlayer(Player player) {
		removePlayer(player, true);
	}
	
	private void removePlayer(Player player, boolean notify) {
		lock.lock();
		try {
			if (notify){
				CallUpdate("removeplayer", player);
			}
			playerList.remove(player.getUniqueId());
			if (NoteBlockAPI.getSongPlayersByPlayer(player) == null) {
				return;
			}
			ArrayList<SongPlayer> songs = new ArrayList<>(
					NoteBlockAPI.getSongPlayersByPlayer(player));
			songs.remove(this);
			NoteBlockAPI.setSongPlayersByPlayer(player, songs);
			if (playerList.isEmpty() && autoDestroy) {
				SongEndEvent event = new SongEndEvent(this);
				Bukkit.getPluginManager().callEvent(event);
				destroy();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the current volume of this SongPlayer
	 * @return volume (0-100)
	 */
	public byte getVolume() {
		return volume;
	}

	/**
	 * Sets the current volume of this SongPlayer
	 * @param volume (0-100)
	 */
	public void setVolume(byte volume) {
		if (volume > 100){
			volume = 100;
		} else if (volume < 0){
			volume = 0;
		}
		this.volume = volume;
		
		fadeIn.setFadeTarget(volume);
		fadeOut.setFadeStart(volume);
		
		CallUpdate("volume", volume);
	}

	/**
	 * Gets the Song being played by this SongPlayer
	 * @return
	 */
	public Song getSong() {
		return song;
	}
	
	/**
	 * Gets the Playlist being played by this SongPlayer
	 * @return
	 */
	public Playlist getPlaylist() {
		return playlist;
	}
	
	/**
	 * Sets the Playlist being played by this SongPlayer. Will affect next Song
	 */
	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}
	
	/**
	 * Get index of actually played {@link Song} in {@link Playlist}
	 * @return
	 */
	public int getPlayedSongIndex(){
		return actualSong;
	}
	
	/**
	 * Start playing {@link Song} at specified index in {@link Playlist}
	 * If there is no {@link Song} at this index, {@link SongPlayer} will continue playing current song
	 * @param index
	 */
	public void playSong(int index){
		lock.lock();
		try {
			if (playlist.exist(index)){
				song = playlist.get(index);
				actualSong = index;
				tick = -1;
				fadeIn.setFadeDone(0);
				fadeOut.setFadeDone(0);
				CallUpdate("song", song);
				CallUpdate("fadeDone", fadeIn.getFadeDone());
				CallUpdate("tick", tick);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets the SoundCategory of this SongPlayer
	 * @see SoundCategory
	 * @return SoundCategory of this SongPlayer
	 */
	public SoundCategory getCategory() {
		return soundCategory;
	}

	/**
	 * Sets the SoundCategory for this SongPlayer
	 * @param soundCategory
	 */
	public void setCategory(SoundCategory soundCategory) {
		this.soundCategory = soundCategory;
		CallUpdate("soundCategory", soundCategory.name());
	}
	
	/**
	 * Sets whether the SongPlayer will loop
	 * @param loop
	 */
	public void setLoop(boolean loop){
		this.loop = loop;
	}
	
	/**
	 * Gets whether the SongPlayer will loop
	 * @return is loop
	 */
	public boolean isLoop(){
		return loop;
	}

	void CallUpdate(String key, Object value){
		try {
			Method m = moonplex.tajln.NoteBlockAPI.SongPlayer.class.getDeclaredMethod("update", String.class, Object.class);
			m.setAccessible(true);
			m.invoke(oldSongPlayer, key, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}
	
	void makeNewClone(Class newClass){
		try {
			Constructor c = newClass.getDeclaredConstructor(new Class[] { SongPlayer.class });
			c.setAccessible(true);
			oldSongPlayer = (moonplex.tajln.NoteBlockAPI.SongPlayer) c.newInstance(new Object[]{this});
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

}