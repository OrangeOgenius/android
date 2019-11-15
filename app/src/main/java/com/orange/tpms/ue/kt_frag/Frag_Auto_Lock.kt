package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__auto__lock.view.*
import java.util.ArrayList


class Frag_Auto_Lock : RootFragement() {
    var Auto_LockList= ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__auto__lock, container, false)
        Auto_LockList.clear()
        Auto_LockList.add(resources.getString(R.string.array_autolock_1))
        Auto_LockList.add(resources.getString(R.string.array_autolock_3))
        Auto_LockList.add(resources.getString(R.string.array_autolock_5))
        Auto_LockList.add(resources.getString(R.string.array_autolock_ten))
        Auto_LockList.add(resources.getString(R.string.array_autolock_thirty))
        Auto_LockList.add(resources.getString(R.string.array_autolock_never))
        val lanAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner, Auto_LockList)
        rootview.spinner.adapter=lanAdapter
        Log.d("sleep",""+act.SleepTime/1000L)
        when(act.SleepTime/1000){
            60L->{rootview.spinner.setSelection(0)}
            180L->{rootview.spinner.setSelection(1)}
            300L->{rootview.spinner.setSelection(2)}
            600L->{rootview.spinner.setSelection(3)}
            1800L->{rootview.spinner.setSelection(4)}
            3600L->{rootview.spinner.setSelection(5)}
        }
        rootview.bt1.setOnClickListener {
            rootview.spinner.setSelection(5)
            SetUp()

        }
        rootview.setup.setOnClickListener {
            SetUp()
            GoMenu()
        }
        return rootview
    }


fun SetUp(){
    when(rootview.spinner.selectedItemPosition){
        0->{SetSleep(60)}
        1->{SetSleep(180)}
        2->{SetSleep(300)}
        3->{SetSleep(600)}
        4->{SetSleep(1800)}
        5->{SetSleep(3600)}
    }
}

}
