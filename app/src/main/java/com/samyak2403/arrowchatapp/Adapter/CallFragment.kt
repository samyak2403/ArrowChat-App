/*
 * Created by Samyak Kamble on8/9/24, 10:02 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 10:02 PM
 */

package com.samyak2403.arrowchatapp.Adapter

import com.samyak2403.arrowchatapp.R



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.annotations.Nullable

class CallFragment : Fragment() {

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.callfragment, container, false)
    }
}
