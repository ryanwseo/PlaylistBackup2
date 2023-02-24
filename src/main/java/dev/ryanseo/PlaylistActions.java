package dev.ryanseo;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.google.gson.JsonParser.parseString;

public class PlaylistActions {
    /**
     * Returns an ArrayList of Videos from a playlist
     *
     * @param playlistId ID of playlist
     * @param DEVELOPER_KEY Developer key
     * @return ArrayList of Videos
     */
    public static ArrayList<Video> retrievePlaylist(String playlistId, String DEVELOPER_KEY, String playlistName) throws GeneralSecurityException, IOException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Video.class, new JsonDeserializer<Video>() {
            @Override
            public Video deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                // json is an element of itemsArray
                JsonObject itemsArrayObject = json.getAsJsonObject();
                JsonObject snippet = itemsArrayObject.getAsJsonObject("snippet");

                Video fromPlaylist;
                try {
                    fromPlaylist = new Video(
                            snippet.getAsJsonObject("resourceId").get("videoId").getAsString(),
                            snippet.get("title").getAsString(),
                            snippet.get("videoOwnerChannelId").getAsString(),
                            snippet.get("videoOwnerChannelTitle").getAsString(),
                            PrivacyStatus.valueOf(itemsArrayObject.getAsJsonObject("status").get("privacyStatus").getAsString().toUpperCase()),
                            snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").get("url").getAsString());
                } catch (NullPointerException e) {
                   fromPlaylist = new Video();
                }
                return fromPlaylist;
            }
        });

        LinkedList<Video> playlistVideos = new LinkedList<>();

        FileWriter writer = new FileWriter(String.format("src/main/resources/%s.json", playlistName));
        writer.append('[');

        YouTube.PlaylistItems.List request = Main.getService().playlistItems().list("status, snippet");
        PlaylistItemListResponse response = request.setKey(DEVELOPER_KEY)
                .setPlaylistId(playlistId)
                .setMaxResults(50L)
                .setPageToken(null)  // rewrite this part
                .execute();
        JsonArray itemsArray = parseString(response.toString()).getAsJsonObject().getAsJsonArray("items");
        playlistVideos.addAll(gsonBuilder.create().fromJson(itemsArray.toString(), new TypeToken<LinkedList<Video>>(){}.getType()));  // TypeToken is a class literal for LinkedList<Video>
        writer.append(response.toPrettyString()).append(", ");

        while (response.getNextPageToken() != null) {
            response = request.setPageToken(response.getNextPageToken()).execute();
            itemsArray = parseString(response.toString()).getAsJsonObject().getAsJsonArray("items");
            playlistVideos.addAll(gsonBuilder.create().fromJson(itemsArray.toString(), new TypeToken<LinkedList<Video>>(){}.getType()));

            writer.append(response.toPrettyString());
            if (response.getNextPageToken() != null) {
                writer.append(", ");
            }
        }
        writer.append(']');
        writer.close();

        return new ArrayList<>(playlistVideos);
    }

    /**
     *
     * @param videos
     */
    public static void saveVideosToJson(ArrayList<Video> videos) {

    }
}
