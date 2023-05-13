package com.rlapz.tippy

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var skTip: SeekBar
    private lateinit var tvTipDescription: TextView
    private lateinit var tvTipPercentLbl: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    private fun initComponents() {
        etBaseAmount = findViewById(R.id.et_base_amount)
        skTip = findViewById(R.id.sb_tip)
        tvTipDescription = findViewById(R.id.tv_tip_description)
        tvTipPercentLbl = findViewById(R.id.tv_tip_percent_lbl)
        tvTipAmount = findViewById(R.id.tv_tip_amount)
        tvTotalAmount = findViewById(R.id.tv_total_amount)

        skTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLbl.text = "${INITIAL_TIP_PERCENT}%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        // uses anonymous class as the parameter
        skTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged: $progress")
                tvTipPercentLbl.text = "${progress}%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // uses anonymous class as the parameter
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged: $s")
                computeTipAndTotal()
            }
        })
    }

    private fun computeTipAndTotal() {
        val baseAmountStr = etBaseAmount.text.toString()
        if (baseAmountStr.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = baseAmountStr.toDouble()
        val tipAmount = baseAmount * skTip.progress / 100
        val totalAmount = baseAmount + tipAmount

        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    private fun updateTipDescription(num: Int) {
        tvTipDescription.text = when (num) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        // update the color of the text view (tip description)
        val color = ArgbEvaluator().evaluate(
            num.toFloat() / skTip.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        )

        tvTipDescription.setTextColor(color as Int)
    }
}