package com.orange.testlauncher.Adapter

import android.content.ComponentName
import android.content.Intent
import com.orange.blelibrary.blelibrary.Adapter.RootAdapter
import com.orange.tpms.HomeScream.Beans.AppBeans
import com.orange.tpms.R
import kotlinx.android.synthetic.main.appitem.view.*

class AppAdapter(val beans: AppBeans): RootAdapter(R.layout.appitem) {
    override fun getItemCount(): Int = beans.app.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mView.appname.text=beans.app[position].loadLabel(holder.mView.context.packageManager)
        holder.mView.setOnClickListener {
            var pkg = beans.app[position].activityInfo.packageName;
            //应用的主activity类
            var cls = beans.app[position].activityInfo.name;
            var componet = ComponentName(pkg, cls);
            var intent =  Intent();
            intent.component = componet;
            holder.mView.context.startActivity(intent);
        }
        holder.mView.appim.setImageDrawable(beans.app[position].activityInfo.loadIcon(holder.mView.context.packageManager))
    }
}