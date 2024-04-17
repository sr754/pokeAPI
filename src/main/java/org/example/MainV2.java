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
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainV2 {

    private static final HttpClient cliente = HttpClient.newHttpClient();
    private static final HashSet<String> pokemonConsultados = new HashSet<>();

    public static void main(String[] args) {
        JSONObject pokemonInfo;
        String nombrePokemon;
        int profundidad;

        nombrePokemon = Utils.string("Introduce el nombre de un Pokémon: ").toLowerCase();
        profundidad = Utils.integer("Introduce el número de niveles de profundidad para la búsqueda: ");

        pokemonInfo = solicitarPokemon(nombrePokemon);
        if (pokemonInfo != null) {
            mostrarInformacionPokemon(pokemonInfo);
            Set<String> resultados = new HashSet<>();
            buscarPokemons(pokemonInfo, profundidad, resultados, 1);
        } else {
            System.out.println("El Pokémon no existe.");
        }
    }

    private static JSONObject solicitarPokemon(String nombrePokemon) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + nombrePokemon;
        return realizarSolicitudHTTP(url);
    }

    private static JSONObject realizarSolicitudHTTP(String url) {
        url = url.trim();

        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() == 200) {
                return new JSONObject(respuesta.body());
            } else {
                System.out.println("No se pudo obtener información del servidor: " + respuesta.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error de comunicación: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON: " + e.getMessage());
        }
        return null;
    }

    private static void mostrarInformacionPokemon(JSONObject pokemonInfo) {
        try {
            String nombre = pokemonInfo.getString("name");
            System.out.println(nombre + " tiene las siguientes habilidades:");
            JSONArray habilidadesArray = pokemonInfo.getJSONArray("abilities");

            for (int i = 0; i < habilidadesArray.length(); i++) {
                JSONObject habilidad = habilidadesArray.getJSONObject(i).getJSONObject("ability");
                String urlHabilidad = habilidad.getString("url");
                String nombreHabilidadTraducido = obtenerHabilidadTraducida(urlHabilidad, "es");
                String nombreHabilidad = habilidad.getString("name");

                if (nombreHabilidadTraducido != null) {
                    System.out.println("- " + nombreHabilidadTraducido);
                } else {
                    System.out.println("- " + nombreHabilidad);
                }
            }
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON: " + e.getMessage());
        }
    }

    private static void buscarPokemons(JSONObject pokemonInfo, int profundidad, Set<String> resultados,
            int nivelActual) {
        if (profundidad < 1 || pokemonInfo == null)
            return;

        Set<String> pokemonUnicos = obtenerPokemonsConMismasHabilidades(pokemonInfo);
        resultados.addAll(pokemonUnicos);

        if (nivelActual <= profundidad) {
            System.out.println("\nPokémon con habilidades similares en el nivel de búsqueda " + nivelActual + ":");
            mostrarResultados(resultados);
            resultados.clear();

            for (String pokemon : pokemonUnicos) {
                if (!pokemonConsultados.contains(pokemon)) {
                    pokemonConsultados.add(pokemon);
                    JSONObject info = solicitarPokemon(pokemon);
                    if (info != null) {
                        buscarPokemons(info, profundidad, resultados, nivelActual + 1);
                    }
                }
            }
        }
    }

    private static void mostrarResultados(Set<String> resultados) {
        List<String> sortedPokemon = new ArrayList<>(resultados);
        Collections.sort(sortedPokemon);
        sortedPokemon.forEach(System.out::println);
        System.out.println("Total: " + sortedPokemon.size() + " Pokémon(s)");
    }


    private static Set<String> obtenerPokemonsConMismasHabilidades(JSONObject pokemonInfo) {
        HashSet<String> pokemonUnicos = new HashSet<>();
        try {
            JSONArray habilidadesArray = pokemonInfo.getJSONArray("abilities");
            for (int i = 0; i < habilidadesArray.length(); i++) {
                JSONObject habilidad = habilidadesArray.getJSONObject(i).getJSONObject("ability");
                String urlHabilidad = habilidad.getString("url");
                agregarPokemonsConHabilidad(urlHabilidad, pokemonUnicos);
            }
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON: " + e.getMessage());
        }
        return pokemonUnicos;
    }

    private static void agregarPokemonsConHabilidad(String urlHabilidad, HashSet<String> pokemonUnicos) {
        JSONObject detallesHabilidad = realizarSolicitudHTTP(urlHabilidad);
        if (detallesHabilidad == null)
            return;

        try {
            JSONArray pokemonArray = detallesHabilidad.getJSONArray("pokemon");
            for (int i = 0; i < pokemonArray.length(); i++) {
                JSONObject pokemonEntry = pokemonArray.getJSONObject(i);
                JSONObject pokemon = pokemonEntry.getJSONObject("pokemon");
                String pokemonName = pokemon.getString("name");
                pokemonUnicos.add(pokemonName);
            }
        } catch (JSONException e) {
            System.err.println("Error al analizar JSON de habilidad: " + e.getMessage());
        }
    }

    private static String obtenerHabilidadTraducida(String urlHabilidad, String codigoIdioma) {
        JSONObject habilidadDetalles = realizarSolicitudHTTP(urlHabilidad);
        if (habilidadDetalles == null)
            return null;

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
        return null;
    }

}