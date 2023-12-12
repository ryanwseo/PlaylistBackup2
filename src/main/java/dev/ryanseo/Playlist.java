package dev.ryanseo;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.gson.JsonParser.parseString;

public class Playlist {
    private static final String DEVELOPER_KEY = "AIzaSyAA_VUqOVdF9z2gujF84petXl4GuNlo5-E";
    private final String playlistId;
    private String playlistName;
    private ArrayList<Video> videos;


    public Playlist(String playlistId) throws GeneralSecurityException, IOException {
        this.playlistId = playlistId;
        retrievePlaylistName();
    }

    public String getPlaylistId() {
        return playlistId;
    }
    
    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
    }

    public void retrievePlaylistName() throws GeneralSecurityException, IOException {
        YouTube.Playlists.List request = Main.getService().playlists().list(List.of("snippet"));
        PlaylistListResponse response = request.setKey(DEVELOPER_KEY)
                .setId(List.of(getPlaylistId()))
                .execute();

        JsonObject topLevel = JsonParser.parseString(response.toPrettyString()).getAsJsonObject();
        String playlistName = topLevel.get("items").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("snippet").getAsJsonObject()
                .get("title").getAsString();

        this.setPlaylistName(playlistName);
    }

    public void retrieveVideos() throws GeneralSecurityException, IOException {

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
                            snippet.getAsJsonObject("thumbnails").getAsJsonObject("high").get("url").getAsString(),
                            snippet.get("position").getAsInt());
                }
                catch (NullPointerException e) {
                    fromPlaylist = new Video(snippet.getAsJsonObject("resourceId").get("videoId").getAsString(),
                            null, null, null, null, null,
                            snippet.get("position").getAsInt());
                }
                return fromPlaylist;
            }
        });

        LinkedList<Video> playlistVideos = new LinkedList<>();

        FileWriter writer = new FileWriter(String.format("src/main/resources/%s.json", getPlaylistName()));
        writer.append("[\n");

        // API updated so parameter must be of type List<String>
        // You can input "contentDetails" in list() to get videoID easier, but todo later
        YouTube.PlaylistItems.List request = Main.getService().playlistItems().list(List.of("status", "snippet"));

        PlaylistItemListResponse response = request.setKey(DEVELOPER_KEY)
                .setPlaylistId(this.getPlaylistId())
                .setMaxResults(50L)
                .setPageToken(null)  // rewrite this part
                .execute();
        JsonArray itemsArray = parseString(response.toString()).getAsJsonObject().getAsJsonArray("items");
        playlistVideos.addAll(gsonBuilder.create().fromJson(itemsArray.toString(), new TypeToken<LinkedList<Video>>(){}.getType()));  // TypeToken is a class literal for LinkedList<Video>
        writer.append(response.toPrettyString()).append(",\n");

        while (response.getNextPageToken() != null) {
            response = request.setPageToken(response.getNextPageToken()).execute();
            itemsArray = parseString(response.toString()).getAsJsonObject().getAsJsonArray("items");
            playlistVideos.addAll(gsonBuilder.create().fromJson(itemsArray.toString(), new TypeToken<LinkedList<Video>>(){}.getType()));

            writer.append(response.toPrettyString());
            if (response.getNextPageToken() != null) {
                writer.append(",\n");
            }
        }

        writer.append("\n]");
        writer.close();

        this.setVideos(new ArrayList<>(playlistVideos));
    }

    public void printPlaylist() {
        for (Video v : getVideos()) {
            if (v.getTitle() != null || v.getPrivacyStatus() != PrivacyStatus.PRIVATE) {
                System.out.println(v.getTitle() + "  |  PrivacyStatus: " + v.getPrivacyStatus() + "  |  VideoOwner: " + v.getVideoOwnerChannelTitle() + "  |  Pos: " + v.getPosition());
            }
            else {
                throw new RuntimeException(String.format("The video at position %d (ID: %s) is misbehaving", v.getPosition(), v.getVideoId()));
            }
        }

        System.out.println();
        System.out.println(this.getPlaylistName());
        System.out.println(getVideos().size() + " videos");
        System.out.println("Latest video: " + getVideos().get(0).getTitle() + " by " + getVideos().get(0).getVideoOwnerChannelTitle());
        System.out.println();
        System.out.println("———————————————————");
    }

}
