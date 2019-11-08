package com.app.model.main.article;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static com.app.util.Constants.*;

public class SelectedArticle implements Parcelable
{
    private String title;
    private String contentModel;
    private String description;
    private String descriptionSource;
    private List<FetchArticleImages> fetchArticleImagesList;

    public SelectedArticle(JSONObject jsonObject, String pageId) {

        fetchArticleImagesList = new ArrayList<>();

        if(jsonObject.has(queryTag))
            if(!jsonObject.isNull(queryTag))
            {
                JSONObject queryJson = jsonObject.optJSONObject(queryTag);

                if (queryJson != null && queryJson.has(pagesTag))
                {
                    if (!queryJson.isNull(pagesTag))
                    {
                        JSONObject pagesJson = queryJson.optJSONObject(pagesTag);

                        if (pagesJson != null && pagesJson.has(pageId))
                        {
                            if (!pagesJson.isNull(pageId))
                            {
                                JSONObject pageIdJson = pagesJson.optJSONObject(pageId);

                                if(pageIdJson != null)
                                {
                                    if(pageIdJson.has(titleTag))
                                        if(!pageIdJson.isNull(titleTag))
                                            title = pageIdJson.optString(titleTag);

                                    if(pageIdJson.has(contentModelTag))
                                        if(!pageIdJson.isNull(contentModelTag))
                                            contentModel = pageIdJson.optString(contentModelTag);

                                    if(pageIdJson.has(descriptionTag))
                                        if(!pageIdJson.isNull(descriptionTag))
                                            description = pageIdJson.optString(descriptionTag);

                                    if(pageIdJson.has(descriptionSourcetag))
                                        if(!pageIdJson.isNull(descriptionSourcetag))
                                            descriptionSource = pageIdJson.optString(descriptionSourcetag);

                                    JSONArray imagesArray = pageIdJson.optJSONArray(imagesTag);

                                    if(imagesArray != null && imagesArray.length() >= 1)
                                    {
                                        for (int i = 0; i < imagesArray.length(); i++)
                                        {
                                            JSONObject imagesObject = imagesArray.optJSONObject(i);
                                            FetchArticleImages fetchArticleImages = new FetchArticleImages(imagesObject);
                                            fetchArticleImagesList.add(fetchArticleImages);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentModel() {
        return contentModel;
    }

    public void setContentModel(String contentModel) {
        this.contentModel = contentModel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionSource() {
        return descriptionSource;
    }

    public void setDescriptionSource(String descriptionSource) {
        this.descriptionSource = descriptionSource;
    }

    public List<FetchArticleImages> getFetchArticleImagesList() {
        return fetchArticleImagesList;
    }

    public void setFetchArticleImagesList(List<FetchArticleImages> fetchArticleImagesList) {
        this.fetchArticleImagesList = fetchArticleImagesList;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "SelectedArticle{" +
                "title='" + title + '\'' +
                "contentModel='" + contentModel + '\'' +
                "description='" + description + '\'' +
                "descriptionsource='" + descriptionSource + '\'' +
                ", mediaEntityList='" + fetchArticleImagesList + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(contentModel);
        dest.writeString(description);
        dest.writeString(descriptionSource);
        dest.writeTypedList(fetchArticleImagesList);
    }

    private SelectedArticle(Parcel in)
    {
        title = in.readString();
        contentModel = in.readString();
        description = in.readString();
        in.readTypedList(fetchArticleImagesList, FetchArticleImages.CREATOR);
    }

    public static final Creator<SelectedArticle> CREATOR = new Creator<SelectedArticle>() {
        @Override
        public SelectedArticle createFromParcel(Parcel in) {
            return new SelectedArticle(in);
        }

        @Override
        public SelectedArticle[] newArray(int size) {
            return new SelectedArticle[size];
        }
    };
}
