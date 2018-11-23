package moonplex.tajln.NoteBlockAPI.songplayer;

import moonplex.tajln.NoteBlockAPI.NoteBlockAPI;
import moonplex.tajln.NoteBlockAPI.SongPlayer;
import moonplex.tajln.NoteBlockAPI.event.PlayerRangeStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import moonplex.tajln.NoteBlockAPI.model.CustomInstrument;
import moonplex.tajln.NoteBlockAPI.model.Layer;
import moonplex.tajln.NoteBlockAPI.model.Note;
import moonplex.tajln.NoteBlockAPI.model.NotePitch;
import moonplex.tajln.NoteBlockAPI.model.Playlist;
import moonplex.tajln.NoteBlockAPI.model.Song;
import moonplex.tajln.NoteBlockAPI.model.SoundCategory;
import moonplex.tajln.NoteBlockAPI.utils.CompatibilityUtils;
import moonplex.tajln.NoteBlockAPI.utils.InstrumentUtils;

/**
 * SongPlayer created at a specified Location
 *
 */
public class PositionSongPlayer extends RangeSongPlayer {

	private Location targetLocation;

	public PositionSongPlayer(Song song) {
		super(song);
		makeNewClone(moonplex.tajln.NoteBlockAPI.PositionSongPlayer.class);
	}

	public PositionSongPlayer(Song song, SoundCategory soundCategory) {
		super(song, soundCategory);
		makeNewClone(moonplex.tajln.NoteBlockAPI.PositionSongPlayer.class);
	}
	
	private PositionSongPlayer(SongPlayer songPlayer) {
		super(songPlayer);
	}
	
	public PositionSongPlayer(Playlist playlist, SoundCategory soundCategory) {
		super(playlist, soundCategory);
		makeNewClone(moonplex.tajln.NoteBlockAPI.PositionSongPlayer.class);
	}

	public PositionSongPlayer(Playlist playlist) {
		super(playlist);
		makeNewClone(moonplex.tajln.NoteBlockAPI.PositionSongPlayer.class);
	}

	@Override
	void update(String key, Object value) {
		super.update(key, value);
		
		switch (key){
			case "targetLocation":
				targetLocation = (Location) value;
				break;
		}
	}

	/**
	 * Gets location on which is the PositionSongPlayer playing
	 * @return {@link Location}
	 */
	public Location getTargetLocation() {
		return targetLocation;
	}

	/**
	 * Sets location on which is the PositionSongPlayer playing
	 */
	public void setTargetLocation(Location targetLocation) {
		this.targetLocation = targetLocation;
		CallUpdate("targetLocation", targetLocation);
	}

	@Override
	public void playTick(Player player, int tick) {
		if (!player.getWorld().getName().equals(targetLocation.getWorld().getName())) {
			return; // not in same world
		}

		byte playerVolume = NoteBlockAPI.getPlayerVolume(player);

		for (Layer layer : song.getLayerHashMap().values()) {
			Note note = layer.getNote(tick);
			if (note == null) continue;

			float volume = ((layer.getVolume() * (int) this.volume * (int) playerVolume) / 1000000F) 
					* ((1F / 16F) * getDistance());
			float pitch = NotePitch.getPitch(note.getKey() - 33);

			if (InstrumentUtils.isCustomInstrument(note.getInstrument())) {
				CustomInstrument instrument = song.getCustomInstruments()
						[note.getInstrument() - InstrumentUtils.getCustomInstrumentFirstIndex()];

				if (instrument.getSound() != null) {
					CompatibilityUtils.playSound(player, targetLocation, instrument.getSound(),
							this.soundCategory, volume, pitch, false);
				} else {
					CompatibilityUtils.playSound(player, targetLocation, instrument.getSoundFileName(),
							this.soundCategory, volume, pitch, false);
				}
			} else {
				CompatibilityUtils.playSound(player, targetLocation,
						InstrumentUtils.getInstrument(note.getInstrument()), this.soundCategory, 
						volume, pitch, false);
			}

			if (isInRange(player)) {
				if (!this.playerList.get(player.getUniqueId())) {
					playerList.put(player.getUniqueId(), true);
					Bukkit.getPluginManager().callEvent(new PlayerRangeStateChangeEvent(this, player, true));
				}
			} else {
				if (this.playerList.get(player.getUniqueId())) {
					playerList.put(player.getUniqueId(), false);
					Bukkit.getPluginManager().callEvent(new PlayerRangeStateChangeEvent(this, player, false));
				}
			}
		}
	}
	
	/**
	 * Returns true if the Player is able to hear the current PositionSongPlayer 
	 * @param player in range
	 * @return ability to hear the current PositionSongPlayer
	 */
	@Override
	public boolean isInRange(Player player) {
		return player.getLocation().distance(targetLocation) <= getDistance();
	}

	

}
