package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.adapter.FavAdapter
import kotlinx.android.synthetic.main.fragment_my_favorite.view.*
import java.util.*


class Frag_Favorite : RootFragement() {
    lateinit var re:RecyclerView
    lateinit var adapter: FavAdapter
    var data= ArrayList<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootview=inflater.inflate(R.layout.fragment_my_favorite, container, false)
        rootview.add.visibility=View.GONE
        rootview.tit.gravity= Gravity.CENTER
        rootview.add.setOnClickListener {
            act.ChangePage(Frag_AddFavorite(),R.id.frage,"Frag_AddFavorite",true)
        }
//        SetModel()
        Getmodel()
        adapter=FavAdapter(data,act,true)
        re=rootview.findViewById(R.id.adapter)
        re.layoutManager=LinearLayoutManager(activity,1,true)
        re.adapter=adapter
        (re.layoutManager as LinearLayoutManager).scrollToPosition(data.size)
        return rootview
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
