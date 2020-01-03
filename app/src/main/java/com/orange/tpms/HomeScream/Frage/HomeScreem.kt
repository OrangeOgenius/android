package com.orange.testlauncher.Frage


import android.content.Intent
import android.content.res.Resources
import android.util.Log
import com.orange.testlauncher.Adapter.AppAdapter
import com.orange.tpms.HomeScream.Beans.AppBeans
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeScreem : RootFragement(R.layout.fragment_home) {
    override fun viewInit() {
        adapter= AppAdapter(appbeans)
        rootview.re.layoutManager= androidx.recyclerview.widget.GridLayoutManager(act, 4)
        rootview.re.adapter=adapter
        loadApps()
    }

    var appbeans: AppBeans = AppBeans()
    lateinit var adapter:AppAdapter
    lateinit var mResources: Resources;

    fun loadApps() {
        appbeans.clear()
        val mainIntent =  Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        for(i in act.packageManager.queryIntentActivities(mainIntent, 0)){
                appbeans.app.add(i)
        }
        Log.e("size",""+appbeans.app.size)
        adapter.notifyDataSetChanged()
    }

}
