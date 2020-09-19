package com.example.teskotlin.activity

//import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.teskotlin.R
import com.example.teskotlin.adapter.RVAdapterStudent
import com.example.teskotlin.api.ApiEndPoint
import com.example.teskotlin.model.ClsStudents
import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var arrayList = ArrayList<ClsStudents>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "BiodataApp"
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        loadAllStudents()
        mFloatingActionButton.setOnClickListener {
            startActivity(Intent(this, ManageStudentActivity::class.java))
        }
    }

    private fun loadAllStudents(){

        val loading = ProgressDialog(this)
        loading.setMessage("Loading data...")
        loading.show()

        AndroidNetworking.get(ApiEndPoint.READ)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{

                override fun onResponse(response: JSONObject?) {
                    arrayList.clear()

                    val jsonArray = response?.optJSONArray("result")

                    if(jsonArray?.length() == 0){
                        loading.dismiss()
                        Toast.makeText(applicationContext,"Student data is empty, Please add the data first",Toast.LENGTH_SHORT).show()
                    }

                    for(i in 0 until jsonArray?.length()!!){

                        val jsonObject = jsonArray?.optJSONObject(i)
                        arrayList.add(
                            ClsStudents(
                                jsonObject.getString("nim"),
                                jsonObject.getString("name"),
                                jsonObject.getString("address"),
                                jsonObject.getString("gender")
                            )
                        )

                        if(jsonArray?.length() - 1 == i){

                            loading.dismiss()
                            val adapter =
                                RVAdapterStudent(
                                    applicationContext,
                                    arrayList
                                )
                            adapter.notifyDataSetChanged()
                            mRecyclerView.adapter = adapter
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    loading.dismiss()
                    Log.d("ONERROR",anError?.errorDetail?.toString())
                    Toast.makeText(applicationContext,"Connection Failure",Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        loadAllStudents()
    }
}