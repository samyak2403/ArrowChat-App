package com.samyak2403.arrowchatapp


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mgetphonenumber: EditText
    private lateinit var msendotp: android.widget.Button
    private lateinit var mcountrycodepicker: CountryCodePicker
    private lateinit var mprogressbarofmain: ProgressBar

    private lateinit var firebaseAuth: FirebaseAuth
    private var countrycode: String = ""
    private var phonenumber: String = ""
    private var codesent: String = ""

    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mcountrycodepicker = findViewById(R.id.countrycodepicker)
        msendotp = findViewById(R.id.sendotpbutton)
        mgetphonenumber = findViewById(R.id.getphonenumber)
        mprogressbarofmain = findViewById(R.id.progressbarofmain)

        firebaseAuth = FirebaseAuth.getInstance()

        countrycode = mcountrycodepicker.selectedCountryCodeWithPlus

        mcountrycodepicker.setOnCountryChangeListener {
            countrycode = mcountrycodepicker.selectedCountryCodeWithPlus
        }

        msendotp.setOnClickListener {
            val number = mgetphonenumber.text.toString()
            if (number.isEmpty()) {
                Toast.makeText(applicationContext, "Please Enter Your number", Toast.LENGTH_SHORT).show()
            } else if (number.length < 10) {
                Toast.makeText(applicationContext, "Please Enter correct number", Toast.LENGTH_SHORT).show()
            } else {
                mprogressbarofmain.visibility = View.VISIBLE
                phonenumber = countrycode + number

                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phonenumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(mCallbacks)
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // Handle auto verification if needed
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Handle verification failure
            }

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Toast.makeText(applicationContext, "OTP is Sent", Toast.LENGTH_SHORT).show()
                mprogressbarofmain.visibility = View.INVISIBLE
                codesent = s
                val intent = Intent(this@MainActivity, OtpAuthentication::class.java)
                intent.putExtra("otp", codesent)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
