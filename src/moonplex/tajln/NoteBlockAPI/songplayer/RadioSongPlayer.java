package moonplex.tajln.NoteBlockAPI.songplayer;

import moonplex.tajln.NoteBlockAPI.NoteBlockAPI;
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
 * SongPlayer playing to everyone added to it no matter where he is
 *
 */
public class RadioSongPlayer extends SongPlayer {
	
	protected boolean stereo = true;
	
	public RadioSongPlayer(Song song) {
		super(song);
		makeNewClone(moonplex.tajln.NoteBlockAPI.RadioSongPlayer.class);
	}

	public RadioSongPlayer(Song song, SoundCategory soundCategory) {
		super(song, soundCategory);
		makeNewClone(moonplex.tajln.NoteBlockAPI.RadioSongPlayer.class);
	}

	private RadioSongPlayer(moonplex.tajln.NoteBlockAPI.SongPlayer songPlayer) {
		super(songPlayer);
	}

	public RadioSongPlayer(Playlist playlist, SoundCategory soundCategory) {
		super(playlist, soundCategory);
		makeNewClone(moonplex.tajln.NoteBlockAPI.RadioSongPlayer.class);
	}

	public RadioSongPlayer(Playlist playlist) {
		super(playlist);
		makeNewClone(moonplex.tajln.NoteBlockAPI.RadioSongPlayer.class);
	}

	@Override
	public void playTick(Player player, int tick) {
		byte playerVolume = NoteBlockAPI.getPlayerVolume(player);

		for (Layer layer : song.getLayerHashMap().values()) {
			Note note = layer.getNote(tick);
			if (note == null) {
				continue;
			}

			float volume = (layer.getVolume() * (int) this.volume * (int) playerVolume) / 1000000F;
			float pitch = NotePitch.getPitch(note.getKey() - 33);

			if (InstrumentUtils.isCustomInstrument(note.getInstrument())) {
				CustomInstrument instrument = song.getCustomInstruments()
						[note.getInstrument() - InstrumentUtils.getCustomInstrumentFirstIndex()];

				if (instrument.getSound() != null) {
					CompatibilityUtils.playSound(player, player.getEyeLocation(),
							instrument.getSound(),
							this.soundCategory, volume, pitch, stereo);
				} else {
					CompatibilityUtils.playSound(player, player.getEyeLocation(),
							instrument.getSoundFileName(),
							this.soundCategory, volume, pitch, stereo);
				}
			} else {
				CompatibilityUtils.playSound(player, player.getEyeLocation(),
						InstrumentUtils.getInstrument(note.getInstrument()), this.soundCategory, volume, pitch, stereo);
			}
		}
	}

	/**
	 * Returns if the SongPlayer will play Notes from two sources as stereo
	 * @return
	 */
	public boolean isStereo(){
		return stereo;
	}
	
	/**
	 * Sets if the SongPlayer will play Notes from two sources as stereo
	 * @param stereo
	 */
	public void setStereo(boolean stereo){
		this.stereo = stereo;
	}

}
