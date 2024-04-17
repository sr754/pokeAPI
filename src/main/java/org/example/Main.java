package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.util.Utils;

import java.util.List;
import java.util.ArrayList;

public class Main {

    private static final HttpClient cliente = HttpClient.newHttpClient();

    public static void main(String[] args) throws JSONException {
        JSONObject pokemonInfo;
        String nombrePokemon;

        while (true) {
            nombrePokemon = Utils.string("Introduce el nombre de un pokémon: ").toLowerCase();
            pokemonInfo = solicitarPokemon(nombrePokemon);
            if (pokemonInfo != null) {
                mostrarInformacionPokemon(pokemonInfo);
                break;
            }
        }
    }

    private static JSONObject solicitarPokemon(String nombrePokemon) throws JSONException {
        String url = "https://pokeapi.co/api/v2/pokemon/" + nombrePokemon;
        return realizarSolicitudHTTP(url);
    }

    private static void mostrarInformacionPokemon(JSONObject pokemonInfo) {
        try {
            String nombre = pokemonInfo.getString("name");
            System.out.println(nombre + " tiene las siguientes habilidades:");
            JSONArray habilidadesArray = pokemonInfo.getJSONArray("abilities");
            List<String> habilidadesEnEspañol = new ArrayList<>();
            List<String> urlsHabilidades = new ArrayList<>();

            for (int i = 0; i < habilidadesArray.length(); i++) {
                JSONObject habilidad = habilidadesArray.getJSONObject(i).getJSONObject("ability");
                String urlHabilidad = habilidad.getString("url");
                String nombreHabilidadEnEspañol = obtenerHabilidadTraducida(urlHabilidad, "es");
                System.out.println("- " + nombreHabilidadEnEspañol + ".");
                habilidadesEnEspañol.add(nombreHabilidadEnEspañol);
                urlsHabilidades.add(urlHabilidad);
            }

            for (int i = 0; i < urlsHabilidades.size(); i++) {
                mostrarPokemonConHabilidad(urlsHabilidades.get(i), habilidadesEnEspañol.get(i));
            }
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON: " + e.getMessage());
        }
    }

    private static String obtenerHabilidadTraducida(String urlHabilidad, String codigoIdioma) throws JSONException {
        JSONObject habilidadDetalles = realizarSolicitudHTTP(urlHabilidad);
        if (habilidadDetalles == null) return "Traducción no encontrada";

        try {
            JSONArray arrayNombres = habilidadDetalles.getJSONArray("names");
            for (int i = 0; i < arrayNombres.length(); i++) {
                JSONObject entradaNombre = arrayNombres.getJSONObject(i);
                JSONObject idioma = entradaNombre.getJSONObject("language");

                if (idioma.getString("name").equals(codigoIdioma)) {
                    return entradaNombre.getString("name");
                }
            }
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON de habilidad: " + e.getMessage());
        }
        return "Traducción no encontrada";
    }

    private static void mostrarPokemonConHabilidad(String urlHabilidad, String nombreHabilidadEnEspañol) throws JSONException {
        JSONObject detallesHabilidad = realizarSolicitudHTTP(urlHabilidad);
        if (detallesHabilidad == null) return;

        try {
            JSONArray pokemonArray = detallesHabilidad.getJSONArray("pokemon");
            System.out.println("\nLa habilidad \"" + nombreHabilidadEnEspañol + "\" la poseen los siguientes Pokémon:");
            for (int i = 0; i < pokemonArray.length(); i++) {
                JSONObject pokemonEntry = pokemonArray.getJSONObject(i);
                JSONObject pokemon = pokemonEntry.getJSONObject("pokemon");
                String pokemonName = pokemon.getString("name");
                System.out.println("- " + pokemonName);
            }
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON de habilidad: " + e.getMessage());
        }
    }

    private static JSONObject realizarSolicitudHTTP(String url) throws JSONException {
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                return new JSONObject(respuesta.body());
            } else {
                System.out.println("No se pudo obtener información del servidor.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error de comunicación: " + e.getMessage());
        }
        return null;
    }
}