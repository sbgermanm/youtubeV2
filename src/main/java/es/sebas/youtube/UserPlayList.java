package es.sebas.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistSnippet;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebas
 */
public class UserPlayList {

    public static void main(String[] args) {

        // Authorization.
        Credential credential = null;
        try {
            credential = PlayListTools.authorize();
        } catch (IOException ex) {
            Logger.getLogger(UserPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Playlist> playlists = PlayListTools.getAllPlayLists(credential);

        System.out.println("You have " + playlists.size() + " play lists");
        System.out.println("----------------------");
        for (Playlist playlist : playlists) {
            String title = playlist.getSnippet().getTitle();
            String id = playlist.getId();
            System.out.println("Title : " + title + ", ID : " + id);
        }

    }
}

