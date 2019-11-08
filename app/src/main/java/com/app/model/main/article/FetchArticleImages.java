package com.app.model.main.article;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import static com.app.util.Constants.titleTag;

public class FetchArticleImages implements Parcelable
{
    private String imageTitle;

    FetchArticleImages(JSONObject jsonObject)
    {
        if (jsonObject.has(titleTag))
            if (!jsonObject.isNull(titleTag))
                imageTitle = jsonObject.optString(titleTag);
    }

    public String getTitle() {
        return imageTitle;
    }

    public void setTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "FetchArticleImages{" +
                "imageTitle='" + imageTitle + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageTitle);
    }

    private FetchArticleImages(Parcel in)
    {
        imageTitle = in.readString();
    }

    public static final Creator<FetchArticleImages> CREATOR = new Creator<FetchArticleImages>() {
        @Override
        public FetchArticleImages createFromParcel(Parcel in) {
            return new FetchArticleImages(in);
        }

        @Override
        public FetchArticleImages[] newArray(int size) {
            return new FetchArticleImages[size];
        }
    };
}
