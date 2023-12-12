package com.example.examapplication;

import android.util.Log;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResearchFragment extends Fragment {

    private static final String API_KEY = "AIzaSyDI0ejE9j2HxzSxY0bdLVHvO5iqcpgKtJA";
    private static final String SEARCH_ENGINE_ID = "1732f3bf1a653426d";
    private EditText editTextSearch;
    private Button searchButton;
    private WebView resultWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_research, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        searchButton = view.findViewById(R.id.searchButton);
        resultWebView = view.findViewById(R.id.resultWebView);

        // Abilita l'esecuzione di JavaScript nel WebView
        resultWebView.getSettings().setJavaScriptEnabled(true);

        resultWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Abilita la navigazione all'interno del WebView
                view.loadUrl(url);
                return true;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esegui la ricerca quando il pulsante viene premuto
                String searchTerm = editTextSearch.getText().toString();
                Log.d("SearchFragment", "Termine di ricerca: " + searchTerm);
                performSearch(searchTerm);
            }
        });

        return view;
    }

    private void performSearch(String query) {
        // Mostra un indicatore di caricamento durante la ricerca
        // E nascondilo quando la ricerca è completa
        resultWebView.loadData("Ricerca in corso...", "text/html", "UTF-8");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/customsearch/v1?key=AIzaSyDI0ejE9j2HxzSxY0bdLVHvO5iqcpgKtJA&cx=1732f3bf1a653426d&q=carica")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleCustomSearchApi searchApi = retrofit.create(GoogleCustomSearchApi.class);
        Call<SearchResponse> call = searchApi.search(API_KEY, SEARCH_ENGINE_ID, query);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    List<SearchResponse.Item> items = response.body().getItems();

                    if (items != null && !items.isEmpty()) {
                        // Processa i risultati
                        for (SearchResponse.Item item : items) {
                            Log.d("SearchFragment", "Titolo: " + item.getTitle() + ", Link: " + item.getLink());
                        }
                    } else {
                        // Nessun risultato trovato
                        Log.d("SearchFragment", "Nessun risultato trovato");
                    }
                } else {
                    // Gestisci errori di risposta HTTP
                    int statusCode = response.code();
                    Log.e("SearchFragment", "Errore nella ricerca. Codice: " + statusCode);

                    // Aggiungi log per visualizzare il corpo della risposta (se disponibile)
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("SearchFragment", "Corpo dell'errore: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // Gestisci errori di rete
                resultWebView.loadData("Errore di rete", "text/html", "UTF-8");
            }
        });
    }
}