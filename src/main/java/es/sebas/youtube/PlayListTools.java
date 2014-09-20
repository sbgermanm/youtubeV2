/*
 * api explorer https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.playlistItems.list?part=snippet%252CcontentDetails%252C+status&playlistId=PL0ryXv6Lj74dqXYJ54OqJ3pUVnJEo5oWs&_h=7&
 * code sample https://developers.google.com/youtube/v3/code_samples/java?hl=es#create_a_playlist
 * api referenca with code sample https://developers.google.com/youtube/v3/docs/playlists/delete?hl=es
 */
package es.sebas.youtube;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.google.api.services.youtube.model.ResourceId;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebas
 */
public class PlayListTools {

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String RESOURCE_KIND_VIDEO = "youtube#video";
    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @throws java.io.IOException
     * @return Credential of the authorized user
     */
    public static Credential authorize() throws IOException {

        List<String> scopes = Arrays.asList("https://www.googleapis.com/auth/youtube");

        Reader reader = new InputStreamReader(PlayListTools.class.getResourceAsStream("/client_secrets.json"));
        // Load client secrets.
        GoogleClientSecrets clientSecrets
                = GoogleClientSecrets.load(
                        JSON_FACTORY,
                        reader);


        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore("youtubesebas");

        // Set up authorization code flow.
        GoogleAuthorizationCodeFlow flow
                = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
                        JSON_FACTORY,
                        clientSecrets,
                        scopes)
                        .setCredentialDataStore(datastore)
                        .build();

