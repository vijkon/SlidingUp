package com.app.ui.main.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.model.main.article.FetchArticleImages
import com.app.slidingup.R
import com.app.slidingup.databinding.ArticleImageAdapterBinding
import java.util.ArrayList

class ArticleImagesAdapter : RecyclerView.Adapter<ArticleImagesAdapter.MyViewHolder>() {
    private var articleItemList: ArrayList<FetchArticleImages> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ArticleImageAdapterBinding = DataBindingUtil
            .inflate(LayoutInflater.from(parent.context), R.layout.article_image_adapter, parent, false)

        return MyViewHolder(binding)
    }

    // Binds each data in the ArrayList to a view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataModel = articleItemList[position]
        holder.dataItemBinding.dataManager = dataModel
    }

    // Gets the number of Items in the list
    override fun getItemCount(): Int = articleItemList.size

    override fun getItemViewType(position: Int): Int = position

    // UpdateData method is to add items in the articleItemList and notify the adapter for the data change.
    fun updateData(articleItemList: ArrayList<FetchArticleImages>)
    {
        this.articleItemList.clear()
        this.articleItemList = articleItemList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(dataItemLayoutBinding: ArticleImageAdapterBinding) :
        RecyclerView.ViewHolder(dataItemLayoutBinding.root) {
        var dataItemBinding: ArticleImageAdapterBinding = dataItemLayoutBinding

        init {
            dataItemLayoutBinding.root.tag = dataItemLayoutBinding.root
        }
    }
}