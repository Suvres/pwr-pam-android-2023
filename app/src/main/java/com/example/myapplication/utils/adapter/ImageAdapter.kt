package com.example.myapplication.utils.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView


class ImageAdapter {

    private var mThumbIds: List<Int>? = null
    private var mContext: Context? = null

    fun ImageAdaptor(mThumbIds: List<Int>?, mContext: Context?) {
        this.mThumbIds = mThumbIds
        this.mContext = mContext
    }

    fun getCount(): Int {
        return mThumbIds!!.size
    }

    fun getItem(position: Int): Any? {
        return null
    }

    fun getItemId(position: Int): Long {
        return mThumbIds!![position].toLong()
    }

    fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var imageView: ImageView? = convertView as ImageView?
        if (imageView == null) {
            imageView = ImageView(mContext)
            imageView.setLayoutParams(AbsListView.LayoutParams(350, 450))
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
        }
        imageView.setImageResource(mThumbIds!![position])
        return imageView
    }

    /*
    getCount() -  returns the number that how many time the adapter executes.

getItemId() - returns the id of the item which is click on the gridview.

getView() - here the actual view is specified i.e. in each grid item what is and how it to be shown, in this case a simple imageview is created.
     */


}