        // Build the local server and bind it to port 9000
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }

    /**
     * Creates YouTube Playlist and adds it to the authorized account.
     *
     * @param playlistTitle
     * @param credential
     * @param playlistDesc
     * @return Id of the newly created playlist
     * @throws java.io.IOException
     */
    public static String insertPlaylist(String playlistTitle, String playlistDesc, Credential credential) throws IOException {

        /*
         * We need to first create the parts of the Playlist before the playlist itself.  Here we are
         * creating the PlaylistSnippet and adding the required data.
         */
        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(playlistTitle);
        playlistSnippet.setDescription(playlistDesc);

        // Here we set the privacy status (required).
        PlaylistStatus playlistStatus = new PlaylistStatus();
        playlistStatus.setPrivacyStatus("private");

        /*
         * Now that we have all the required objects, we can create the Playlist itself and assign the
         * snippet and status objects from above.
         */
        Playlist youTubePlaylist = new Playlist();
        youTubePlaylist.setSnippet(playlistSnippet);
        youTubePlaylist.setStatus(playlistStatus);

        /*
         * This is the object that will actually do the insert request and return the result.  The
         * first argument tells the API what to return when a successful insert has been executed.  In
         * this case, we want the snippet and contentDetails info.  The second argument is the playlist
         * we wish to insert.
         */
        // YouTube object used to make all API requests.
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("youtube-sebas")
                .build();

        YouTube.Playlists.Insert playlistInsertCommand
                = youtube.playlists().insert("snippet,status", youTubePlaylist);
        Playlist playlistInserted = playlistInsertCommand.execute();

        // Pretty print results.
        System.out.println("New Playlist name: " + playlistInserted.getSnippet().getTitle());
        System.out.println(" - Privacy: " + playlistInserted.getStatus().getPrivacyStatus());
        System.out.println(" - Description: " + playlistInserted.getSnippet().getDescription());
        System.out.println(" - Channel: " + playlistInserted.getSnippet().getChannelId() + "\n");
        return playlistInserted.getId();

    }

    /**
     * Creates YouTube PlaylistItem with specified video id and adds it to the
     * specified playlist id for the authorized account.
     *
     * @param playlistId assign to newly created playlistitem
     * @param videoId YouTube video id to add to playlistitem
     */
    private static String insertPlaylistItem(Credential credential, String playlistId, String videoId, String title) throws IOException {

        /*
         * The Resource type (video,playlist,channel) needs to be set along with the resource id. In
         * this case, we are setting the resource to a video id, since that makes sense for this
         * playlist.
         */
        ResourceId resourceId = new ResourceId();
        resourceId.setKind(RESOURCE_KIND_VIDEO);
        resourceId.setVideoId(videoId);

        /*
         * Here we set all the information required for the snippet section.  We also assign the
         * resource id from above to the snippet object.
         */
        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setTitle(title);
        playlistItemSnippet.setPlaylistId(playlistId);
        playlistItemSnippet.setResourceId(resourceId);

        /*
         * Now that we have all the required objects, we can create the PlaylistItem itself and assign
         * the snippet object from above.
         */
        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        /*
         * This is the object that will actually do the insert request and return the result.  The
         * first argument tells the API what to return when a successful insert has been executed.  In
         * this case, we want the snippet and contentDetails info.  The second argument is the
         * playlistitem we wish to insert.
         */
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("youtube-sebas")
                .build();
        YouTube.PlaylistItems.Insert playlistItemsInsertCommand
                = youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
        PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();

        printPlayListItem(returnedPlaylistItem);

        return returnedPlaylistItem.getId();

    }

    
    public static Playlist getPlayList(Credential credential, String playListID) {
        Playlist playList = null;
        try {
            // YouTube object used to make all API requests.
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
                    .setApplicationName("youtube-sebas")
                    .build();

            YouTube.Playlists.List playListsRequest = youtube.playlists().list("snippet");
            playListsRequest.setId(playListID);

            PlaylistListResponse playlistsResponse = playListsRequest.execute();
            playList = playlistsResponse.getItems().get(0);

        } catch (IOException ex) {
            Logger.getLogger(PlayListTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playList;
    }

    
    
    
    public static List<Playlist> getAllPlayLists(Credential credential) {
        List<Playlist> playLists = new ArrayList<Playlist>();
        try {
            // YouTube object used to make all API requests.
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
                    .setApplicationName("youtube-sebas")
                    .build();

            YouTube.Playlists.List playListsRequest = youtube.playlists().list("snippet");
            playListsRequest.setMine(Boolean.TRUE);
            String nextToken = "";

            do {
                playListsRequest.setPageToken(nextToken);

                PlaylistListResponse playlistsResponse = playListsRequest.execute();
                playLists.addAll(playlistsResponse.getItems());

                nextToken = playlistsResponse.getNextPageToken();
            } while (nextToken != null);

        } catch (IOException ex) {
            Logger.getLogger(PlayListTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playLists;
    }

    public static List<PlaylistItem> getAllWatchLaterItems(Credential credential) {

        String wlPlaylistId = getWlID(credential);
        System.out.println("WatchLaterId : " + wlPlaylistId);
        return getAllPlayListItems(credential, wlPlaylistId);
    }

    public static List<PlaylistItem> getAllPlayListItems(Credential credential, String wlPlaylistId) {
        // List to store all PlaylistItem items associated with the uploadPlaylistId.
        ArrayList<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

        /*
         * Now that the user is authenticated, the app makes a channel list request to get the
         * authenticated user's channel. Returned with that data is the playlist id for the uploaded
         * videos. https://developers.google.com/youtube/v3/docs/channels/list
         */
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("youtube-sebas")
                .build();

        /*
         * Now that we have the playlist id for your uploads, we will request the playlistItems
         * associated with that playlist id, so we can get information on each video uploaded. This
         * is the template for the list call. We call it multiple times in the do while loop below
         * (only changing the nextToken to get all the videos).
         * https://developers.google.com/youtube/v3/docs/playlistitems/list
         */
        YouTube.PlaylistItems.List playlistItemRequest;
        try {
            playlistItemRequest = youtube.playlistItems().list("id,contentDetails,snippet");
            playlistItemRequest.setPlaylistId(wlPlaylistId);

            // This limits the results to only the data we need and makes things more efficient.
            playlistItemRequest.setFields(
                    "items(id,contentDetails/videoId,snippet/title),nextPageToken,pageInfo");

            
            String nextToken = "";

            // Loops over all search page results returned for the uploadPlaylistId.
            do {
                playlistItemRequest.setPageToken(nextToken);
                PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

                playlistItemList.addAll(playlistItemResult.getItems());

                nextToken = playlistItemResult.getNextPageToken();
            } while (nextToken != null);

        } catch (IOException ex) {
            Logger.getLogger(PlayListTools.class.getName()).log(Level.SEVERE, null, ex);
        }

        return playlistItemList;
    }

    public static void insertPlaylistItems(Credential credential, String playlistId, List<PlaylistItem> wlPlayListItems) {

        for (PlaylistItem playlistItem : wlPlayListItems) {
            try {
                System.out.println("Inserting Video, ID=" + playlistItem.getContentDetails().getVideoId() + " with title: " + playlistItem.getSnippet().getTitle());
                insertPlaylistItem(credential, playlistId, playlistItem.getContentDetails().getVideoId(), playlistItem.getSnippet().getTitle());
            } catch (IOException ex) {
                Logger.getLogger(PlayListTools.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static String getWlID(Credential credential) {
        String wlPlaylistId = null;

        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("youtube-sebas")
                .build();
        /*
         * Now that the user is authenticated, the app makes a channel list request to get the
         * authenticated user's channel. Returned with that data is the playlist id for the uploaded
         * videos. https://developers.google.com/youtube/v3/docs/channels/list
         */
        YouTube.Channels.List channelRequest;
        try {
            channelRequest = youtube.channels().list("contentDetails");
            /*
             * Limits the results to only the data we needo which makes things more efficient.
             */
            channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
            channelRequest.setMine(Boolean.TRUE);
            ChannelListResponse channelResult = channelRequest.execute();

            /*
             * Gets the list of channels associated with the user. This sample only pulls the uploaded
             * videos for the first channel (default channel for user).
             */
            List<Channel> channelsList = channelResult.getItems();

            if (channelsList != null) {
                // Gets user's default channel id (first channel in list).
                wlPlaylistId = channelsList.get(0).getContentDetails().getRelatedPlaylists().getWatchLater();
            }
        } catch (IOException ex) {
            Logger.getLogger(PlayListTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wlPlaylistId;
    }

    public static Playlist getWlPlayList(Credential credential) {
        Playlist wlPlayList = null;
        String wlID = getWlID(credential);

        // YouTube object used to make all API requests.
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("youtube-sebas")
                .build();

        try {
            YouTube.Playlists.List playListsRequest = youtube.playlists().list("id, snippet, contentDetails");
//            playListsRequest.setMine("true");
            playListsRequest.setId(wlID);
            PlaylistListResponse playlistsResponse = playListsRequest.execute();
            wlPlayList = playlistsResponse.getItems().get(0);
        } catch (IOException ex) {
            Logger.getLogger(PlayListTools.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wlPlayList;
    }

    public static void deleteAllPlayListItems(Credential credential, String listID) throws IOException {

        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential)
                .setApplicationName("youtube-sebas")
                .build();

        List<PlaylistItem> allPlayListItems = getAllPlayListItems(credential, listID);
        System.out.println("Deleting " + allPlayListItems.size() + " items");
        System.out.println("---------------------");
        int i = 0;
        for (PlaylistItem playlistItem : allPlayListItems) {
            System.out.println("Deleting item " + ++i + "" + playlistItem.toPrettyString());
            YouTube.PlaylistItems.Delete delete = youtube.playlistItems().delete(playlistItem.getId());
            delete.execute();
        }

    }

    private static void printPlayListItem(PlaylistItem playlistItem) {
        System.out.println("PlaylistItem name: " + playlistItem.getSnippet().getTitle());
        System.out.println(" - Video id: " + playlistItem.getSnippet().getResourceId().getVideoId());
        System.out.println(" - Posted: " + playlistItem.getSnippet().getPublishedAt());
        System.out.println(" - Channel: " + playlistItem.getSnippet().getChannelId());
    }
}
