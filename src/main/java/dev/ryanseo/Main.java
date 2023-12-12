package dev.ryanseo;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

/*
    TODO Stuff to add:
    Now:
    - Keep track of videos that have been backed up, so new backups don't take so much space (most important)

    Future:
    - Scrape more information about each video in a playlist
    - Backup not just the video, but the thumbnail, comments, etc.
    - Make a graphical interface that shows info about playlists
    - Facilitate managing multiple playlists

    Maybes:
    - Make this program into a web app using TypeScript and Bootstrap
    - Use IndexedDB to store playlist data locally
 */

public class Main {

    private static final String APPLICATION_NAME = "Playlist Backup";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final ArrayList<Playlist> managedPlaylists = new ArrayList<>();

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
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        managedPlaylists.add(new Playlist("PL6Y2H3WgxO8G--X_l6SP0faS6gFR4tJJu"));  // Side Culture
        managedPlaylists.add(new Playlist("PL6Y2H3WgxO8EybvA8KeKUweGL300AfswY"));  // Sing-a-long
        managedPlaylists.add(new Playlist("PL6Y2H3WgxO8EaAein4qjh0omQ81AtJJc2"));  // Ryan's Cultured Playlist

        for (Playlist playlist : managedPlaylists) {
            playlist.retrieveVideos();
            playlist.retrievePlaylistName();
            playlist.printPlaylist();
        }

    }

}