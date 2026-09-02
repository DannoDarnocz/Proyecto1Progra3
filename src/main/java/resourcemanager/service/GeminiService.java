package resourcemanager.service;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class GeminiService {
    private static final String MODELO = "gemini-3.6-flash";
    private static final String ENDPOINT_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/";
    private final String apiKey;
    private final HttpClient httpClient;
    public GeminiService() {
        this.apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "No se encontro la variable de entorno GEMINI_API_KEY.");
        }
        this.httpClient = HttpClient.newHttpClient();
    }
    public String requestJSON(String prompt, JsonArray availableCategories) throws IOException, InterruptedException {
        String instruccion = """

    Answer exclusively with a JSON following this format and using comments for your own reference as to what to fill each field with. If any data is missing, just type "null"
    for the whole thing.
    {
      "description": "string", // description for the reservation
      "date": "YYYY-MM-DD", // formatted as a Java LocalDate in the ISO-8601 calendar system
      "startHour": int, // HOUR at which the reservation is required to start
      "startMinute": int, // MINUTE at which the reservation is required to start
      "endHour": int, // HOUR at which the reservation ends
      "endMinute": int, // MINUTE at which the reservation ends
      "categories": ["cat1","cat2","cat3"...] // each one is a string for an existent categoryId, if none match, use null
    }
    Do not wrap it in markdown code fences, do not add comments, and do not add any explanation before or after it.
    The user requests the following: """ + prompt + ". The available categories are: " + availableCategories;

        String url = ENDPOINT_BASE + MODELO + ":generateContent";
        JSONObject parte = new JSONObject().put("text", instruccion);
        JSONObject contenido = new JSONObject()
                .put("parts", new JSONArray().put(parte));
        JSONObject cuerpo = new JSONObject()
                .put("contents", new JSONArray().put(contenido));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
                .build();
        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Error de la API (HTTP "
                    + response.statusCode() + "): " + response.body());
        }
        return extraerTexto(response.body());
    }
    private String extraerTexto(String jsonRespuesta) {
        JSONObject raiz = new JSONObject(jsonRespuesta);

        JSONArray candidatos = raiz.getJSONArray("candidates");
        JSONObject primerCandidato = candidatos.getJSONObject(0);
        JSONObject contenido = primerCandidato.getJSONObject("content");
        JSONArray partes = contenido.getJSONArray("parts");

        return partes.getJSONObject(0).getString("text");
    }
}