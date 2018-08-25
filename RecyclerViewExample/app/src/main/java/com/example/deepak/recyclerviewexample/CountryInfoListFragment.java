package com.example.deepak.recyclerviewexample;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by deepak on 17/08/18.
 */

public class CountryInfoListFragment extends Fragment {

    ArrayList<CountryInfo> countryInfoArrayList = new ArrayList<>();
    RecyclerView recyclerView = null;


    public CountryInfoListFragment(){
        loadData();
    }

    private void loadData() {
        new RetrieveJsonTask().execute("https://restcountries.eu/rest/v2/all");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view  =  inflater.inflate(R.layout.country_info_list,container,false);
       return view;
    }

    public static String GET(String urlString) {

        String jsonData = null;
        HttpURLConnection urlConnection =null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            jsonData = convertInputStreamToString(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

        return jsonData;
    }

    // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public void loadCountryInfo(String jsonString){
        if(jsonString==null || jsonString.length()<=0)
        {
            Log.e(MainActivity.tag,"Failed to retrieve JSON");
            return;
        }
        else
        {
//            Log.d(MainActivity.tag,"Country Info In JSON = \n" + jsonString);
        }

        try {
            JSONArray array = new JSONArray(jsonString);
            if(array!=null)
            {
//                Log.d(MainActivity.tag," Object:" +object.toString());
                for(int i=0;i<array.length();i++)
                {
                    JSONObject object = array.getJSONObject(i);
//                    Log.d(MainActivity.tag,"Index " + i + ": "+object.toString());
                    String countryName = object.getString("name");
                    String currenyName = object.getJSONArray("currencies").getJSONObject(0).getString("name");
                    String languageName = object.getJSONArray("languages").getJSONObject(0).getString("name");

//                    Log.d(MainActivity.tag,"Country Name : " + object.getString("name") + " - Currencies :" +object.getString("currencies") +
//                    " - Languages :" +object.getString("languages"));

                    Log.d(MainActivity.tag,"Country : "  +countryName + " - Currency : "+currenyName + " - Language: " + languageName);
                    CountryInfo countryInfo = new CountryInfo(countryName,currenyName,languageName);
                    countryInfoArrayList.add(countryInfo);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(countryInfoArrayList.size()>0)
        {
            loadRecyclerView();
        }

    }

    private void loadRecyclerView() {

        if(getView()==null)
            return;

        getView().findViewById(R.id.progressBar).setVisibility(View.GONE);

        recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(new MainAdapter());
        recyclerView.setHasFixedSize(true);
        setUpItemTouchHelper();
    }


    class MainAdapter extends RecyclerView.Adapter<CountryInfoHolder>{

        ArrayList<CountryInfo> itemsPendingRemoval;

        public MainAdapter(){
            itemsPendingRemoval = new ArrayList<>();
        }

        @Override
        public CountryInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.country_info_row,parent,false);
            CountryInfoHolder holder = new CountryInfoHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CountryInfoHolder holder, final int position) {

            final CountryInfo item = countryInfoArrayList.get(position);

            holder.name.setText(countryInfoArrayList.get(position).getName());
            holder.currency.setText(countryInfoArrayList.get(position).getCurrency());
            holder.language.setText(countryInfoArrayList.get(position).getLanguage());

            if (itemsPendingRemoval.contains(item)) {
                holder.removeButton.setVisibility(View.VISIBLE);
                holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notifyItemRemoved(position);
                        itemsPendingRemoval.clear();
                    }
                });
            }
            else
            {
                holder.removeButton.setVisibility(View.GONE);
                holder.removeButton.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return countryInfoArrayList.size();
        }

        public void pendingRemoval(int position) {
            clearPreviousPendingRemoval();

            final CountryInfo item = countryInfoArrayList.get(position);
            if (!itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.add(item);
                notifyItemChanged(position);
            }

        }

        void clearPreviousPendingRemoval(){
            for (int counter = 0; counter < itemsPendingRemoval.size(); counter++) {
                CountryInfo countryInfo =itemsPendingRemoval.get(counter);
                itemsPendingRemoval.remove(counter);
                int position =countryInfoArrayList.indexOf(countryInfo);
                if(position!=-1)
                {
                    notifyItemChanged(position);
                }
            }
        }

        public void remove(int position) {
            clearPreviousPendingRemoval();
            CountryInfo item = countryInfoArrayList.get(position);
            if (countryInfoArrayList.contains(item)) {
                countryInfoArrayList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public boolean isPendingRemoval(int position) {
            CountryInfo item = countryInfoArrayList.get(position);
            return itemsPendingRemoval.contains(item);
        }
    }

    class RetrieveJsonTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String jsonString  = GET(urls[0]);
            return jsonString;
        }

        protected void onPostExecute(String jsonString) {
            loadCountryInfo(jsonString);
        }
    }

    class CountryInfoHolder extends RecyclerView.ViewHolder {

        TextView name,language,currency;
        ImageButton removeButton;

        public CountryInfoHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.countryName);
            language = itemView.findViewById(R.id.languageName);
            currency = itemView.findViewById(R.id.currenyName);
            removeButton = itemView.findViewById(R.id.remove);
        }
    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.purple));
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                MainAdapter testAdapter = (MainAdapter)recyclerView.getAdapter();
                if (testAdapter.isPendingRemoval(position)) {

                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                MainAdapter adapter = (MainAdapter)recyclerView.getAdapter();

                Log.d(MainActivity.tag,"SwipeThreshlow : " +getSwipeThreshold(viewHolder) + " MoveThreshpold " + getMoveThreshold(viewHolder));
                adapter.remove(swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                if(isCurrentlyActive) {

                    if(dX<=-500) {
                        Log.d(MainActivity.tag, "onChildDraw dX: " + dX);

                        int position = viewHolder.getAdapterPosition();
                        MainAdapter adapter = (MainAdapter) recyclerView.getAdapter();
                        if (!adapter.isPendingRemoval(position)) {

                            adapter.pendingRemoval(position);
                        }
                    }
                }

                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
