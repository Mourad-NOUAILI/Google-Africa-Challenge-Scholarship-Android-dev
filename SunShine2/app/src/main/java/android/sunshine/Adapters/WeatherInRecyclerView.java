package android.sunshine.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sunshine.MainActivity;
import android.sunshine.R;
import android.sunshine.Utilities.NetworkUtils;
import android.sunshine.Utilities.SharedPreferenceUtils;
import android.sunshine.data.WeatherEntry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class WeatherInRecyclerView
        extends RecyclerView.Adapter<WeatherInRecyclerView.WeatherViewHolder>{

    WeatherEntry[] weather_data;

    Context context;

    final private a_day_weather_click a_day_weather_click_listener;

    private boolean use_today_layout;
    int layout_id;


    public WeatherInRecyclerView(Context context, a_day_weather_click listener) {
        this.context = context;
        this.a_day_weather_click_listener = listener;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 1: {
                layout_id = R.layout.layout_for_today_recycler_view;
                break;
            }
            case 0: {
                layout_id = R.layout.layout_for_recycler_view;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);


        }

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout_id, parent, false);
        view.setFocusable(true);
        WeatherViewHolder view_holder = new WeatherViewHolder(view);

        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (weather_data == null)
            return 0;
        return weather_data.length;
    }

    public void set_weather_data(WeatherEntry[] wd) {
        weather_data = wd;
        notifyDataSetChanged();
    }


    class WeatherViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView weather_data_text_view;
        TextView description_text_view;
        TextView min_temp_text_view;
        TextView max_temp_text_view;

        ImageView weather_data_image_view;

        AsyncTask load_image_background;

        Bitmap bitmap;




        public WeatherViewHolder(View itemView) {
            super(itemView);
            weather_data_text_view = (TextView)
                    itemView.findViewById(R.id.weather_forecast_textview_in_recyclerview);
            weather_data_image_view = (ImageView)
                    itemView.findViewById(R.id.weather_forecast_imageview_in_recyclerview);

            description_text_view = (TextView)
                    itemView.findViewById(R.id.descrption_textview_in_recycler_view);

            min_temp_text_view = (TextView)
                    itemView.findViewById(R.id.min_temp_textview_in_recycler_view);

            max_temp_text_view = (TextView)
                    itemView.findViewById(R.id.max_temp_textview_in_recycler_view);

            itemView.setOnClickListener(this);
        }

        void bind(final int position){


            String temp_unit = "°";
            if (SharedPreferenceUtils.is_imperial(context))
                temp_unit = "F";



            weather_data_text_view.setText(weather_data[position].getDate_time_txt());
            description_text_view.setText(weather_data[position].getDescription());


            min_temp_text_view.setText(weather_data[position].getTemp_min()+temp_unit);
            max_temp_text_view.setText(weather_data[position].getTemp_max()+temp_unit);


            load_image_background = new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] objects) {
                    bitmap = null;
                    if (weather_data != null && weather_data.length > 0) {
                        String url_string = "http://openweathermap.org/img/w/"
                                + weather_data[position].getIcon()
                                + ".png";
                        bitmap = NetworkUtils.load_icon(url_string);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    if (bitmap != null)
                        weather_data_image_view.setImageBitmap(bitmap);
                }
            };

            load_image_background.execute();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            a_day_weather_click_listener.on_a_day_weather_click(position);

        }
    }

    public interface a_day_weather_click{
        void on_a_day_weather_click(int position);
    }

    @Override
    public int getItemViewType(int position) {
        use_today_layout = context.getResources().getBoolean(R.bool.use_today_layout);
        if (use_today_layout && position == 0)
            return 1;
        else
            return 0;
    }

}
