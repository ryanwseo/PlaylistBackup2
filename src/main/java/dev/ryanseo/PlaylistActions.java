package dev.ryanseo;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.gson.JsonParser.parseString;

public class PlaylistActions {
    /**
     * Returns an ArrayList of Videos in a playlist
     *
     * @param playlistId ID of saved playlist
     * @param DEVELOPER_KEY Developer key
     * @return ArrayList of Videos
     */
    public static ArrayList<Video> savePlaylist(String playlistId, String DEVELOPER_KEY) throws GeneralSecurityException, IOException {

        YouTube youtubeService = Main.getService();

        YouTube.PlaylistItems.List request = youtubeService.playlistItems().list("status, snippet");
        request.setMaxResults(50L);

        PlaylistItemListResponse response = request.setKey(DEVELOPER_KEY)
                .setPlaylistId(playlistId)
                .execute();

        FileWriter writer = new FileWriter("src/main/resources/playlist_data.json");
        writer.append(response.toPrettyString());
        writer.close();


        JsonArray itemsArray = parseString(response.toString()).getAsJsonObject().getAsJsonArray("items");

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Video.class, (JsonDeserializer<Video>) (json, typeOfT, context) -> {
            // json is an element of itemsArray
            JsonObject itemsArrayObject = json.getAsJsonObject();
            JsonObject snippet = itemsArrayObject.getAsJsonObject("snippet");

            Video fromPlaylist;
            try {
                fromPlaylist = new Video(snippet.getAsJsonObject("resourceId").get("videoId").getAsString(),
                        snippet.get("title").getAsString(),
                        snippet.get("videoOwnerChannelId").getAsString(),
                        snippet.get("videoOwnerChannelTitle").getAsString(),
                        PrivacyStatus.valueOf(itemsArrayObject.getAsJsonObject("status").get("privacyStatus").getAsString().toUpperCase()),
                        snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").get("url").getAsString());
            } catch (NullPointerException e) {
                throw new NullPointerException();
            }

            return fromPlaylist;
        });

        gsonBuilder.registerTypeAdapter()
                ?70

        ArrayList<Video> playlistVideos;

        int videoProcessed = 0;
        try {
            TypeToken<Collection<Video>> collectionType = new TypeToken<>(){};
            playlistVideos = new ArrayList<>(gsonBuilder.create().fromJson(itemsArray.toString(), collectionType));
            videoProcessed += 50;

            while (response.getNextPageToken() != null) {
                request.setPageToken(response.getNextPageToken());
                response = request.execute();
                itemsArray = parseString(response.toString()).getAsJsonObject().getAsJsonArray("items");
                playlistVideos.addAll(gsonBuilder.create().fromJson(itemsArray.toString(), collectionType));
                videoProcessed += 50;
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("Video processed: " + videoProcessed);
        }

        return playlistVideos;
    }

}
