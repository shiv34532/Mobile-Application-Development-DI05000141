package com.example.arpitapplication;

import com.example.arpitapplication.R;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Practical11Backend extends AppCompatActivity {

    private static final String NEWS_FEED_URL = "https://timesofindia.indiatimes.com/rssfeedstopstories.cms";

    // ==========================================
    // 1. RETROFIT API INTERFACE
    // ==========================================
    public interface ApiService {
        @GET("v1/api.json")
        Call<NewsResponse> getNews(@Query("rss_url") String rssUrl);
    }

    // ==========================================
    // 2. GSON DATA MODELS
    // ==========================================
    public static class NewsResponse {
        @SerializedName("status")
        public String status;

        @SerializedName("items")
        public List<NewsItem> items;
    }

    public static class NewsItem {
        @SerializedName("title")
        public String title;

        @SerializedName("pubDate")
        public String pubDate;

        @SerializedName("link")
        public String link;

        @SerializedName("author")
        public String author;

        @SerializedName("thumbnail")
        public String thumbnail;

        @SerializedName("description")
        public String description;

        @SerializedName("enclosure")
        public Enclosure enclosure;

        public static class Enclosure {
            @SerializedName("link")
            public String link;
        }

        public String getCleanDescription() {
            if (description == null || description.trim().isEmpty()) {
                return "Tap here to read full breaking news coverage on the web...";
            }
            String clean = description.replaceAll("<[^>]*>", "").trim();
            return clean.isEmpty() ? "Tap here to read full breaking news coverage on the web..." : clean;
        }

        public String getImageUrl() {
            if (enclosure != null && enclosure.link != null && !enclosure.link.trim().isEmpty()) {
                return enclosure.link.trim();
            }
            if (thumbnail != null && !thumbnail.trim().isEmpty()) {
                return thumbnail.trim();
            }
            return "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=400";
        }
    }

    // ==========================================
    // 3. UI STATE & ACTIVITY LOGIC
    // ==========================================
    private Button btnFetchPosts;
    private ProgressBar pbLoading;
    private TextView tvStatusBadge;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvPosts;
    private NewsAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical11_frontend);

        // Bind Views
        btnFetchPosts = findViewById(R.id.btnFetchPosts);
        pbLoading = findViewById(R.id.pbLoading);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rvPosts = findViewById(R.id.rvPosts);

        // Configure SwipeRefresh spinner colors
        swipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#B91C1C"),
                Color.parseColor("#DC2626"),
                Color.parseColor("#EA580C")
        );

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.rss2json.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Setup RecyclerView & Adapter
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(new OnNewsClickListener() {
            @Override
            public void onNewsClick(NewsItem newsItem) {
                if (newsItem.link != null && !newsItem.link.trim().isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.link));
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(Practical11Backend.this, "Unable to open browser: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Practical11Backend.this, "Article link not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvPosts.setAdapter(adapter);

        // Centered Fetch Button Listener
        btnFetchPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchNewsFromApi(true);
            }
        });

        // Swipe-to-Refresh Listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNewsFromApi(false);
            }
        });

        // Initial fetch on screen load
        fetchNewsFromApi(true);
    }

    private void fetchNewsFromApi(final boolean isManualButtonClick) {
        if (isManualButtonClick) {
            pbLoading.setVisibility(View.VISIBLE);
            btnFetchPosts.setEnabled(false);
        }
        tvStatusBadge.setText("⏳ Fetching live news from server...");

        apiService.getNews(NEWS_FEED_URL).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (isManualButtonClick) {
                    pbLoading.setVisibility(View.GONE);
                    btnFetchPosts.setEnabled(true);
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (response.isSuccessful() && response.body() != null && response.body().items != null) {
                    List<NewsItem> newsList = response.body().items;
                    adapter.setNews(newsList);
                    tvStatusBadge.setText("🟢 " + newsList.size() + " Live Headlines Available");
                    Toast.makeText(Practical11Backend.this, "Updated " + newsList.size() + " English headlines!", Toast.LENGTH_SHORT).show();
                } else {
                    tvStatusBadge.setText("🔴 Server error: Code " + response.code());
                    Toast.makeText(Practical11Backend.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                if (isManualButtonClick) {
                    pbLoading.setVisibility(View.GONE);
                    btnFetchPosts.setEnabled(true);
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                tvStatusBadge.setText("🔴 Network error. Check internet.");
                Toast.makeText(Practical11Backend.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ==========================================
    // 4. CLICK LISTENER INTERFACE
    // ==========================================
    public interface OnNewsClickListener {
        void onNewsClick(NewsItem newsItem);
    }

    // ==========================================
    // 5. RECYCLERVIEW ADAPTER
    // ==========================================
    public static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

        private List<NewsItem> newsList = new ArrayList<>();
        private final OnNewsClickListener listener;

        public NewsAdapter(OnNewsClickListener listener) {
            this.listener = listener;
        }

        public void setNews(List<NewsItem> news) {
            this.newsList = news != null ? news : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.practical11_item_post, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
            NewsItem item = newsList.get(position);

            // Source Badge
            String source = (item.author != null && !item.author.trim().isEmpty()) ? item.author.trim() : "India & World News";
            if (source.length() > 22) {
                source = source.substring(0, 20) + "...";
            }
            holder.tvPostIdBadge.setText("Live • " + source);

            // Headline & Description
            holder.tvPostTitle.setText(item.title != null ? item.title.trim() : "Breaking News");
            holder.tvPostBody.setText(item.getCleanDescription());

            // Date & Tap Prompt
            String dateText = (item.pubDate != null && item.pubDate.length() >= 10) ? item.pubDate.substring(0, 10) : "";
            if (!dateText.isEmpty()) {
                holder.tvNewsMeta.setText(dateText + " • Tap to read full story ↗");
            } else {
                holder.tvNewsMeta.setText("Tap to read full story ↗");
            }

            // Server Image with Glide
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_news_placeholder)
                    .error(R.drawable.ic_news_placeholder)
                    .centerCrop()
                    .into(holder.ivNewsImage);

            // Click -> Open Browser
            holder.cardNewsItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onNewsClick(item);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsList != null ? newsList.size() : 0;
        }

        public static class NewsViewHolder extends RecyclerView.ViewHolder {
            CardView cardNewsItem;
            TextView tvPostIdBadge, tvPostTitle, tvPostBody, tvNewsMeta;
            ImageView ivNewsImage;

            public NewsViewHolder(@NonNull View itemView) {
                super(itemView);
                cardNewsItem = itemView.findViewById(R.id.cardNewsItem);
                tvPostIdBadge = itemView.findViewById(R.id.tvPostIdBadge);
                tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
                tvPostBody = itemView.findViewById(R.id.tvPostBody);
                tvNewsMeta = itemView.findViewById(R.id.tvNewsMeta);
                ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
            }
        }
    }
}
