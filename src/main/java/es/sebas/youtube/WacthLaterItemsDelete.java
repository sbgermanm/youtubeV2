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
public class WacthLaterItemsDelete {

    public static void main(String[] args) {

        // Authorization.
        Credential credential = null;
        try {
            credential = PlayListTools.authorize();
        } catch (IOException ex) {
            Logger.getLogger(WacthLaterItemsDelete.class.getName()).log(Level.SEVERE, null, ex);
        }

        String wlID = PlayListTools.getWlID(credential);
        System.out.println("List ID : " + wlID);
        try {
            PlayListTools.deleteAllPlayListItems(credential, wlID);
        } catch (IOException ex) {
            Logger.getLogger(WacthLaterItemsDelete.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
