package moonplex.tajln.NoteBlockAPI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import moonplex.tajln.NoteBlockAPI.utils.InstrumentUtils;

public class NoteBlockSongPlayer extends SongPlayer {

	private Block noteBlock;
	private int distance = 16;

	public NoteBlockSongPlayer(Song song) {
		super(song);
		makeNewClone(moonplex.tajln.NoteBlockAPI.songplayer.NoteBlockSongPlayer.class);
	}

	public NoteBlockSongPlayer(Song song, SoundCategory soundCategory) {
		super(song, soundCategory);
		makeNewClone(moonplex.tajln.NoteBlockAPI.songplayer.NoteBlockSongPlayer.class);
	}
	
	public NoteBlockSongPlayer(moonplex.tajln.NoteBlockAPI.songplayer.SongPlayer songPlayer) {
		super(songPlayer);
	}

	@Override
	void update(String key, Object value) {
		super.update(key, value);
		
		switch (key){
			case "distance":
				distance = (int) value;
				break;
			case "noteBlock":
				noteBlock = (Block) value;
				break;
		}
	}

	public Block getNoteBlock() {
		return noteBlock;
	}

	public void setNoteBlock(Block noteBlock) {
		this.noteBlock = noteBlock;
		CallUpdate("noteBlock", noteBlock);
	}

	@Override
	public void playTick(Player player, int tick) {
		if (noteBlock.getType() != Material.NOTE_BLOCK) {
			return;
		}
		if (!player.getWorld().getName().equals(noteBlock.getWorld().getName())) {
			// not in same world
			return;
		}
		byte playerVolume = NoteBlockAPI.getPlayerVolume(player);

		for (Layer layer : song.getLayerHashMap().values()) {
			Note note = layer.getNote(tick);
			if (note == null) {
				continue;
			}
			player.playNote(noteBlock.getLocation(), InstrumentUtils.getBukkitInstrument(note.getInstrument()),
					new org.bukkit.Note(note.getKey() - 33));

			float volume = ((layer.getVolume() * (int) this.volume * (int) playerVolume) / 1000000F) 
					* ((1F / 16F) * getDistance());
			float pitch = NotePitch.getPitch(note.getKey() - 33);

			if (InstrumentUtils.isCustomInstrument(note.getInstrument())) {
				CustomInstrument instrument = song.getCustomInstruments()
						[note.getInstrument() - InstrumentUtils.getCustomInstrumentFirstIndex()];

				if (instrument.getSound() != null) {
					CompatibilityUtils.playSound(player, noteBlock.getLocation(), 
							instrument.getSound(), this.soundCategory, volume, pitch);
				} else {
					CompatibilityUtils.playSound(player, noteBlock.getLocation(), 
							instrument.getSoundfile(), this.soundCategory, volume, pitch);
				}
			} else {
				CompatibilityUtils.playSound(player, noteBlock.getLocation(),
						InstrumentUtils.getInstrument(note.getInstrument()), this.soundCategory, volume, pitch);
			}

			if (isPlayerInRange(player)) {
				if (!this.playerList.get(player.getName())) {
					playerList.put(player.getName(), true);
					Bukkit.getPluginManager().callEvent(new PlayerRangeStateChangeEvent(this, player, true));
				}
			} else {
				if (this.playerList.get(player.getName())) {
					playerList.put(player.getName(), false);
					Bukkit.getPluginManager().callEvent(new PlayerRangeStateChangeEvent(this, player, false));
				}
			}
		}
	}

	public void setDistance(int distance) {
		this.distance = distance;
		CallUpdate("distance", distance);
	}

	public int getDistance() {
		return distance;
	}

	public boolean isPlayerInRange(Player player) {
		if (player.getLocation().distance(noteBlock.getLocation()) > getDistance()) {
			return false;
		} else {
			return true;
		}
	}
}
