package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.JzFragement
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_add_favorite.view.*
import java.util.*


class   Frag_AddFavorite : RootFragement(R.layout.fragment_add_favorite) {
    override fun viewInit() {
        GetMake()
        GetFav()
        rootview.make.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                GetModel()
            }

            override  fun onNothingSelected(parent: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }
        rootview.model.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                GetYear()
            }

            override  fun onNothingSelected(parent: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }
        rootview.year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            }

            override  fun onNothingSelected(parent: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }
        rootview.button7.setOnClickListener {
            if(rootview.model.selectedItem==null){  return@setOnClickListener}
            if(rootview.make.selectedItem==null){  return@setOnClickListener}
            if(rootview.year.selectedItem==null){ return@setOnClickListener }
            if(!data.contains("${rootview.make.selectedItem}☆${rootview.model.selectedItem}☆${rootview.year.selectedItem}")){ data.add("${rootview.make.selectedItem}☆${rootview.model.selectedItem}☆${rootview.year.selectedItem}")}
            SetFav()
            JzActivity.getControlInstance().goBack()
        }
        rootview.button3.setOnClickListener {
            JzActivity.getControlInstance().goBack()
        }
    }

    var data= ArrayList<String>()
    var MakeList= ArrayList<String>()
    var ModelList= ArrayList<String>()
    var YearList= ArrayList<String>()
    fun GetYear(){
        YearList.clear()
        if(rootview.model.selectedItem==null){return}
        if(rootview.make.selectedItem==null){return}
        for(i in (activity as KtActivity).itemDAO.getYear(rootview.make.selectedItem.toString(),rootview.model.selectedItem.toString())!!){YearList.add(i)}
        val arrayAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner, YearList)
        rootview.year.adapter=arrayAdapter
    }
    fun GetModel(){
        ModelList.clear()
        if(rootview.make.selectedItem==null){return}
        for(i in (activity as KtActivity).itemDAO.getModel(rootview.make.selectedItem.toString())!!){ModelList.add(i)}
        val arrayAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner, ModelList)
        rootview.model.adapter=arrayAdapter
    }
    fun GetMake(){
        MakeList.clear()
        for(i in (activity as KtActivity).itemDAO.getMake(act)!!){MakeList.add(i.make)}
        val arrayAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner, MakeList)
        rootview.make.adapter=arrayAdapter
    }
    fun SetFav(){
        val profilePreferences = activity!!.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("count",data.size).commit()
        for(i in 0 until data.size){
            profilePreferences.edit().putString("$i",data[i]).commit()
        }
    }
    fun GetFav(){
        data.clear()
        val profilePreferences = activity!!.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        val a= profilePreferences.getInt("count",0)
        for(i in 0 until a){
            var tmpdata=profilePreferences.getString("$i","nodata")
            if (!tmpdata.equals("nodata")){   data.add(tmpdata)}
        }
    }

}
