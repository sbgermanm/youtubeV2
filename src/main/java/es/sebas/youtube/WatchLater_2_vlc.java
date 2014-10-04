/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sebas.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebas, use : mvn exec:java -Dexec.mainClass=”es.sebas.youtube.WatchLater_2_vlc” 
 */
public class WatchLater_2_vlc {

    private static final String VLC_XSPF_CABECERA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String VLC_XSPF_OPEN = "<playlist xmlns=\"http://xspf.org/ns/0/\" xmlns:vlc=\"http://www.videolan.org/vlc/playlist/ns/0/\" version=\"1\">";
    private static final String VLC_XSPF_EXTENSION = "<extension application=\"http://www.videolan.org/vlc/playlist/0\">";

    public static void main(String[] args) throws IOException {
        // Authorization.
        Credential credential = null;
        try {
            credential = PlayListTools.authorize();
        } catch (IOException ex) {
            Logger.getLogger(UserPlayList.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Looking for the videos in ALL the playlist of the User WatchLater list : ");
        Playlist wlPlayList = PlayListTools.getWlPlayList(credential);
        crearListaVLC(credential, wlPlayList);

    }

    private static void crearListaVLC(Credential credential, Playlist playlist) throws IOException {

        String titleAux = playlist.getSnippet().getTitle();
        System.out.println("--------------------------------------------");
        System.out.println("Processing playlist : " + titleAux);
        titleAux = titleAux + "_" + Calendar.getInstance().getTime();
        String title = titleAux.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        System.out.println("File name : " + title + ".xspf");

        FileWriter fichero = null;
        fichero = new FileWriter("output/" + title + ".xspf");
        PrintWriter printWriter = new PrintWriter(fichero);
        printWriter.println(VLC_XSPF_CABECERA);
        printWriter.println(VLC_XSPF_OPEN);
        printWriter.println("<title>" + title + "</title>");
        printWriter.println("<trackList>");

        List<PlaylistItem> allPlayListItems = PlayListTools.getAllPlayListItems(credential, playlist.getId());
        int item = 0;
        System.out.println("There are " + allPlayListItems.size() + " videos in " + titleAux);
        for (PlaylistItem playlistItem : allPlayListItems) {
            printWriter.println("<track>");
            System.out.println("añadiendo track : " + playlistItem.getSnippet().getTitle());
            printWriter.println("<location>https://www.youtube.com/watch?v=" + playlistItem.getContentDetails().getVideoId() + "</location>");
            printWriter.println(VLC_XSPF_EXTENSION);
            printWriter.println("<vlc:id>" + item++ + "</vlc:id>");
            printWriter.println("</extension>");
            printWriter.println("</track>");
        }
        printWriter.println("</trackList>");

        printWriter.println(VLC_XSPF_EXTENSION);
        for (int i = 0; i < item; i++) {
            printWriter.println("<vlc:item tid=\"" + i + "\"/>");
        }
        printWriter.println("</extension>");
        printWriter.println("</playlist>");

        fichero.close();
    }

}
