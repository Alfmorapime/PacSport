package com.example.projectpacsport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GameActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private ArrayList<Lineup> lineups = new ArrayList<>();
    private ArrayList<Player> homeTeamPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Getting yesterday's date for API requests
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String dateString = dateFormat.format(cal.getTime());

        Bundle bundle = getIntent().getExtras();
        Result result = (Result) bundle.getSerializable("Result");

        SharedPreferences sharedPref = getSharedPreferences("User", Context.MODE_PRIVATE);
        final String league = sharedPref.getString("League", "nba");

        if(league == "mlb")
        {
            String url = "http://api.mysportsfeeds.com/v2.1/pull/" + league + "/2019-regular/games/" + "20190506" + "-"
                    + result.getAwayTeam().getAbbreviation() + "-" + result.getHomeTeam().getAbbreviation() + "/lineup.json";
            Log.e("url", url);
            getPlayers(url);
        }
        else if(league == "nhl")
        {
            String url = "http://api.mysportsfeeds.com/v2.1/pull/" + league + "/2019-2020/games/" + "20200308" + "-"
                    + result.getAwayTeam().getAbbreviation() + "-" + result.getHomeTeam().getAbbreviation() + "/lineup.json";
            Log.e("url", url);
            getPlayers(url);
        }
        else
        {
            String url = "http://api.mysportsfeeds.com/v2.1/pull/" + league + "/current/games/" + "20200308" + "-"
                    + result.getAwayTeam().getAbbreviation() + "-" + result.getHomeTeam().getAbbreviation() + "/lineup.json";
            Log.e("url", url);
            getPlayers(url);
        }



        ImageView imgAwayTLogo = findViewById(R.id.imgAwayTeamLogo);
        ImageView imgHomeTLogo = findViewById(R.id.imgHomeTeamLogo);
        TextView tvAwayTName = findViewById(R.id.tvAwayTeamName);
        TextView tvHomeTName = findViewById(R.id.tvHomeTeamName);
        TextView tvAwayTScore = findViewById(R.id.tvAwayTeamScore);
        TextView tvHomeTScore = findViewById(R.id.tvHomeTeamScore);

        GlideToVectorYou.justLoadImage(this, Uri.parse(result.getAwayTeam().getLogo()), imgAwayTLogo);
        tvAwayTName.setText(result.getAwayTeam().getName());
        tvAwayTScore.setText(String.valueOf(result.getAwayTeam().getScore()));
        GlideToVectorYou.justLoadImage(this, Uri.parse(result.getHomeTeam().getLogo()), imgHomeTLogo);
        tvHomeTName.setText(result.getHomeTeam().getName());
        tvHomeTScore.setText(String.valueOf(result.getHomeTeam().getScore()));

        //Alfredo Code, preparation of all the data to be send to TeamActivity
        LinearLayout linearAway = findViewById(R.id.LineAwayResult);
        LinearLayout linearHome = findViewById(R.id.LineHomeResult);

        final String teamNameAway = result.getAwayTeam().getName();
        final String teamAwayLogo = result.getAwayTeam().getLogo();
        final String teamAbbreviationAway = result.getAwayTeam().getAbbreviation();

        final String teamNameHome = result.getHomeTeam().getName();
        final String teamHomeLogo = result.getHomeTeam().getLogo();
        final String teamAbbreviationHome = result.getHomeTeam().getAbbreviation();

        linearAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GameActivity.this, TeamActivity.class);
                Bundle bundleTeam = new Bundle();
                bundleTeam.putString("team", teamNameAway);
                bundleTeam.putString("logo", teamAwayLogo);
                bundleTeam.putString("abre", teamAbbreviationAway);
                bundleTeam.putString("league", league);
                intent.putExtras(bundleTeam);
                startActivity(intent);
            }
        });

        linearHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, TeamActivity.class);
                Bundle bundleTeam = new Bundle();
                bundleTeam.putString("team", teamNameHome);
                bundleTeam.putString("logo", teamHomeLogo);
                bundleTeam.putString("abre", teamAbbreviationHome);
                bundleTeam.putString("league", league);
                intent.putExtras(bundleTeam);
                startActivity(intent);
            }
        });

        // bundle pass string players
        bundle.putString("League", league);
    }

    public void getPlayers(String url) {
        VolleyService request = new VolleyService(this);
        request.executeRequest(url, new VolleyService.VolleyCallback() {
            @Override
            public void getResponse(String response) {
                try {
                    Lineup lineup;
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray teamLineups = jsonObj.getJSONArray("teamLineups");
                    JSONObject awayTeam = teamLineups.getJSONObject(0);
                    JSONObject awayTeamLineups = awayTeam.getJSONObject("actual");
                    JSONArray awayTeamLineupsArray = awayTeamLineups.getJSONArray("lineupPositions");
                    for (int i = 0; i < awayTeamLineupsArray.length(); i++) {
                        Player awayTeamPlayer = new Player();
                        JSONObject playerObj = awayTeamLineupsArray.getJSONObject(i);
                        JSONObject player = playerObj.optJSONObject("player");
                        if (player != null) {
                            lineup = new Lineup();
                            awayTeamPlayer.setId(player.getInt("id"));
                            awayTeamPlayer.setFirstName(player.getString("firstName"));
                            awayTeamPlayer.setLastName(player.getString("lastName"));
                            lineup.setAway(awayTeamPlayer);
                            lineups.add(lineup);
                        }
                    }

                    JSONObject homeTeam = teamLineups.getJSONObject(1);
                    JSONObject homeTeamLineups = homeTeam.getJSONObject("actual");
                    JSONArray homeTeamLineupsArray = homeTeamLineups.getJSONArray("lineupPositions");
                    for (int i = 0; i < homeTeamLineupsArray.length(); i++) {
                        Player homeTeamPlayer = new Player();
                        JSONObject playerObj = homeTeamLineupsArray.getJSONObject(i);
                        JSONObject player = playerObj.optJSONObject("player");
                        if (player != null) {
                            homeTeamPlayer.setId(player.getInt("id"));
                            homeTeamPlayer.setFirstName(player.getString("firstName"));
                            homeTeamPlayer.setLastName(player.getString("lastName"));
                            homeTeamPlayers.add(homeTeamPlayer);
                        }
                    }

                    int lineupsSize = lineups.size();
                    if (homeTeamPlayers.size() >= lineups.size()) {
                        for (int i = 0; i < lineups.size(); i++) {
                            lineups.get(i).setHome(homeTeamPlayers.get(i));
                        }
                        for (int i = lineupsSize; i < homeTeamPlayers.size(); i++) {
                            lineup = new Lineup();
                            lineup.setAway(new Player("", ""));//avoid null object reference
                            lineup.setHome(homeTeamPlayers.get(i));
                            lineups.add(lineup);
                        }
                    } else {
                        for (int i = 0; i < homeTeamPlayers.size(); i++) {
                            lineups.get(i).setHome(homeTeamPlayers.get(i));
                        }
                        for (int i = homeTeamPlayers.size(); i < lineupsSize; i++) {
                            lineups.get(i).setHome(new Player("", "")); //avoid null object reference
                        }
                    }

                    // Create newInstance of Fragment  dummy
                    Fragment lineupFragment = GameLineupFragment.newInstance();
                    Fragment highlightsFragment = GameHighLightsFragment.newInstance();

                    // Create bundles for passing data from the Activity to Fragments
                    Bundle bundleLineups = new Bundle();
                    Bundle bundleHighlight = getIntent().getExtras();

                    bundleLineups.putSerializable("Lineups", lineups);

                    // Pass bundles to the fragments
                    lineupFragment.setArguments(bundleLineups);
                    highlightsFragment.setArguments(bundleHighlight);

                    // SectionPageAdapter
                    mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
                    mSectionPageAdapter.addFragment(lineupFragment, "LineUps");
                    mSectionPageAdapter.addFragment(highlightsFragment, "HighLights");

                    // ViewPager init
                    mViewPager = findViewById(R.id.view_pager);
                    mViewPager.setAdapter(mSectionPageAdapter);

                    // TabLayout init
                    TabLayout tabLayout = findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(mViewPager);

                } catch (JSONException ex) { // catch for the JSON parsing error
                    Log.e("JSON: ", ex.getMessage());
                } catch (Exception ex) {
                    Log.e("Request: ", ex.getMessage());
                }
            }
        });
    }
}