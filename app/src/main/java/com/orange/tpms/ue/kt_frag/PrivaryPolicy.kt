package com.orango.electronic.orangetxusb.SettingPagr


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.ue.kt_frag.Frag_Wifi


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class PrivaryPolicy : RootFragement() {
    companion object{
        var place=0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_privary_policy, container, false)
        (rootview.findViewById(R.id.button5) as Button).setOnClickListener {
//            act.finish()
        }
        (rootview.findViewById(R.id.button6) as Button).setOnClickListener {
            if(place==0){
                act.ChangePage(Frag_Wifi(),R.id.frage,"Sign_in",false)
            }else{
                act.GoBack()
            }

        }
        return rootview
    }
    override fun onResume() {
        super.onResume()
    }

}
