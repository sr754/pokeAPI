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

    public static void main(String[] args) {
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

    private static JSONObject solicitarPokemon(String nombrePokemon) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + nombrePokemon;
        HttpRequest peticion = crearHttpRequest(url);

        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                return new JSONObject(respuesta.body());
            } else {
                System.out.println("No existe ese pokémon, por favor prueba con otro nombre.");
            }
        } catch (IOException | InterruptedException | JSONException e) {
            System.err.println("Error durante la solicitud: " + e.getMessage());
        }
        return null;
    }

    private static HttpRequest crearHttpRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
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

    private static String obtenerHabilidadTraducida(String urlHabilidad, String codigoIdioma) {
        HttpRequest peticion = crearHttpRequest(urlHabilidad);

        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            JSONObject detallesHabilidad = new JSONObject(respuesta.body());
            JSONArray arrayNombres = detallesHabilidad.getJSONArray("names");
            for (int i = 0; i < arrayNombres.length(); i++) {
                JSONObject entradaNombre = arrayNombres.getJSONObject(i);
                JSONObject idioma = entradaNombre.getJSONObject("language");

                if (idioma.getString("name").equals(codigoIdioma)) {
                    return entradaNombre.getString("name");
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error durante la solicitud de habilidad: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON de habilidad: " + e.getMessage());
        }
        return "Traducción no encontrada";
    }

    private static void mostrarPokemonConHabilidad(String urlHabilidad, String nombreHabilidadEnEspañol) {
        HttpRequest peticion = crearHttpRequest(urlHabilidad);

        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            JSONObject detallesHabilidad = new JSONObject(respuesta.body());
            JSONArray pokemonArray = detallesHabilidad.getJSONArray("pokemon");

            System.out.println("\nLa habilidad \"" + nombreHabilidadEnEspañol + "\" la poseen los siguientes Pokémon:");
            for (int i = 0; i < pokemonArray.length(); i++) {
                JSONObject pokemonEntry = pokemonArray.getJSONObject(i);
                JSONObject pokemon = pokemonEntry.getJSONObject("pokemon");
                String pokemonName = pokemon.getString("name");
                System.out.println("- " + pokemonName);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error durante la solicitud de habilidad: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON de habilidad: " + e.getMessage());
        }
    }
}
