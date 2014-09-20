package es.sebas.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebas
 */
public class YoutubePlayList2TxtList {

    public static void main(String[] args) throws IOException {
        // Authorization.
        Credential credential = null;
        try {
            credential = PlayListTools.authorize();
        } catch (IOException ex) {
            Logger.getLogger(UserPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Playlist> playlists = null;

        if (args.length == 1) {
            System.out.println("Looking for the videos in the playlist : " + args[0]);
            Playlist playList = PlayListTools.getPlayList(credential, args[0]);
            playlists = new ArrayList<Playlist>();
            playlists.add(playList);
        } else {
            System.out.println("Looking for the videos in ALL the playlist of the User: ");
            playlists = PlayListTools.getAllPlayLists(credential);
            Playlist wlPlayList = PlayListTools.getWlPlayList(credential);
            playlists.add(wlPlayList);
        }

        for (Playlist playlist : playlists) {
            crearListaTxt(credential, playlist);
        }
    }

    private static void crearListaTxt(Credential credential, Playlist playlist) throws IOException {
        String titleAux = playlist.getSnippet().getTitle();
        System.out.println("--------------------------------------------");
        System.out.println("Processing playlist : " + titleAux);
        String title = titleAux.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        String nombreFichero = "output/" + title + ".txt";
        System.out.println("File name : " + nombreFichero);

        FileWriter fichero =  new FileWriter(nombreFichero);
        PrintWriter printWriter = new PrintWriter(fichero);

        List<PlaylistItem> allPlayListItems = PlayListTools.getAllPlayListItems(credential, playlist.getId());
        System.out.println("There are " + allPlayListItems.size() + " videos in " + titleAux);

        for (PlaylistItem playlistItem : allPlayListItems) {
            String video = playlistItem.getSnippet().getTitle();
            System.out.println("adding video : " + video);
            printWriter.println(video + " : https://www.youtube.com/watch?v=" + playlistItem.getContentDetails().getVideoId());
        }
        fichero.close();
    }

}
