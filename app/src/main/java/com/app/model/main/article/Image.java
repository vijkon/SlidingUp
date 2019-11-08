package com.app.model.main.article;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import static com.app.util.Constants.*;

public class Image implements Parcelable
{
    private String imageThumbnail;

    public Image(JSONObject jsonObject)
    {
        if(jsonObject.has(queryTag))
            if(!jsonObject.isNull(queryTag))
            {
                JSONObject queryJson = jsonObject.optJSONObject(queryTag);

                if (queryJson != null && queryJson.has(pagesTag))
                {
                    if (!queryJson.isNull(pagesTag))
                    {
                        JSONArray pagesArray = queryJson.optJSONArray(pagesTag);

                        if(pagesArray != null && pagesArray.length() >= 1)
                        {
                            for (int i = 0; i < pagesArray.length(); i++)
                            {
                                JSONObject imagesObject = pagesArray.optJSONObject(i);

                                if (imagesObject != null && imagesObject.has(piPropTag))
                                {
                                    JSONObject thumbnailJson = imagesObject.optJSONObject(piPropTag);

                                    if (thumbnailJson != null && thumbnailJson.has(sourceTag))
                                        if (!thumbnailJson.isNull(sourceTag))
                                            imageThumbnail = thumbnailJson.optString(sourceTag);
                                }
                            }
                        }
                    }
                }
            }
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public void setImageThumbnail(String imageThumbnail) {
        this.imageThumbnail = imageThumbnail;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "Image{" +
                "imageThumbnail='" + imageThumbnail + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(imageThumbnail);
    }

    private Image(Parcel in)
    {
        imageThumbnail = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
