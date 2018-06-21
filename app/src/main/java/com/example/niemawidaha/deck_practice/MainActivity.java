package com.example.niemawidaha.deck_practice;

import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.niemawidaha.deck_practice.controller.CardDeckAdapter;
import com.example.niemawidaha.deck_practice.model.CardModel;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CardDeckAdapter.ItemClickListener {

    // private members:
    private final static String LOG_TAG = "LOG_TAG: Main Activity";
    private TextView m_tv_NumberOfCardsRemaining;
    private Button m_btn_ShuffleNewDeck;
    private EditText m_et_SpecifiedCards;
    private Button m_btn_DrawCards;
    private CardDeckAdapter mCardDeckAdapter;


    // values:
    private List<CardModel> cardDeck;
    private String deck_id; // stores the DECK ID
    private int currentCount; // stores the current count of the remaining cards
    private int remainingCards = 52; // stores the DECK ID
    private int numOfUserSelectedValue; // the value user selects after validation

    public static final String ShuffleNewDeckEndPoint = "https://deckofcardsapi.com/";

    /**
     * networking:
     */
    final String CARD_DECK_BASE_URL = "https://deckofcardsapi.com/api/deck/new/shuffle/";
    String deckOfCards_BASE_URL = "https://deckofcardsapi.com/api/deck/" + deck_id + "/draw/?";
    final String QUERY_PARAM = "count"; // Parameter for the search string


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views:
        findViews();

        //buildUp52CardsURI(); // builds a URI for the 52 cards
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /**
     * findsview:
     */
    public void findViews() {
        m_tv_NumberOfCardsRemaining = findViewById(R.id.tv_cards_remaining);
        m_btn_ShuffleNewDeck = findViewById(R.id.btn_shuffle_new_deck);
        m_et_SpecifiedCards = findViewById(R.id.et_draw_cards);
        m_btn_DrawCards = findViewById(R.id.btn_display_drawn_cards);

        setUpRV();
    }


    /**
     * setup recycler view:
     */
    public void setUpRV() {

        // set up RV:
        int numOfColumns = 2;

        RecyclerView recyclerView = findViewById(R.id.rv_user_cards);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numOfColumns));
        mCardDeckAdapter = new CardDeckAdapter(this, cardDeck);
        mCardDeckAdapter.setClickListener(this);



    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked card " + mCardDeckAdapter.getItem(position) );
    }


    //}

    /**
     * isCardDeckSelectedEmpty():
     * helper method for checking if the card deck selected was a null value
     *
     * @param mUserInput
     * @return
     */
    public boolean isCardDeckSelectedEmpty(EditText mUserInput) {

        return mUserInput.length() == 0;

    }

    /**
     * drawDeck():
     *
     * @param view
     */
    public void drawDeck(View view) {

        // if user passes validation:
        // the value needed to draw is the numOfUserSelected from the et_draw_cards
        // grab the user selected value for the deck of cards endpoint

        boolean isUserValueValid;

        int numOfUserSelection = Integer.valueOf(m_et_SpecifiedCards.getText().toString()); // the value specified by the user on correct check:


        if (isCardDeckSelectedEmpty(m_et_SpecifiedCards) || (numOfUserSelection < 0)) {

            Toast.makeText(MainActivity.this, "You must draw atleast one card", Toast.LENGTH_LONG).show();

            isUserValueValid = false;

        } else if (numOfUserSelection > remainingCards) {

            String error = "There are only " + String.valueOf(remainingCards) + " cards remaining";
            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();

            isUserValueValid = false;

        } else {

            // if the value is valid then store the value selected by user in a global var:
            numOfUserSelectedValue = numOfUserSelection;

            Toast.makeText(MainActivity.this, "Thats a valid value", Toast.LENGTH_LONG).show();

            isUserValueValid = true;

        }

        // grab the value of the deck_id for the endpoint:
        // make an HTTP GET request ot the "draw cards" API endpoint
        // https://deckofcardsapi.com/api/deck/{deck_id}/draw/?count={num_cards}
        // take the value and make an HTTP get request: query parameter count:
        // endpoint: https://deckofcardsapi.com/api/deck/{deck_id}/draw/?count={num_of_cards}


    }

    /**
     * shuffleNewDeck():
     *
     * @param view
     */
    public void shuffleNewDeck(View view) throws IOException {

        // build query URI:
        Uri builtURI = Uri.parse(CARD_DECK_BASE_URL)
                .buildUpon()
                .build();

        // convert the Card Deck URI to a string:
        String cardDeckURL = builtURI.toString();

        // CONNECT AND DOWNLOAD DATA:
        // pass this string into the downloadURL()
        String cardDeckResponse = downloadUrl(cardDeckURL);

        // get the JSON OBJECT: deck_id
        try {
            JSONObject fullData = new JSONObject(cardDeckResponse);
            deck_id = fullData.getString("deck_id");
            //String success = fullData.getString("success");
            //String remaining = fullData.getString("remaining");

            m_tv_NumberOfCardsRemaining.setText(deck_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        /**
         * query for the 52 cards:
         */
        // get the string:
        //https://deckofcardsapi.com/api/deck/5966wkl4tcmd/draw/?count=52
        // query: count
        // make a new string out of the id:
        //String deckOfCards_BASE_URL = "https://deckofcardsapi.com/api/deck/" + deck_id + "/draw/?";

        // build up the query URI, limiting results to 52 cards:
        Uri secondBuiltURI = Uri.parse(deckOfCards_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, "52")
                .build();

        // convert the second URI to a string:
        String cardDeck52URL = secondBuiltURI.toString();

        // CONNECT & DOWNLOAD DATA:
        // pass this string to downloadURL
        String cardDeck52Response = downloadUrl(cardDeck52URL);


        try {
            JSONObject fullData = new JSONObject(cardDeck52Response); // gets the full json object

            // to get the cards json array:
            JSONArray fiftyTwoCards = new JSONArray(fullData.getJSONArray("cards"));

            // get the JSON OBJECT: card deck of 52 cards:
            // for a single card:

            // since u have a list of cards:
            for ( int i = 0 ; i < 52; i ++ ) {
                for (CardModel card : cardDeck) {

                    // create a json object and get the values from the json array:
                    JSONObject cardJSON = fiftyTwoCards.getJSONObject(i);

                    // get these specific values from the json object
                    String suit = cardJSON.getString("suit"); // suit
                    String image = cardJSON.getString("image"); // image
                    String code = cardJSON.getString("code");// code
                    String value = cardJSON.getString("value");// value

                    // add the values to the card:
                    card.setSuit(suit);
                    card.setImage(image);
                    card.setCode(code);
                    card.setValue(value);

                    // add the card to the list
                    cardDeck.add(card);

                    // data supposedely full
                }

            }  // ends try catch

            deck_id = fullData.getString("deck_id");

            m_tv_NumberOfCardsRemaining.setText(deck_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * updateCardsRemainingText():
     */
    public void updateCardsRemainingText() {


    }

    /**
     * downloadURL():
     */
    public String downloadUrl(String cardDeckUrl) throws IOException {

        InputStream inputStream = null;

        // Only dislay the first 100 characters of the
        // web page content:
        int len = 100;


        HttpURLConnection connection = null;
        try {

            URL url = new URL(cardDeckUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);

            // Start the Query:
            connection.connect();

            int response = connection.getResponseCode();

            Log.d(LOG_TAG, "The response is" + response);

            inputStream = connection.getInputStream();

            // conver the input stream into a string
            String contentAsString = convertInputToString(inputStream, len);
            return contentAsString;

            // Close the InputStream and connectoin
        } finally {

            connection.disconnect();

            if (inputStream != null) {
                inputStream.close();
            }
        } // ends try-finally for url connection
    }

    /**
     * convertInputToString(): converts the InputStream to a string so tht
     * the activity can display it, the method uses an InputStreamReader instance
     * to read bytes and decode them into characters.
     */
    public String convertInputToString(InputStream stream, int length) throws IOException {

        Reader reader = null;

        reader = new InputStreamReader(stream, "UTF-8");

        char[] buffer = new char[length];

        reader.read(buffer);

        return new String(buffer);


    }


    /**
     * this method builds up the Query URI, limiting results to 52 cards
     */
    public void buildUp52CardsURI() {

    }

}
