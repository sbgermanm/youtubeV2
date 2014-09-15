package es.sebas.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.model.PlaylistItem;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebas
 */
public class WacthLaterCopy {

    public static void main(String[] args) {

        // Authorization.
        Credential credential = null;
        try {
            credential = PlayListTools.authorize();
        } catch (IOException ex) {
            Logger.getLogger(WacthLaterCopy.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<PlaylistItem> wlPlayListItems = PlayListTools.getAllWatchLaterItems(credential);

        String playlistTitle = "Temporal Playlist " + Calendar.getInstance().getTime();
        String playlistDesc = "A private temporal playlist created with the YouTube API v3";
        String playlistId;
        try {
            playlistId = PlayListTools.insertPlaylist(playlistTitle, playlistDesc, credential);

            // If a valid playlist was created, adds a new playlistitem with a video to that playlist.
            PlayListTools.insertPlaylistItems(credential, playlistId, wlPlayListItems);
        } catch (IOException ex) {
            Logger.getLogger(WacthLaterCopy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
