package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__register.view.*
import java.util.ArrayList


class Frag_Register : RootFragement() {
    lateinit var AreaSpinner: Spinner
    lateinit var Store: Spinner
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var repeatpassword: EditText
    lateinit var serialnumber: EditText
    lateinit var firstname: EditText
    lateinit var lastname: EditText
    lateinit var company: EditText
    lateinit var phone: EditText
    lateinit var streat: EditText
    lateinit var city: EditText
    lateinit var state: EditText
    lateinit var zpcode: EditText
    lateinit var loadtitle: TextView
    lateinit var load: RelativeLayout
    var Arealist= ArrayList<String>()
    var Arealist2= ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__register, container, false)
        rootview.cancel.setOnClickListener {
            act.GoBack()
        }
        rootview.next.setOnClickListener {

        }
        load=rootview.findViewById(R.id.load)
        loadtitle=rootview.findViewById(R.id.textView11)
        email=rootview.findViewById(R.id.email)
        password=rootview.findViewById(R.id.password)
        repeatpassword=rootview.findViewById(R.id.repeatpassword)
        serialnumber=rootview.findViewById(R.id.serialnumber)
        firstname=rootview.findViewById(R.id.firstname)
        lastname=rootview.findViewById(R.id.lastname)
        company=rootview.findViewById(R.id.company)
        phone=rootview.findViewById(R.id.phone)
        streat=rootview.findViewById(R.id.streat)
        city=rootview.findViewById(R.id.city)
        state=rootview.findViewById(R.id.state)
        zpcode=rootview.findViewById(R.id.zpcode)
        Store=rootview.findViewById(R.id.spinner6)
        AreaSpinner=rootview.findViewById(R.id.spinner5)
        Arealist.add("Select")
        Arealist.add("EU")
        Arealist.add("North America")
        Arealist.add("台灣")
        Arealist.add("中國大陸")
        Arealist2.add(getString(R.string.Distributor))
        Arealist2.add(getString(R.string.Retailer))
        val arrayAdapter = ArrayAdapter<String>(act, R.layout.spinner, Arealist)
        val arrayAdapter2 = ArrayAdapter<String>(act, R.layout.spinner, Arealist2)
        AreaSpinner.adapter=arrayAdapter
        Store.adapter=arrayAdapter2
        return rootview
    }


}
