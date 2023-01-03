package dev.ryanseo;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Main {
    private static final String DEVELOPER_KEY = "AIzaSyAA_VUqOVdF9z2gujF84petXl4GuNlo5-E";
    private static final String APPLICATION_NAME = "Playlist Backup";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static void main(String[] args)
            throws GeneralSecurityException, IOException, GoogleJsonResponseException {

        try {
            ArrayList<Video> playlistVideos = PlaylistActions.savePlaylist("PL6Y2H3WgxO8EaAein4qjh0omQ81AtJJc2", DEVELOPER_KEY);

            int counter = 0;
            for (Video v : playlistVideos) {
                System.out.println(v.getTitle() + "  |  ID: " + v.getVideoId());
                counter++;
            }
            System.out.println();
            System.out.println(counter + " videos");
            System.out.println("Latest video: " + playlistVideos.get(0).getTitle() + " by " + playlistVideos.get(0).getVideoOwnerChannelTitle());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}