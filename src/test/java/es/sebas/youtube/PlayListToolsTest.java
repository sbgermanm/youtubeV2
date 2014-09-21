/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sebas.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemContentDetails;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sebas
 */
public class PlayListToolsTest {

    Credential credential = null;

    public PlayListToolsTest() {
    }

    @Before
    public void setUp() throws IOException {
        credential = PlayListTools.authorize();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of authorize method, of class PlayListTools.
     */
    @Test
    public void testAuthorize() throws Exception {
        System.out.println("authorize");
        assertNotNull(credential);
    }

    /**
     * Test of getPlayList method, of class PlayListTools.
     */
    @Test
    public void testGetPlayList() {
        System.out.println("getPlayList");
        String playListID = "RDNoev4ofXpAA";
        Playlist result = PlayListTools.getPlayList(credential, playListID);
        assertNotNull(result);
    }

    /**
     * Test of getAllPlayLists method, of class PlayListTools.
     */
    @Test
    public void testGetAllPlayLists() {
        System.out.println("getAllPlayLists");
        List<Playlist> result = PlayListTools.getAllPlayLists(credential);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getAllWatchLaterItems method, of class PlayListTools.
     */
    @Test
    public void testGetAllWatchLaterItems() {
        System.out.println("getAllWatchLaterItems");
        List<PlaylistItem> result = PlayListTools.getAllWatchLaterItems(credential);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of getAllPlayListItems method, of class PlayListTools.
     */
    @Test
    public void testGetAllPlayListItems() {
        System.out.println("getAllPlayListItems");
        String playlistId = "RDNoev4ofXpAA";
        List<PlaylistItem> result = PlayListTools.getAllPlayListItems(credential, playlistId);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test of insertPlaylistItems method, of class PlayListTools.
     */
    @Test
    public void testInsertPlaylistItems() throws IOException {
        System.out.println("insertPlaylistItems");
        String playListID = PlayListTools.insertPlaylist("borrar", "Lista temporal para test", credential);
        
        PlaylistItem playlistItem = new PlaylistItem(); 
        playlistItem.setContentDetails(new PlaylistItemContentDetails());
        playlistItem.getContentDetails().setVideoId("Noev4ofXpAA");
        playlistItem.setSnippet(new PlaylistItemSnippet());
        playlistItem.getSnippet().setTitle("Sidonie - El Incendio");
        
        List<PlaylistItem> playListItems = new ArrayList<PlaylistItem>();
        playListItems.add(playlistItem);
        PlayListTools.insertPlaylistItems(credential, playListID, playListItems);
        
        PlayListTools.deleteAllPlayListItems(credential, playListID);
        PlayListTools.deletePlayList(credential, playListID);

        
    }

    /**
     * Test of getWlID method, of class PlayListTools.
     */
    @Test
    public void testGetWlID() {
        System.out.println("getWlID");
        String result = PlayListTools.getWlID(credential);
        assertNotNull(result);
    }

    /**
     * Test of getWlPlayList method, of class PlayListTools.
     */
    @Test
    public void testGetWlPlayList() {
        System.out.println("getWlPlayList");
        Playlist result = PlayListTools.getWlPlayList(credential);
        assertNotNull(result);
    }


}
