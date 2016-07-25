package com.enterprises.wayne.yugicards;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    /* constants */
    public final String LOG_TAG = MainActivity.class.getSimpleName();

    /* UI */
    ListView listViewCards;
    ArrayAdapter<String> adapterCards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference the list view
        listViewCards = (ListView) findViewById(R.id.list_view_cards);

        // create an adapter with empty data
        ArrayList<String> emptyData = new ArrayList<>();
        adapterCards = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                emptyData
        );

        // bind the adapter to the list view
        listViewCards.setAdapter(adapterCards);

        loadData();
    }

    /**
     * downloads the cards data from the backend API
     */
    private void loadData()
    {
        // show a progress dialog
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));

        // make a GET requests
        String url = "https://greek-302.herokuapp.com/cards/monster";
        Ion.with(this)
                .load("GET", url)
                .asString()
                .setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        // dismiss the dialog
                        progressDialog.dismiss();

                        // check error
                        if (e != null)
                        {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // parse the data
                        List<Card> cards = ParsingUtils.parseResponse(result);
                        if (cards == null)
                        {
                            Toast.makeText(MainActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // convert to a list of strings
                        List<String> cardsString = new ArrayList<String>();
                        for (Card card : cards)
                            cardsString.add(card.getTitle());

                        // add to the adapter
                        adapterCards.clear();
                        adapterCards.addAll(cardsString);
                    }
                });
    }
}
