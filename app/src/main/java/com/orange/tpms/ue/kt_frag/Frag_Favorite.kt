package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.FavAdapter
import kotlinx.android.synthetic.main.fragment_my_favorite.view.*
import java.util.*


class Frag_Favorite : RootFragement(R.layout.fragment_my_favorite) {
    lateinit var re: androidx.recyclerview.widget.RecyclerView
    lateinit var adapter: FavAdapter
    var data= ArrayList<String>()
    override fun viewInit() {
        rootview.add.visibility=View.GONE
        rootview.tit.gravity= Gravity.CENTER
        rootview.add.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_AddFavorite(),R.id.frage,"Frag_AddFavorite",true)
        }
        Getmodel()
        adapter=FavAdapter(data,true)
        re=rootview.findViewById(R.id.adapter)
        re.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(activity, LinearLayout.VERTICAL, true)
        re.adapter=adapter
        (re.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPosition(data.size)
    }

    fun SetModel(){
        val profilePreferences = activity!!.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("count",data.size).commit()
        for(i in 0 until data.size){
            profilePreferences.edit().putString("$i",data[i]).commit()
        }
    }
    fun Getmodel(){
        data.clear()
        val profilePreferences = activity!!.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        val a= profilePreferences.getInt("count",0)
        for(i in 0 until a){
            var tmpdata=profilePreferences.getString("$i","nodata")
            if (!tmpdata.equals("nodata")){   data.add(tmpdata)}
        }
    }
    override fun onResume() {
        super.onResume()
    }

}
