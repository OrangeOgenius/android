package com.orango.electronic.orangetxusb.SettingPagr


import android.widget.Button
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.ue.kt_frag.Frag_Wifi


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class PrivaryPolicy : RootFragement(R.layout.fragment_privary_policy) {
    override fun viewInit() {
        (rootview.findViewById(R.id.button5) as Button).setOnClickListener {
            //            act.finish()
        }
        (rootview.findViewById(R.id.button6) as Button).setOnClickListener {
            if(place==0){
                JzActivity.getControlInstance().changeFrag(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
            }else{
                JzActivity.getControlInstance().goBack()
            }

        }
    }

    companion object{
        var place=0
    }


    override fun onResume() {
        super.onResume()
    }

}
